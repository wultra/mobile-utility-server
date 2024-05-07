# Administration Services

<!-- TEMPLATE api -->

The Administration Services of Mobile Utility Server provide functionality for managing applications,
certificates, versions, and text resources.

## Possible Error Codes

| HTTP Status Code | Error Code        | Description                                                                        |
|------------------|-------------------|------------------------------------------------------------------------------------|
| `400`            | `APP_EXCEPTION`   | Indicates error happening during app initialization.                               |
| `400`            | `APP_EXCEPTION`   | Indicates error happening during app initialization.                               |
| `400`            | `INVALID_REQUEST` | Indicates that the request contains invalid parameters or missing required fields. |
| `404`            | `NOT_FOUND`       | The specified resource was not found.                                              |
| `404`            | `APP_NOT_FOUND`   | Indicates that the app with a provided ID was not found                            |
| `500`            | `INTERNAL_ERROR`  | An internal server error occurred, potentially due to misconfiguration.            |

## Services

<!-- begin api POST /admin/apps -->

### Create Application

Create a new application entry in the system. This endpoint is used to register a new application with its initial
configuration and metadata.

#### Request

##### Request Body

```json
{
  "name": "Application Name",
  "description": "Description of the Application",
  "config": {
    "setting1": "value1",
    "setting2": "value2"
  }
}
```

| Attribute     | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `name`        | `String` | Name of the application.                            |
| `description` | `String` | Brief description of the application.               |
| `config`      | `Object` | Configuration settings specific to the application. |

#### Response 200

```json
{
  "id": "1234",
  "name": "Application Name",
  "description": "Description of the Application",
  "status": "ACTIVE",
  "config": {
    "setting1": "value1",
    "setting2": "value2"
  }
}
```

| Attribute     | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `id`          | `String` | Unique identifier of the application.               |
| `name`        | `String` | Name of the application.                            |
| `description` | `String` | Description of the application.                     |
| `status`      | `String` | Current status of the application (e.g., ACTIVE).   |
| `config`      | `Object` | Configuration settings specific to the application. |

#### Response 400

Application creation failed due to invalid input or missing required fields.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "INVALID_REQUEST",
    "message": "Required fields are missing"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/apps -->

### List Applications

Retrieve a list of all registered applications. This endpoint provides a summary view of each application, including
basic details.

#### Response 200

```json
{
  "applications": [
    {
      "id": "1234",
      "name": "Application Name",
      "description": "Description of the Application",
      "status": "ACTIVE"
    },
    {
      "id": "5678",
      "name": "Another Application",
      "description": "Another application description",
      "status": "INACTIVE"
    }
  ]
}
```

