# Meet Minutes frontend

React + Vite single-page frontend using JavaScript and JSX. It calls the existing Spring Boot API through the centralized `src/api/api.js` client.

## Start locally (PowerShell)

```powershell
cd C:\Users\godha\Desktop\MeetMinutes\backend\meet-minutes-backend
.\mvnw.cmd spring-boot:run
```

In a second PowerShell window:

```powershell
cd C:\Users\godha\Desktop\MeetMinutes\frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Open <http://localhost:5173>. Spring Boot's default port is `8080`; Vite proxies `/api` requests there. Configure the backend's existing environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and the configured AI variables) before starting it. The frontend `.env` contains no secrets. For a deployed frontend, set `VITE_API_BASE_URL` to the backend API base URL ending in `/api` and configure backend CORS for the deployed frontend origin.

See [API_INVENTORY.md](./API_INVENTORY.md) for the endpoint and DTO inventory verified against the current backend source.
