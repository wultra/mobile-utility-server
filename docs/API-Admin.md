# Administration Services

<!-- TEMPLATE api -->

The Administration Services of Mobile Utility Server provide functionality for managing applications,
certificates, versions, and text resources.

## Possible Error Codes

| HTTP Status Code | Error Code             | Description                                                                                       |
|------------------|------------------------|---------------------------------------------------------------------------------------------------|
| `400`            | `APP_EXCEPTION`        | Indicates an error occurring during app operations like creating an already existing app.         |
| `400`            | `ERROR_REQUEST`        | Request did not pass a structural validation (mandatory field is null, invalid field type, etc.). |
| `400`            | `UNKNOWN_ERROR`        | An error occurred when processing the request. Problems with request binding                      |
| `401`            | `ERROR_AUTHENTICATION` | Unauthorized access attempt. This occurs when invalid credentials are provided.                   |
| `404`            | `APP_NOT_FOUND`        | Indicates that the app with the provided ID was not found.                                        |
| `500`            | `INTERNAL_ERROR`       | An internal server error occurred, potentially due to misconfiguration.                           |

## Services

<!-- begin api POST /admin/apps -->

### Create Application

Create a new application with specified name.

#### Request

##### Request Body

```json
{
  "name": "mobile-app",
  "displayName": "Mobile App"
}
```

| Attribute                                                     | Type     | Description                      |
|---------------------------------------------------------------|----------|----------------------------------|
| `name`                                                        | `String` | Name of the application.         |
| `displayName`<span class="required" title="Required">*</span> | `String` | Display name of the application. |

#### Response 200

```json
{
  "name": "Application Name",
  "displayName": "Display name of the Application",
  "publicKey": "pk1",
  "domains": [
    {
      "name": "name1",
      "certificates": [
        {
          "pem": "pem1",
          "fingerprint": "fingerprint1",
          "expires": 100
        }
      ]
    }
  ]
}
```

| Attribute                              | Type       | Description                                          |
|----------------------------------------|------------|------------------------------------------------------|
| `name`                                 | `String`   | Name of the application.                             |
| `displayName`                          | `String`   | Display name of the application.                     |
| `publicKey`                            | `String`   | Public key of the application.                       |
| `domains`                              | `Object[]` | List of domain configurations for the application.   |
| `domains[].name`                       | `String`   | Name of the domain.                                  |
| `domains[].certificates`               | `Object[]` | List of certificates for the domain.                 |
| `domains[].certificates[].pem`         | `String`   | PEM-encoded certificate.                             |
| `domains[].certificates[].fingerprint` | `String`   | Fingerprint of the certificate.                      |
| `domains[].certificates[].expires`     | `Long`     | Expiration time of the certificate in epoch seconds. |

#### Response 400

Application creation failed due to invalid input or missing required fields.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "ERROR_REQUEST",
    "message": "Required fields are missing"
  }
}
```

Application creation failed due to already used app name or error while generating cryptographic keys.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "APP_EXCEPTION",
    "message": "Application with name already exists: XY / Error while generating cryptographic keys"
  }
}
```

Possible Error states are:

- `ERROR_REQUEST` - Request did not pass a structural validation (mandatory field is null, invalid field type, etc.).
- `APP_EXCEPTION` - Application with name already exists: XY or Error while generating cryptographic keys.

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/apps -->

### List Applications

Retrieve a list of all registered applications.

#### Response 200

```json
{
  "applications": [
    {
      "name": "mobile-app-1",
      "displayName": "Mobile App 1"
    },
    {
      "name": "mobile-app-2",
      "displayName": "Mobile App 2"
    }
  ]
}
```

| Attribute                    | Type       | Description                      |
|------------------------------|------------|----------------------------------|
| `applications`               | `Object[]` | List of registered applications. |
| `applications[].name`        | `String`   | Name of the application.         |
| `applications[].displayName` | `String`   | Display name of the application. |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/apps/{name} -->

### Application Detail

Retrieve detailed information about a specific application by its name. This endpoint provides complete details
including configuration settings.

#### Request

##### Path Params

| Param  | Type     | Description                                      |
|--------|----------|--------------------------------------------------|
| `name` | `String` | Name of the application to retrieve details for. |

