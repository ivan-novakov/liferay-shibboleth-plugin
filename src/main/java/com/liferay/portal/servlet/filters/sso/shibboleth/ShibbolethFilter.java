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
				// String loginHeader =
				// request.getHeader(Util.getHeaderName(companyId));
				_log.info("Checking for attribute [" + Util.getHeaderName(companyId) + "]");
				String loginHeader = (String) request.getAttribute(Util.getHeaderName(companyId));
				if (Validator.isNotNull(loginHeader)) {
					_log.info("Found value for attribute [" + Util.getHeaderName(companyId) + "]: '" + loginHeader + "'");
					session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN, loginHeader);
				} else {
					getLog().error("Header name=" + Util.getHeaderName(companyId) + " not present in request");
				}
			}
		}
		processFilter(ShibbolethFilter.class, request, response, filterChain);
	}

	private static Log _log = LogFactoryUtil.getLog(ShibbolethFilter.class);

}
