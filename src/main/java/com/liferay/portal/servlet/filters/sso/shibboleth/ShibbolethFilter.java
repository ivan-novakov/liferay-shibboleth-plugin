package com.liferay.portal.servlet.filters.sso.shibboleth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.shibboleth.util.ShibbolethPropsKeys;
import com.liferay.portal.shibboleth.util.Util;
import com.liferay.portal.util.PortalUtil;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Romeo Sheshi
 * @author Ivan Novakov <ivan.novakov@debug.cz>
 */
public class ShibbolethFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(HttpServletRequest request, HttpServletResponse response) {
		try {
			long companyId = PortalUtil.getCompanyId(request);
			if (Util.isEnabled(companyId)) {
				return true;
			}
		} catch (Exception e) {
			_log.error(e, e);
		}
		return false;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws Exception {

		_log.info("Shibboleth filter");

		String pathInfo = request.getPathInfo();
		HttpSession session = request.getSession();
		long companyId = PortalUtil.getCompanyId(request);

		if (pathInfo.indexOf("/portal/logout") != -1) {
			if (Util.isLogoutEnabled(companyId)) {
				session.invalidate();
				String logoutUrl = Util.getLogoutUrl(companyId);
				response.sendRedirect(logoutUrl);
				return;
			}
		} else {
			String login = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);
			if (Validator.isNull(login)) {
				processHeader(Util.getHeaderName(companyId), request, ShibbolethPropsKeys.SHIBBOLETH_LOGIN, true);
				processHeader(Util.getEmailHeaderName(companyId), request, ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL,
						false);
				processHeader(Util.getFirstnameHeaderName(companyId), request,
						ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME, false);
				processHeader(Util.getSurnameHeaderName(companyId), request,
						ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME, false);
				processHeader(Util.getAffiliationHeaderName(companyId), request,
						ShibbolethPropsKeys.SHIBBOLETH_HEADER_AFFILIATION, false);
			}
		}
		processFilter(ShibbolethFilter.class, request, response, filterChain);
	}

	protected void processHeader(String headerName, HttpServletRequest request, String sessionIndex, boolean logError)
			throws Exception {
		HttpSession session = request.getSession();
		String headerValue = (String) request.getAttribute(headerName);

		_log.info("Header [" + headerName + "]: " + headerValue);

		if (Validator.isNotNull(headerValue)) {
			session.setAttribute(sessionIndex, headerValue);
		} else if (logError == true) {
			_log.error("Required header [" + headerName + "] not found");
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ShibbolethFilter.class);

}
