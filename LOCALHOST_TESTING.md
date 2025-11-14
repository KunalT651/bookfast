# Localhost Testing Guide

## Starting the Application

### Prerequisites
1. **Node.js** installed (v18 or higher)
2. **Angular CLI** installed globally (optional, but recommended)
3. **Backend running** on port 8080

### Step 1: Start Backend (if not already running)
```bash
cd backEnd
./mvnw spring-boot:run
```
Or if using Maven directly:
```bash
cd backEnd
mvn spring-boot:run
```

The backend should be accessible at: `http://localhost:8080`

### Step 2: Start Frontend
```bash
cd frontEnd
npm install  # Only needed first time or after dependency changes
npm start
```

The frontend will be accessible at: `http://localhost:4200`

## Correct URLs to Access

### ⚠️ Important: Routes are at Root Level
The application routes are configured at the **root level**, not under `/bookfast/`.

### ✅ Correct URLs:
- **Home/Registration**: `http://localhost:4200` or `http://localhost:4200/registration`
- **Login**: `http://localhost:4200/login`
- **Admin Login**: `http://localhost:4200/admin/login`
- **Provider Registration**: `http://localhost:4200/provider/registration`
- **Customer Home**: `http://localhost:4200/customer/home` (requires login)
- **Admin Dashboard**: `http://localhost:4200/admin/dashboard` (requires admin login)
- **Provider Dashboard**: `http://localhost:4200/provider/dashboard` (requires provider login)

### ❌ Incorrect URLs:
- `http://localhost:4200/bookfast/registration` ❌ (doesn't exist)
- `http://localhost:4200/bookfast/login` ❌ (doesn't exist)

## Route Configuration

The routes are defined in `frontEnd/src/app/app.routes.ts`:

```typescript
export const routes: Routes = [
  { path: 'registration', component: RegisterComponent },
  { path: 'provider/registration', component: RegisterProviderComponent },
  { path: 'login', component: LoginComponent },
  { path: 'admin/login', component: AdminLoginComponent },
  // ... other routes
];
```

All routes are at the root level (`/`), not under `/bookfast/`.

## Testing Checklist

### 1. Frontend Server
- [ ] Frontend is running on `http://localhost:4200`
- [ ] No console errors in browser
- [ ] Application loads correctly

### 2. Backend Server
- [ ] Backend is running on `http://localhost:8080`
- [ ] API endpoints are accessible
- [ ] Database connection is working

### 3. Proxy Configuration
The `proxy.conf.json` file is configured to proxy `/api` requests to `http://localhost:8080`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

This means:
- Frontend requests to `/api/*` will be proxied to `http://localhost:8080/api/*`
- No CORS issues in development
- Backend doesn't need CORS configuration for localhost

### 4. Environment Configuration
The `frontEnd/src/environments/environment.ts` file is configured for localhost:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Common Issues

### Issue 1: Connection Refused
**Error**: `ERR_CONNECTION_REFUSED` when accessing `localhost:4200`

**Solution**:
1. Make sure the Angular dev server is running
2. Check if port 4200 is already in use
3. Try accessing `http://127.0.0.1:4200` instead

### Issue 2: 404 Not Found
**Error**: Page not found when accessing a route

**Solution**:
1. Check the route in `app.routes.ts`
2. Make sure you're using the correct URL (without `/bookfast/`)
3. Check browser console for routing errors

### Issue 3: API Calls Failing
**Error**: API calls return 404 or CORS errors

**Solution**:
1. Make sure backend is running on port 8080
2. Check `proxy.conf.json` configuration
3. Verify `environment.ts` has correct API URL
4. Check browser network tab for actual request URLs

### Issue 4: Port Already in Use
**Error**: Port 4200 is already in use

**Solution**:
1. Kill the process using port 4200:
   ```bash
   # Windows
   netstat -ano | findstr :4200
   taskkill /PID <PID> /F
   ```
2. Or use a different port:
   ```bash
   ng serve --port 4201
   ```

## Development Workflow

### 1. Start Backend First
```bash
cd backEnd
./mvnw spring-boot:run
```
Wait for backend to start (look for "Started BackendApplication" message)

### 2. Start Frontend
```bash
cd frontEnd
npm start
```
Wait for Angular to compile (look for "Application bundle generation complete" message)

### 3. Open Browser
Navigate to `http://localhost:4200`

### 4. Test Features
- Register a new customer
- Login as customer
- Test booking flow
- Test admin features (if admin account exists)
- Test provider features (if provider account exists)

## Hot Reload

The Angular dev server supports hot reload:
- Changes to TypeScript files will automatically recompile
- Changes to HTML/CSS will automatically refresh
- No need to restart the server for code changes

## Stopping Servers

### Stop Frontend
Press `Ctrl+C` in the terminal where `npm start` is running

### Stop Backend
Press `Ctrl+C` in the terminal where Spring Boot is running

## Next Steps

After testing locally:
1. Fix any bugs found
2. Test all features thoroughly
3. Deploy to Vercel (frontend) and Render (backend)
4. Test in production environment

## Additional Resources

- **Angular CLI Documentation**: [https://angular.io/cli](https://angular.io/cli)
- **Spring Boot Documentation**: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **Vercel Deployment Guide**: See `VERCEL_MANUAL_DEPLOYMENT.md`
- **Backend Deployment Guide**: See `DEPLOYMENT.md`

