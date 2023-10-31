# Configuration

Several administrative tasks should be performed regularly while working with Mobile Utility Server to keep the published content up-to-date.


## Network

The context path `/admin` for administrative purposes is authenticated, but it should be exposed only in trusted networks.


## Creating First Admin User

When calling admin APIs provided by the Mobile Utility Server, basic HTTP authentication is required.
This why we need to add users and their passwords in the database.

The first user we need to add is basically the main admin user, let's call the user `system-admin`.
The first user needs to be inserted in the database.

First, we need to generate the password via `openssl`:

```sh
openssl rand -base64 12
```

Output will look like this:

```
DH4v3SCoDRDUAFBD
```

In order to store the password in the database, we need to encode it using `bcrypt` first.
You can use the `htpasswd` command to achieve this:

```sh
htpasswd -bnBC 12 "" DH4v3SCoDRDUAFBD | tr -d ':'
```

Output will look like this:

```
$2y$12$GM/J9uvNy1W.eSy1mB2HB.xk2uLYtnjcD3rUEkqPkVCL43b0F9xa.
```

You can now put this all together and store the user in the database:

```sql
INSERT INTO mus_user (id, username, password, enabled)
VALUES (nextval('mus_user_seq'), 'system-admin', '$2y$12$GM/J9uvNy1W.eSy1mB2HB.xk2uLYtnjcD3rUEkqPkVCL43b0F9xa.', true);

INSERT INTO mus_user_authority (id, user_id, authority)
VALUES (nextval('mus_user_authority_seq'), (SELECT id FROM mus_user WHERE username = 'system-admin'), 'ROLE_ADMIN');
```

- We stored the hashed password for the `system-admin` user.
- We used authority `ROLE_ADMIN` for the administrator user who can call services published under `/admin` context.
  Regular users who only need to call registration and operation APIs can use a restricted `ROLE_USER` authority.

You can now check the changes in the database:

- `mus_user` - New user record with the bcrypt-hashed password.
- `mus_user_authority` - New user authority record.

