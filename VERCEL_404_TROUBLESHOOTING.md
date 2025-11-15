# Vercel 404 Error Troubleshooting

## Issue: 404 NOT_FOUND on Vercel Deployment

If your Vercel deployment shows "404: NOT_FOUND" even though the deployment status is "Ready", this is usually a configuration issue.

## Common Causes

### 1. **Root Directory Not Set Correctly** ⚠️ MOST COMMON

Vercel needs to know that your Angular app is in the `frontEnd` folder.

**Fix:**
1. Go to Vercel Dashboard → Your Project → **Settings** → **General**
2. Scroll to **Root Directory**
3. Set it to: `frontEnd`
4. Click **Save**
5. Vercel will automatically redeploy

### 2. **Output Directory Mismatch**

The `outputDirectory` in `vercel.json` must match Angular's actual build output.

**Check:**
- `vercel.json` has: `"outputDirectory": "dist/bookfast-ui/browser"`
- Angular outputs to: `dist/bookfast-ui/browser` (default for Angular 17+)

**If different**, update `vercel.json` to match Angular's output.

### 3. **Build Command Issue**

**Check:**
- `vercel.json` has: `"buildCommand": "npm run build"`
- `package.json` has: `"build": "ng build --configuration production"`

Both should be correct. If you have a custom build script, update `vercel.json`.

### 4. **Missing index.html**

The build might not be generating `index.html` in the output directory.

**Verify:**
1. Check Vercel build logs
2. Look for errors during the build process
3. Ensure Angular build completes successfully

## Step-by-Step Fix

### Step 1: Verify Vercel Project Settings

1. Go to **Vercel Dashboard** → Your Project → **Settings** → **General**
2. Verify these settings:
   - **Root Directory**: `frontEnd` ✅
   - **Framework Preset**: `Angular` (or `Other`)
   - **Build Command**: `npm run build` (or leave empty if in vercel.json)
   - **Output Directory**: `dist/bookfast-ui/browser` (or leave empty if in vercel.json)

### Step 2: Check vercel.json Location

The `vercel.json` file must be in the **root directory** that Vercel uses.

- If Root Directory is `frontEnd`, then `vercel.json` should be at: `frontEnd/vercel.json` ✅
- If `vercel.json` is in the repo root, Vercel won't find it when Root Directory is `frontEnd`

### Step 3: Verify Build Output

1. Go to Vercel Dashboard → Your Project → **Deployments**
2. Click on the latest deployment
3. Click **Build Logs**
4. Look for the build output path
5. Verify it says something like: `Output directory: dist/bookfast-ui/browser`

### Step 4: Check File Structure

After build, Vercel should find:
```
dist/bookfast-ui/browser/
  ├── index.html
  ├── main.js
  ├── styles.css
  └── ... (other assets)
```

If `index.html` is missing, the build failed or output directory is wrong.

## Quick Fix Checklist

- [ ] Root Directory in Vercel Settings = `frontEnd`
- [ ] `vercel.json` exists at `frontEnd/vercel.json`
- [ ] `vercel.json` has correct `outputDirectory`: `dist/bookfast-ui/browser`
- [ ] Build completes successfully (check Build Logs)
- [ ] `index.html` exists in build output (check Build Logs)
- [ ] Rewrites rule in `vercel.json` routes all requests to `/index.html`

## Current vercel.json Configuration

Your `frontEnd/vercel.json` should be:
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

## If Still Not Working

### Option 1: Manual Redeploy
1. Go to Deployments
2. Click **⋯** (three dots) on latest deployment
3. Select **Redeploy**
4. Choose **Rebuild** (clean build)
5. Click **Redeploy**

### Option 2: Check Build Logs
1. Go to Deployments → Latest Deployment
2. Click **Build Logs**
3. Look for errors or warnings
4. Common issues:
   - Build fails → Fix build errors
   - Wrong output path → Update `outputDirectory` in `vercel.json`
   - Missing dependencies → Check `package.json`

### Option 3: Verify Angular Build Locally
```bash
cd frontEnd
npm install
npm run build
ls -la dist/bookfast-ui/browser/
```
You should see `index.html` in that directory. If not, the Angular build configuration is wrong.

## Testing After Fix

1. Wait for redeployment to complete
2. Visit your Vercel URL: `https://bookfast-q319.vercel.app`
3. Should show the registration page (not 404)
4. Try navigating to `/login` - should work
5. Try `/reset-password?token=test` - should show the reset form (even if token is invalid)

## Still Getting 404?

1. **Check Vercel Function Logs** (if using serverless functions)
2. **Verify domain settings** (if using custom domain)
3. **Check for redirects** that might interfere
4. **Contact Vercel support** with deployment URL and build logs

