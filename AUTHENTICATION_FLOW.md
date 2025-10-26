# Authentication & Registration Flow

## Overview
This document explains the complete authentication and user registration flow, including email verification and admin approval for organisateurs.

## User States

### Field Meanings
- **`is_verified`**: Email has been verified with verification code (true after email verification)
- **`is_active`**: Account is active and can login (true for participants immediately, false for organisateurs until admin approval)
- **`is_suspended`**: Account has been suspended by admin (default: false)

## Registration Flow

### 1. **PARTICIPANT Registration**
```
User fills form → Email verification code sent → User enters code → Account created
```

**After successful registration:**
- `is_verified = true` (email verified)
- `is_active = true` (can login immediately)
- `is_suspended = false`
- **Redirected to:** `/login` page with success message
- **Can login:** ✅ YES - immediately

---

### 2. **ORGANISATEUR Registration**
```
User fills form → Email verification code sent → User enters code → Account created (pending approval)
```

**After successful registration:**
- `is_verified = true` (email verified)
- `is_active = false` (⚠️ waiting for admin approval)
- `is_suspended = false`
- **Redirected to:** `/login` page with message: "Votre compte est en attente d'approbation par un administrateur"
- **Can login:** ❌ NO - must wait for admin approval

**After admin approval:**
- `is_active = true` (admin approved)
- **Can login:** ✅ YES - can now access organiser dashboard

---

## Login Validation Checks

When a user tries to login, the system checks in this order:

### 1. Email & Password
```java
Utilisateur utilisateur = authenticate(email, password);
if (utilisateur == null) → "Email ou mot de passe incorrect"
```

### 2. Email Verification
```java
if (!utilisateur.getIsVerified()) → "Votre compte n'est pas vérifié"
```

### 3. Suspension Status
```java
if (utilisateur.getIsSuspended()) → "Votre compte est suspendu. Raison: [reason]"
```

### 4. Active Status (Admin Approval for Organisateurs)
```java
if (!utilisateur.getIsActive()) {
    if (ORGANISATEUR) → "Votre compte est en attente d'approbation par un administrateur"
    else → "Votre compte n'est pas actif"
}
```

### 5. Redirect Based on User Type
- **PARTICIPANT** → `/participant/dashboard`
- **ORGANISATEUR** → `/organizer/dashboard`
- **ADMIN** → `/admin/dashboard`

---

## Admin Dashboard

### Pending Organisateurs Section
The admin dashboard shows a yellow warning box at the top when there are organisateurs awaiting approval:

**Criteria for "Pending":**
- `userType = ORGANISATEUR`
- `is_active = false`
- `is_suspended = false`

**Admin Actions:**
1. **Approuver** (Approve):
   - Sets `is_active = true`
   - User can now login
   
2. **Refuser** (Reject):
   - Prompts for reason
   - Sets `is_suspended = true`
   - Sets `is_active = false`
   - User cannot login

### User List
Shows all users with badges indicating their status:
- **Email vérifié** (green) - `is_verified = true`
- **Approuvé** (green) - `is_active = true` (for organisateurs only)
- **En attente** (yellow) - `is_active = false` (for organisateurs)
- **Suspendu** (red) - `is_suspended = true`

---

## Code Changes Summary

### Files Modified:

#### 1. **UtilisateurService.java** - `createUser()` method
```java
// Set verification and activation status
utilisateur.setIsVerified(true); // Email is verified after code verification

if (userType == UserType.PARTICIPANT) {
    utilisateur.setIsActive(true); // Participants active immediately
} else {
    utilisateur.setIsActive(false); // Organisateurs need admin approval
}
```

#### 2. **LoginServlet.java** - `doPost()` method
Added validation checks:
- Email verification check
- Suspension check  
- Active status check with specific message for organisateurs

Added `doGet()` method:
- Displays success message after registration

#### 3. **RegisterServlet.java** - `handleVerifyCode()` method
Changed redirect logic:
- No longer creates user session immediately
- Redirects to `/login` instead of dashboard
- Sets success message in session

#### 4. **AdminService.java**
- `getPendingOrganisateurs()`: Changed filter from `!is_verified` to `!is_active`
- `getDashboardStats()`: Changed pending count from `!is_verified` to `!is_active`
- `verifyOrganisateur()`: Changed action from setting `is_verified = true` to `is_active = true`

#### 5. **AdminUserManagementServlet.java**
- Updated success message: "Organisateur approuvé avec succès"

#### 6. **users.jsp** (Admin UI)
- Updated badges to show both email verification AND approval status
- Changed button text from "Valider" to "Approuver"
- Updated button visibility logic to check `!is_active` instead of `!is_verified`
- Added descriptive text in pending section

---

## Testing Checklist

### ✅ Participant Registration
1. Register as participant
2. Verify email with code
3. Should redirect to login with success message
4. Should be able to login immediately
5. Should see participant dashboard

### ✅ Organisateur Registration
1. Register as organisateur
2. Verify email with code
3. Should redirect to login with "pending approval" message
4. Try to login → Should see "en attente d'approbation" error
5. Login as admin → Should see organisateur in pending section
6. Admin approves → Organisateur can now login
7. Login as organisateur → Should see organiser dashboard

### ✅ Admin Dashboard
1. Create new organisateur account
2. Login as admin
3. Dashboard should show "Organisateurs en attente d'approbation (1)"
4. Yellow warning box should appear with pending organisateur
5. Click "Approuver" → Success message
6. Organisateur disappears from pending section
7. Appears in main user list with "Approuvé" badge

---

## Database Schema

### utilisateur table
```sql
- id (BIGINT, PRIMARY KEY)
- nom (VARCHAR)
- email (VARCHAR, UNIQUE)
- mot_de_passe_hash (VARCHAR)
- user_type (ENUM: PARTICIPANT, ORGANISATEUR, ADMIN)
- is_verified (BOOLEAN, DEFAULT 0) -- Email verification
- is_active (BOOLEAN, DEFAULT 1) -- Account activation/approval
- is_suspended (BOOLEAN, DEFAULT 0) -- Suspension status
- suspension_reason (VARCHAR, NULLABLE)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### Default Values
- **Participant**: `is_verified=1, is_active=1, is_suspended=0`
- **Organisateur**: `is_verified=1, is_active=0, is_suspended=0` (until admin approves)
- **Admin**: `is_verified=1, is_active=1, is_suspended=0`
