package com.liferay.portal.security.auth;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
//import com.liferay.portal.model.Contact;
//import com.liferay.portal.model.Group;
//import com.liferay.portal.model.ClassName;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
//import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
//import com.liferay.portal.service.ClassNameLocalServiceUtil;
//import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.shibboleth.util.ShibbolethPropsKeys;
import com.liferay.portal.shibboleth.util.Util;
import com.liferay.portal.util.PortalUtil;
//import com.liferay.counter.service.CounterLocalServiceUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

/**
 * Performs autologin based on the header values passed by Shibboleth. 
 * 
 * The Shibboleth user ID header set in the configuration must contain the user ID, if users are authenticated by
 * screen name or the user email, if the users are authenticated by email (Portal settings --> Authentication -->
 * General).
 * 
 * @author Romeo Sheshi
 * @author Ivan Novakov <ivan.novakov@debug.cz>
 */
public class ShibbolethAutoLogin implements AutoLogin {

	private static Log _log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

	public String[] login(HttpServletRequest req, HttpServletResponse res) throws AutoLoginException {

		User user = null;
		String[] credentials = null;
		HttpSession session = req.getSession(false);
		long companyId = PortalUtil.getCompanyId(req);
		String login = null;

		try {
			_log.info("Shibboleth Autologin [modified 1]");

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
		String login = null;
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
			}

			_log.info("User found: " + user.getScreenName() + " (" + user.getEmailAddress() + ")");

			if (Util.autoUpdateUser(companyId)) {
				_log.info("Auto-updating user...");
				updateUserFromSession(user, session);
			}

		} catch (NoSuchUserException e) {
			_log.info("User not found");

			if (Util.autoCreateUser(companyId)) {
				_log.info("Importing user from session...");
				user = createUserFromSession(companyId, session);
				_log.info("Created user with ID: " + user.getUserId());
			} else if (Util.importUser(companyId)) {
				_log.info("Importing user from LDAP...");
				user = PortalLDAPImporterUtil.importLDAPUser(companyId, StringPool.BLANK, login);
			}
		}

		return user;
	}

	private User createUserFromSession(long companyId, HttpSession session) throws Exception {
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

		user = addUser(companyId, screenName, emailAddress, firstname, surname);

		return user;
	}

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

		User user = UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, password2,
				autoScreenName, screenName, emailAddress, facebookId, openId, locale, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);

		return user;
	}

	/*
	 * private Contact createContactForUser(User user) throws Exception { long
	 * contactId = CounterLocalServiceUtil.increment(Contact.class.getName());
	 * 
	 * Contact contact = ContactLocalServiceUtil.createContact(contactId);
	 * contact.setCompanyId(user.getCompanyId()); contact.setCreateDate(new
	 * Date()); contact.setModifiedDate(new Date());
	 * contact.setUserName(user.getScreenName());
	 * contact.setUserId(user.getUserId()); contact.setLastName("contact-" +
	 * contact.getContactId()); contact.setFirstName("contact-" +
	 * contact.getContactId());
	 * 
	 * ContactLocalServiceUtil.addContact(contact);
	 * 
	 * return contact; }
	 */

	/*
	 * private Group createGroupForUser(User user) throws Exception { ClassName
	 * clsNameUser =
	 * ClassNameLocalServiceUtil.getClassName("com.liferay.portal.model.User");
	 * long classNameId = clsNameUser.getClassNameId(); long userid[] =
	 * {user.getUserId()};
	 * 
	 * long groupId = CounterLocalServiceUtil.increment(Group.class.getName());
	 * 
	 * Group userGrp = GroupLocalServiceUtil.createGroup(groupId);
	 * userGrp.setClassNameId(classNameId); userGrp.setClassPK(userid[0]);
	 * userGrp.setCompanyId(user.getCompanyId()); userGrp.setName("group" +
	 * String.valueOf(userid[0])); userGrp.setFriendlyURL("/group" + groupId);
	 * userGrp.setCreatorUserId(0); userGrp.setActive(true);
	 * 
	 * GroupLocalServiceUtil.addGroup(userGrp);
	 * 
	 * return userGrp; }
	 */

	private void updateUserFromSession(User user, HttpSession session) throws Exception {
		boolean modified = false;

		String emailAddress = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL);
		if (!Validator.isNull(emailAddress) && !user.getEmailAddress().equals(emailAddress)) {
			_log.info("User [" + user.getScreenName() + "]: update email address [" + user.getEmailAddress()
					+ "] --> [" + emailAddress + "]");
			user.setEmailAddress(emailAddress);
			modified = true;
		}

		String firstname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME);
		if (!Validator.isNull(firstname) && !user.getFirstName().equals(firstname)) {
			_log.info("User [" + user.getScreenName() + "]: update first name [" + user.getFirstName() + "] --> ["
					+ firstname + "]");
			user.setFirstName(firstname);
			modified = true;
		}

		String surname = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME);
		if (!Validator.isNull(surname) && !user.getLastName().equals(surname)) {
			_log.info("User [" + user.getScreenName() + "]: update last name [" + user.getLastName() + "] --> ["
					+ surname + "]");
			user.setLastName(surname);
			modified = true;
		}

		UserLocalServiceUtil.updateUser(user);
	}

	public void logError(Exception e) {
		_log.error("Exception message = " + e.getMessage() + " cause = " + e.getCause());
		if (_log.isDebugEnabled()) {
			e.printStackTrace();
		}

	}

}