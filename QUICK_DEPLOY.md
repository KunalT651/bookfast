# Quick Deployment Guide - BookFast

## 🚀 Fast Track Deployment (15 minutes)

### 1. Backend + Database on Railway

1. **Sign up**: https://railway.app (use GitHub)
2. **Create Project** → **New Database** → **MySQL**
3. **Add Service** → **GitHub Repo** → Select your repo
4. **Set Root Directory**: `backEnd`
5. **Add Environment Variables** (from `backEnd/application.properties.template`):
   - Copy all variables from template
   - Replace MySQL values with Railway MySQL service variables
6. **Get Backend URL**: Settings → Generate Domain → Copy URL

### 2. Frontend on Vercel

1. **Sign up**: https://vercel.com (use GitHub)
2. **Add Project** → Import your GitHub repo
3. **Configure**:
   - Root Directory: `frontEnd`
   - Build Command: `npm run build`
   - Output Directory: `dist/bookfast-ui/browser`
   - Environment Variable: `NG_APP_API_URL` = Your Railway backend URL
4. **Deploy**

### 3. Update Frontend Environment

Edit `frontEnd/src/environments/environment.prod.ts`:
```typescript
apiUrl: 'https://your-railway-backend-url.railway.app'
```

### 4. Update Backend CORS

In Railway backend environment variables, add:
```
FRONTEND_URL=https://your-vercel-app.vercel.app
```

### 5. Update Google OAuth

1. Go to https://console.cloud.google.com
2. APIs & Services → Credentials
3. Add to Authorized redirect URIs:
   - `https://your-vercel-app.vercel.app/callback`
   - `https://your-vercel-app.vercel.app/calendar/callback`

### 6. Redeploy Both Services

Push changes or manually redeploy.

---

## ⚠️ Important: Frontend Service Updates Needed

You need to update all service files to use `environment.apiUrl` instead of hardcoded URLs.

**Quick Fix Script:**
```bash
# Find all files that need updating
grep -r "localhost:8080" frontEnd/src
```

**Manual Update Example:**
```typescript
// Before
private apiUrl = 'http://localhost:8080/api/auth';

// After  
import { environment } from '../../environments/environment';
private apiUrl = `${environment.apiUrl}/api/auth`;
```

---

## 📝 Required Railway Environment Variables

```
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
SERVER_PORT=8080

SENDGRID_API_KEY=YOUR_SENDGRID_API_KEY
SENDGRID_SENDER_EMAIL=thakarekunaljb@gmail.com
SENDGRID_SENDER_NAME=BookFast

JWT_SECRET=mySecretKey123456789012345678901234567890123456789012345678901234567890

ADMIN_DEFAULT_EMAIL=admin@bookfast.com
ADMIN_DEFAULT_PASSWORD=admin123
ADMIN_DEFAULT_FIRSTNAME=System
ADMIN_DEFAULT_LASTNAME=Administrator
ADMIN_AUTO_CREATE=true

GOOGLE_CALENDAR_CLIENT_ID=55660237313-c098vshn8oplkk31ethrihbjro4cq5dh.apps.googleusercontent.com
GOOGLE_CALENDAR_CLIENT_SECRET=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
GOOGLE_CALENDAR_REDIRECT_URI=https://your-frontend-url.vercel.app
GOOGLE_CALENDAR_SCOPE=https://www.googleapis.com/auth/calendar
GOOGLE_CALENDAR_AUTH_URI=https://accounts.google.com/o/oauth2/auth
GOOGLE_CALENDAR_TOKEN_URI=https://oauth2.googleapis.com/token

FRONTEND_URL=https://your-frontend-url.vercel.app
```

**Note:** Railway automatically provides MySQL variables as `${{MySQL.VARIABLE_NAME}}`

---

## ✅ Checklist

- [ ] Railway MySQL database created
- [ ] Railway backend deployed
- [ ] All environment variables set in Railway
- [ ] Backend URL obtained
- [ ] Vercel frontend deployed
- [ ] Frontend environment.prod.ts updated with backend URL
- [ ] FRONTEND_URL added to Railway backend env vars
- [ ] Google OAuth redirect URIs updated
- [ ] All frontend services updated to use environment.apiUrl
- [ ] Test registration and welcome email
- [ ] Test login and features

---

**Time Estimate**: 15-30 minutes

**Cost**: $0 (Free tiers)

