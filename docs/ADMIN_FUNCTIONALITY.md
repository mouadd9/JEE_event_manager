# Admin Functionality - EventManager

## Overview
Complete admin panel for managing users, validating organisateurs, and moderating events on the EventManager platform.

## Features Implemented

### 1. **Admin Dashboard** (`/admin/dashboard`)
- **Statistics Display:**
  - Total users, participants, organisateurs, and admins
  - Pending organisateurs awaiting validation
  - Suspended user accounts
  - Event statistics (published, drafts, hidden, cancelled)
- **Quick Actions:**
  - Manage users
  - Moderate events
  - Validate organisateurs
- **Visual alerts** for pending actions

### 2. **User Management** (`/admin/users`)
- **View all users** with filtering by:
  - User type (Participant, Organisateur, Admin)
  - Verification status
  - Suspension status
- **Pending Organisateurs Section** - highlighted for quick validation
- **User Actions:**
  - ✅ Verify/validate organisateur accounts
  - 🚫 Suspend user accounts (with reason)
  - ✔️ Activate/unsuspend accounts
  - 🗑️ Delete user accounts (cannot delete admins)
- **Real-time updates** with AJAX
- **User details display:**
  - User type badge
  - Verification status
  - Suspension status and reason

### 3. **Event Moderation** (`/admin/events`)
- **View all events** with filtering by status:
  - Published
  - Drafts
  - Hidden/Masked
  - Cancelled
- **Event Actions:**
  - 👁️ Hide/mask problematic events
  - ✅ Publish hidden or draft events
  - 🗑️ Delete events
  - 👀 View event details
- **Event information:**
  - Event thumbnail/image
  - Title, location, date
  - Organisateur name
  - Current status
- **Real-time moderation** with AJAX

## Database Changes

### New Fields in `utilisateur` table:
```sql
is_active BOOLEAN DEFAULT 1
is_suspended BOOLEAN DEFAULT 0
is_verified BOOLEAN DEFAULT 0
suspension_reason VARCHAR(500)
```

### New Tables:
- **`admin`** table extending utilisateur
  - `role` (SUPER_ADMIN, MODERATOR)
  - `permissions` (JSON or comma-separated)

### New UserType:
- `ADMIN` added to UserType enum

## Installation & Setup

### 1. Database Migration
The database schema will be automatically updated by Hibernate when you run the application.

### 2. Create Admin User
Run the SQL script to create an admin account:

```bash
mysql -u eventuser -peventpass event_manager < admin_setup.sql
```

**Admin Credentials:**
- Email: `admin@eventmanager.com`
- Password: `Admin@123`

### 3. Build and Run
```bash
.\mvnw clean package
.\mvnw cargo:run
```

### 4. Access Admin Panel
Navigate to: `http://localhost:8081/jee-event-manager/login`
Login with admin credentials and you'll be redirected to `/admin/dashboard`

## User Workflow

### For Regular Users:
- **Participants** → Redirected to `/participant/dashboard`
- **Organisateurs** → Redirected to `/organisateur/dashboard`
- **Admins** → Redirected to `/admin/dashboard`

### Organisateur Validation Flow:
1. Organisateur registers → Account created but **not verified**
2. Admin sees pending organisateur in dashboard alert
3. Admin goes to Users Management → Pending section
4. Admin clicks **Validate** → Organisateur is verified
5. Organisateur can now create events

### User Suspension Flow:
1. Admin identifies problematic user
2. Admin clicks **Suspend** and enters reason
3. User account is suspended (is_active = false)
4. User cannot login or perform actions
5. Admin can **Activate** to restore access

### Event Moderation Flow:
1. Organisateur publishes event
2. Admin reviews event in Events Management
3. Admin can:
   - **Hide** problematic events (status → CACHE)
   - **Publish** hidden events (status → PUBLIE)
   - **Delete** violating events permanently

## Security Features

- **Role-based access control** - Only ADMIN userType can access admin pages
- **Session validation** on every admin request
- **CSRF protection** recommended (can be added)
- **Audit trail** with created_at and updated_at timestamps

## Design Consistency

All admin pages follow the same design as participant/organisateur dashboards:
- **Purple gradient header** (#8c65a7 to #7d24bd)
- **Card-based layout** with hover effects
- **Font Awesome icons** throughout
- **Bootstrap 5** components
- **Responsive design** for mobile compatibility
- **Smooth animations** and transitions

## API Endpoints

### GET Endpoints:
- `GET /admin/dashboard` - Admin dashboard with statistics
- `GET /admin/users?userType=X&verified=Y&suspended=Z` - User management with filters
- `GET /admin/events?statut=X` - Event moderation with filters

### POST Endpoints (JSON responses):
- `POST /admin/users` - User management actions
  - Actions: `verify`, `suspend`, `activate`, `delete`
  - Parameters: `action`, `userId`, `reason` (for suspend)
  
- `POST /admin/events` - Event moderation actions
  - Actions: `hide`, `publish`, `unhide`, `delete`
  - Parameters: `action`, `eventId`

## Files Created/Modified

### New Files:
- `Admin.java` - Admin entity model
- `AdminQualifier.java` - CDI qualifier
- `AdminRepository.java` - Admin repository interface
- `AdminRepositoryImpl.java` - Admin repository implementation
- `AdminService.java` - Admin business logic
- `AdminDashboardServlet.java` - Dashboard servlet
- `AdminUserManagementServlet.java` - User management servlet
- `AdminEventModerationServlet.java` - Event moderation servlet
- `dashboard.jsp` - Admin dashboard view
- `users.jsp` - User management view
- `events.jsp` - Event moderation view
- `admin_setup.sql` - Admin user creation script

### Modified Files:
- `UserType.java` - Added ADMIN enum value
- `Utilisateur.java` - Added management fields
- `LoginServlet.java` - Added admin redirect logic

## Testing

### Test Admin Login:
1. Start application: `.\mvnw cargo:run`
2. Navigate to: `http://localhost:8081/jee-event-manager/login`
3. Login: `admin@eventmanager.com` / `Admin@123`
4. Should redirect to admin dashboard

### Test User Management:
1. Register new organisateur account
2. Login as admin
3. See pending organisateur in dashboard
4. Navigate to Users Management
5. Validate the organisateur
6. Refresh - organisateur should be verified

### Test Event Moderation:
1. Login as organisateur
2. Create and publish an event
3. Login as admin
4. Navigate to Events Management
5. Try hiding/publishing the event
6. Verify status changes

## Future Enhancements

- 📊 Advanced analytics and charts
- 📧 Email notifications for admin actions
- 📝 Detailed audit logs
- 🔍 Advanced search and filtering
- 👥 Multi-level admin roles
- 💬 Message system between admins and users
- 🚨 Automated content moderation
- 📱 Mobile app for admin tasks

## Troubleshooting

### Issue: Admin not redirected after login
**Solution:** Check session attributes, ensure userType is set to "ADMIN"

### Issue: Cannot access admin pages
**Solution:** Verify `isAdmin()` method in servlets, check session

### Issue: Actions not working
**Solution:** Check browser console for AJAX errors, verify servlet POST handlers

### Issue: Database errors on first run
**Solution:** Drop and recreate database to apply schema changes:
```sql
DROP DATABASE event_manager;
CREATE DATABASE event_manager;
```

## Support
For issues or questions, check the application logs in `logs/` directory.
