# Database Schema Documentation

## PostgreSQL 16 Database Schema

### Database: `eship`

## Tables

### 1. users
Main table for storing user accounts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| username | VARCHAR(50) | NOT NULL, UNIQUE | User's unique username |
| email | VARCHAR(100) | NOT NULL, UNIQUE | User's email address |
| password | VARCHAR(120) | NOT NULL | Encrypted password (BCrypt) |
| created_at | TIMESTAMP | NOT NULL | Account creation timestamp |
| updated_at | TIMESTAMP | NULL | Last update timestamp |
| enabled | BOOLEAN | NOT NULL, DEFAULT true | Account active status |

### 2. user_roles
Table for storing user roles (Many-to-Many relationship with users).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT | FOREIGN KEY -> users(id) | Reference to user |
| role | VARCHAR(255) | NOT NULL | Role name (e.g., 'USER', 'ADMIN') |

**Index**: Composite index on (user_id, role)

## Available Roles

- **USER**: Standard user role with basic access
- **ADMIN**: Administrator role with full access to all endpoints

## Database Setup

### 1. Create Database

```sql
CREATE DATABASE eship;
```

### 2. Connect to Database

```sql
\c eship
```

### 3. Verify Schema

Tables will be created automatically by Spring Boot JPA when the application starts (using `spring.jpa.hibernate.ddl-auto=update`).

To verify the schema:

```sql
\dt  -- List all tables
\d users  -- Describe users table
\d user_roles  -- Describe user_roles table
```

## Sample Queries

### Get all users with their roles

```sql
SELECT u.id, u.username, u.email, u.enabled, u.created_at, 
       array_agg(r.role) as roles
FROM users u
LEFT JOIN user_roles r ON u.id = r.user_id
GROUP BY u.id, u.username, u.email, u.enabled, u.created_at;
```

### Find users by role

```sql
SELECT DISTINCT u.*
FROM users u
JOIN user_roles r ON u.id = r.user_id
WHERE r.role = 'ADMIN';
```

### Count users by role

```sql
SELECT role, COUNT(*) as user_count
FROM user_roles
GROUP BY role;
```

## Security Notes

- Passwords are encrypted using BCrypt before storage
- Never store plain-text passwords
- JWT tokens are used for authentication (not stored in database)
- Token expiration is configured in application.properties (default: 24 hours)

## Maintenance

### Backup Database

```bash
pg_dump -U postgres eship > eship_backup.sql
```

### Restore Database

```bash
psql -U postgres eship < eship_backup.sql
```
