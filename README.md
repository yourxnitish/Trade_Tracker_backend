# TradeTrack Backend – Personal Trading Journal REST API

Robust Java Spring Boot REST API for managing trades, calculating daily trading journal analytics, and persisting performance records in MySQL.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.5** (Spring Web, Spring Data JPA, Spring Validation)
- **MySQL 8.0**
- **Maven 3.9+**
- **HikariCP** Connection Pooling

## Database Setup

1. Make sure MySQL Server is running locally or hosted on a cloud provider.
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS trading_journal;
   ```
3. Set environment variables (or rely on default localhost configuration):
   - `DB_URL`: JDBC connection URL
   - `DB_USERNAME`: Database username (default: `root`)
   - `DB_PASSWORD`: Database password (e.g. `Root`)
   - `PORT`: Server port (default: `8080`)
   - `CORS_ALLOWED_ORIGINS`: Allowed origins for web clients (e.g. `http://localhost:5173,https://your-frontend.vercel.app`)

Hibernate DDL mode `update` will automatically create and maintain all database schemas on application startup:
- `trades`: Individual executed and open trade records
- `daily_journals`: Daily session reflections and calculated metrics
- `watchlist`: Monitored stocks with planned triggers and stop-losses
- `notes`: Qualitative notes across 6 distinct trading psychology categories
- `account_settings`: Configurable trader preferences (starting balance, currency)
- `account_transactions`: Capital deposits and withdrawals

## API Endpoints

### Trades
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/trades` | Paginated, filtered, and sorted trade list |
| `GET` | `/api/trades/all` | Fetch all trades |
| `GET` | `/api/trades/{id}` | Get trade by ID |
| `POST` | `/api/trades` | Create new trade record |
| `PUT` | `/api/trades/{id}` | Update existing trade |
| `DELETE` | `/api/trades/{id}` | Delete trade record |
| `GET` | `/api/trades/instruments` | Distinct instrument list |
| `GET` | `/api/trades/strategies` | Distinct strategy list |
| `GET` | `/api/trades/exchanges` | Distinct exchange list |
| `GET` | `/api/trades/export/csv` | Download filtered trades in CSV format |

### Daily Journals
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/journals` | List all historical journal entries |
| `GET` | `/api/journals/{id}` | Get journal by ID |
| `GET` | `/api/journals/date/{date}` | Daily summary aggregated with trades for given date |
| `POST` | `/api/journals` | Create or update daily journal reflections |
| `DELETE` | `/api/journals/{id}` | Delete journal record |

### Watchlist
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/watchlist` | Get watchlist items (optional `status` filter) |
| `GET` | `/api/watchlist/{id}` | Get item by ID |
| `POST` | `/api/watchlist` | Add item to watchlist |
| `PUT` | `/api/watchlist/{id}` | Update item |
| `PATCH` | `/api/watchlist/{id}/status` | Update item status (e.g., READY, TRADED) |
| `DELETE` | `/api/watchlist/{id}` | Remove item from watchlist |

### Notes & Observations
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/notes` | Get notes (supports `category` and `search` query params) |
| `GET` | `/api/notes/{id}` | Get note by ID |
| `POST` | `/api/notes` | Create observation note |
| `PUT` | `/api/notes/{id}` | Update note |
| `DELETE` | `/api/notes/{id}` | Delete note |

### Dashboard & Analytics
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Summary cards, P/L curves, and distribution metrics |
| `GET` | `/api/analytics` | Deep metrics: win %, heatmaps, streaks, R:R |

### Settings & Capital Management
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/settings` | Get trader preferences |
| `POST` | `/api/settings` | Update trader preferences |
| `GET` | `/api/settings/transactions` | List capital deposits/withdrawals |
| `POST` | `/api/settings/transactions` | Add deposit or withdrawal |
| `DELETE` | `/api/settings/transactions/{id}` | Delete transaction |

## Running Locally

```bash
# From trading-journal-backend directory:
mvn clean compile
mvn spring-boot:run
```

## Deployment to Render

1. Create a MySQL database on a managed cloud provider (e.g., Aiven, PlanetScale, Railway, or AWS RDS).
2. Push `trading-journal-backend` to GitHub.
3. In Render, select **New +** -> **Web Service**.
4. Connect the repository and configure:
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/trading-journal-backend-1.0.0.jar`
5. Under **Environment Variables**, provide:
   - `DB_URL`: `jdbc:mysql://<cloud-db-host>:3306/<database>?useSSL=true&serverTimezone=UTC`
   - `DB_USERNAME`: Database username
   - `DB_PASSWORD`: Database password
   - `PORT`: `8080`
   - `CORS_ALLOWED_ORIGINS`: Your Vercel frontend URL (e.g., `https://tradetrack.vercel.app`)
6. Click **Create Web Service**.
