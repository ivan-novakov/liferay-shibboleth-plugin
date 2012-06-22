package com.liferay.portal.shibboleth.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.model.CompanyConstants;

public final class Util {
    private Util() {
    }

    public static boolean isEnabled(long companyId) throws Exception {
        return GetterUtil.get(
                getValue(companyId, ShibbolethPropsKeys.SHIBBOLETH_ENABLED),
                ShibbolethPropsValues.SHIBBOLETH_ENABLED);
    }

    public static boolean isLogoutEnabled(long companyId) throws Exception {
        return GetterUtil.get(
                getValue(companyId,
                        ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_ENABLE),
                ShibbolethPropsValues.SHIBBOLETH_LOGOUT_ENABLE);
    }

    public static boolean importUser(long companyId) throws Exception {
        return GetterUtil.get(
                getValue(companyId,
                        ShibbolethPropsKeys.SHIBBOLETH_USER_LDAP_IMPORT),
                ShibbolethPropsValues.SHIBBOLETH_USER_LDAP_IMPORT);
    }

    public static String getLogoutUrl(long companyId) throws Exception {
        return GetterUtil.getString(
                getValue(companyId, ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_URL),
                ShibbolethPropsValues.SHIBBOLETH_LOGOUT_URL);
    }

    public static String getHeaderName(long companyId) throws Exception {
        return GetterUtil.getString(
                getValue(companyId, ShibbolethPropsKeys.SHIBBOLETH_HEADER),
                ShibbolethPropsValues.SHIBBOLETH_HEADER);
    }

    public static String getAuthType(long companyId) throws Exception {
        return GetterUtil.getString(
                getValue(companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE), CompanyConstants.AUTH_TYPE_EA);
    }

    private static String getValue(long companyId, String key) throws Exception {
        return PrefsPropsUtil.getString(companyId, key);
    }

}
