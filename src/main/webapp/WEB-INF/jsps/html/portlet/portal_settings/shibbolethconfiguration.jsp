<%
    final String SHIBBOLETH_ENABLED = "shibboleth.enabled";
    final String SHIBBOLETH_HEADER = "shibboleth.header";
    final String SHIBBOLETH_USER_LDAP_IMPORT = "shibboleth.user.ldap.import";
    final String SHIBBOLETH_LOGOUT_ENABLE = "shibboleth.logout.enabled";
    final String SHIBBOLETH_LOGOUT_URL = "shibboleth.logout.url";


    String shibbolethEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_ENABLED, "false");
    String shibbolethHeader = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_HEADER, "");
    String shibbolethUserLdapImport = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_USER_LDAP_IMPORT, "false");
    String shibbolethLogoutEnabled = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_LOGOUT_ENABLE, "false");
    String shibbolethLogoutUrl = PrefsPropsUtil.getString(company.getCompanyId(), SHIBBOLETH_LOGOUT_URL, "");


%>
<liferay-ui:section>
    <aui:fieldset>
        <aui:input label="enabled" name='<%="settings--" + SHIBBOLETH_ENABLED + "--" %>' type="checkbox"
                   value="<%= shibbolethEnabled %>"/>
        <aui:input cssClass="lfr-input-text-container" label="shibboleth-user-header"
                   name='<%= "settings--" + SHIBBOLETH_HEADER + "--" %>' type="text" value="<%= shibbolethHeader %>"/>
        <aui:input label="import-shibboleth-users-from-ldap"
                   name='<%= "settings--" + SHIBBOLETH_USER_LDAP_IMPORT + "--" %>' type="checkbox"
                   value="<%= shibbolethUserLdapImport %>"/>
        <aui:input label="shibboleth-logout-enable" name='<%= "settings--" + SHIBBOLETH_LOGOUT_ENABLE + "--" %>'
                   type="checkbox" value="<%= shibbolethLogoutEnabled %>"/>
        <aui:input cssClass="lfr-input-text-container" label="logout-url"
                   name='<%= "settings--" + SHIBBOLETH_LOGOUT_URL + "--" %>' type="text"
                   value="<%= shibbolethLogoutUrl %>"/>
    </aui:fieldset>
</liferay-ui:section>



