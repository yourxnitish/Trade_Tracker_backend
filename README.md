# Trading Journal Backend

Pure Node.js API (no external dependencies) for the Trading Journal app.

## Local Run

```bash
node server.js
```

Runs on port 3001 (or `PORT` env var).

## Deploy to Render

1. Create a new **Web Service** on [Render](https://render.com)
2. Connect your GitHub repo (or use manual deploy)
3. Settings:
   - **Build Command**: leave empty (or `echo "no build"`)
   - **Start Command**: `node server.js`
   - **Environment**: Node
4. Add environment variable (optional):
   - `JWT_SECRET` = some long random string
5. Deploy. Note the URL, e.g. `https://your-app.onrender.com`

Data is stored in `./data/` as JSON files (persists on free tier disk until sleep).

## API Endpoints

- `POST /api/register` { email, password, name? }
- `POST /api/login` { email, password }
- `GET /api/me` (Bearer token)
- `GET /api/journals` – list all
- `GET /api/journals/:date`
- `POST/PUT /api/journals/:date` – save journal
- `DELETE /api/journals/:date`
