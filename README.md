# FinManagement

A full-stack personal finance management dashboard built with Spring Boot and Next.js. Track income and expenses, visualize spending trends, and manage users with role-based access control.

## What's Inside

- **Dashboard** with summary cards, income/expense charts, category breakdowns, and trend lines
- **Financial Records** — add, edit, delete, filter by type/category/date, search by notes, paginated
- **Role-Based Access** — Viewer (dashboard only), Analyst (view records + insights), Admin (full CRUD + user management)
- **Authentication** — JWT-based login/register with token refresh
- **Admin Panel** — manage users, change roles, view system-wide summaries

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.3.6, Java 17, Maven |
| Database | SQLite (file-based, auto-created) |
| Auth | Spring Security + JWT (HS512, 24h expiry) |
| API Docs | Swagger UI at `/api/swagger-ui.html` |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind CSS 4 |
| Charts | Recharts (bar, pie, line, area) |
| Forms | React Hook Form + Zod validation |
| Rate Limiting | Bucket4j (60 req/min per client) |

## Project Structure

```
FinManagement/
├── backend/                      # Spring Boot API
│   ├── src/main/java/com/finmanagement/
│   │   ├── config/               # Security, JWT, CORS, rate limiting, OpenAPI
│   │   ├── controller/           # Auth, User, Record, Dashboard endpoints
│   │   ├── dto/                  # Request/response objects
│   │   ├── entity/               # JPA entities (User, FinancialRecord)
│   │   ├── exception/            # Global error handling
│   │   ├── mapper/               # MapStruct entity-DTO mappers
│   │   ├── repository/           # Spring Data JPA repositories
│   │   └── service/              # Business logic
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── frontend/                     # Next.js App Router
│   ├── src/
│   │   ├── app/                  # Pages (login, register, dashboard, records, admin)
│   │   ├── components/           # UI, charts, layout, records, admin
│   │   ├── context/              # Auth context provider
│   │   ├── hooks/                # useAuth, useRecords, useDashboard
│   │   ├── lib/                  # API client, auth utilities
│   │   ├── types/                # TypeScript interfaces
│   │   └── utils/                # Formatters, validators
│   ├── package.json
│   └── tsconfig.json
│
└── .gitignore
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.8+

### 1. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. A SQLite database file (`finmanagement.db`) is created automatically in the backend directory. Swagger docs are available at `http://localhost:8080/api/swagger-ui.html`.

### 2. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000` in your browser. You'll be redirected to the registration page.

### 3. Create an Account

Register with a username, email, password, and choose a role:

| Role | What You Can Do |
|------|----------------|
| **Viewer** | View your own dashboard data (summary, charts) |
| **Analyst** | View all records across users, dashboard analytics, trends |
| **Admin** | Full access — create/edit/delete records, manage users, system-wide dashboard |

## API Endpoints

### Authentication (public)
```
POST /api/auth/register    — Create account (with role selection)
POST /api/auth/login       — Login, returns JWT token
```

### Financial Records
```
GET    /api/records         — List records (paginated, filterable)
GET    /api/records/{id}    — Get single record
POST   /api/records         — Create record (Admin only)
PUT    /api/records/{id}    — Update record (Admin only)
DELETE /api/records/{id}    — Soft-delete record (Admin only)
```

Query params for listing: `page`, `size`, `type` (INCOME/EXPENSE), `category`, `startDate`, `endDate`, `search`

### Dashboard
```
GET /api/dashboard/summary          — Income, expenses, net balance, record count
GET /api/dashboard/category-totals  — Breakdown by category
GET /api/dashboard/trends           — Monthly or weekly income/expense trends
GET /api/dashboard/recent-activity  — Latest N records
GET /api/dashboard/admin/summary    — System-wide summary (Admin only)
```

### Admin — User Management
```
GET    /api/admin/users       — List users (paginated, searchable)
GET    /api/admin/users/{id}  — Get user details
PUT    /api/admin/users/{id}  — Update user (role, email)
DELETE /api/admin/users/{id}  — Soft-delete user
```

## Database

SQLite is used for zero-config setup. Two tables are auto-created by Hibernate:

- **users** — id, username, email, password (bcrypt), role, deleted (soft delete), timestamps
- **financial_records** — id, user_id (FK), amount, type, category, date, notes, deleted, timestamps

The database file lives at `backend/finmanagement.db` and is excluded from git via `.gitignore`.

## Configuration

### Backend (`application.yml`)

| Setting | Default | Notes |
|---------|---------|-------|
| Server port | 8080 | |
| Database path | `./finmanagement.db` | Change via `APP_DB_PATH` env var |
| JWT expiration | 24 hours | |
| Rate limit | 60 req/min | Per client IP |

### Frontend

The API base URL defaults to `http://localhost:8080`. Override it by setting `NEXT_PUBLIC_API_URL` in `frontend/.env.local`.

## Running Tests

```bash
cd backend
mvn test
```

Unit tests cover all service layers — auth, user management, financial records, and dashboard analytics.

## Screenshots

*Dashboard with summary cards and charts:*

The main dashboard shows total income, expenses, net balance, and transaction count at the top. Below that, income vs expense bar chart, expense category pie chart, net balance trend line, and recent activity table.

*Records page with filters:*

Filterable table of all financial records with type badges, formatted amounts, and action buttons (visible to Admin users). Supports pagination and text search.

*Admin user management:*

Table of all registered users with role assignment and account status management.

## License

This project is open source and available for personal and educational use.
