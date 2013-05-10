Shibboleth Authentication Plugin for Liferay
============================================

This plugin is an extended version of the plugin by rsheshi@gmail.com (see below).

Additional features has been added:

* auto create users using Shibboleth attributes (email, first name, last name)
* auto update user information upon login
* added options to specify the headers (Shibboleth attributes) to be used for extracting email, first name and last name
* basic attribute to role mapping

Requirements
------------

* Apache 2.x with mod_ssl and mod_proxy_ajp
* Shibboleth SP 2.x

Introduction
------------

Currently, there is no native Java Shibboleth service provider. If you need to protect your Java web
with Shibboleth, you have to run Apache with mod_shib in front of your servlet container (Tomcat, JBoss, ...).
The protected application must not be accessible directly, it must be run on a private address. Apache will intercept
requests, and after performing all authentication related tasks, it will pass the request to the backend servlet
container using AJP (Apache JServ Protocol).

Shibboleth Service Provider
---------------------------

A standard Shibboleth Service Provider instance may be used with one difference - the attribute preffix must bes
set to "AJP_", otherwise user attributes from Shibboleh will not be accessible in the application.

    <ApplicationDefaults entityID="https://liferay-test/shibboleth"
        REMOTE_USER="uid eppn persistent-id targeted-id" 
        attributePrefix="AJP_">


Apache configuration
--------------------

First, we need to set the AJP communication with the backend in our virtual host configuration:

    ProxyPass / ajp://localhost:8009/
    ProxyPassReverse / ajp://localhost:8009/
    
Then we'll configure Shibboleth to be "activated" for the whole site:

    <Location />
        AuthType shibboleth
        require shibboleth
    </Location>
    
And require a Shibboleth session at the "login" location:

    <Location /c/portal/login>
        AuthType shibboleth
        ShibRequireSession On
        require valid-user
    </Location>


Liferay AJP connector
---------------------


Licence
-------

[MIT Licence](http://opensource.org/licenses/mit-license.php)


Contact
-------

* homepage: https://github.com/ivan-novakov/liferay-shibboleth-plugin


Original plugin
---------------

By rsheshi@gmail.com:

http://code.google.com/p/liferay-shibboleth-plugin/
