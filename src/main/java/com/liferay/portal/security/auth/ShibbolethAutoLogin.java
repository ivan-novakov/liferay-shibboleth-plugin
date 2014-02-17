package com.liferay.portal.security.auth;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.shibboleth.util.ShibbolethPropsKeys;
import com.liferay.portal.shibboleth.util.Util;
import com.liferay.portal.util.PortalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Performs autologin based on the header values passed by Shibboleth.
 * 
 * The Shibboleth user ID header set in the configuration must contain the user
 * ID, if users are authenticated by screen name or the user email, if the users
 * are authenticated by email (Portal settings --> Authentication --> General).
 * 
 * @author Romeo Sheshi
 * @author Ivan Novakov <ivan.novakov@debug.cz>
 */
public class ShibbolethAutoLogin implements AutoLogin {

	private static Log _log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

    @Override
    public String[] handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws AutoLoginException {
        // taken from BaseAutoLogin
        if (Validator.isNull(request.getAttribute(AutoLogin.AUTO_LOGIN_REDIRECT))) {
            throw new AutoLoginException(e);
        }
        _log.error(e, e);
        return null;
    }

    @Override
    public String[] login(HttpServletRequest req, HttpServletResponse res) throws AutoLoginException {

        User user;
        String[] credentials = null;
        HttpSession session = req.getSession(false);
        long companyId = PortalUtil.getCompanyId(req);


        try {
            _log.info("Shibboleth Autologin [modified by mheder v0.2]");

            if (!Util.isEnabled(companyId)) {
                return credentials;
            }

            user = loginFromSession(companyId, session);
            if (Validator.isNull(user)) {
                return credentials;
            }

            credentials = new String[3];
            credentials[0] = String.valueOf(user.getUserId());
            credentials[1] = user.getPassword();
            credentials[2] = Boolean.TRUE.toString();
            return credentials;

        } catch (NoSuchUserException e) {
            logError(e);
        } catch (Exception e) {
            logError(e);
            throw new AutoLoginException(e);
        }

        return credentials;
    }

    private User loginFromSession(long companyId, HttpSession session) throws Exception {
        String login;
        User user = null;

        login = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);
        if (Validator.isNull(login)) {
            return null;
        }

        String authType = Util.getAuthType(companyId);

