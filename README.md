# Trade Tracker - Backend

Zero-dependency Node.js REST API for the Trade Tracker / Trading Journal application.

---

## 🚀 Features

- **Built with Pure Node.js**: Zero third-party npm runtime dependencies.
- **Authentication**: JWT token-based auth with secure SHA-256 password hashing.
- **Journal Storage**: JSON-based persistent storage for user accounts and daily trading logs.
- **Pre-seeded Demo Data**: Demo account (`demo@tradingjournal.com` / `password123`) included out of the box.
- **Static Serving Support**: Automatically serves frontend when placed alongside the frontend folder, or functions as a standalone API when deployed alone.

---

## 💻 Local Run

```bash
node server.js
# or
npm start
```

Default port is `3001` (or set the `PORT` environment variable).

---

## 🌐 Deploy to Render / Railway / VPS

### Deploy on Render:
1. Create a new **Web Service** on [Render](https://render.com).
2. Connect your GitHub repository: `Trade_Tracker_backend`.
3. Configuration:
   - **Environment:** `Node`
   - **Build Command:** leave empty (or `npm install`)
   - **Start Command:** `node server.js`
4. Environment Variables (optional):
   - `JWT_SECRET`: A secure random string for JWT signatures.
5. Deploy! Render will give you a public URL (e.g. `https://trade-tracker-backend.onrender.com`).

---

## 📡 REST API Endpoints

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/health` | Health check | No |
| `POST` | `/api/register` | Create account | No |
| `POST` | `/api/login` | Authenticate | No |
| `GET` | `/api/me` | Current user info | Yes |
| `GET` | `/api/journals` | List all journal dates | Yes |
| `GET` | `/api/journals/:date` | Get journal for a date | Yes |
| `PUT` | `/api/journals/:date` | Save/update journal | Yes |
| `DELETE` | `/api/journals/:date` | Delete journal | Yes |

---

## 🔗 Related

- **Frontend Repository:** [Trade_Tracker_frontend](https://github.com/yourxnitish/Trade_Tracker_frontend)