| Attribute     | Type     | Description                                       |
|---------------|----------|---------------------------------------------------|
| `id`          | `String` | Unique identifier of the application.             |
| `name`        | `String` | Name of the application.                          |
| `description` | `String` | Description of the application.                   |
| `status`      | `String` | Current status of the application (e.g., ACTIVE). |

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
  "id": "1234",
  "name": "Application Name",
  "description": "Description of the Application",
  "status": "ACTIVE",
  "config": {
    "setting1": "value1",
    "setting2": "value2"
  }
}
```

| Attribute     | Type     | Description                                         |
|---------------|----------|-----------------------------------------------------|
| `id`          | `String` | Unique identifier of the application.               |
| `name`        | `String` | Name of the application.                            |
| `description` | `String` | Description of the application.                     |
| `status`      | `String` | Current status of the application (e.g., ACTIVE).   |
| `config`      | `Object` | Configuration settings specific to the application. |

#### Response 404

The specified application was not found in the system.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "NOT_FOUND",
    "message": "Application not found"
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

| Param  | Type     | Description                                                        |
|--------|----------|--------------------------------------------------------------------|
| `name` | `String` | Name of the application for which the certificate will be created. |

##### Request Body

```json
{
  "certificateType": "RSA",
  "validityDays": 365
}
```

| Attribute         | Type      | Description                                        |
|-------------------|-----------|----------------------------------------------------|
| `certificateType` | `String`  | Type of the certificate to generate (e.g., 'RSA'). |
| `validityDays`    | `Integer` | Number of days the certificate will be valid.      |

#### Response 200

```json
{
  "name": "Application Name",
  "certificateId": "cert123",
  "issuedAt": "2023-05-07T14:48:00Z",
  "expiresAt": "2024-05-07T14:48:00Z",
  "certificateType": "RSA"
}
```

| Attribute         | Type     | Description                                 |
|-------------------|----------|---------------------------------------------|
| `name`            | `String` | Name of the application.                    |
| `certificateId`   | `String` | Unique identifier of the certificate.       |
| `issuedAt`        | `String` | Timestamp when the certificate was issued.  |
| `expiresAt`       | `String` | Timestamp when the certificate will expire. |
| `certificateType` | `String` | Type of the certificate.                    |

#### Response 400

Failed to create the certificate due to invalid input or server error.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "INVALID_CERTIFICATE_CREATION",
    "message": "Certificate creation failed due to invalid input"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/apps/{name}/certificates/pem -->

### Create PEM Application Certificate

Manually add a PEM format certificate to a specified application. This endpoint accepts a PEM-encoded certificate and
associates it with the application.

#### Request

##### Path Params

| Param  | Type     | Description                                                        |
|--------|----------|--------------------------------------------------------------------|
| `name` | `String` | Name of the application to which the certificate will be attached. |

##### Request Body

```json
{
  "pemCertificate": "-----BEGIN CERTIFICATE-----\\nMIICdzCCAeCgAwIBAgIB...\\n-----END CERTIFICATE-----",
  "validityDays": 365
}
```

| Attribute        | Type      | Description                                   |
|------------------|-----------|-----------------------------------------------|
| `pemCertificate` | `String`  | PEM encoded certificate string.               |
| `validityDays`   | `Integer` | Number of days the certificate will be valid. |

#### Response 200

```json
{
  "name": "Application Name",
  "certificateId": "cert456",
  "issuedAt": "2023-05-07T14:48:00Z",
  "expiresAt": "2024-05-07T14:48:00Z",
  "certificateType": "PEM"
}
```

| Attribute         | Type     | Description                                 |
|-------------------|----------|---------------------------------------------|
| `name`            | `String` | Name of the application.                    |
| `certificateId`   | `String` | Unique identifier of the certificate.       |
| `issuedAt`        | `String` | Timestamp when the certificate was issued.  |
| `expiresAt`       | `String` | Timestamp when the certificate will expire. |
| `certificateType` | `String` | Type of the certificate ('PEM').            |

#### Response 400

Failed to create the certificate due to invalid PEM format or server error.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "INVALID_PEM_FORMAT",
    "message": "Invalid PEM format provided"
  }
}
```

<!-- end -->

<!-- begin api DELETE /admin/apps/{name}/certificates -->

### Delete Certificates

Delete all certificates associated with a specific application based on domain and fingerprint criteria.

#### Request

##### Path Params

| Param  | Type     | Description                                                      |
|--------|----------|------------------------------------------------------------------|
| `name` | `String` | Name of the application from which certificates will be deleted. |

##### Query Parameters

| Parameter     | Type     | Description                                                         |
|---------------|----------|---------------------------------------------------------------------|
| `domain`      | `String` | Domain associated with the certificate.                             |
| `fingerprint` | `String` | Fingerprint of the certificate to specifically target for deletion. |

#### Response 200

Indicates that the certificates were successfully deleted.

```json
{
  "status": "SUCCESS",
  "message": "Certificates deleted successfully"
}
```

#### Response 400

Failed to delete certificates due to invalid domain or fingerprint criteria.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "INVALID_CERTIFICATE_DELETION",
    "message": "No certificates match the provided criteria"
  }
}
```

<!-- end -->

<!-- begin api DELETE /admin/apps/{name}/domains -->

### Delete Domain

Delete a domain associated with a specific application. This endpoint removes the domain linkage, effectively unlisting
the domain from the application.

#### Request

##### Path Params

| Param  | Type     | Description                                                    |
|--------|----------|----------------------------------------------------------------|
| `name` | `String` | Name of the application from which the domain will be deleted. |

##### Query Parameters

| Parameter | Type     | Description                                |
|-----------|----------|--------------------------------------------|
| `domain`  | `String` | Domain to be deleted from the application. |

#### Response 200

Indicates that the domain was successfully deleted.

```json
{
  "status": "SUCCESS",
  "message": "Domain deleted successfully"
}
```

#### Response 400

Failed to delete the domain due to it not being found or not being associated with the specified application.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "DOMAIN_NOT_FOUND",
    "message": "The specified domain does not exist or is not associated with this application"
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
  "status": "SUCCESS",
  "message": "Expired certificates deleted successfully"
}
```

