# How to Manually Trigger Vercel Deployment

## Method 1: Via Vercel Dashboard (Easiest)

### Option A: Redeploy Latest Deployment
1. Go to [Vercel Dashboard](https://vercel.com)
2. Select your project (`bookfast-q319`)
3. Click on the **"Deployments"** tab
4. Find the latest deployment (even if it failed)
5. Click the **three dots (⋯)** menu on the right side of the deployment
6. Select **"Redeploy"**
7. Choose:
   - **"Use Existing Build Cache"** (faster, reuses previous build)
   - **"Rebuild"** (clean build, recommended if you want a fresh build)
8. Click **"Redeploy"**
9. Wait for deployment to complete (usually 1-3 minutes)

### Option B: Deploy from GitHub Branch
1. Go to [Vercel Dashboard](https://vercel.com)
2. Select your project (`bookfast-q319`)
3. Click the **"Deployments"** tab
4. Click the **"Deploy"** button (top right)
5. Select **"Deploy from GitHub"**
6. Choose:
   - **Branch**: `main` (or any other branch)
   - **Commit**: Select a specific commit or use latest
7. Click **"Deploy"**
8. Wait for deployment to complete

## Method 2: Via Vercel CLI (Command Line)

### Prerequisites
1. Install Vercel CLI:
   ```bash
   npm install -g vercel
   ```

2. Login to Vercel:
   ```bash
   vercel login
   ```

### Deploy from Command Line
1. Navigate to your frontend directory:
   ```bash
   cd frontEnd
   ```

2. Link your project (first time only):
   ```bash
   vercel link
   ```
   - Select your project (`bookfast-q319`)
   - Confirm settings (root directory, build command, etc.)

3. Deploy to production:
   ```bash
   vercel --prod
   ```

   Or deploy to preview:
   ```bash
   vercel
   ```

## Method 3: Trigger via Git Push (Automatic)

1. Make a small change to any file:
   ```bash
   # Option 1: Update a comment or add a space
   # Option 2: Update version in package.json
   # Option 3: Add a console.log statement
   ```

2. Commit and push:
   ```bash
   git add .
   git commit -m "Trigger: Manual deployment trigger"
   git push origin main
   ```

3. Vercel will automatically detect the push and deploy

## Method 4: Create Empty Commit (No Code Changes)

If you want to trigger deployment without changing code:

```bash
git commit --allow-empty -m "Trigger: Empty commit to trigger deployment"
git push origin main
```

## Troubleshooting

### If Deployment Still Doesn't Trigger:

1. **Check Project Settings**:
   - Go to Vercel Dashboard → Project → Settings → General
   - Verify **Root Directory** is set to `frontEnd`
   - Verify **Production Branch** is set to `main`

2. **Check Git Integration**:
   - Go to Vercel Dashboard → Project → Settings → Git
   - Verify repository is connected
   - Check webhook status
   - If disconnected, reconnect the repository

3. **Check Build Settings**:
   - Go to Vercel Dashboard → Project → Settings → General
   - Verify **Build Command**: `npm run build`
   - Verify **Output Directory**: `dist/bookfast-ui/browser`
   - Verify **Install Command**: `npm install`

4. **Manual Webhook Test**:
   - Go to GitHub → Repository → Settings → Webhooks
   - Find Vercel webhook
   - Click "Recent Deliveries"
   - Check if webhooks are being received

## Quick Reference

### Fastest Method:
1. Vercel Dashboard → Deployments → Latest Deployment → ⋯ → Redeploy → Rebuild

### Most Reliable Method:
1. Make a small code change
2. `git commit -m "Trigger deployment"`
3. `git push origin main`
4. Wait for auto-deployment

### Without Code Changes:
```bash
git commit --allow-empty -m "Trigger deployment"
git push origin main
```

## Recommended Approach

For your `bookfast-q319` project:
1. Use **Method 1, Option A** (Redeploy) if you want immediate deployment
2. Use **Method 3** (Git Push) if you want to test the automatic deployment
3. Use **Method 4** (Empty Commit) if you just want to trigger without changes