At this point, you can open [http://localhost:8080/admin/apps](http://localhost:8080/admin/apps) and use `system-admin` as username and `DH4v3SCoDRDUAFBD`, your password value respectively (plaintext) as the password.


## Adding New Mobile Application

You can easily add a new mobile application by running the following API call:

```sh
curl --request POST \
  --url http://localhost:8080/admin/apps \
  --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
  --json '{
  "name": "mobile-app",
  "displayName": "My Mobile App"
}'
```

## Adding New Certificate

Whenever your TLS/SSL certificate is about to expire, you need to add a new replacement certificate and associated fingerprint into the database.

There are several ways to add a new certificate.

### Fetching Certificate Based on Domain

You can let our systems do the heavy lifting and fetch the SSL certificate automatically:

```sh
curl --request POST \
  --url http://localhost:8080/admin/apps/mobile-app/certificates/auto \
  --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
  --json '{
  "domain": "google.com"
}'
```

### Importing Certificate in PEM Format

You can push PEM file to our systems. You can typically obtain a PEM format from the certificate vendor, or you can fetch it from a domain using `openssl` command:

```sh
openssl s_client -connect google.com:443 -servername google.com  2>/dev/null </dev/null |  sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'
```

Call the following service to import the certificate (notice the `\n` symbols in the certificate string):

```sh
curl --request POST \
  --url http://localhost:8080/admin/apps/mobile-app/certificates/pem \
  --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
  --json '{
  "domain": "google.com",
  "pem": "-----BEGIN CERTIFICATE-----\nMIIOOzCCDSOgAwIBAgIRAOtktS1T8xbLEmgT/IaWpuEwDQYJKoZIhvcNAQELBQAw\nRjELMAkGA1UEBhMCVVMxIjAgBgNVBAoTGUdvb2dsZSBUcnVzdCBTZXJ2aWNlcyBM\nTEMxEzARBgNVBAMTCkdUUyBDQSAxQzMwHhcNMjMwMjA4MDQzNDMwWhcNMjMwNTAz\nMDQzNDI5WjAXMRUwEwYDVQQDDAwqLmdvb2dsZS5jb20wWTATBgcqhkjOPQIBBggq\nhkjOPQMBBwNCAATt1Q07sURF52V6U8ASJ0JPgIwyErLwD36WUnuHZDU8MCaNtrEO\nyPo9zVlTttTx6lUQQ7fm9PMxKANBXF3C4G8Xo4IMHDCCDBgwDgYDVR0PAQH/BAQD\nAgeAMBMGA1UdJQQMMAoGCCsGAQUFBwMBMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYE\nFDRGztOLSrVak9gBPXmAgIEk8vQgMB8GA1UdIwQYMBaAFIp0f6+Fze6VzT2c0OJG\nFPNxNR0nMGoGCCsGAQUFBwEBBF4wXDAnBggrBgEFBQcwAYYbaHR0cDovL29jc3Au\ncGtpLmdvb2cvZ3RzMWMzMDEGCCsGAQUFBzAChiVodHRwOi8vcGtpLmdvb2cvcmVw\nby9jZXJ0cy9ndHMxYzMuZGVyMIIJzQYDVR0RBIIJxDCCCcCCDCouZ29vZ2xlLmNv\nbYIWKi5hcHBlbmdpbmUuZ29vZ2xlLmNvbYIJKi5iZG4uZGV2ghUqLm9yaWdpbi10\nZXN0LmJkbi5kZXaCEiouY2xvdWQuZ29vZ2xlLmNvbYIYKi5jcm93ZHNvdXJjZS5n\nb29nbGUuY29tghgqLmRhdGFjb21wdXRlLmdvb2dsZS5jb22CCyouZ29vZ2xlLmNh\nggsqLmdvb2dsZS5jbIIOKi5nb29nbGUuY28uaW6CDiouZ29vZ2xlLmNvLmpwgg4q\nLmdvb2dsZS5jby51a4IPKi5nb29nbGUuY29tLmFygg8qLmdvb2dsZS5jb20uYXWC\nDyouZ29vZ2xlLmNvbS5icoIPKi5nb29nbGUuY29tLmNvgg8qLmdvb2dsZS5jb20u\nbXiCDyouZ29vZ2xlLmNvbS50coIPKi5nb29nbGUuY29tLnZuggsqLmdvb2dsZS5k\nZYILKi5nb29nbGUuZXOCCyouZ29vZ2xlLmZyggsqLmdvb2dsZS5odYILKi5nb29n\nbGUuaXSCCyouZ29vZ2xlLm5sggsqLmdvb2dsZS5wbIILKi5nb29nbGUucHSCEiou\nZ29vZ2xlYWRhcGlzLmNvbYIPKi5nb29nbGVhcGlzLmNughEqLmdvb2dsZXZpZGVv\nLmNvbYIMKi5nc3RhdGljLmNughAqLmdzdGF0aWMtY24uY29tgg9nb29nbGVjbmFw\ncHMuY26CESouZ29vZ2xlY25hcHBzLmNughFnb29nbGVhcHBzLWNuLmNvbYITKi5n\nb29nbGVhcHBzLWNuLmNvbYIMZ2tlY25hcHBzLmNugg4qLmdrZWNuYXBwcy5jboIS\nZ29vZ2xlZG93bmxvYWRzLmNughQqLmdvb2dsZWRvd25sb2Fkcy5jboIQcmVjYXB0\nY2hhLm5ldC5jboISKi5yZWNhcHRjaGEubmV0LmNughByZWNhcHRjaGEtY24ubmV0\nghIqLnJlY2FwdGNoYS1jbi5uZXSCC3dpZGV2aW5lLmNugg0qLndpZGV2aW5lLmNu\nghFhbXBwcm9qZWN0Lm9yZy5jboITKi5hbXBwcm9qZWN0Lm9yZy5jboIRYW1wcHJv\namVjdC5uZXQuY26CEyouYW1wcHJvamVjdC5uZXQuY26CF2dvb2dsZS1hbmFseXRp\nY3MtY24uY29tghkqLmdvb2dsZS1hbmFseXRpY3MtY24uY29tghdnb29nbGVhZHNl\ncnZpY2VzLWNuLmNvbYIZKi5nb29nbGVhZHNlcnZpY2VzLWNuLmNvbYIRZ29vZ2xl\ndmFkcy1jbi5jb22CEyouZ29vZ2xldmFkcy1jbi5jb22CEWdvb2dsZWFwaXMtY24u\nY29tghMqLmdvb2dsZWFwaXMtY24uY29tghVnb29nbGVvcHRpbWl6ZS1jbi5jb22C\nFyouZ29vZ2xlb3B0aW1pemUtY24uY29tghJkb3VibGVjbGljay1jbi5uZXSCFCou\nZG91YmxlY2xpY2stY24ubmV0ghgqLmZscy5kb3VibGVjbGljay1jbi5uZXSCFiou\nZy5kb3VibGVjbGljay1jbi5uZXSCDmRvdWJsZWNsaWNrLmNughAqLmRvdWJsZWNs\naWNrLmNughQqLmZscy5kb3VibGVjbGljay5jboISKi5nLmRvdWJsZWNsaWNrLmNu\nghFkYXJ0c2VhcmNoLWNuLm5ldIITKi5kYXJ0c2VhcmNoLWNuLm5ldIIdZ29vZ2xl\ndHJhdmVsYWRzZXJ2aWNlcy1jbi5jb22CHyouZ29vZ2xldHJhdmVsYWRzZXJ2aWNl\ncy1jbi5jb22CGGdvb2dsZXRhZ3NlcnZpY2VzLWNuLmNvbYIaKi5nb29nbGV0YWdz\nZXJ2aWNlcy1jbi5jb22CF2dvb2dsZXRhZ21hbmFnZXItY24uY29tghkqLmdvb2ds\nZXRhZ21hbmFnZXItY24uY29tghhnb29nbGVzeW5kaWNhdGlvbi1jbi5jb22CGiou\nZ29vZ2xlc3luZGljYXRpb24tY24uY29tgiQqLnNhZmVmcmFtZS5nb29nbGVzeW5k\naWNhdGlvbi1jbi5jb22CFmFwcC1tZWFzdXJlbWVudC1jbi5jb22CGCouYXBwLW1l\nYXN1cmVtZW50LWNuLmNvbYILZ3Z0MS1jbi5jb22CDSouZ3Z0MS1jbi5jb22CC2d2\ndDItY24uY29tgg0qLmd2dDItY24uY29tggsybWRuLWNuLm5ldIINKi4ybWRuLWNu\nLm5ldIIUZ29vZ2xlZmxpZ2h0cy1jbi5uZXSCFiouZ29vZ2xlZmxpZ2h0cy1jbi5u\nZXSCDGFkbW9iLWNuLmNvbYIOKi5hZG1vYi1jbi5jb22CFGdvb2dsZXNhbmRib3gt\nY24uY29tghYqLmdvb2dsZXNhbmRib3gtY24uY29tgh4qLnNhZmVudXAuZ29vZ2xl\nc2FuZGJveC1jbi5jb22CDSouZ3N0YXRpYy5jb22CFCoubWV0cmljLmdzdGF0aWMu\nY29tggoqLmd2dDEuY29tghEqLmdjcGNkbi5ndnQxLmNvbYIKKi5ndnQyLmNvbYIO\nKi5nY3AuZ3Z0Mi5jb22CECoudXJsLmdvb2dsZS5jb22CFioueW91dHViZS1ub2Nv\nb2tpZS5jb22CCyoueXRpbWcuY29tggthbmRyb2lkLmNvbYINKi5hbmRyb2lkLmNv\nbYITKi5mbGFzaC5hbmRyb2lkLmNvbYIEZy5jboIGKi5nLmNuggRnLmNvggYqLmcu\nY2+CBmdvby5nbIIKd3d3Lmdvby5nbIIUZ29vZ2xlLWFuYWx5dGljcy5jb22CFiou\nZ29vZ2xlLWFuYWx5dGljcy5jb22CCmdvb2dsZS5jb22CEmdvb2dsZWNvbW1lcmNl\nLmNvbYIUKi5nb29nbGVjb21tZXJjZS5jb22CCGdncGh0LmNuggoqLmdncGh0LmNu\nggp1cmNoaW4uY29tggwqLnVyY2hpbi5jb22CCHlvdXR1LmJlggt5b3V0dWJlLmNv\nbYINKi55b3V0dWJlLmNvbYIUeW91dHViZWVkdWNhdGlvbi5jb22CFioueW91dHVi\nZWVkdWNhdGlvbi5jb22CD3lvdXR1YmVraWRzLmNvbYIRKi55b3V0dWJla2lkcy5j\nb22CBXl0LmJlggcqLnl0LmJlghphbmRyb2lkLmNsaWVudHMuZ29vZ2xlLmNvbYIb\nZGV2ZWxvcGVyLmFuZHJvaWQuZ29vZ2xlLmNughxkZXZlbG9wZXJzLmFuZHJvaWQu\nZ29vZ2xlLmNughhzb3VyY2UuYW5kcm9pZC5nb29nbGUuY24wIQYDVR0gBBowGDAI\nBgZngQwBAgEwDAYKKwYBBAHWeQIFAzA8BgNVHR8ENTAzMDGgL6AthitodHRwOi8v\nY3Jscy5wa2kuZ29vZy9ndHMxYzMvZlZKeGJWLUt0bWsuY3JsMIIBAwYKKwYBBAHW\neQIEAgSB9ASB8QDvAHUA6D7Q2j71BjUy51covIlryQPTy9ERa+zraeF3fW0GvW4A\nAAGGL4TTZQAABAMARjBEAiBswmmK4qfRedFn3UOA/0R2GB0QDLT1Q3fQxshN0EQU\nMQIgZp8jeEdbzesoPEAauUhycH4tisuH5jxYbBIVV/bzZ1MAdgCzc3cH4YRQ+GOG\n1gWp3BEJSnktsWcMC4fc8AMOeTalmgAAAYYvhNN6AAAEAwBHMEUCIAS3+QtyIe7e\nqJ3MaZ06WKSOiP5oL6q0JYTH11C60kakAiEA38nXUV9Irb6fmWN2fQlLA4DUMNsO\nvRugCpNY6+H+dwMwDQYJKoZIhvcNAQELBQADggEBAKSUlwf6bi/1PYLTpTmW7F8v\nk+vp2SO1rbhgCYeoz9nfZbmfROGRYw2mmlC9anePCTQFbNkbiozeHs/cUBI4pm6D\nZy8aMDhh7ZbOA0/4ZoHXoAq/WElI8vz1g/uZ5DII8MPsSxwwe0eU8jKPzBN5mN0g\n0ObRYnTFK3odJsj5A7AX/29RaeYOX0gSWv/KeK2Z8ov3UQSBAEqbkDkznsP75n7H\nfl04q0D/QxxG+wKsf+ZsTb6ILBvcDQcnAjvjfQl+AVeCBnZTtwn0vLsgYzniBoRk\nwCItyBscNhKgLc7C8Ho1RLa/lfeW8lOGQN5ONUEuW2v6P5d9w2rLGYgSeDAIcsw=\n-----END CERTIFICATE-----"
}'
```


## Force Update

Sometimes it is needed to suggest (or even to force) the client to update the mobile application version.
It is a part of the response from [Get App Fingerprints](./Public-REST-API.md#get-app-fingerprints).

The rules are store in the [database table mus_mobile_app_version](./Database-Structure.md#mobile-application-version).

The logic is lenient, returning `OK` even if the application version is not configured.
Otherwise, compare the given application version (respecting the platform) to configured suggested or required version.

Let's say, we have an application name `my-testing-app` in current version `3.5.0` and want to suggest clients to update when they have version lower than or equal to `3.3.0`.
Moreover, we want to force update when the version is lower than or equal to `3.1.0`.
Configuration `major_os_version=null` means that the rule applies for all operation system versions of the given platform.
The following configuration force update of application version `2.9.0` because it is lower than the suggested `3.3.0` and even lower than the required `3.1.1`.    

```sql
insert into mus_mobile_app_version(id, app_id, platform, suggested_version, required_version, major_os_version, message_key)
values (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '3.3.0', '3.1.0', null, 'my-testing-app.message-key');
```

In may happen, that it is not possible to update to the new version, because it is using newer API not available in the older operation system.
In that case, configure a rule for the specific operation system major version (For iOS e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29), which overrides the generic one.
The following configuration return `OK` for application version `2.9.0` if the operation system version is `11.x.x`.

```sql
insert into mus_mobile_app_version(id, app_id, platform, suggested_version, required_version, major_os_version, message_key)
values (nextval('mus_mobile_app_version_seq'), 1, 'IOS', '2.7.0', '1.9.0', 11, 'my-testing-app.message-key');
```

The feature is enabled by default.
For performance optimization you may disable it by setting `mobile-utility-server.features.version-verification.enabled=false`


## Force Update REST Administration

You may administrate force update feature via REST.


### Localized Texts

To insert a localized text:

```sh
curl --request POST \
  --url http://localhost:8080/admin/texts \
  --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
  --json '{
  "messageKey": "required-app.internet-banking",
  "language": "en",
  "text": "Upgrade is required to make internet banking working."
}'
```


### Application Versions

To insert an application version:

```sh
curl --request POST \
  --url http://localhost:8080/admin/apps/mobile-app/versions \
  --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
  --json '{
  "majorOsVersion": 11,
  "platform": "IOS",
  "suggestedVersion": "3.2.1",
  "requiredVersion": "2.9.8",
  "messageKey": "required-app.internet-banking"
}'
```