#### Response 500

Failed to remove expired certificates due to an internal server error.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Failed to delete expired certificates due to a server error"
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

| Param  | Type     | Description                                                  |
|--------|----------|--------------------------------------------------------------|
| `name` | `String` | Name of the application for which versions are being listed. |

#### Response 200

```json
{
  "versions": [
    {
      "id": "v1",
      "description": "Initial release",
      "status": "ACTIVE"
    },
    {
      "id": "v2",
      "description": "Major update with new features",
      "status": "DEPRECATED"
    }
  ]
}
```

| Attribute     | Type     | Description                                               |
|---------------|----------|-----------------------------------------------------------|
| `id`          | `String` | Unique identifier of the version.                         |
| `description` | `String` | Description of what the version includes.                 |
| `status`      | `String` | Current status of the version (e.g., ACTIVE, DEPRECATED). |

<!-- end -->

<!-- begin api GET /admin/apps/{name}/versions/{id} -->

### Application Version Detail

Retrieve detailed information about a specific version of an application. This endpoint provides comprehensive details
such as the version's description, status, and creation date.

#### Request

##### Path Parameters

| Param  | Type     | Description                                        |
|--------|----------|----------------------------------------------------|
| `name` | `String` | Name of the application.                           |
| `id`   | `String` | Identifier of the version to retrieve details for. |

#### Response 200

```json
{
  "id": "v1",
  "description": "Initial release",
  "status": "ACTIVE",
  "creationDate": "2023-01-01T00:00:00Z"
}
```

| Attribute      | Type     | Description                                               |
|----------------|----------|-----------------------------------------------------------|
| `id`           | `String` | Unique identifier of the version.                         |
| `description`  | `String` | Description of what the version includes.                 |
| `status`       | `String` | Current status of the version (e.g., ACTIVE, DEPRECATED). |
| `creationDate` | `String` | ISO 8601 formatted date when the version was created.     |

#### Response 404

The specified version was not found for the application.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "VERSION_NOT_FOUND",
    "message": "The specified version does not exist for this application"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/apps/{name}/versions -->

### Create Application Version

Add a new version to a specific application. This endpoint allows the registration of a new version with details about
what it includes and its intended status.

#### Request

##### Path Parameters

| Param  | Type     | Description                                    |
|--------|----------|------------------------------------------------|
| `name` | `String` | Name of the application to add the version to. |

##### Request Body

```json
{
  "versionId": "v3",
  "description": "Security updates and minor improvements",
  "status": "ACTIVE"
}
```

| Attribute     | Type     | Description                                       |
|---------------|----------|---------------------------------------------------|
| `versionId`   | `String` | Identifier for the new version.                   |
| `description` | `String` | Description of what the version includes.         |
| `status`      | `String` | Status to set for the new version (e.g., ACTIVE). |

#### Response 200

```json
{
  "id": "v3",
  "description": "Security updates and minor improvements",
  "status": "ACTIVE",
  "creationDate": "2023-05-07T14:48:00Z"
}
```

| Attribute      | Type     | Description                                           |
|----------------|----------|-------------------------------------------------------|
| `id`           | `String` | Unique identifier of the created version.             |
| `description`  | `String` | Description of what the version includes.             |
| `status`       | `String` | Current status of the version (e.g., ACTIVE).         |
| `creationDate` | `String` | ISO 8601 formatted date when the version was created. |

#### Response 400

Failed to create the version due to invalid input or conflict with existing version identifiers.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "VERSION_CREATION_FAILED",
    "message": "Invalid input or version identifier conflict"
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

| Param  | Type     | Description                          |
|--------|----------|--------------------------------------|
| `name` | `String` | Name of the application.             |
| `id`   | `String` | Identifier of the version to delete. |