        try {
            if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
                _log.info("Trying to find user with screen name: " + login);
                user = UserLocalServiceUtil.getUserByScreenName(companyId, login);
            } else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
                _log.info("Trying to find user with email: " + login);
                user = UserLocalServiceUtil.getUserByEmailAddress(companyId, login);
            } else {
                throw new NoSuchUserException();
            }

            _log.info("User found: " + user.getScreenName() + " (" + user.getEmailAddress() + ")");

            if (Util.autoUpdateUser(companyId)) {
                _log.info("Auto-updating user...");
                updateUserFromSession(user, session);
            }

        } catch (NoSuchUserException e) {
            _log.error("User "  + login + " not found");

            if (Util.autoCreateUser(companyId)) {
                _log.info("Importing user from session...");
                user = createUserFromSession(companyId, session);
                _log.info("Created user with ID: " + user.getUserId());
            } else if (Util.importUser(companyId)) {
                _log.info("Importing user from LDAP...");
                user = PortalLDAPImporterUtil.importLDAPUser(companyId, StringPool.BLANK, login);
            }
        }

        try {
            updateUserRolesFromSession(companyId, user, session);
        } catch (Exception e) {
            _log.error("Exception while updating user roles from session: " + e.getMessage());
        }

        return user;
    }

    /**
     * Create user from session
     */
    protected User createUserFromSession(long companyId, HttpSession session) throws Exception {
        User user = null;

        String screenName = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);
        if (Validator.isNull(screenName)) {
            _log.error("Cannot create user - missing screen name");
            return user;
        }

        String emailAddress = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL);
        if (Validator.isNull(emailAddress)) {
            _log.error("Cannot create user - missing email");
            return user;
        }

        String firstname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME);
        if (Validator.isNull(firstname)) {
            _log.error("Cannot create user - missing firstname");
            return user;
        }

        String surname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME);
        if (Validator.isNull(surname)) {
            _log.error("Cannot create user - missing surname");
            return user;
        }

        _log.info("Creating user: screen name = [" + screenName + "], emailAddress = [" + emailAddress
                + "], first name = [" + firstname + "], surname = [" + surname + "]");

        return addUser(companyId, screenName, emailAddress, firstname, surname);
    }

    /**
     * Store user
     */
    private User addUser(long companyId, String screenName, String emailAddress, String firstName, String lastName)
            throws Exception {

        long creatorUserId = 0;
        boolean autoPassword = true;
        String password1 = null;
        String password2 = null;
        boolean autoScreenName = false;
        long facebookId = 0;
        String openId = StringPool.BLANK;
        Locale locale = Locale.US;
        String middleName = StringPool.BLANK;
        int prefixId = 0;
        int suffixId = 0;
        boolean male = true;
        int birthdayMonth = Calendar.JANUARY;
        int birthdayDay = 1;
        int birthdayYear = 1970;
        String jobTitle = StringPool.BLANK;

        long[] groupIds = null;
        long[] organizationIds = null;
        long[] roleIds = null;
        long[] userGroupIds = null;

        boolean sendEmail = false;
        ServiceContext serviceContext = null;

        return UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, password2,
                autoScreenName, screenName, emailAddress, facebookId, openId, locale, firstName, middleName, lastName,
                prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
                organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
    }

    protected void updateUserFromSession(User user, HttpSession session) throws Exception {
        boolean modified = false;

        String emailAddress = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL);
        if (Validator.isNotNull(emailAddress) && !user.getEmailAddress().equals(emailAddress)) {
            _log.info("User [" + user.getScreenName() + "]: update email address [" + user.getEmailAddress()
                    + "] --> [" + emailAddress + "]");
            user.setEmailAddress(emailAddress);
            modified = true;
        }

        String firstname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME);
        if (Validator.isNotNull(firstname) && !user.getFirstName().equals(firstname)) {
            _log.info("User [" + user.getScreenName() + "]: update first name [" + user.getFirstName() + "] --> ["
                    + firstname + "]");
            user.setFirstName(firstname);
            modified = true;
        }

        String surname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME);
        if (Validator.isNotNull(surname) && !user.getLastName().equals(surname)) {
            _log.info("User [" + user.getScreenName() + "]: update last name [" + user.getLastName() + "] --> ["
                    + surname + "]");
            user.setLastName(surname);
            modified = true;
        }

        if (modified) {
            UserLocalServiceUtil.updateUser(user);
        }
    }

    private void updateUserRolesFromSession(long companyId, User user, HttpSession session) throws Exception {
        if (!Util.autoAssignUserRole(companyId)) {
            return;
        }

        List<Role> currentFelRoles = getRolesFromSession(companyId, session);
        long[] currentFelRoleIds = roleListToLongArray(currentFelRoles);

        List<Role> felRoles = getAllRolesWithConfiguredSubtype(companyId);
        long[] felRoleIds = roleListToLongArray(felRoles);

        RoleLocalServiceUtil.unsetUserRoles(user.getUserId(), felRoleIds);
        RoleLocalServiceUtil.addUserRoles(user.getUserId(), currentFelRoleIds);

        _log.info("User '" + user.getScreenName() + "' has been assigned " + currentFelRoleIds.length + " role(s): "
                + Arrays.toString(currentFelRoleIds));
    }

    private long[] roleListToLongArray(List<Role> roles) {
        long[] roleIds = new long[roles.size()];

        for (int i = 0; i < roles.size(); i++) {
            roleIds[i] = roles.get(i).getRoleId();
        }

        return roleIds;
    }

    private List<Role> getAllRolesWithConfiguredSubtype(long companyId) throws Exception {
        String roleSubtype = Util.autoAssignUserRoleSubtype(companyId);
        return RoleLocalServiceUtil.getSubtypeRoles(roleSubtype);
    }

    private List<Role> getRolesFromSession(long companyId, HttpSession session) throws SystemException {
        List<Role> currentFelRoles = new ArrayList<Role>();
        String affiliation = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_AFFILIATION);

        if (Validator.isNull(affiliation)) {
            return currentFelRoles;
        }

        String[] affiliationList = affiliation.split(";");

        for (String roleName : affiliationList) {
            Role role;
            try {
                role = RoleLocalServiceUtil.getRole(companyId, roleName);
            } catch (PortalException e) {
                _log.debug("Exception while getting role with name '" + roleName + "': " + e.getMessage());
                continue;
            }

            currentFelRoles.add(role);
        }

        return currentFelRoles;
    }

    private void logError(Exception e) {
        _log.error("Exception message = " + e.getMessage() + " cause = " + e.getCause());
        if (_log.isDebugEnabled()) {
            _log.error(e);
        }

    }

}