# Admin Setup - Quick Start Guide

## Problem Fixed
The admin dashboard was showing errors because existing users in the database didn't have values for the new fields (is_active, is_suspended, is_verified). This has been fixed with null-safe code.

## Setup Steps

### 1. Stop the Application (if running)
Press `Ctrl+C` in the terminal running cargo:run

### 2. Update Database for Existing Users
Run this SQL script to set default values for existing users:

```bash
mysql -u eventuser -peventpass event_manager < update_existing_users.sql
```

This will:
- Set `is_active = 1` for all users
- Set `is_suspended = 0` for all users  
- Set `is_verified = 0` for new users (or 1 for existing organisateurs)

### 3. Create Admin User
Run this SQL script to create the admin account:

```bash
mysql -u eventuser -peventpass event_manager < admin_setup.sql
```

**Admin Credentials:**
- Email: `admin@eventmanager.com`
- Password: `Admin@123`

### 4. Build and Run
```bash
.\mvnw clean package
.\mvnw cargo:run
```

### 5. Login as Admin
1. Navigate to: `http://localhost:8081/jee-event-manager/login`
2. Enter email: `admin@eventmanager.com`
3. Enter password: `Admin@123`
4. You'll be redirected to the admin dashboard

## What Was Fixed

### Code Changes:
1. **AdminService.java** - Added null-safe checks:
   ```java
   // Before (would throw NullPointerException):
   .filter(u -> !u.getIsVerified())
   
   // After (null-safe):
   .filter(u -> u.getIsVerified() == null || !u.getIsVerified())
   ```

2. **Utilisateur.java** - Initialize fields in @PrePersist:
   ```java
   if (isActive == null) isActive = true;
   if (isSuspended == null) isSuspended = false;
   if (isVerified == null) isVerified = false;
   ```

3. **Database Migration** - Created `update_existing_users.sql` to set default values

### Why This Happened:
- The new fields (is_active, is_suspended, is_verified) were added to existing users
- Existing database records had NULL values for these fields
- Java code tried to call methods on NULL, causing NullPointerException
- Fixed by adding null checks and updating database

## Verification

After setup, verify everything works:

### 1. Check Admin Dashboard
- Should see user statistics (participants, organisateurs, admins)
- Should see event statistics (published, drafts, hidden, cancelled)
- No errors should appear

### 2. Check User Management
- Navigate to Users tab
- Should see list of all users
- Filter by user type, verification, suspension status
- Try validating an organisateur

### 3. Check Event Moderation  
- Navigate to Events tab
- Should see list of all events
- Filter by status
- Try hiding/publishing an event

## Troubleshooting

### Issue: Stats still not showing
**Solution:** Run the database update script:
```bash
mysql -u eventuser -peventpass event_manager < update_existing_users.sql
```

### Issue: Cannot login as admin
**Solution:** Verify admin user exists:
```sql
SELECT * FROM utilisateur WHERE email = 'admin@eventmanager.com';
```
If not found, run:
```bash
mysql -u eventuser -peventpass event_manager < admin_setup.sql
```

### Issue: NullPointerException in logs
**Solution:** Restart the application after running both SQL scripts

### Issue: Admin redirects to catalogue instead of dashboard
**Solution:** Check LoginServlet is updated and userType is set to "ADMIN" in session

## Database Schema

New columns in `utilisateur` table:
```sql
is_active BOOLEAN DEFAULT 1
is_suspended BOOLEAN DEFAULT 0  
is_verified BOOLEAN DEFAULT 0
suspension_reason VARCHAR(500)
```

New `admin` table:
```sql
CREATE TABLE admin (
  utilisateur_id BIGINT PRIMARY KEY,
  role VARCHAR(50),
  permissions VARCHAR(255),
  FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);
```

## Quick Commands Reference

```bash
# Update existing users
mysql -u eventuser -peventpass event_manager < update_existing_users.sql

# Create admin user
mysql -u eventuser -peventpass event_manager < admin_setup.sql

# Build project
.\mvnw clean package

# Run application
.\mvnw cargo:run

# Check database
mysql -u eventuser -peventpass event_manager
```

## Success Indicators

✅ Admin dashboard loads without errors
✅ Statistics are displayed with numbers
✅ User management page shows all users
✅ Event moderation page shows all events
✅ Filters work correctly
✅ Actions (verify, suspend, hide) work via AJAX

All admin functionality should now work perfectly!