#### Response 200

Indicates that the version was successfully deleted.

```json
{
  "status": "SUCCESS",
  "message": "Version deleted successfully"
}
```

#### Response 404

The specified version was not found and could not be deleted.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "VERSION_NOT_FOUND",
    "message": "The specified version does not exist"
  }
}
```

<!-- end -->

<!-- begin api GET /admin/texts -->

### List Texts

Retrieve a list of all text entries managed within the system. This endpoint provides an overview of each text,
including key identifiers and language codes.

#### Response 200

```json
{
  "texts": [
    {
      "key": "welcome_message",
      "language": "en",
      "text": "Welcome to our application!",
      "lastUpdated": "2023-01-01T00:00:00Z"
    },
    {
      "key": "farewell_message",
      "language": "es",
      "text": "¡Hasta la vista!",
      "lastUpdated": "2023-01-02T00:00:00Z"
    }
  ]
}
```

| Attribute     | Type     | Description                                             |
|---------------|----------|---------------------------------------------------------|
| `key`         | `String` | Unique key identifier for the text.                     |
| `language`    | `String` | ISO 639-1 two-letter language code.                     |
| `text`        | `String` | The content of the text.                                |
| `lastUpdated` | `String` | ISO 8601 formatted date when the text was last updated. |

<!-- end -->

<!-- begin api GET /admin/texts/{key}/{language} -->

### Text Detail

Retrieve detailed information about a specific text identified by its key and language. This endpoint provides the
content and the last update timestamp.

#### Request

##### Path Parameters

| Param      | Type     | Description                      |
|------------|----------|----------------------------------|
| `key`      | `String` | The key identifier for the text. |
| `language` | `String` | The language code for the text.  |

#### Response 200

```json
{
  "key": "welcome_message",
  "language": "en",
  "text": "Welcome to our application!",
  "lastUpdated": "2023-01-01T00:00:00Z"
}
```

| Attribute     | Type     | Description                                             |
|---------------|----------|---------------------------------------------------------|
| `key`         | `String` | Unique key identifier for the text.                     |
| `language`    | `String` | ISO 639-1 two-letter language code.                     |
| `text`        | `String` | The content of the text.                                |
| `lastUpdated` | `String` | ISO 8601 formatted date when the text was last updated. |

#### Response 404

The specified text was not found for the given key and language.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "TEXT_NOT_FOUND",
    "message": "The specified text does not exist for the provided key and language"
  }
}
```

<!-- end -->

<!-- begin api POST /admin/texts -->

### Create Text

Add a new text entry to the system. This endpoint allows the creation of text content with a specific key and language.

#### Request

##### Request Body

```json
{
  "key": "new_message",
  "language": "fr",
  "text": "Bienvenue dans notre application!"
}
```

| Attribute  | Type     | Description                                      |
|------------|----------|--------------------------------------------------|
| `key`      | `String` | Unique key identifier for the new text.          |
| `language` | `String` | ISO 639-1 two-letter language code for the text. |
| `text`     | `String` | The content of the new text.                     |

#### Response 200

```json
{
  "status": "SUCCESS",
  "message": "Text created successfully"
}
```

#### Response 400

Failed to create the text due to invalid input or a duplicate key and language combination.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "TEXT_CREATION_FAILED",
    "message": "Duplicate key and language combination or invalid input"
  }
}
```

<!-- end -->
<!-- begin api DELETE /admin/texts/{key}/{language} -->

### Delete Text

Remove a specific text entry from the system identified by its key and language. This action is irreversible and ensures
that the text is permanently deleted from the database.

#### Request

##### Path Parameters

| Param      | Type     | Description                      |
|------------|----------|----------------------------------|
| `key`      | `String` | The key identifier for the text. |
| `language` | `String` | The language code for the text.  |

#### Response 200

```json
{
  "status": "SUCCESS",
  "message": "Text deleted successfully"
}
```

#### Response 404

The specified text was not found for the given key and language and could not be deleted.

```json
{
  "status": "ERROR",
  "responseObject": {
    "code": "TEXT_NOT_FOUND",
    "message": "The specified text does not exist for the provided key and language"
  }
}
```

<!-- end -->
