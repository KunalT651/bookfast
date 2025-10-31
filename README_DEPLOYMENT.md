# 🚀 BookFast Free Deployment Guide

Deploy your full-stack application (Spring Boot + Angular + MySQL) **100% FREE** using Railway and Vercel.

## 📦 What You'll Deploy

- ✅ **Backend**: Spring Boot on Railway
- ✅ **Database**: MySQL on Railway  
- ✅ **Frontend**: Angular on Vercel
- ✅ **Email**: SendGrid (already configured)
- ✅ **Calendar**: Google OAuth (needs redirect URI update)

---

## 🎯 Quick Start (Choose One)

### Option 1: Simple Deployment (Recommended for Beginners)
See `QUICK_DEPLOY.md` for step-by-step instructions.

### Option 2: Detailed Guide
See `DEPLOYMENT.md` for comprehensive instructions.

---

## ⚡ 5-Minute Summary

### Step 1: Deploy Database + Backend (Railway)
1. Sign up at https://railway.app
2. New Project → Add MySQL Database
3. Add Service → GitHub Repo → Set root to `backEnd`
4. Add environment variables (see below)
5. Get your backend URL

### Step 2: Deploy Frontend (Vercel)
1. Sign up at https://vercel.com
2. Import GitHub repo → Set root to `frontEnd`
3. Build: `npm run build`, Output: `dist/bookfast-ui/browser`
4. Add env var: `NG_APP_API_URL` = your Railway URL
5. Get your frontend URL

### Step 3: Connect Them
- Update `frontEnd/src/environments/environment.prod.ts` with backend URL
- Add `FRONTEND_URL` env var in Railway (your Vercel URL)
- Update Google OAuth redirect URIs

---

## 🔑 Required Environment Variables

### Railway Backend Service:

```bash
# Database (Railway provides these automatically)
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}

# App Config
SERVER_PORT=8080
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Email
SENDGRID_API_KEY=YOUR_SENDGRID_API_KEY
SENDGRID_SENDER_EMAIL=thakarekunaljb@gmail.com
SENDGRID_SENDER_NAME=BookFast

# Security
JWT_SECRET=mySecretKey123456789012345678901234567890123456789012345678901234567890

# Admin
ADMIN_DEFAULT_EMAIL=admin@bookfast.com
ADMIN_DEFAULT_PASSWORD=admin123
ADMIN_DEFAULT_FIRSTNAME=System
ADMIN_DEFAULT_LASTNAME=Administrator
ADMIN_AUTO_CREATE=true

# Google Calendar (Update redirect URI after getting Vercel URL)
GOOGLE_CALENDAR_CLIENT_ID=55660237313-c098vshn8oplkk31ethrihbjro4cq5dh.apps.googleusercontent.com
GOOGLE_CALENDAR_CLIENT_SECRET=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
GOOGLE_CALENDAR_REDIRECT_URI=https://your-vercel-app.vercel.app
GOOGLE_CALENDAR_SCOPE=https://www.googleapis.com/auth/calendar
GOOGLE_CALENDAR_AUTH_URI=https://accounts.google.com/o/oauth2/auth
GOOGLE_CALENDAR_TOKEN_URI=https://oauth2.googleapis.com/token

# CORS (Add after getting Vercel URL)
FRONTEND_URL=https://your-vercel-app.vercel.app
```

### Vercel Frontend:

```bash
NG_APP_API_URL=https://your-railway-backend.railway.app
```

---

## 📝 Important: Update Frontend Services

**Before deploying**, you need to update frontend service files to use `environment.apiUrl`:

1. **Create/Update environment files** (already done ✅)
2. **Update each service** to import and use environment:

Example:
```typescript
// Add to top of file
import { environment } from '../../environments/environment';

// Change
private apiUrl = 'http://localhost:8080/api/auth';
// To
private apiUrl = `${environment.apiUrl}/api/auth`;
```

**Files to update** (44 files found):
- All `*.service.ts` files in `frontEnd/src/app/features/**/services/`
- Component files with API calls in `frontEnd/src/app/features/**/components/**/`

**Quick find command:**
```bash
grep -r "localhost:8080" frontEnd/src
```

---

## 🎓 Step-by-Step Video Walkthrough

1. **Railway Setup** (5 min)
   - Create account → New Project → Add MySQL
   - Connect GitHub → Deploy backend
   - Set environment variables

2. **Vercel Setup** (3 min)
   - Create account → Import repo
   - Configure build settings
   - Set environment variable

3. **Connect Services** (2 min)
   - Update frontend environment.prod.ts
   - Add FRONTEND_URL to Railway
   - Update Google OAuth URIs

4. **Test** (2 min)
   - Visit Vercel URL
   - Register user → Check email
   - Test login and features

**Total Time: ~12 minutes**

---

## 🔧 Troubleshooting

| Issue | Solution |
|-------|----------|
| Backend won't start | Check Railway logs, verify MySQL connection string |
| Frontend can't connect | Check CORS, verify backend URL in environment.prod.ts |
| Database errors | Verify MySQL variables are correctly set |
| Email not sending | Check SendGrid API key, verify sender email |
| CORS errors | Add FRONTEND_URL to Railway env vars, redeploy |

---

## 💰 Cost Breakdown

| Service | Cost | Limits |
|---------|------|--------|
| Railway | $0 | $5 credit/month, 500 hours |
| Vercel | $0 | Unlimited projects, 100GB bandwidth |
| SendGrid | $0 | 100 emails/day |
| **Total** | **$0** | Perfect for development/testing |

---

## 📚 Files Created

- ✅ `DEPLOYMENT.md` - Comprehensive guide
- ✅ `QUICK_DEPLOY.md` - Quick reference
- ✅ `backEnd/Dockerfile` - Container build
- ✅ `backEnd/nixpacks.toml` - Railway build config
- ✅ `backEnd/railway.json` - Railway service config
- ✅ `vercel.json` - Vercel deployment config
- ✅ `frontEnd/vercel.json` - Frontend-specific config
- ✅ `frontEnd/src/environments/environment.prod.ts` - Production env
- ✅ `.gitignore` - Protect sensitive files

---

## ✅ Pre-Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] All services updated to use `environment.apiUrl`
- [ ] Railway account created
- [ ] Vercel account created
- [ ] Ready to add environment variables

---

**Ready to deploy?** Start with `QUICK_DEPLOY.md` for the fastest path!

