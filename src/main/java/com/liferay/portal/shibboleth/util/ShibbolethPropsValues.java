package com.liferay.portal.shibboleth.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Romeo Sheshi
 * @author Ivan Novakov <ivan.novakov@debug.cz>
 */
public class ShibbolethPropsValues {
    public static final boolean SHIBBOLETH_ENABLED = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_ENABLED));

    public static final String SHIBBOLETH_HEADER = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER);
    
    public static final String SHIBBOLETH_HEADER_EMAIL = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER_EMAIL);
    
    public static final String SHIBBOLETH_HEADER_FIRSTNAME = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER_FIRSTNAME);
    
    public static final String SHIBBOLETH_HEADER_SURNAME = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER_SURNAME);
    
    public static final String SHIBBOLETH_HEADER_AFFILIATION = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_HEADER_AFFILIATION);
    
    public static final boolean SHIBBOLETH_USER_AUTO_CREATE = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_AUTO_CREATE));
    
    public static final boolean SHIBBOLETH_USER_AUTO_UPDATE = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_AUTO_UPDATE));

    public static final boolean SHIBBOLETH_USER_LDAP_IMPORT = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_LDAP_IMPORT));
    
    public static final boolean SHIBBOLETH_USER_ROLE_AUTO_ASSIGN = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_ROLE_AUTO_ASSIGN));
    
    public static final String SHIBBOLETH_USER_ROLE_AUTO_ASSIGN_SUBTYPE = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_USER_ROLE_AUTO_ASSIGN_SUBTYPE);

    public static final boolean SHIBBOLETH_LOGOUT_ENABLE = GetterUtil.getBoolean(PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_ENABLE));

    public static final String SHIBBOLETH_LOGOUT_URL = PropsUtil.get(ShibbolethPropsKeys.SHIBBOLETH_LOGOUT_URL);

}
