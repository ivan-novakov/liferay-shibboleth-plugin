package com.liferay.portal.security.auth;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.shibboleth.util.ShibbolethPropsKeys;
import com.liferay.portal.shibboleth.util.Util;
import com.liferay.portal.util.PortalUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Romeo Sheshi
 */
public class ShibbolethAutoLogin implements AutoLogin {

	private static Log _log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

	public String[] login(HttpServletRequest req, HttpServletResponse res) throws AutoLoginException {

		String[] credentials = null;
		HttpSession session = req.getSession(false);
		long companyId = PortalUtil.getCompanyId(req);
		String login = null;

		try {
			_log.info("Shibboleth Autologin");

			if (!Util.isEnabled(companyId)) {
				return credentials;
			}

			login = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);
			if (Validator.isNull(login)) {
				return credentials;
			}
			session.removeAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);

			String authType = Util.getAuthType(companyId);
			
			_log.info("Authentication type: " + authType);
			
			User user = null;
			if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				user = loginByScreenName(companyId, login);
			} else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				user = loginByEmail(companyId, login);
			}
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

	private User loginByEmail(long companyId, String login) throws Exception {
		if (Util.importUser(companyId)) {
			_log.info("Importing user by email address");
			return PortalLDAPImporterUtil.importLDAPUser(companyId, login, StringPool.BLANK);
		} else {
			_log.info("Using local user by email address");
			return UserLocalServiceUtil.getUserByEmailAddress(companyId, login);
		}
	}

	private User loginByScreenName(long companyId, String login) throws Exception {
		if (Util.importUser(companyId)) {
			_log.info("Importing user by screen name");
			return PortalLDAPImporterUtil.importLDAPUser(companyId, StringPool.BLANK, login);
		} else {
			_log.info("Using local user by screen name");
			return UserLocalServiceUtil.getUserByScreenName(companyId, login);
		}

	}

	public void logError(Exception e) {
		_log.error("Exception message = " + e.getMessage() + " cause = " + e.getCause());
		if (_log.isDebugEnabled()) {
			e.printStackTrace();
		}

	}

}