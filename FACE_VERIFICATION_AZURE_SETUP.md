# Azure Face Verification Setup (New Device Login)

This project now supports face verification for `STUDENT` logins on untrusted devices.

## What It Does

- Password login still runs first.
- For students, if the device is not yet trusted, the user is redirected to `/student/face-verification`.
- First-time face setup stores a reference face photo in the `users` table.
- Future new-device logins verify captured face photo against the saved reference via Azure Face API.
- On successful verification, a trusted-device cookie is issued and a hashed token is stored in `trusted_devices`.

## Azure Prerequisites

1. Create an Azure Face resource (F0/free tier is enough for initial testing).
2. Get these values from Azure Portal:
`Endpoint`
`Key`

Example endpoint format:
`https://<your-resource-name>.cognitiveservices.azure.com`

## Application Configuration

Set these in `src/main/resources/application.properties`:

```properties
app.face-verification.enabled=true
app.face-verification.endpoint=https://<your-resource-name>.cognitiveservices.azure.com
app.face-verification.api-key=<your-face-api-key>
app.face-verification.match-threshold=0.55
app.face-verification.trusted-device-days=180
app.face-verification.cookie-name=exam_trusted_device
```

## New Database Columns/Tables

With `spring.jpa.hibernate.ddl-auto=update`, schema is updated automatically.

- `users.face_reference_image_base64` (LONGTEXT)
- `users.face_reference_created_at`
- `trusted_devices` table

## Flow Notes

- Teachers are not affected by this flow.
- Students cannot open `/student/**` routes until face verification succeeds in the current session.
- If Azure is not configured while the feature is enabled, verification will show an error and cannot complete.

## Security Notes

- Device token cookie is `HttpOnly` and `SameSite=Lax`.
- Stored token in DB is hashed with SHA-256.
- Face matching uses Azure `verify` confidence and your configured threshold.
