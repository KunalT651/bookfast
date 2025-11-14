# Vercel Deployment Checklist

## Issue: Deployment not triggering automatically

### Solution 1: Check Vercel Project Settings
1. Go to Vercel Dashboard → Your Project → Settings → General
2. Verify **Root Directory** is set to `frontEnd`
3. If not set, update it to `frontEnd` and save
4. Go to **Git** settings and verify the repository is connected
5. Verify **Production Branch** is set to `main`

### Solution 2: Manual Deployment Trigger
1. Go to Vercel Dashboard → Your Project → Deployments
2. Click "Redeploy" on the latest deployment
3. Or click "Deploy" → "Deploy from GitHub" → Select `main` branch

### Solution 3: Check Vercel Configuration
- The `frontEnd/vercel.json` file should be used (root `vercel.json` has been removed)
- Configuration should be:
  ```json
  {
    "rewrites": [
      {
        "source": "/(.*)",
        "destination": "/index.html"
      }
    ],
    "buildCommand": "npm run build",
    "outputDirectory": "dist/bookfast-ui/browser",
    "installCommand": "npm install",
    "framework": "angular"
  }
  ```

### Solution 4: Reconnect Repository
1. Go to Vercel Dashboard → Your Project → Settings → Git
2. Disconnect the repository
3. Reconnect it and select `frontEnd` as root directory
4. This will trigger a new deployment

## After Fix
- Push a new commit or trigger manual deployment
- Check Vercel dashboard for deployment status
- Verify the deployment succeeds

