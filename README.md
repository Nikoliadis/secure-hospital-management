# Secure Hospital Management System

A secure web application for managing patient visits in a hospital environment, built with Spring Boot and Spring Security.

## Features

- **Role-Based Access Control** — Four roles with distinct permissions: Admin, Doctor, Secretariat, Patient
- **JWT Authentication** — Stateless REST API secured with JSON Web Tokens (Admin only)
- **AES-256 Encryption** — Patient diagnosis stored encrypted in the database
- **BCrypt Password Hashing** — Strength factor 12
- **Password Policy** — Minimum 8 characters, uppercase, lowercase, digit and special character required
- **Account Lockout** — Account locked after 5 consecutive failed login attempts
- **Audit Logging** — Every login attempt (success/failure) logged to database with IP address
- **CSRF Protection** — All forms protected against Cross-Site Request Forgery
- **Session Management** — 30-minute timeout, maximum one session per user
- **Input Validation** — Server-side validation on all user inputs
- **Security Headers** — Content Security Policy, Referrer Policy, Frame Options

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security 6
- **Database:** MySQL (XAMPP)
- **ORM:** Spring Data JPA / Hibernate
- **Frontend:** Thymeleaf, HTML5, CSS3
- **Authentication:** Spring Security (form login) + JWT (REST API)
- **Build Tool:** Maven

## Roles & Permissions

| Feature | Admin | Doctor | Secretariat | Patient |
|---|:---:|:---:|:---:|:---:|
| Manage Users | ✅ | ❌ | ❌ | ❌ |
| Manage Patients | ✅ | ❌ | ✅ | ❌ |
| Manage Doctors | ✅ | ❌ | ✅ | ❌ |
| Register Visit | ✅ | ✅ | ❌ | ❌ |
| View All Visits | ✅ | Own only | ❌ | ❌ |
| Edit Visit | ✅ | Own only | ❌ | ❌ |
| View Diagnosis | ✅ | Own patients | ❌ | ❌ |
| REST API Access | ✅ | ❌ | ❌ | ❌ |
| Audit Log | ✅ | ❌ | ❌ | ❌ |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- XAMPP (MySQL)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/secure-hospital-management.git
   cd secure-hospital-management
   ```

2. **Start MySQL** via XAMPP Control Panel

3. **Configure database credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or run `HospitalApplication.java` directly from your IDE.

5. **Open** `http://localhost:8080`

### Default Accounts

| Username | Password | Role |
|---|---|---|
| admin | Admin@1234! | Admin |
| dr.smith | Doctor@1234! | Doctor |
| dr.jones | Doctor@1234! | Doctor |
| secretary | Secr@1234! | Secretariat |
| patient1 | Patient@123! | Patient |

## REST API

The REST API is accessible only with a valid JWT token issued to an Admin account.

**Get token:**
```http
POST /api/auth/token
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@1234!"
}
```

**Use token:**
```http
GET /api/admin/users
Authorization: Bearer <token>
```

**Available endpoints:**
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/token` | Get JWT token |
| GET | `/api/admin/users` | List all users |
| GET | `/api/admin/users/{id}` | Get user by ID |
| DELETE | `/api/admin/users/{id}` | Delete user |
| GET | `/api/admin/patients` | List all patients |
| GET | `/api/admin/visits` | List all visits |

## Running Tests

```bash
mvn test
```

Tests use an in-memory H2 database and cover:
- Authentication and authorization per role
- JWT token generation, validation and tampering detection
- AES-256 encryption/decryption
- Password complexity validation

## Security Highlights

- Diagnosis field encrypted with AES-256 CBC before database storage
- Only the attending doctor and Admin can decrypt and view a diagnosis
- All database queries use JPA parameterized queries (SQL Injection prevention)
- Passwords never stored in plaintext
- Failed login attempts tracked per user with automatic account lockout
- All sensitive actions logged to audit table with timestamp and IP address

## Project Structure

```
src/
├── main/
│   ├── java/com/hospital/app/
│   │   ├── controller/      # Web & REST controllers
│   │   ├── dto/             # Form & request objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── security/        # Security config, JWT, handlers
│   │   ├── service/         # Business logic
│   │   └── util/            # Encryption utility
│   └── resources/
│       ├── templates/       # Thymeleaf HTML templates
│       ├── static/          # CSS, favicon
│       └── application.properties
└── test/
    └── java/com/hospital/app/
        ├── SecurityConfigTest.java
        ├── JwtUtilTest.java
        ├── EncryptionUtilTest.java
        └── PasswordValidatorTest.java
```