#### Response 200

```json
{
  "name": "Application Name",
  "displayName": "Display name of the Application",
  "publicKey": "pk1",
  "domains": [
    {
      "name": "name1",
      "certificates": [
        {
          "pem": "pem1",
          "fingerprint": "fingerprint1",
          "expires": 100
        }
      ]
    }
  ]
}
```

| Attribute                              | Type       | Description                                          |
|----------------------------------------|------------|------------------------------------------------------|
| `name`                                 | `String`   | Name of the application.                             |
| `displayName`                          | `String`   | Display name of the application.                     |
| `publicKey`                            | `String`   | Public key of the application.                       |
| `domains`                              | `Object[]` | List of domain configurations for the application.   |
| `domains[].name`                       | `String`   | Name of the domain.                                  |
| `domains[].certificates`               | `Object[]` | List of certificates for the domain.                 |
| `domains[].certificates[].pem`         | `String`   | PEM-encoded certificate.                             |
| `domains[].certificates[].fingerprint` | `String`   | Fingerprint of the certificate.                      |
| `domains[].certificates[].expires`     | `Long`     | Expiration time of the certificate in epoch seconds. |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/apps/{name}/certificates/auto -->

### Create Automatic Application Certificate

Automatically generate and associate a certificate with a specified application. This endpoint handles the creation and
registration of a new certificate using server-defined parameters.

#### Request

##### Path Params

| Param                                                  | Type     | Description                                                        |
|--------------------------------------------------------|----------|--------------------------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application for which the certificate will be created. |

##### Request Body

```json
{
  "domain": "domain1"
}
```

| Attribute                                                | Type     | Description                                     |
|----------------------------------------------------------|----------|-------------------------------------------------|
| `domain`<span class="required" title="Required">*</span> | `String` | The domain from which to fetch the certificate. |

#### Response 200

```json
{
  "name": "Domain Name",
  "pem": "pem1",
  "fingerprint": "fingerprint1",
  "expires": 100
}
```

| Attribute                                                     | Type     | Description                                 |
|---------------------------------------------------------------|----------|---------------------------------------------|
| `name`<span class="required" title="Required">*</span>        | `String` | Name of the domain.                         |
| `pem`<span class="required" title="Required">*</span>         | `String` | PEM format of the certificate.              |
| `fingerprint`<span class="required" title="Required">*</span> | `String` | Fingerprint of the certificate..            |
| `expires`<span class="required" title="Required">*</span>     | `Long`   | Timestamp when the certificate will expire. |

#### Response 400

Certificate creation failed due to invalid input or missing required fields.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "ERROR_REQUEST",
    "message": "Required fields are missing"
  }
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

#### Response 404

Failed to create the certificate because the requested app was not found.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "APP_NOT_FOUND",
    "message": "App with a provided ID was not found."
  }
}
```

#### Response 500

Error occurred during app execution.

```json
{
  "timestamp": "TIMESTAMP",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/admin/apps/mobile-app22/certificates/auto"
}
```

<!-- end -->
<!-- begin api POST /admin/apps/{name}/certificates/pem -->

### Create PEM Application Certificate

Manually add a PEM format certificate to a specified application. This endpoint accepts a PEM-encoded certificate and
associates it with the application.

#### Request

##### Path Params

| Param                                                  | Type     | Description                                                        |
|--------------------------------------------------------|----------|--------------------------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application to which the certificate will be attached. |

##### Request Body

```json
{
  "pem": "pem1"
}
```

| Attribute                                             | Type     | Description                     |
|-------------------------------------------------------|----------|---------------------------------|
| `pem`<span class="required" title="Required">*</span> | `String` | PEM encoded certificate string. |

#### Response 200

```json
{
  "name": "Domain Name",
  "pem": "pem1",
  "fingerprint": "fingerprint1",
  "expires": 100
}
```

| Attribute                                                     | Type     | Description                                 |
|---------------------------------------------------------------|----------|---------------------------------------------|
| `name`<span class="required" title="Required">*</span>        | `String` | Name of the domain.                         |
| `pem`<span class="required" title="Required">*</span>         | `String` | PEM format of the certificate.              |
| `fingerprint`<span class="required" title="Required">*</span> | `String` | Fingerprint of the certificate..            |
| `expires`<span class="required" title="Required">*</span>     | `Long`   | Timestamp when the certificate will expire. |

#### Response 400

Certificate creation failed due to invalid input or missing required fields.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "ERROR_REQUEST",
    "message": "Required fields are missing"
  }
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

#### Response 404

Failed to create the certificate because the requested app was not found.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "APP_NOT_FOUND",
    "message": "App with a provided ID was not found."
  }
}
```

