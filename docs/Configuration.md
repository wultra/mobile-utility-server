# Configuration

Several administrative tasks should be performed regularly while working with Mobile Utility Server to keep the published content up-to-date.

## Adding New Certificate Fingerprint

Whenever your TLS/SSL certificate is about to expire, you need to add a new replacement fingerprint into the database.

To add a new certificate fingerprint, you first need to obtain the fingerprint value. You can use our [Dynamic SSL Pinning Utility](https://github.com/wultra/ssl-pinning-tool/releases) to do this. Mobile Utility Server manages the signatures for you. You can simply generate a dummy key pair and generate fingerprint for a provided domain PEM certificate:

```sh
$ java -jar ssl-pinning-tool.jar keygen -o dummy.pem -p [password]
$ java -jar ssl-pinning-tool.jar sign -k dummy.pem -c cert.pem -o output.json -p [password]
```

Note: In the case you do not have your certificate in the PEM format at hand, you can use this `openssl` call to obtain it:

```sh
$ openssl s_client -showcerts -connect my.domain.com:443 -servername my.domain.com < /dev/null | openssl x509 -outform PEM > cert.pem
```

You will obtain a result that looks like this:

```json
{
  "name" : "my.domain.com",
  "fingerprint" : "jymEKdgGPv1zS....857iLA=",
  "expires" : 1543322263,
  "signature" : "MEUCICOs9bb6TIEmRNHCekx....da166w="
}
```

You can ignore the signature value - in the case of Mobile Utility Server, the fingerprints are signed dynamically by the server. You can now simply insert the new fingerprint to the database, using the application ID as a reference (in our examples, the `app_id=1`):

```sql
INSERT INTO mobile_ssl_pinning (id, name, fingerprint, expires, app_id)
VALUES (nextval('hibernate_sequence'), 'my.domain.com', 'jymEKdgGPv1zS....857iLA=', 1543322263, 1)
```

## Adding New Application

To add a new mobile application, you need to first prepare a signing key pair. The private and public keys must be encoded in the correct Base64 encoded format used in all PowerAuth applications. Once you obtain such key pair, you can simply insert a new application record in the database:

```sql
INSERT INTO mobile_app (id, name, display_name, sign_private_key, sign_public_key)
VALUES (nextval('hibernate_sequence'), 'my-app-2', 'My App 2', 'MEU....ed8f7=', 'MBII....fed87=')
```

The application will have an automatically assigned ID from the hibernate sequence. You need to use this ID in other tables to reference the DB.
