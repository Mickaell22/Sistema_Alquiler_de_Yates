# Sistema de Gestion para Alquiler de Yates Turisticos

Aplicacion movil desarrollada en Android para la gestion de alquiler de yates turisticos.

## Grupo de Desarrollo

- Moran Vera Mickaell
- Salazar Mejia Marco Antonio
- Leon Vera Bryan Jhoel

## Arquitectura

El proyecto utiliza el patron **MVC (Model-View-Controller)** con las siguientes capas:

- **Models**: Entidades de datos (User, Cliente, Yate, Reserva, ActivityLog)
- **Views**: Activities y Layouts XML (LoginActivity, MainActivity)
- **Controllers**: Logica de negocio (AuthController, UserController)
- **Database**: Room Database con DAOs
- **Repositories**: Capa de acceso a datos
- **Utils**: Utilidades y helpers

## Tecnologias Utilizadas

- **Lenguaje**: Java
- **Base de Datos**: Room (SQLite)
- **UI**: Material Design 3
- **Seguridad**: BCrypt para encriptacion de contrasenas
- **Gestion de Configuracion**: Archivo .env en assets

## Credenciales de Prueba

### Usuario Administrador
- **Usuario**: `admin`
- **Contrasena**: `admin123`
- **Rol**: ADMIN

### Usuario Empleado
- **Usuario**: `empleado1`
- **Contrasena**: `emp123`
- **Rol**: EMPLEADO

## Estructura del Proyecto

```
app/src/main/java/com/example/sistemadeyates/
├── models/              (Entidades)
│   ├── User.java
│   └── ActivityLog.java
│
├── controllers/         (Logica de negocio)
│   └── AuthController.java
│
├── views/              (Activities)
│   └── LoginActivity.java
│
├── database/           (Room Database)
│   ├── AppDatabase.java
│   ├── DatabaseHelper.java
│   └── dao/
│       ├── UserDao.java
│       └── ActivityLogDao.java
│
├── repositories/       (Acceso a datos)
│   ├── UserRepository.java
│   └── ActivityLogRepository.java
│
└── utils/              (Utilidades)
    ├── ConfigManager.java
    ├── ValidationUtils.java
    ├── EncryptionUtils.java
    └── Constants.java
```

## Caracteristicas Implementadas

### Fase 1 - Sistema de Login

- [x] Pantalla de login con diseno moderno
- [x] Validacion de credenciales con BCrypt
- [x] Sistema de autenticacion segura
- [x] Almacenamiento de sesion
- [x] Dashboard principal con menu de modulos
- [x] Logs de actividad de usuarios
- [x] Inicializacion automatica de usuarios de prueba

## Caracteristicas de Seguridad

1. **Encriptacion de Contrasenas**: Uso de BCrypt con 12 rounds
2. **Validacion de Inputs**: Validacion de email, usuario y contrasena
3. **Sesion con Timeout**: 1 hora de duracion
4. **Logs de Actividad**: Registro de login/logout
5. **Usuarios Inactivos**: Control de estado de usuarios

## Proximas Funcionalidades

### Modulo 1: Gestion de Usuarios
- CRUD completo de usuarios
- Panel administrativo
- Gestion de roles y permisos

### Modulo 2: Gestion de Clientes
- CRUD de clientes
- Historial de alquileres
- Gestion de documentos

### Modulo 3: Gestion de Yates
- CRUD de yates
- Inventario de embarcaciones
- Galeria multimedia

### Modulo 4: Sistema de Reservas
- Motor de busqueda
- Calendario de disponibilidad
- Pagos y facturacion
- Generacion de contratos

## Instalacion y Ejecucion

1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Sincronizar Gradle (Build > Make Project)
4. Ejecutar en emulador o dispositivo fisico

## Configuracion

El archivo `.env` en `app/src/main/assets/` contiene la configuracion:

```env
APP_NAME=Sistema de Alquiler de Yates
DB_NAME=yates_db
DB_VERSION=1
PASSWORD_MIN_LENGTH=8
SESSION_TIMEOUT=3600
BCRYPT_ROUNDS=12
```

## Base de Datos

### Tablas Creadas

**users**
- id (PRIMARY KEY)
- username (UNIQUE)
- email (UNIQUE)
- password (HASHED)
- rol (ADMIN/EMPLEADO)
- activo (BOOLEAN)
- fecha_creacion (TIMESTAMP)

**activity_logs**
- id (PRIMARY KEY)
- user_id (FOREIGN KEY)
- accion (LOGIN/LOGOUT/etc)
- timestamp
- detalles (TEXT)

## Flujo de Autenticacion

1. Usuario abre la app (LoginActivity)
2. Ingresa credenciales
3. Validacion local de campos vacios
4. AuthController.login() busca usuario en DB
5. Verifica password con BCrypt
6. Guarda sesion en SharedPreferences
7. Registra log de actividad
8. Redirige a MainActivity (Dashboard)

## Notas Importantes

- Las contrasenas NUNCA se almacenan en texto plano
- La sesion expira despues de 1 hora de inactividad
- Los usuarios inactivos no pueden iniciar sesion
- El sistema inicializa automaticamente 2 usuarios de prueba
- Se implementan buenas practicas: sin emojis, sin rutas hardcodeadas

## Contacto y Soporte

Para consultas o reportar problemas, contactar al equipo de desarrollo.

---

