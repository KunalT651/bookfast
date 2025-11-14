# Vercel Free Tier Limits & How to Check Remaining Deployments

## 🎯 Quick Answer: How to Check Remaining Deployments

### Method 1: Account Usage Page (Recommended)
1. Go to [https://vercel.com/account/usage](https://vercel.com/account/usage)
2. Look for the **"Deployments"** section
3. You'll see: **"X / 100 deployments today"**
4. Check the **"Reset time"** (usually midnight UTC)

### Method 2: Via Dashboard
1. Go to [https://vercel.com/dashboard](https://vercel.com/dashboard)
2. Click on your **profile icon** (top right)
3. Select **"Usage"** from the dropdown
4. View deployment usage and limits

### Method 3: Manual Count (If usage page not visible)
1. Go to your project → **"Deployments"** tab
2. Filter by **"Today"**
3. Count the deployments manually
4. Subtract from 100 to get remaining

## 📊 Vercel Free Tier (Hobby) Limits

### Daily Limits
- **100 deployments per day** (resets at midnight UTC)
- **Unlimited bandwidth** (with fair use policy)
- **Automatic deployments** on git push

### Monthly Limits
- **6,000 build minutes per month** (resets on 1st of month)
- **100 GB bandwidth per month**
- **45 minutes max build time** per deployment

### What Counts as a Deployment?
- ✅ **Production deployments** (pushes to main branch)
- ✅ **Preview deployments** (pull requests, branches)
- ✅ **Manual redeployments**
- ✅ **Failed deployments** (still count!)
- ❌ **Cancelled deployments** (before build starts)

## 🔍 Detailed Steps to Check Usage

### Step 1: Access Usage Page
1. Visit [https://vercel.com](https://vercel.com)
2. Log in to your account
3. Click on your **profile icon** (top right corner)
4. Select **"Usage"** from the dropdown menu

### Step 2: View Deployment Usage
On the usage page, you'll see:
- **Deployments**: X / 100 today
- **Build Minutes**: X / 6,000 this month
- **Bandwidth**: X / 100 GB this month
- **Reset Time**: When limits reset (midnight UTC for daily, 1st of month for monthly)

### Step 3: Check Reset Time
- **Daily limit**: Resets at **midnight UTC** (00:00 UTC)
- **Monthly limit**: Resets on the **1st of each month**
- **Timezone**: UTC (convert to your local time)

## ⚠️ What Happens When You Hit the Limit?

### Deployment Limit Reached (100/day)
- ⚠️ **New deployments will be queued**
- ⚠️ **Deployments may be delayed** until reset
- ⚠️ **Preview deployments may fail**
- ✅ **Existing deployments continue to work**
- ✅ **Limit resets at midnight UTC**

### Build Minutes Limit Reached (6,000/month)
- ⚠️ **New builds will fail**
- ⚠️ **Deployments will be cancelled**
- ⚠️ **You'll need to upgrade** or wait for monthly reset
- ✅ **Limit resets on the 1st of each month**

## 💡 Tips to Avoid Hitting Limits

### 1. Reduce Unnecessary Deployments
- **Avoid empty commits** unless necessary
- **Batch commits** instead of pushing individually
- **Use local testing** before pushing
- **Close old preview deployments** (auto-delete after 30 days)

### 2. Optimize Build Times
- **Use build cache** (Vercel automatically caches node_modules)
- **Optimize build process** (remove unused dependencies)
- **Minimize build dependencies**
- **Use incremental builds** where possible

### 3. Monitor Usage
- **Check usage daily** if you're close to limits
- **Track deployment patterns** to optimize
- **Set reminders** for reset times
- **Use preview deployments wisely**

## 🚀 Upgrade Options

### If You Need More Deployments

#### Pro Plan ($20/month per user)
- ✅ **Unlimited deployments** per day
- ✅ **6,000 build minutes** per month
- ✅ **Team collaboration** features
- ✅ **Advanced analytics**
- ✅ **Priority support**

#### Enterprise Plan (Custom pricing)
- ✅ **Unlimited deployments**
- ✅ **Custom build minutes**
- ✅ **Priority support**
- ✅ **Advanced security features**
- ✅ **SLA guarantees**

## 📝 Quick Reference

### Check Usage
- **URL**: [https://vercel.com/account/usage](https://vercel.com/account/usage)
- **Dashboard**: Profile → Usage
- **Reset Time**: Midnight UTC (daily) / 1st of month (monthly)

### Free Tier Limits
- **Deployments**: 100 per day
- **Build minutes**: 6,000 per month
- **Bandwidth**: 100 GB per month
- **Build timeout**: 45 minutes per build

### Conversion to Your Timezone
- **UTC to EST**: UTC - 5 hours (UTC - 4 hours during DST)
- **UTC to PST**: UTC - 8 hours (UTC - 7 hours during DST)
- **UTC to IST**: UTC + 5:30 hours

## 🔧 Troubleshooting

### Can't See Usage Information?
1. **Check your account type** (Hobby, Pro, Enterprise)
2. **Verify you're logged in** to the correct account
3. **Check team settings** if using a team account
4. **Clear browser cache** and try again
5. **Contact Vercel support** if usage page is not accessible

### Usage Page Not Loading?
1. **Check internet connection**
2. **Try a different browser**
3. **Clear browser cache**
4. **Disable browser extensions**
5. **Try incognito/private mode**

### Deployment Failing Due to Limits?
1. **Check usage page** for remaining deployments
2. **Wait for daily reset** (midnight UTC)
3. **Optimize deployment frequency**
4. **Consider upgrading** if consistently hitting limits
5. **Use preview deployments** for testing

## 📚 Additional Resources

- **Vercel Pricing**: [https://vercel.com/pricing](https://vercel.com/pricing)
- **Vercel Documentation**: [https://vercel.com/docs](https://vercel.com/docs)
- **Vercel Support**: [https://vercel.com/support](https://vercel.com/support)
- **Vercel Limits**: [https://vercel.com/docs/limits](https://vercel.com/docs/limits)
- **Vercel Usage API**: [https://vercel.com/docs/rest-api#endpoints/usage](https://vercel.com/docs/rest-api#endpoints/usage)

## 🎯 Summary

### How to Check Remaining Deployments:
1. Go to [https://vercel.com/account/usage](https://vercel.com/account/usage)
2. Look for **"Deployments: X / 100 today"**
3. Check **"Reset time"** (midnight UTC)

### Free Tier Limits:
- **100 deployments per day** (resets at midnight UTC)
- **6,000 build minutes per month** (resets on 1st of month)
- **100 GB bandwidth per month**

### If You Hit the Limit:
- Wait for daily reset (midnight UTC)
- Optimize deployment frequency
- Consider upgrading to Pro plan ($20/month)
