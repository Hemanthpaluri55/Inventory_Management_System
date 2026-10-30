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
Backend environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`, `INVENTORY_ADMIN_USERNAME`, `INVENTORY_ADMIN_PASSWORD`, `INVENTORY_EMPLOYEE_USERNAME`, `INVENTORY_EMPLOYEE_PASSWORD`, `INVENTORY_EMAIL_NOTIFICATIONS_ENABLED`, `INVENTORY_ALERT_EMAIL_TO`, `INVENTORY_ALERT_EMAIL_FROM`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS`, `PORT`.
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


## Optional email notifications (Brevo SMTP)

The backend can send transactional email notifications to the configured inventory contact for:
- **Low-stock alerts:** when total stock across warehouses crosses from at/above the product's minimum stock threshold to below it.
- **Out-of-stock alerts:** when a stock-out operation reduces a positive total quantity to zero.
- **Product creation:** after a product is successfully saved.
- **Product updates:** after an existing product is successfully updated.

Notifications are disabled by default. To use Brevo, create a Brevo account, enable transactional email, create/use an allowed sender, and retrieve the SMTP credentials from **Settings → SMTP & API → SMTP**. Use the SMTP login and an **SMTP key** (not your Brevo account password and not the Brevo API key). Brevo's SMTP relay is `smtp-relay.brevo.com`; port `587` uses STARTTLS. See [Brevo SMTP setup](https://help.brevo.com/hc/en-us/articles/7924908994450-Send-transactional-emails-using-Brevo-SMTP) and [SMTP relay documentation](https://developers.brevo.com/docs/smtp-integration).

Set these environment variables in the **backend environment only** (PowerShell example):

```powershell
$env:INVENTORY_EMAIL_NOTIFICATIONS_ENABLED="true"
$env:INVENTORY_ALERT_EMAIL_TO="inventory-manager@example.com"
$env:INVENTORY_ALERT_EMAIL_FROM="verified-sender@your-domain.com"
$env:MAIL_HOST="smtp-relay.brevo.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-brevo-smtp-login"
$env:MAIL_PASSWORD="your-brevo-smtp-key"
$env:MAIL_SMTP_AUTH="true"
$env:MAIL_SMTP_STARTTLS="true"

cd backend
mvn spring-boot:run
```

Replace the sample addresses and credentials with your real settings. The `From` address must be an allowed/verified sender in Brevo. Do not put credentials in React/Vite environment variables, source files, screenshots, or Git commits. If a secret was committed, revoke/rotate it. On Render, add the same variables under the backend service's Environment settings; the included `render.yaml` keeps secrets unset and notifications disabled by default.

Email failures are logged by the backend and do not intentionally fail the inventory operation. This means a product change may succeed even if Brevo is temporarily unavailable; inspect backend logs and Brevo transactional logs during testing. Email delivery has not been verified without real SMTP credentials.
