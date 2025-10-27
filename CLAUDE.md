# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Sistema de Gestion para Alquiler de Yates Turisticos - Android mobile application for tourist yacht rental management.

**Language**: Java
**Architecture**: MVC (Model-View-Controller)
**Database**: Room (SQLite local database)
**UI Framework**: Material Design 3
**Min SDK**: 26 (Android 8.0)
**Target SDK**: 36

## Build Commands

```bash
# Clean and build the project
./gradlew clean assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Check dependencies
./gradlew dependencies
```

## Architecture

The project follows **MVC pattern** with clear separation of concerns:

### Layer Structure
```
models/          → Data entities (User, ActivityLog)
views/           → Activities (LoginActivity, MainActivity)
controllers/     → Business logic (AuthController)
database/        → Room database + DAOs
repositories/    → Data access layer
utils/           → Utilities (Encryption, Validation, Config)
```

### Key Architectural Patterns

1. **Singleton Pattern**: Used for `AppDatabase`, `DatabaseHelper`, `ConfigManager`
2. **Repository Pattern**: Data access abstraction via `UserRepository`, `ActivityLogRepository`
3. **Callback Pattern**: Async operations use custom callbacks (e.g., `AuthController.LoginCallback`)
4. **Configuration Management**: `.env` file in `app/src/main/assets/` loaded via `ConfigManager`

## Database Architecture

**Room Database** with 2 entities:

- **users**: username (UNIQUE), email (UNIQUE), password (BCrypt hashed), rol (ADMIN/EMPLEADO), activo (boolean)
- **activity_logs**: user_id (FK), accion, timestamp, detalles

Database initialization happens automatically on first launch via `DatabaseHelper.initializeDefaultUsers()`.

## Security Implementation

1. **Password Encryption**: BCrypt with 12 rounds (configured in `.env`)
2. **Session Management**: Stored in SharedPreferences with 1-hour timeout
3. **Input Validation**: `ValidationUtils` for email, username, password
4. **Activity Logging**: All login/logout actions tracked in `activity_logs` table

**Important**: `.env` file contains MySQL Railway credentials - treat as sensitive.

## Theme System

The app supports **Dark Mode** with the following implementation:

- **Theme Persistence**: Stored in SharedPreferences (`ThemePrefs`)
- **Theme Toggle**: Via settings icon in MainActivity toolbar
- **Theme Application**: Both `MainActivity` and `LoginActivity` apply theme in `onCreate()` using `AppCompatDelegate.setDefaultNightMode()`
- **Color Resources**: Separate `values/colors.xml` and `values-night/colors.xml`
- **Dynamic Components**: Drawables use `@color/surface` instead of hardcoded colors to adapt to theme

### Color Philosophy
- Light mode: Nautical theme (dark blue #0A2463, ocean blue #0077BE)
- Dark mode: Light blue accents (#64B5F6) on dark backgrounds (#121212, #1E1E1E)

## Authentication Flow

1. `LoginActivity` validates credentials locally
2. `AuthController.login()` queries database via `UserRepository`
3. Password verified using `EncryptionUtils.verifyPassword()` (BCrypt)
4. Session saved to SharedPreferences with timestamp
5. Activity log created via `ActivityLogRepository`
6. User redirected to `MainActivity` with username and role data

**Session Check**: `AuthController.isLoggedIn()` validates session hasn't expired.

## Testing Credentials

```
Admin:
- Username: admin
- Password: admin123
- Role: ADMIN

Employee:
- Username: empleado1
- Password: emp123
- Role: EMPLEADO
```

## Configuration

Edit `app/src/main/assets/.env` for app configuration:

```env
APP_NAME=Sistema de Alquiler de Yates
DB_NAME=yates_db
PASSWORD_MIN_LENGTH=8
SESSION_TIMEOUT=3600
BCRYPT_ROUNDS=12
```

Configuration loaded at runtime via `ConfigManager.getInstance(context).get(key, defaultValue)`.

## Room Database Schema

Room schemas exported to `app/schemas/` directory (configured in `build.gradle.kts`).

To view schema:
```bash
cat app/schemas/com.example.sistemadeyates.database.AppDatabase/1.json
```

## Future Modules (Placeholders)

The dashboard shows 4 module cards that currently display "Proximamente" toasts:
1. Usuarios (User management CRUD)
2. Clientes (Client management)
3. Yates (Yacht inventory)
4. Reservas (Booking system)

When implementing these, create new activities in `views/` package and corresponding controllers in `controllers/` package.

## Development Notes

- **No Emojis Policy**: Code and commits should not contain emojis unless explicitly requested
- **Background Operations**: Database operations run on background threads (Room enforces main thread query protection)
- **Thread Safety**: Repository methods use `Executors.newSingleThreadExecutor()` for async operations
- **Destructive Migration**: Database uses `.fallbackToDestructiveMigration()` - data loss on schema changes
- **CRLF Line Endings**: Project uses Windows line endings (CRLF)

## Material Design Components

- `MaterialToolbar` for app bars
- `MaterialCardView` for dashboard modules
- `MaterialButton` for actions
- `TextInputLayout` + `TextInputEditText` for form inputs
- `MaterialAlertDialogBuilder` for dialogs (e.g., settings dialog)

## Gradle Configuration

- **Java Version**: 11 (source/target compatibility)
- **Kotlin DSL**: Build scripts use `.gradle.kts`
- **Version Catalog**: Dependencies managed in `gradle/libs.versions.toml`
- **Room Annotation Processing**: Schema location configured for Room compiler
