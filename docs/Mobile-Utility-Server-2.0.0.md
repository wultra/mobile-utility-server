# Migration from 1.10.x to 2.0.0

This guide provides instructions for migrating from PowerAuth Mobile Utility Server version `1.10.x` to version `2.0.0`.

## Database Changes

- A new column `depth` has been added to the database table `mus_certificate`.
- A new column `ssl_pinning_required` has been added to the database table `mus_mobile_domain`.

For convenience, you can use liquibase for your database migration.

For manual changes, use SQL scripts:
- [PostgreSQL script](sql/postgresql/2.0.0-migration.sql)
- [Oracle script](sql/oracle/2.0.0-migration.sql)

## REST API Changes

The REST API has been modified to extend the SSL pinning configuration with:
- the depth of the certificate in the certification chain
- an option to disable SSL pinning for specific domains

### Admin API changes

#### POST /admin/apps

The response now contains the `domains[].certificates[].depth` attribute indicating the depth of the certificate in the certification chain.

#### GET /admin/apps/{name}

The response now contains the `domains[].certificates[].depth` attribute indicating the depth of the certificate in the certification chain.

#### PUT /admin/apps/{name}/pinning-bypass-domains

A new endpoint to define domains that are not subject to SSL pinning. See [Disable SSL Pinning for Domains](API-Admin.md#disable-ssl-pinning-for-domains) for endpoint details.

#### POST /admin/apps/{name}/certificates/auto

The response now contains the `depth` attribute indicating the depth of the certificate in the certification chain - always `0` (leaf) in this case.

#### POST /admin/apps/{name}/certificates/pem

The request can now contain the `depth` attribute to define the depth of the certificate in the certification chain. If the attribute is missing, the default value of `0` is used (leaf certificate).

The response now contains the `depth` attribute indicating the depth of the certificate in the certification chain.

### Public API changes

#### GET /app/init?appName=${name}

The response has been extended with:
- `fingerprints[].depth` - attribute indicating the depth in the certification chain of the certificate represented by the fingerprint
- `domainsConfig` - an attribute (type object) containing domain-specific configuration (see [Get App Fingerprints response](Public-REST-API.md#200-ok-1) for details) 
