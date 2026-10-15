# Final demonstration checklist

## Before demo day
- [ ] Verify Java, Maven, Node.js and PostgreSQL versions on the laptop.
- [ ] Confirm PostgreSQL service is running and `inventory_db` exists.
- [ ] Set local DB credentials in `application.properties` or environment variables.
- [ ] Run backend and frontend locally; test `/api/health`.
- [ ] Change default demo passwords before using any public URL.
- [ ] Deploy backend, then set the exact Vercel origin in `CORS_ALLOWED_ORIGINS`.
- [ ] Set Vercel `VITE_API_BASE_URL` to the Render backend origin.
- [ ] Test admin write access and employee read-only access, including direct API calls.
- [ ] Keep a local copy and a short screen recording as a presentation backup.

## Role test matrix
| Action | Admin | Employee |
|---|---|---|
| Read products/dashboard/stock/transactions | Allowed | Allowed |
| Create or update product | Allowed | Denied (403) |
| Delete product | Allowed | Denied (403) |
| Create supplier/warehouse | Allowed | Denied (403) |
| Stock-in/out/transfer | Allowed | Denied (403) |

## Important
The sample accounts are in-memory and do not persist as user records. Stock data persists in PostgreSQL. H2 demo profile data is temporary and resets when the backend restarts.
