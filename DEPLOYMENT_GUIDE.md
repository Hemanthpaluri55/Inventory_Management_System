# Final demonstration deployment guide (Render + Neon PostgreSQL + Vercel)

This project is configured for a hosted demo with a public React frontend, Spring Boot API, and hosted PostgreSQL database. Use HTTPS URLs and set your own demo credentials before sharing.

## Deployment layout
- **Database:** Neon PostgreSQL (or another hosted PostgreSQL provider).
- **Backend:** Render Docker web service using `backend/Dockerfile`.
- **Frontend:** Vercel static site using the `frontend` directory.

## 1. Create hosted PostgreSQL database
1. Create a PostgreSQL project/database with your provider.
2. Copy the host, database name, username, password, and SSL settings from the provider.
3. Construct `DB_URL` as a JDBC URL, for example: `jdbc:postgresql://HOST:5432/DATABASE?sslmode=require`. Use the actual host and database from your provider; don't copy the placeholders.

Do not use `localhost` for a cloud database. The database must allow the Render service to connect.

## 2. Deploy backend to Render
1. Push this project to a GitHub repository. Do not commit real passwords or database credentials.
2. In Render, create a **New Blueprint** from the repository and select the included `render.yaml`, or create a Web Service with Docker using `backend` as the root directory and `backend/Dockerfile` as the Dockerfile.
3. Add these environment variables in Render: 
   - `DB_URL` = hosted PostgreSQL JDBC URL including `sslmode=require` if provider requires SSL
   - `DB_USERNAME` = database username
   - `DB_PASSWORD` = database password
   - `CORS_ALLOWED_ORIGINS` = the final Vercel site origin, e.g. `https://your-app.vercel.app` (no trailing slash)
   - `INVENTORY_ADMIN_USERNAME` = your chosen admin username
   - `INVENTORY_ADMIN_PASSWORD` = a strong, unique demo password
   - `INVENTORY_EMPLOYEE_USERNAME` = your chosen employee username
   - `INVENTORY_EMPLOYEE_PASSWORD` = a different strong password
4. Deploy and wait for the service to become healthy. Test `https://YOUR-API.onrender.com/api/health`.

The included `render.yaml` leaves secrets unset intentionally; enter them in Render's environment settings.

## 3. Deploy frontend to Vercel
1. Import the same GitHub repository into Vercel.
2. Set **Root Directory** to `frontend`.
3. Build command: `npm run build`. Output directory: `dist`. Install command: `npm install`.
4. Add environment variable `VITE_API_BASE_URL` = backend origin only, e.g. `https://YOUR-API.onrender.com` (do not append `/api`).
5. Deploy.
6. Copy the final Vercel origin into Render's `CORS_ALLOWED_ORIGINS`, save it, and redeploy/restart the backend.

## 4. Final demo checklist
- Open the Vercel URL in an incognito/private window.
- Sign in as admin; add a test product and record a stock-in.
- Sign out, sign in as employee; confirm inventory can be viewed but create/edit/delete and stock movement actions are unavailable.
- Test API health and refresh the page.
- Verify product and transaction data remain after backend redeploy.
- Use only test data; do not expose database credentials in screenshots or source control.

## Security notes
- This demo uses in-memory role accounts and HTTP Basic credentials sent over HTTPS. It does not have a user-registration screen or persistent user table.
- Use strong unique demo passwords and never deploy the sample passwords from the README.
- This is a student demonstration configuration, not a substitute for a production security review.

## Local development remains supported
Frontend defaults to `http://localhost:8080` when `VITE_API_BASE_URL` is not set. Backend defaults to PostgreSQL on `localhost:5432/inventory_db` and local Vite CORS origins.


## Email notifications (optional, Brevo SMTP)

Keep `INVENTORY_EMAIL_NOTIFICATIONS_ENABLED=false` until Brevo is configured. For the backend service's environment settings, configure:

- `INVENTORY_EMAIL_NOTIFICATIONS_ENABLED=true`
- `INVENTORY_ALERT_EMAIL_TO` = destination address for inventory notifications
- `INVENTORY_ALERT_EMAIL_FROM` = a sender address allowed/verified in Brevo
- `MAIL_HOST=smtp-relay.brevo.com`
- `MAIL_PORT=587`
- `MAIL_USERNAME` = the SMTP login shown in Brevo's SMTP settings (not necessarily your account login email)
- `MAIL_PASSWORD` = Brevo SMTP key (not the API key and not your account password)
- `MAIL_SMTP_AUTH=true`
- `MAIL_SMTP_STARTTLS=true`

Create or retrieve the SMTP credentials from Brevo **Settings → SMTP & API → SMTP**. See [Brevo's official SMTP instructions](https://help.brevo.com/hc/en-us/articles/7924908994450-Send-transactional-emails-using-Brevo-SMTP). Use port 587 with STARTTLS. The sending address must be permitted by Brevo; authenticating your sending domain improves deliverability.

Notifications cover low-stock threshold crossings, out-of-stock transitions, and successful product create/update operations. Test with a dedicated recipient and check backend logs and Brevo's transactional logs. Do not commit SMTP credentials to GitHub. Delivery cannot be verified until valid Brevo credentials and a permitted sender are configured.
