package com.liferay.portal.shibboleth.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Romeo Sheshi
 */
public class ShibbolethPropsValues {
    public static final boolean SHIBBOLETH_ENABLED = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_ENABLED));

    public static final String SHIBBOLETH_HEADER = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER);

    public static final boolean SHIBBOLETH_USER_LDAP_IMPORT = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_LDAP_IMPORT));

    public static final boolean SHIBBOLETH_LOGOUT_ENABLE = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_ENABLE));

    public static final String SHIBBOLETH_LOGOUT_URL = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_URL);

}
