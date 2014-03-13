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

        if (pathInfo.contains("/portal/logout")) {
            if (Util.isLogoutEnabled(companyId)) {
                session.invalidate();
                String logoutUrl = Util.getLogoutUrl(companyId);
                response.sendRedirect(logoutUrl);
                return;
            }
        } else {
            extractData(session, companyId, request);
        }
        processFilter(ShibbolethFilter.class, request, response, filterChain);
    }

    /**
     * Extracts user data from AJP or HTTP header
     *
     * @return true if any data is present
     */
    protected boolean extractData(HttpSession session, long companyId, HttpServletRequest request) throws Exception {
        String login = (String) session.getAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN);
        if (Validator.isNull(login)) {

            boolean headersEnabled = Util.isHeadersEnabled(companyId);

            if (headersEnabled) {
                _log.info("Using HTTP headers as source for attribute values");
            } else {
                _log.info("Using Environment variables as source for attribute values");
            }

            String aaiProvidedLoginName = getHeader(Util.getHeaderName(companyId), request, headersEnabled);

            String aaiProvidedEmail = getHeader(Util.getEmailHeaderName(companyId), request, headersEnabled);

            String aaiProvidedFirstname = getHeader(Util.getFirstnameHeaderName(companyId), request, headersEnabled);

            String aaiProvidedSurname = getHeader(Util.getSurnameHeaderName(companyId), request, headersEnabled);

            String aaiProvidedAffiliation = getHeader(Util.getAffiliationHeaderName(companyId), request, headersEnabled);

            if (Validator.isNull(aaiProvidedLoginName)) {
                _log.error("Required header [" + Util.getHeaderName(companyId) + "] not found");
                _log.error("AAI authentication failed as login name header is empty.");
                return false;
            }
            if (Util.isScreenNameTransformEnabled(companyId)) {
                _log.info("ScreenName transform is enabled.");
                //check validity of screen name 
                if (Validator.isEmailAddress(aaiProvidedLoginName)) {
                    // most probably it is an eduPersonPrincipalName. Make transformations
                    _log.info("The login name provided by AAI looks like an "
                            + "email (or eduPersonPrincipalName): "
                            + aaiProvidedLoginName
                            + " It needs to be converted to be a Liferay screen name.");
                    aaiProvidedLoginName = aaiProvidedLoginName.replaceAll("@", ".at.");
                    _log.info("Login name is converted to:" + aaiProvidedLoginName);
                }
                //Liferay does not like underscores
                if (aaiProvidedLoginName.contains("_")) {
                    _log.info("The login name provided by AAI contains underscores:"
                            + aaiProvidedLoginName
                            + "It needs to be converted to be a Liferay screen name.");
                    aaiProvidedLoginName = aaiProvidedLoginName.replaceAll("_", "-");
                    _log.info("Login name is converted to:" + aaiProvidedLoginName);
                }
            }
            else {
                _log.info("ScreenName transform is disabled.");
            }
            
            _log.info("AAI-provided screen name is:" + aaiProvidedLoginName);
            session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_LOGIN, aaiProvidedLoginName);

            //get the first of multi-valued email address
            if (aaiProvidedEmail.contains(";")) {
                _log.info("The email address string provided by AAI is multi-valued:"
                        + aaiProvidedEmail
                        + " Using the first value.");
                String[] emails = aaiProvidedEmail.split(";");
                aaiProvidedEmail = emails[0];
            }
            _log.info("AAI-provided email is:" + aaiProvidedEmail);
            session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL, aaiProvidedEmail);

            if (Validator.isNull(aaiProvidedFirstname)) {
                _log.error("No First name provided in: "
                        + Util.getFirstnameHeaderName(companyId)
                        + " using a default value instead.");
                aaiProvidedFirstname = "MissingFirstName";
            }
            _log.info("AAI-provided first name is:" + aaiProvidedFirstname);
            session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME, aaiProvidedFirstname);

            if (Validator.isNull(aaiProvidedSurname)) {
                _log.error("No Surname provided in: "
                        + Util.getSurnameHeaderName(companyId)
                        + " using a default value instead.");
                aaiProvidedSurname = "MissingSurname";
            }
            _log.info("AAI-provided Surname is:" + aaiProvidedSurname);
            session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME, aaiProvidedSurname);

            if (Validator.isNull(aaiProvidedAffiliation)) {
                _log.debug("No affiliation provided");
                aaiProvidedAffiliation = "";
            }
            if (Util.isAffiliationTruncateEnabled(companyId) && aaiProvidedAffiliation.contains(":")) {
                _log.info("affiliation contains ':' characters: "
                        + aaiProvidedAffiliation
                        + " assuming eduPersonEntitlement format");
                // AAI-provided affiliation is multi-valued
                if (aaiProvidedAffiliation.contains(";")) {
                    _log.info("AAI-provided affiliation is multi-valued:"
                            + aaiProvidedAffiliation
                            + " Processing each vale");
                    String[] affiliations = aaiProvidedAffiliation.split(";");
                    aaiProvidedAffiliation = "";

                    for (int i = 0; i < affiliations.length; i++) {
                        aaiProvidedAffiliation += affiliations[i];
                        if (i < affiliations.length - 1) {
                            aaiProvidedAffiliation += ";";
                        }
                    }

                } else {
                    String[] parts = aaiProvidedAffiliation.split(":");
                    aaiProvidedAffiliation = parts[parts.length - 1];
                }
            }
            _log.info("AAI-provided affiliation is:" + aaiProvidedAffiliation);
            session.setAttribute(ShibbolethPropsKeys.SHIBBOLETH_HEADER_AFFILIATION, aaiProvidedAffiliation);

            return true;
        } else {
            return false;
        }
    }

    protected String getHeader(String headerName, HttpServletRequest request, boolean headersEnabled) {
        if (Validator.isNull(headerName)) {
            return null;
        }
        String headerValue;

        if (headersEnabled) {
            headerValue = request.getHeader(headerName);
        } else {
            headerValue = (String) request.getAttribute(headerName);
        }

        _log.info("Header [" + headerName + "]: " + headerValue);

        return headerValue;
    }

    private static final Log _log = LogFactoryUtil.getLog(ShibbolethFilter.class);

}
