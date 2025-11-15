# Password Reset Link Troubleshooting

## Issue: Password Reset Link Points to Wrong URL

If clicking the password reset link from email shows:
- **403 Forbidden** on `bookfast-backend.onrender.com` → Link is pointing to backend instead of frontend
- **404 NOT_FOUND** on `bookfast-q319.vercel.app` → Frontend route not found or Vercel routing issue

## Root Cause

The `FRONTEND_URL` environment variable in Render is either:
1. **Not set** → Defaults to `http://localhost:4200`
2. **Set incorrectly** → Points to backend URL instead of frontend URL
3. **Missing trailing slash or protocol** → Malformed URL

## Solution

### Step 1: Verify Frontend URL in Render

1. Go to Render Dashboard → Your Backend Service → **Environment** tab
2. Check if `FRONTEND_URL` is set
3. It should be: `https://bookfast-q319.vercel.app` (or your actual Vercel URL)
4. **NOT**: `https://bookfast-backend.onrender.com` (this is the backend URL)

### Step 2: Set Correct FRONTEND_URL

In Render Environment Variables, set:
```bash
FRONTEND_URL=https://bookfast-q319.vercel.app
```

**Important**: 
- Use `https://` (not `http://`)
- Use your actual Vercel frontend URL
- No trailing slash
- Must match your Vercel deployment URL exactly

### Step 3: Verify CORS Configuration

Also set:
```bash
CORS_ALLOWED_ORIGINS=https://bookfast-q319.vercel.app,https://bookfast-q319-git-main.vercel.app
```

### Step 4: Redeploy Backend

After setting environment variables:
1. Render will automatically redeploy
2. Wait for deployment to complete
3. Test password reset again

## How to Verify the Fix

1. Request a new password reset email
2. Check the email link - it should be:
   ```
   https://bookfast-q319.vercel.app/reset-password?token=...
   ```
3. NOT:
   ```
   https://bookfast-backend.onrender.com/reset-password?token=...
   ```

## Testing Locally

If testing locally, the link should be:
- `http://localhost:4200/reset-password?token=...` (for local dev)
- Or `http://YOUR_IP:4200/reset-password?token=...` (for mobile testing)

## Additional Checks

### Check Backend Logs

In Render logs, you should see:
```
[PasswordReset] Using frontend URL: https://bookfast-q319.vercel.app (custom: null, default: https://bookfast-q319.vercel.app)
[PasswordReset] Reset email sent to: user@example.com with link: https://bookfast-q319.vercel.app/reset-password?token=...
```

If you see `localhost:4200` or `bookfast-backend.onrender.com`, the `FRONTEND_URL` is wrong.

### Check Email Content

The email should contain a link like:
```
https://bookfast-q319.vercel.app/reset-password?token=abc123...
```

If it shows `bookfast-backend.onrender.com`, the backend is using the wrong URL.

## Common Mistakes

1. ❌ Setting `FRONTEND_URL` to backend URL
2. ❌ Forgetting to set `FRONTEND_URL` at all
3. ❌ Using `http://` instead of `https://` for production
4. ❌ Adding trailing slash: `https://bookfast-q319.vercel.app/` (should be without `/`)
5. ❌ Using preview URL instead of production URL

## Quick Fix Command

If you have Render CLI access:
```bash
render env:set FRONTEND_URL=https://bookfast-q319.vercel.app
```

Or set it manually in Render Dashboard → Environment tab.

