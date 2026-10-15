# StockPilot — Inventory Management System

Full-stack inventory management application using React + Vite, Spring Boot 3 + Maven, Spring Data JPA, PostgreSQL, and Spring Security.

## Features
- Premium responsive light dashboard, inventory KPIs, low-stock indicators and reports
- Product catalogue with search, category/stock filters, create/update/delete and CSV export
- Supplier and warehouse management
- Stock-in, stock-out and warehouse transfers with insufficient-stock validation
- Stock history and warehouse-wise stock levels
- Admin and employee roles enforced by Spring Security on the backend
- BCrypt password encoding for configured demo users
- PostgreSQL local setup and H2 demo profile
- Dockerfile and deployment notes for Render + Neon + Vercel

## Requirements
- Java 17 or newer
- Maven 3.8+
- Node.js 18+
- PostgreSQL 14+ (unless using H2 demo profile)

## Local setup with PostgreSQL
1. Create the database once: `CREATE DATABASE inventory_db;`
2. In `backend/src/main/resources/application.properties`, set `DB_USERNAME` / `DB_PASSWORD` via environment variables, or update local defaults to match your PostgreSQL credentials.
3. Terminal 1:
   ```powershell
   cd backend
   mvn spring-boot:run
   ```
4. Terminal 2:
   ```powershell
   cd frontend
   npm install
   npm run dev
   ```
5. Open `http://localhost:5173`; backend health is at `http://localhost:8080/api/health`.

### Local demo mode without PostgreSQL
From `backend`, run `mvn spring-boot:run "-Dspring-boot.run.profiles=demo"`. The H2 in-memory database resets on restart; use PostgreSQL for persistent demo data.

## Roles
- Admin: may create/update/delete products, suppliers and warehouses and record stock movements.
- Employee: may read inventory, dashboard, transactions and reports. Write operations are blocked by backend authorization, not just hidden in the UI.
- Local default demo credentials are `admin` / `ChangeMeAdmin123!` and `employee` / `ChangeMeEmployee123!`. Set unique passwords through `INVENTORY_ADMIN_PASSWORD` and `INVENTORY_EMPLOYEE_PASSWORD` before public deployment. Do not publish real credentials.

## Configuration
Backend environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`, `INVENTORY_ADMIN_USERNAME`, `INVENTORY_ADMIN_PASSWORD`, `INVENTORY_EMPLOYEE_USERNAME`, `INVENTORY_EMPLOYEE_PASSWORD`, `PORT`.
Frontend: `VITE_API_BASE_URL` is the backend origin without `/api`; it defaults to `http://localhost:8080` locally.

## API endpoints
- `GET /api/health`
- `GET /api/auth/me`
- `GET/POST /api/products`, `GET/PUT/DELETE /api/products/{id}`
- `GET/POST /api/suppliers`, `PUT/DELETE /api/suppliers/{id}`
- `GET/POST /api/warehouses`, `PUT/DELETE /api/warehouses/{id}`
- `GET /api/dashboard`, `/api/stock`, `/api/transactions`
- `POST /api/transactions/stock-in`, `/stock-out`, `/transfer`

See `DEPLOYMENT_GUIDE.md` for public hosting instructions.

## Security and scope
This is a student demonstration project. Demo users are configured in memory from environment variables, not stored in a user table. HTTP Basic credentials are sent with API requests; public deployment must use HTTPS and unique secrets. For a production business system, add persistent user management, account lockout/rate limiting, audit records, database migrations, backups, and a formal security review.
