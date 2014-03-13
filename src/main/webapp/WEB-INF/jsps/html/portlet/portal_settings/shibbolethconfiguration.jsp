<%
    final String SHIBBOLETH_ENABLED = "shibboleth.enabled";
    final String SHIBBOLETH_HEADER = "shibboleth.header";
    final String SHIBBOLETH_USER_LDAP_IMPORT = "shibboleth.user.ldap.import";
    final String SHIBBOLETH_LOGOUT_ENABLE = "shibboleth.logout.enabled";
    final String SHIBBOLETH_LOGOUT_URL = "shibboleth.logout.url";
    final String SHIBBOLETH_HEADERS_ENABLE = "shibboleth.headers.enabled";
    final String SHIBBOLETH_AFFILIATION_TRUNCATE_ENABLE = "shibboleth.affiliation.truncate.enabled";
    final String SHIBBOLETH_SCREENNAME_TRANSFORM_ENABLE = "shibboleth.screenname.transform.enabled";
    
    final String SHIBBOLETH_HEADER_EMAIL = "shibboleth.header.email";
    final String SHIBBOLETH_HEADER_FIRSTNAME = "shibboleth.header.firstname";
    final String SHIBBOLETH_HEADER_SURNAME = "shibboleth.header.surname";
    final String SHIBBOLETH_HEADER_AFFILIATION = "shibboleth.header.affiliation";
    final String SHIBBOLETH_USER_AUTO_CREATE  = "shibboleth.user.auto.create";
    final String SHIBBOLETH_USER_AUTO_UPDATE = "shibboleth.user.auto.update";
    final String SHIBBOLETH_USER_ROLE_AUTO_ASSIGN = "shibboleth.user.role.auto.assign";
    final String SHIBBOLETH_USER_ROLE_AUTO_ASSIGN_SUBTYPE = "shibboleth.user.role.auto.assign.subtype";

    String shibbolethEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_ENABLED, "false");
    String shibbolethHeader = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER, "");
    String shibbolethUserLdapImport = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_LDAP_IMPORT, "false");
    String shibbolethLogoutEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_LOGOUT_ENABLE, "false");
    String shibbolethLogoutUrl = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_LOGOUT_URL, "");
    String shibbolethHeadersEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADERS_ENABLE, "false");
    String shibbolethAffiliationTruncateEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_AFFILIATION_TRUNCATE_ENABLE, "false");
    String shibbolethScreenNameTransformEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_SCREENNAME_TRANSFORM_ENABLE, "false");
    
    String shibbolethHeaderEmail = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER_EMAIL, "mail");
    String shibbolethHeaderFirtsname = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER_FIRSTNAME, "givenname");
    String shibbolethHeaderSurname = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER_SURNAME, "sn");
    String shibbolethHeaderAffiliation = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER_AFFILIATION, "affiliation");
    String shibbolethUserAutoCreate = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_AUTO_CREATE, "false");
    String shibbolethUserAutoUpdate = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_AUTO_UPDATE, "false");
    String shibbolethUserRoleAutoAssign = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_ROLE_AUTO_ASSIGN, "false");
    String shibbolethUserRoleAutoAssignSubtype = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_ROLE_AUTO_ASSIGN_SUBTYPE, "");
%>
<liferay-ui:section>
    <aui:fieldset>
        <aui:input label="enabled" name='<%="settings--" + SHIBBOLETH_ENABLED + "--" %>' type="checkbox"
                   value="<%= shibbolethEnabled %>"/>
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-id-header"
                   name='<%= "settings--" + SHIBBOLETH_HEADER + "--" %>' type="text" value="<%= shibbolethHeader %>"/>
        <aui:input label="shibboleth-screenname-transform-enable" name='<%= "settings--" + SHIBBOLETH_SCREENNAME_TRANSFORM_ENABLE + "--" %>'
                   type="checkbox" value="<%= shibbolethScreenNameTransformEnabled %>"/>
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-header-email"
                   name='<%= "settings--" + SHIBBOLETH_HEADER_EMAIL + "--" %>' type="text" value="<%= shibbolethHeaderEmail %>"/>    
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-header-firstname"
                   name='<%= "settings--" + SHIBBOLETH_HEADER_FIRSTNAME + "--" %>' type="text" value="<%= shibbolethHeaderFirtsname %>"/>  
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-header-surname"
                   name='<%= "settings--" + SHIBBOLETH_HEADER_SURNAME + "--" %>' type="text" value="<%= shibbolethHeaderSurname %>"/>    
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-header-affiliation"
                   name='<%= "settings--" + SHIBBOLETH_HEADER_AFFILIATION + "--" %>' type="text" value="<%= shibbolethHeaderAffiliation %>"/>        
        <aui:input label="shibboleth-affiliation-truncate-enable" name='<%= "settings--" + SHIBBOLETH_AFFILIATION_TRUNCATE_ENABLE + "--" %>'
                   type="checkbox" value="<%= shibbolethAffiliationTruncateEnabled %>"/>
        <aui:input label="auto-create-users"
                   name='<%= "settings--" + SHIBBOLETH_USER_AUTO_CREATE + "--" %>' type="checkbox"
                   value="<%= shibbolethUserAutoCreate %>"/>
        <aui:input label="auto-update-users"
                   name='<%= "settings--" + SHIBBOLETH_USER_AUTO_UPDATE + "--" %>' type="checkbox"
                   value="<%= shibbolethUserAutoUpdate %>"/>
        <aui:input label="import-shibboleth-users-from-ldap"
                   name='<%= "settings--" + SHIBBOLETH_USER_LDAP_IMPORT + "--" %>' type="checkbox"
                   value="<%= shibbolethUserLdapImport %>"/>
        <aui:input label="auto-assign-user-role"
                   name='<%= "settings--" + SHIBBOLETH_USER_ROLE_AUTO_ASSIGN + "--" %>' type="checkbox"
                   value="<%= shibbolethUserRoleAutoAssign %>"/>
        <aui:input cssClass="lfr-input-text-container" label="auto-assign-user-role-subtype"
                   name='<%= "settings--" + SHIBBOLETH_USER_ROLE_AUTO_ASSIGN_SUBTYPE + "--" %>' type="text"
                   value="<%= shibbolethUserRoleAutoAssignSubtype %>"/>                   
        <aui:input label="shibboleth-logout-enable" name='<%= "settings--" + SHIBBOLETH_LOGOUT_ENABLE + "--" %>'
                   type="checkbox" value="<%= shibbolethLogoutEnabled %>"/>
        <aui:input cssClass="lfr-input-text-container" label="logout-url"
                   name='<%= "settings--" + SHIBBOLETH_LOGOUT_URL + "--" %>' type="text"
                   value="<%= shibbolethLogoutUrl %>"/>
        <aui:input label="shibboleth-headers-enable" name='<%= "settings--" + SHIBBOLETH_HEADERS_ENABLE + "--" %>'
                   type="checkbox" value="<%= shibbolethHeadersEnabled %>"/>
    </aui:fieldset>
</liferay-ui:section>



