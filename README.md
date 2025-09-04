# LMS Security (Single Spring Boot Project)

Generated: 2025-09-04T15:22:08.104313

## Features
- Email/password signup & login
- Email OTP verification (activation)
- Forgot-password with email OTP
- Google & Facebook OAuth2 login (OAuth2 client pre-configured keys)
- User settings
- Netflix-style Profiles and Device management
- JWT auth (access + refresh), BCrypt password hashing
- MVC structure (controllers/services/repositories)

## Quick start
1. Set MySQL creds in `application.yml` (schema `lms_security`).
2. Set SMTP (`MAIL_USERNAME`, `MAIL_PASSWORD`) and OAuth env vars.
3. `mvn spring-boot:run`
4. Use `/auth/*` for auth flows; `/me/*` for settings, profiles, devices.

> Replace the demo `security.jwt.secret` with a Base64-encoded 256-bit key.
