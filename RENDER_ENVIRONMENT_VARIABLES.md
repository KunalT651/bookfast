# Render Environment Variables Configuration

This document lists **ALL** environment variables that must be set in Render for production deployment.

## 🔴 **REQUIRED Variables (Must Set)**

### Database Configuration
```bash
DATABASE_URL=jdbc:postgresql://your-db-hostname:5432/your-db-name
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password
```
**Note**: Render provides these automatically if you use their PostgreSQL addon. Check your Render PostgreSQL dashboard for the exact values.

### Server Configuration
```bash
PORT=10000
```
**Note**: Render sets this automatically. Don't override unless needed.

### Frontend URL (Critical for Emails & OAuth)
```bash
FRONTEND_URL=https://your-app.vercel.app
```
**Example**: `FRONTEND_URL=https://bookfast-ui.vercel.app`
- Used for password reset emails
- Used for Google Calendar OAuth redirects
- Used for admin user creation emails

### CORS Configuration
```bash
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,https://your-app-git-main.vercel.app
```
**Example**: `CORS_ALLOWED_ORIGINS=https://bookfast-ui.vercel.app,https://bookfast-ui-git-main.vercel.app`
- Include your main Vercel URL
- Include preview deployment URLs if needed
- Separate multiple URLs with commas (no spaces)

### SendGrid Email API (REQUIRED for Email Functionality)
```bash
SENDGRID_API_KEY=SG.your-actual-api-key-here
SENDGRID_SENDER_EMAIL=your-verified-sender@example.com
SENDGRID_SENDER_NAME=BookFast
```
**Important**:
- Get your API key from [SendGrid Dashboard](https://app.sendgrid.com/settings/api_keys)
- The sender email must be verified in SendGrid
- Without this, password reset emails and admin creation emails will fail

### JWT Secret (Security Critical)
```bash
JWT_SECRET=your-super-secret-jwt-key-minimum-64-characters-long-and-random
```
**Important**: 
- Generate a strong random string (minimum 64 characters)
- Never commit this to git
- Use a different secret for production than development

### Imgur API (For Profile Picture Uploads)
```bash
IMGUR_CLIENT_ID=your-imgur-client-id
```
**Note**: 
- Default demo client ID may hit rate limits
- Get your own from [Imgur API](https://api.imgur.com/oauth2/addclient)
- Optional but recommended for production

## 🟡 **Optional Variables**

### Admin Configuration
```bash
ADMIN_EMAIL=admin@bookfast.com
ADMIN_PASSWORD=your-secure-admin-password
ADMIN_FIRSTNAME=System
ADMIN_LASTNAME=Administrator
ADMIN_AUTO_CREATE=true
```
**Note**: These have defaults but should be customized for production.

### Google Calendar OAuth (Optional)
```bash
GOOGLE_CALENDAR_CLIENT_ID=your-google-client-id
GOOGLE_CALENDAR_CLIENT_SECRET=your-google-client-secret
GOOGLE_CALENDAR_REDIRECT_URI=https://your-app.vercel.app/callback
```
**Note**: Only needed if using Google Calendar sync feature.

### Cookie Configuration (For HTTPS)
```bash
COOKIE_DOMAIN=.vercel.app
COOKIE_SECURE=true
```
**Note**: 
- Set `COOKIE_DOMAIN` to your domain (e.g., `.vercel.app` for all Vercel subdomains)
- Set `COOKIE_SECURE=true` when using HTTPS (production)
- Leave empty for localhost development

### Twilio SMS (Optional - Not Currently Used)
```bash
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_PHONE_NUMBER=
```
**Note**: Leave empty if not using SMS features.

## 📋 **Complete Render Environment Variables Checklist**

Copy and paste this into Render's Environment Variables section:

```bash
# Database (Auto-provided by Render PostgreSQL)
DATABASE_URL=jdbc:postgresql://your-db-hostname:5432/your-db-name
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password

# Frontend URL (REQUIRED)
FRONTEND_URL=https://your-app.vercel.app

# CORS (REQUIRED)
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,https://your-app-git-main.vercel.app

# SendGrid Email (REQUIRED)
SENDGRID_API_KEY=SG.your-actual-api-key
SENDGRID_SENDER_EMAIL=your-verified-email@example.com
SENDGRID_SENDER_NAME=BookFast

# JWT Secret (REQUIRED - Generate a strong random string)
JWT_SECRET=your-super-secret-jwt-key-minimum-64-characters

# Imgur (Recommended)
IMGUR_CLIENT_ID=your-imgur-client-id

# Admin (Customize for production)
ADMIN_EMAIL=admin@bookfast.com
ADMIN_PASSWORD=your-secure-password
ADMIN_FIRSTNAME=System
ADMIN_LASTNAME=Administrator
ADMIN_AUTO_CREATE=true

# Google Calendar (Optional)
GOOGLE_CALENDAR_CLIENT_ID=your-google-client-id
GOOGLE_CALENDAR_CLIENT_SECRET=your-google-client-secret
GOOGLE_CALENDAR_REDIRECT_URI=https://your-app.vercel.app/callback

# Cookies (For HTTPS)
COOKIE_DOMAIN=.vercel.app
COOKIE_SECURE=true
```

## 🔍 **How to Set Environment Variables in Render**

1. Go to your Render Dashboard
2. Select your Web Service (backend)
3. Click on **"Environment"** tab
4. Click **"Add Environment Variable"**
5. Enter the **Key** and **Value**
6. Click **"Save Changes"**
7. Render will automatically redeploy with new variables

## ⚠️ **Common Issues & Solutions**

### Issue: Emails Not Sending
**Solution**: 
- Verify `SENDGRID_API_KEY` is set correctly
- Verify `SENDGRID_SENDER_EMAIL` is verified in SendGrid
- Check Render logs for SendGrid errors

### Issue: CORS Errors
**Solution**:
- Ensure `CORS_ALLOWED_ORIGINS` includes your exact Vercel URL
- Include both main and preview URLs
- No trailing slashes in URLs

### Issue: Password Reset Links Don't Work
**Solution**:
- Verify `FRONTEND_URL` matches your Vercel URL exactly
- Check that the URL is accessible (not localhost)

### Issue: Profile Picture Upload Fails
**Solution**:
- Verify `IMGUR_CLIENT_ID` is set
- Check Imgur rate limits (429 errors)
- Consider getting your own Imgur client ID

### Issue: Google Calendar OAuth Fails
**Solution**:
- Verify `GOOGLE_CALENDAR_REDIRECT_URI` matches exactly in Google Cloud Console
- Ensure `FRONTEND_URL` is set correctly
- Check that redirect URI is added to Google OAuth credentials

## 🧪 **Testing After Deployment**

1. **Test Email**: Request a password reset and verify email is received
2. **Test CORS**: Verify frontend can make API calls
3. **Test Authentication**: Login/logout should work
4. **Test Profile Picture**: Upload a profile picture
5. **Test OAuth**: If using Google Calendar, test OAuth flow

## 📝 **Notes**

- All environment variables are case-sensitive
- No spaces around the `=` sign
- Use quotes only if the value contains special characters
- Render automatically redeploys when environment variables change
- Check Render logs if something doesn't work

