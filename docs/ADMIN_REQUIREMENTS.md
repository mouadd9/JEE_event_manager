# Admin Role Requirements

## Overview
The admin role is responsible for content moderation and system oversight. 
This document outlines all admin-specific features to be implemented by the authentication team.

## Admin Capabilities

### 1. Event Moderation
- **View Reported Events**: Access list of events with participant reports
  - Show event title, organizer, report count, report reasons
- **Hide/Show Events**: 
  - Mark events as "CACHE" (hidden from participants)
  - Unhide previously hidden events
  - Send notification to organizer when event is hidden
- **View Event Report Details**:
  - See who reported the event (participant names)
  - Read report reasons/descriptions
  - View report timestamps

### 2. User Management
- **View All Users**: List of all participants and organizers
  - Filter by user type, registration date, activity level
- **Suspend/Unsuspend Users**: Temporarily disable user accounts
- **Delete Users**: Permanently remove users (with confirmation)
- **View User Activity**: 
  - User's inscriptions, comments, reviews
  - Login history

### 3. Content Moderation
- **Review Comments**: Flag inappropriate comments
- **Review Evaluations**: Flag inappropriate reviews
- **Delete Content**: Remove comments/reviews that violate terms

### 4. Analytics & Reports
- **Platform Statistics**:
  - Total events, users, inscriptions
  - Active vs inactive events
  - User growth charts
- **Export Reports**: System-wide data exports

### 5. System Configuration
- **Manage Categories**: Add/edit/delete event categories
- **Site Settings**: Platform name, contact info, etc.

## API Endpoints Needed

### Event Moderation
- `GET /admin/events/reported` - Get reported events
- `POST /admin/event/{id}/hide` - Hide an event
- `POST /admin/event/{id}/unhide` - Unhide an event
- `GET /admin/event/{id}/reports` - Get report details

### User Management
- `GET /admin/users` - Get all users
- `POST /admin/user/{id}/suspend` - Suspend user
- `POST /admin/user/{id}/unsuspend` - Unsuspend user
- `DELETE /admin/user/{id}` - Delete user

### Content Moderation
- `GET /admin/content/flagged` - Get flagged content
- `DELETE /admin/comment/{id}` - Delete comment
- `DELETE /admin/review/{id}` - Delete review

## Database Schema Updates

### New Tables
1. **event_reports**:
   - id, evenement_id, participant_id, reason, description, created_at

2. **admin_actions**:
   - id, admin_id, action_type, target_id, target_type, reason, created_at

### Status Enum Updates
Add to `StatutEvenement`:
- `CACHE` (hidden by admin)

## Notes for Auth Team
- Admin users should have `userType = ADMIN` in Utilisateur table
- All admin actions should be logged in `admin_actions` table
- Admin pages should be under `/admin/*` path
- Use same UI theme as organizer/participant (purple theme, Bootstrap 5)

## Implementation Priority

### Phase 1: Core Admin Features
1. Admin authentication and authorization
2. Event moderation (hide/show events)
3. User management (view, suspend users)
4. Basic admin dashboard

### Phase 2: Advanced Features
1. Event reporting system
2. Content moderation tools
3. Advanced analytics
4. System configuration

### Phase 3: Reporting & Analytics
1. Comprehensive reporting system
2. Data export functionality
3. Audit logs
4. Performance monitoring

## Security Considerations
- All admin actions must be logged
- Admin access should be restricted to specific IP ranges (if needed)
- Two-factor authentication for admin accounts
- Regular audit of admin actions
- Backup and recovery procedures for admin data

## UI/UX Requirements
- Admin interface should follow the same design system as participant/organizer pages
- Purple theme (#a855f7) with Bootstrap 5
- Responsive design for mobile/tablet access
- Clear navigation and breadcrumbs
- Confirmation dialogs for destructive actions
- Success/error notifications for all operations
