# Mobile Utility Server API

Mobile Utility Server publishes comprehensive RESTful API with all endpoints required by a mobile app:

- [Get App Public Key](#get-app-public-key)
- [Get App Fingerprints](#get-app-fingerprints)

## Swagger Documentation

Application publishes a Swagger UI documentation at the `/swagger-ui.html` path with up-to-date information about published endpoints.

## Error Handling

REST API returns errors in a unified format:

```json
{
    "id": "${ERROR_ID}",
    "code": "${ERROR_CODE}",
    "message": "${ERROR_MESSAGE}"
}
```

| attribute | Descriotion |
|---|---|
| `id` | Unique ID of an error, for correlation in logs. |
| `code` | Unique code of an error, represents the error type. |
| `message` | Description of an error with additional details. |

## Published Resources

### Get App Public Key

Method to obtain the public key for a provided application ID. You should obtain this key manually and include it in your application. Do not download the key from the mobile application directly, since MITM attack is possible.

#### Request `GET /app/init/public-key?appName=${name}`

| Query Param | Description |
|---|---|
| `appName` | Name of the application. |

#### Response

Returns the public key in case of the success, or a standardized error output in the case of an error.

##### 200 OK

In the case of a success.

```json
{
  "publicKey": "BCeNRoRsy2KjIk7J...cp0="
}
```

| Response Attribute | Description |
|---|---|
| `publicKey` | The signature public key for a given application. |

##### 400 Bad Request

In case of any generic error.

```json
{
  "id": "${ERROR_ID}",
  "code": "${ERROR_CODE}",
  "message": "${ERROR_MESSAGE}"
}
```

##### 404 Not Found

In case application with given name is not found.

```json
{
  "id": "717e1111-1111-1111-1111-1111c53fe0da",
  "code": "PUBLIC_KEY_NOT_FOUND",
  "message": "Public key for the provided app name was not found."
}
```

### Get App Fingerprints

Method to obtain the fingerprints of the certificates that are pinned in the app for a provided application ID.

#### Request `GET /app/init?appName=${name}`

| Header | Description |
|---|---|
| `X-Cert-Pinning-Challenge` | Random challenge that is included in the response signature calculation. It must be at least 16 bytes long. |

| Query Param | Description |
|---|---|
| `appName` | Name of the application. |

#### Response

Returns the list of finterprints in case of the success, or a standardized error output in the case of an error.

##### 200 OK

In the case of a successful call.

```json
{
  "timestamp": 1604853448,
  "fingerprints": [
    {
      "name": "test1.wultra.com",
      "fingerprint": "/+zwSJh7....wo7s=",
      "expires": 1627214400
    },
    {
      "name": "test2.wultra.com",
      "fingerprint": "fj8iVdqu....vCig=",
      "expires": 1603108800
    }
  ]
}
```

| Header | Description |
|---|---|
| `X-Cert-Pinning-Signature` | Base64 value of an encoded response signature. The signature contains the response challenge as well as full response data, and it must be validated on the client side before the client accepts the response. |

| Response Attribute | Description |
|---|---|
| `timestamp` | The current timestamp on the server side. |
| `fingerprints` | Array with the TLS/SSL certificate signatures. |
| `fingerprints.name` | Name of the domain, for example, `test1.wultra.com`. |
| `fingerprints.fingerprint` | The value of the certificate/public key fingerprint. |
| `fingerprints.expires` | Unix timestamp (seconds since Jan 01, 1970) of the pinned certificate expiration. |

##### 400 Bad Request

In case of any generic error.

```json
{
    "id": "${ERROR_ID}",
    "code": "${ERROR_CODE}",
    "message": "${ERROR_MESSAGE}"
}
```

##### 403 Unauthorized

Returned if the challenge was missing, or if provided challenge was insufficient.

```json
{
    "id": "93121111-1111-1111-1111-1111e8645505",
    "code": "INSUFFICIENT_CHALLENGE",
    "message": "Request does not contain sufficiently strong challenge header, 16B is required at least."
}
```

##### 404 Not Found

In case application with given name is not found.

```json
{
    "id": "717e1111-1111-1111-1111-1111c53fe0da",
    "code": "PUBLIC_KEY_NOT_FOUND",
    "message": "Public key for the provided app name was not found."
}
```