#### Response 500

Error occurred during app execution.

```json
{
  "timestamp": "TIMESTAMP",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/admin/apps/mobile-app22/certificates/auto"
}
```

<!-- end -->

<!-- begin api DELETE /admin/apps/{name}/certificates -->

### Delete Certificate

Delete a certificate associated with a specific application based on domain and fingerprint criteria.

#### Request

##### Path Params

| Param                                                  | Type     | Description                                                      |
|--------------------------------------------------------|----------|------------------------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application from which certificates will be deleted. |

##### Query Parameters

| Parameter                                                     | Type     | Description                                                         |
|---------------------------------------------------------------|----------|---------------------------------------------------------------------|
| `domain`<span class="required" title="Required">*</span>      | `String` | Domain associated with the certificate.                             |
| `fingerprint`<span class="required" title="Required">*</span> | `String` | Fingerprint of the certificate to specifically target for deletion. |

#### Response 200

```json
{
  "status": "OK"
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api DELETE /admin/apps/{name}/domains -->

### Delete Domain

Delete a domain associated with a specific application.

#### Request

##### Path Params

| Param                                                  | Type     | Description                                                    |
|--------------------------------------------------------|----------|----------------------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application from which the domain will be deleted. |

##### Query Parameters

| Parameter                                                | Type     | Description                                |
|----------------------------------------------------------|----------|--------------------------------------------|
| `domain`<span class="required" title="Required">*</span> | `String` | Domain to be deleted from the application. |

#### Response 200

Indicates that the domain was successfully deleted.

```json
{
  "status": "OK"
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api DELETE /admin/certificates/expired -->

### Delete Expired Certificates

Remove all expired certificates from the system. This endpoint provides a cleanup mechanism for old or no longer valid
certificates.

#### Response 200

Indicates that all expired certificates were successfully removed.

```json
{
  "status": "OK"
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/apps/{name}/versions -->

### List Application Versions

Retrieve a list of all versions for a specific application. This endpoint provides details about each version, including
version number and status.

#### Request

##### Path Parameters

| Param                                                  | Type     | Description                                                  |
|--------------------------------------------------------|----------|--------------------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application for which versions are being listed. |

#### Response 200

```json
{
  "applicationVersions": [
    {
      "id": 1,
      "platform": "ANDROID",
      "majorOsVersion": 12,
      "suggestedVersion": "3.1.2",
      "requiredVersion": "3.1.2",
      "messageKey": "update_required"
    }
  ]
}
```

| Attribute                                                                          | Type       | Description                                                                   |
|------------------------------------------------------------------------------------|------------|-------------------------------------------------------------------------------|
| `applicationVersions`                                                              | `Object[]` | List of app versions.                                                         |
| `applicationVersions[].id`<span class="required" title="Required">*</span>         | `Long`     | Unique identifier of the application version.                                 |
| `applicationVersions[].platform`<span class="required" title="Required">*</span>   | `Enum`     | Platform of the application (e.g., ANDROID, IOS).                             |
| `applicationVersions[].majorOsVersion`                                             | `Integer`  | Major OS version for the application, may be `null` to match all.             |
| `applicationVersions[].suggestedVersion`                                           | `String`   | Suggested version of the application in SemVer 2.0 format.                    |
| `applicationVersions[].requiredVersion`                                            | `String`   | Required version of the application in SemVer 2.0 format.                     |
| `applicationVersions[].messageKey`<span class="required" title="Required">*</span> | `String`   | Key for the message related to the version (e.g., for localization purposes). |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/apps/{name}/versions/{id} -->

### Application Version Detail

Retrieve detailed information about a specific version of an application.

#### Request

##### Path Parameters

| Param                                                  | Type     | Description                                        |
|--------------------------------------------------------|----------|----------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application.                           |
| `id` <span class="required" title="Required">*</span>  | `Long`   | Identifier of the version to retrieve details for. |

#### Response 200

```json
    {
  "id": 1,
  "platform": "ANDROID",
  "majorOsVersion": 12,
  "suggestedVersion": "3.1.2",
  "requiredVersion": "3.1.2",
  "messageKey": "update_required"
}
```

| Attribute                                                    | Type      | Description                                                                   |
|--------------------------------------------------------------|-----------|-------------------------------------------------------------------------------|
| `id`<span class="required" title="Required">*</span>         | `Long`    | Unique identifier of the application version.                                 |
| `platform`<span class="required" title="Required">*</span>   | `Enum`    | Platform of the application (e.g., ANDROID, IOS).                             |
| `majorOsVersion`                                             | `Integer` | Major OS version for the application, may be `null` to match all.             |
| `suggestedVersion`                                           | `String`  | Suggested version of the application in SemVer 2.0 format.                    |
| `requiredVersion`                                            | `String`  | Required version of the application in SemVer 2.0 format.                     |
| `messageKey`<span class="required" title="Required">*</span> | `String`  | Key for the message related to the version (e.g., for localization purposes). |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/apps/{name}/versions -->

### Create Application Version

Add a new version to a specific application.

#### Request

##### Path Parameters

| Param                                                  | Type     | Description                                    |
|--------------------------------------------------------|----------|------------------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application to add the version to. |

##### Request Body

```json
{
  "platform": "ANDROID",
  "majorOsVersion": 12,
  "suggestedVersion": "3.1.2",
  "requiredVersion": "3.1.2",
  "messageKey": "update_required"
}

```

| Attribute                                                          | Type      | Description                                                                   |
|--------------------------------------------------------------------|-----------|-------------------------------------------------------------------------------|
| `platform`<span class="required" title="Required">*</span>         | `Enum`    | Platform of the application (e.g., ANDROID, IOS).                             |
| `majorOsVersion`                                                   | `Integer` | Major OS version for the application, may be `null` to match all.             |
| `suggestedVersion`<span class="required" title="Required">*</span> | `String`  | Suggested version of the application in SemVer 2.0 format.                    |
| `requiredVersion`<span class="required" title="Required">*</span>  | `String`  | Required version of the application in SemVer 2.0 format.                     |
| `messageKey`                                                       | `String`  | Key for the message related to the version (e.g., for localization purposes). |

#### Response 200

```json
    {
  "id": 1,
  "platform": "ANDROID",
  "majorOsVersion": 12,
  "suggestedVersion": "3.1.2",
  "requiredVersion": "3.1.2",
  "messageKey": "update_required"
}
```

| Attribute                                                    | Type      | Description                                                                   |
|--------------------------------------------------------------|-----------|-------------------------------------------------------------------------------|
| `id`<span class="required" title="Required">*</span>         | `Long`    | Unique identifier of the application version.                                 |
| `platform`<span class="required" title="Required">*</span>   | `Enum`    | Platform of the application (e.g., ANDROID, IOS).                             |
| `majorOsVersion`                                             | `Integer` | Major OS version for the application, may be `null` to match all.             |
| `suggestedVersion`                                           | `String`  | Suggested version of the application in SemVer 2.0 format.                    |
| `requiredVersion`                                            | `String`  | Required version of the application in SemVer 2.0 format.                     |
| `messageKey`<span class="required" title="Required">*</span> | `String`  | Key for the message related to the version (e.g., for localization purposes). |

#### Response 400

Certificate creation failed due to invalid input or missing required fields.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "ERROR_REQUEST",
    "message": "Required fields are missing"
  }
}
```

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api DELETE /admin/apps/{name}/versions/{id} -->

### Delete Application Version

Remove a specific version from an application. This action is irreversible and should be used with caution to ensure
that no critical information is lost.

#### Request

##### Path Parameters

| Param                                                  | Type     | Description                          |
|--------------------------------------------------------|----------|--------------------------------------|
| `name`<span class="required" title="Required">*</span> | `String` | Name of the application.             |
| `id`<span class="required" title="Required">*</span>   | `String` | Identifier of the version to delete. |

#### Response 200

Indicates that the version was successfully deleted.

```json
{
  "status": "OK"
}
```

<!-- end -->

<!-- begin api GET /admin/texts -->

### List Texts

Retrieve a list of all text entries managed within the system.

#### Response 200

```json
{
  "texts": [
    {
      "messageKey": "welcome_message",
      "language": "en",
      "text": "Welcome to our application!"
    },
    {
      "messageKey": "farewell_message",
      "language": "es",
      "text": "Gracias por visitar nuestra aplicación."
    }
  ]
}
```

| Attribute                                                            | Type       | Description                         |
|----------------------------------------------------------------------|------------|-------------------------------------|
| `texts`<span class="required" title="Required">*</span>              | `Object[]` | List of texts.                      |
| `texts[].messageKey`<span class="required" title="Required">*</span> | `String`   | Unique key identifier for the text. |
| `texts[].language`<span class="required" title="Required">*</span>   | `String`   | ISO 639-1 two-letter language code. |
| `texts[].text`<span class="required" title="Required">*</span>       | `String`   | The content of the text.            |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/texts/{key}/{language} -->

### Text Detail

Retrieve detailed information about a specific text identified by its key and language. This endpoint provides the
content and the last update timestamp.

#### Request

##### Path Parameters

| Param                                                      | Type     | Description                      |
|------------------------------------------------------------|----------|----------------------------------|
| `key`<span class="required" title="Required">*</span>      | `String` | The key identifier for the text. |
| `language`<span class="required" title="Required">*</span> | `String` | The language code for the text.  |

#### Response 200

```json
{
  "messageKey": "welcome_message",
  "language": "en",
  "text": "Welcome to our application!"
}
```

| Attribute                                                    | Type     | Description                         |
|--------------------------------------------------------------|----------|-------------------------------------|
| `messageKey`<span class="required" title="Required">*</span> | `String` | Unique key identifier for the text. |
| `language`<span class="required" title="Required">*</span>   | `String` | ISO 639-1 two-letter language code. |
| `text`<span class="required" title="Required">*</span>       | `String` | The content of the text.            |

#### Response 401

Invalid username or password was provided while calling the service.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "HTTP_401",
    "message": "Unauthorized"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/texts -->

### Create Text

Add a new text entry to the system.

#### Request

##### Request Body

```json
{
  "messageKey": "new_message",
  "language": "fr",
  "text": "Bienvenue dans notre application!"
}
```

| Attribute                                                    | Type     | Description                         | 
|--------------------------------------------------------------|----------|-------------------------------------|
| `messageKey`<span class="required" title="Required">*</span> | `String` | Unique key identifier for the text. |
| `language`<span class="required" title="Required">*</span>   | `String` | ISO 639-1 two-letter language code. |
| `text`<span class="required" title="Required">*</span>       | `String` | The content of the text.            |

#### Response 200

```json
{
  "messageKey": "new_message",
  "language": "fr",
  "text": "Bienvenue dans notre application!"
}
```

| Attribute                                                    | Type     | Description                         |
|--------------------------------------------------------------|----------|-------------------------------------|
| `messageKey`<span class="required" title="Required">*</span> | `String` | Unique key identifier for the text. |
| `language`<span class="required" title="Required">*</span>   | `String` | ISO 639-1 two-letter language code. |
| `text`<span class="required" title="Required">*</span>       | `String` | The content of the text.            |

#### Response 400

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "ERROR_REQUEST",
    "message": "Required fields are missing"
  }
}
```

<!-- end -->
<!-- begin api DELETE /admin/texts/{key}/{language} -->

### Delete Text

Remove a specific text entry from the system identified by its key and language.

#### Request

##### Path Parameters

| Param                                                      | Type     | Description                      |
|------------------------------------------------------------|----------|----------------------------------|
| `key`<span class="required" title="Required">*</span>      | `String` | The key identifier for the text. |
| `language`<span class="required" title="Required">*</span> | `String` | The language code for the text.  |

#### Response 200

```json
{
  "status": "OK"
}
```

<!-- end -->
