# Quick Start: Testing on Localhost

## 🚀 Quick Start (3 Steps)

### Step 1: Start Backend (Required for Full Functionality)
```bash
cd backEnd
./mvnw spring-boot:run
```
Wait for: `Started BackendApplication` message

### Step 2: Start Frontend (Already Running!)
The frontend is already running on port 4200.

### Step 3: Open Browser
Navigate to: **`http://localhost:4200`**

## ✅ Correct URLs

### Public Routes (No Login Required)
- **Registration**: `http://localhost:4200/registration`
- **Login**: `http://localhost:4200/login`
- **Admin Login**: `http://localhost:4200/admin/login`
- **Provider Registration**: `http://localhost:4200/provider/registration`
- **Password Reset**: `http://localhost:4200/password-reset`

### Protected Routes (Login Required)
- **Customer Home**: `http://localhost:4200/customer/home`
- **Customer Bookings**: `http://localhost:4200/customer/bookings`
- **Admin Dashboard**: `http://localhost:4200/admin/dashboard`
- **Provider Dashboard**: `http://localhost:4200/provider/dashboard`

## ❌ Common Mistakes

### Wrong URL
- ❌ `http://localhost:4200/bookfast/registration` (doesn't exist)
- ❌ `http://localhost:4200/bookfast/login` (doesn't exist)

### Correct URL
- ✅ `http://localhost:4200/registration`
- ✅ `http://localhost:4200/login`

## 🔍 Current Status

- ✅ **Frontend**: Running on port 4200
- ❌ **Backend**: Not running on port 8080

### To Test Frontend Only:
1. Open `http://localhost:4200`
2. You'll see the registration page
3. Navigation will work, but API calls will fail (backend not running)

### To Test Full Application:
1. Start backend: `cd backEnd && ./mvnw spring-boot:run`
2. Wait for backend to start
3. Open `http://localhost:4200`
4. Test registration, login, and all features

## 🐛 Troubleshooting

### If You See "Connection Refused"
1. Make sure frontend is running (check port 4200)
2. Use correct URL: `http://localhost:4200` (not `/bookfast/`)
3. Try `http://127.0.0.1:4200` if `localhost` doesn't work

### If API Calls Fail
1. Start backend on port 8080
2. Check backend logs for errors
3. Verify database connection

### If Routes Don't Work
1. Check browser console for errors
2. Verify you're using correct URLs (without `/bookfast/`)
3. Check `app.routes.ts` for route definitions

## 📝 Testing Checklist

- [ ] Frontend loads at `http://localhost:4200`
- [ ] Registration page displays
- [ ] Navigation works
- [ ] Backend is running (for API calls)
- [ ] Can register a new user
- [ ] Can login
- [ ] Can access protected routes after login

## 🎯 Next Steps

1. **Test Frontend**: Open `http://localhost:4200` and verify UI works
2. **Start Backend**: Run `cd backEnd && ./mvnw spring-boot:run`
3. **Test Full Flow**: Register → Login → Use Application
4. **Check Console**: Look for any errors in browser console
5. **Test Features**: Test all features you've implemented

