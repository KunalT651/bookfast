# BookFast Deployment Guide

This guide will help you deploy BookFast to Vercel (frontend) and Render (backend).

## 🚀 **Deployment Overview**

- **Frontend (Angular)**: Deploy to Vercel
- **Backend (Spring Boot)**: Deploy to Render
- **Database**: Use Render's PostgreSQL addon

## 📋 **Prerequisites**

1. GitHub account
2. Vercel account (free)
3. Render account (free)
4. Your code pushed to GitHub

## 🔧 **Step 1: Prepare Your Code**

### 1.1 Push to GitHub
```bash
git add .
git commit -m "Prepare for deployment"
git push origin main
```

### 1.2 Update Google OAuth Settings
- Go to Google Cloud Console
- Update redirect URIs to include your production URLs:
  - `https://your-frontend-url.vercel.app`
  - `https://your-frontend-url.vercel.app/callback`
  - `https://your-frontend-url.vercel.app/calendar/callback`

## 🌐 **Step 2: Deploy Frontend to Vercel**

### 2.1 Connect to Vercel
1. Go to [vercel.com](https://vercel.com)
2. Sign in with GitHub
3. Click "New Project"
4. Import your repository
5. Select the `frontEnd` folder as the root directory

### 2.2 Configure Vercel
- **Framework Preset**: Angular
- **Root Directory**: `frontEnd`
- **Build Command**: `npm run build`
- **Output Directory**: `dist/bookfast-ui/browser`

### 2.3 Environment Variables
Add these environment variables in Vercel:
```
NODE_ENV=production
```

## 🖥️ **Step 3: Deploy Backend to Render**

### 3.1 Create New Web Service
1. Go to [render.com](https://render.com)
2. Sign in with GitHub
3. Click "New" → "Web Service"
4. Connect your repository

### 3.2 Configure Render
- **Name**: `bookfast-backend`
- **Root Directory**: `backEnd`
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
- **Runtime**: Java 21

### 3.3 Add PostgreSQL Database
1. In Render dashboard, click "New" → "PostgreSQL"
2. Name it `bookfast-db`
3. Note the connection details

### 3.4 Environment Variables
Add these environment variables in Render:

**Database:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-url:5432/your-db-name
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
```

**JWT:**
```
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
```

**Google OAuth:**
```
GOOGLE_CALENDAR_CLIENT_ID=55660237313-c098vshn8oplkk31ethrihbjro4cq5dh.apps.googleusercontent.com
GOOGLE_CALENDAR_CLIENT_SECRET=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
GOOGLE_CALENDAR_REDIRECT_URI=https://your-frontend-url.vercel.app/callback
```

**Admin:**
```
ADMIN_DEFAULT_EMAIL=admin@bookfast.com
ADMIN_DEFAULT_PASSWORD=admin123
ADMIN_DEFAULT_FIRSTNAME=System
ADMIN_DEFAULT_LASTNAME=Administrator
ADMIN_AUTO_CREATE=true
```

## 🔄 **Step 4: Update Frontend Configuration**

### 4.1 Update Environment
Update `frontEnd/src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-backend-url.onrender.com/api'
};
```

### 4.2 Update Google OAuth
Update `backEnd/src/main/resources/application.properties`:
```properties
google.calendar.redirect.uri=https://your-frontend-url.vercel.app/callback
```

## 🗄️ **Step 5: Database Setup**

### 5.1 Update Database Configuration
Update `backEnd/src/main/resources/application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=create-drop
```

## 🚀 **Step 6: Deploy and Test**

### 6.1 Deploy Backend
1. Push your changes to GitHub
2. Render will automatically build and deploy
3. Wait for deployment to complete
4. Note the backend URL (e.g., `https://bookfast-backend.onrender.com`)

### 6.2 Deploy Frontend
1. Update the frontend environment with the backend URL
2. Push changes to GitHub
3. Vercel will automatically deploy
4. Note the frontend URL (e.g., `https://bookfast-ui.vercel.app`)

### 6.3 Update Google OAuth
1. Go to Google Cloud Console
2. Update redirect URIs with your actual Vercel URL
3. Add your Vercel URL to authorized domains

## 🧪 **Step 7: Test Your Deployment**

1. Visit your Vercel URL
2. Try registering a new user
3. Test the Google Calendar integration
4. Verify all features work

## 🔧 **Troubleshooting**

### Common Issues:
1. **CORS errors**: Make sure your backend allows your frontend domain
2. **Database connection**: Check your database credentials
3. **OAuth errors**: Verify redirect URIs match exactly
4. **Build failures**: Check the build logs in Render/Vercel

### Useful Commands:
```bash
# Check backend logs
# In Render dashboard, go to your service → Logs

# Check frontend build
# In Vercel dashboard, go to your project → Functions → View Function Logs
```

## 📞 **Support**

If you encounter issues:
1. Check the deployment logs
2. Verify all environment variables are set
3. Ensure your GitHub repository is up to date
4. Test locally first to ensure everything works

## 🎉 **Success!**

Once deployed, you'll have:
- Frontend: `https://your-app.vercel.app`
- Backend: `https://your-app.onrender.com`
- Database: Managed PostgreSQL on Render

Your BookFast application will be live and accessible worldwide! 🌍
