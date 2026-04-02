# Women's Safety App - Backend Implementation Guide

## Overview
This document outlines the complete backend setup for the Women's Safety App using Firebase as the primary backend service.

## Architecture Components

### 1. **Firebase Services Used**
- **Firebase Authentication** - User login and registration
- **Firestore Database** - NoSQL database for user data, complaints, safety tips
- **Realtime Database** - Real-time location sharing and SOS alerts
- **Storage** - Image uploads for complaints and profile pictures

### 2. **Core Services Created**

#### A. FirebaseAuthService (`services/FirebaseAuthService.java`)
Handles all authentication operations:
- **registerUser()** - Create new user account with email, password, name, and phone
- **loginUser()** - Login existing user with email and password
- **logout()** - Sign out current user
- **getCurrentUser()** - Get current logged-in Firebase user
- **isUserLoggedIn()** - Check if user is authenticated
- **resetPassword()** - Send password reset email

**Callbacks:**
```java
interface OnAuthCallback {
    void onSuccess(String message);
    void onError(String error);
}
```

#### B. FirebaseFirestoreService (`services/FirebaseFirestoreService.java`)
Handles all Firestore database operations:

**User Operations:**
- `addUser()` - Create user profile in Firestore
- `getUser()` - Fetch user profile by ID
- `getAllUsers()` - Get all users (admin only)
- `updateUser()` - Update user profile information
- `deleteUser()` - Delete user account (admin)

**Complaint Operations:**
- `submitComplaint()` - Submit a new complaint
- `getComplaintsByUser()` - Get user's complaints
- `getAllComplaints()` - Get all complaints (admin)
- `updateComplaintStatus()` - Update complaint status to Pending/Under Review/Resolved

**Safety Tips Operations:**
- `getSafetyTips()` - Get all visible safety tips
- `getSafetyTipsByCategory()` - Get tips by category
- `addSafetyTip()` - Add new tip (admin)
- `updateSafetyTip()` - Update tip content (admin)
- `deleteSafetyTip()` - Remove tip (admin)

**Emergency Contacts:**
- `addEmergencyContact()` - Add contact for user
- `getEmergencyContacts()` - Fetch user's emergency contacts
- `deleteEmergencyContact()` - Remove contact

**Location:**
- `saveLocation()` - Save location for emergency tracking
- `getDashboardStats()` - Get admin dashboard statistics

#### C. FirebaseRealtimeDatabaseService (`services/FirebaseRealtimeDatabaseService.java`)
Handles real-time operations:
- `sendSOSAlert()` - Send SOS with live location
- `shareLocation()` - Start sharing live location
- `stopLocationSharing()` - Stop location sharing
- `cancelSOSAlert()` - Cancel active SOS alert
- `listenToLocationUpdates()` - Listen for location changes
- `listenToSOSAlerts()` - Listen for SOS alerts (admin/contacts)

---

## Database Schema

### Firestore Collections

#### 1. **users** Collection
```json
{
  "id": "user_uid",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "077-1234567",
  "status": "Active|Inactive|Blocked",
  "profilePicture": "url",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### 2. **complaints** Collection
```json
{
  "id": "complaint_id",
  "userId": "user_uid",
  "category": "Harassment|Assault|Threat",
  "location": "incident location",
  "latitude": 6.9271,
  "longitude": 80.7744,
  "description": "detailed description",
  "evidence": ["url1", "url2"],
  "status": "Pending|Under Review|Resolved|Closed",
  "timestamp": "complaint time",
  "updatedAt": "last update time"
}
```

#### 3. **safety_tips** Collection
```json
{
  "id": "tip_id",
  "title": "Stay Alert",
  "description": "Detailed safety tip content",
  "category": "Travel|Communication|Emergency",
  "isVisible": true,
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### 4. **emergency_contacts** Subcollection (under users)
```json
{
  "id": "contact_id",
  "name": "Emergency Contact Name",
  "phone": "077-9876543",
  "relationship": "Friend|Family|Police"
}
```

#### 5. **locations** Collection
```json
{
  "id": "location_id",
  "userId": "user_uid",
  "latitude": 6.9271,
  "longitude": 80.7744,
  "timestamp": "time of location",
  "type": "SOS|Share"
}
```

### Realtime Database Paths

#### 1. **sos_alerts**
```
sos_alerts/
  {userId}/
    - userId
    - userName
    - latitude
    - longitude
    - message
    - timestamp
    - status: "active"
```

#### 2. **live_locations**
```
live_locations/
  {userId}/
    - userId
    - userName
    - latitude
    - longitude
    - timestamp
```

---

## Activities Integration

### Updated Activities

#### 1. **LoginActivity**
- Uses `FirebaseAuthService.loginUser()`
- Email and password validation
- Directs to `HomeActivity` for regular users
- Directs to `AdminDashboardActivity` for admin users
- Password reset functionality

#### 2. **RegisterActivity**
- Uses `FirebaseAuthService.registerUser()`
- Full validation: email, name, phone, password strength
- Creates user in both Firebase Auth and Firestore
- Redirects to `HomeActivity` on success

#### 3. **HomeActivity**
- Loads user profile using `FirebaseFirestoreService.getUser()`
- Displays username from database
- Logout on back press with confirmation
- Redirects to login if not authenticated

#### 4. **AdminDashboardActivity**
- Loads statistics using `FirebaseFirestoreService.getDashboardStats()`
- Shows:
  - Total Users
  - Total Complaints
  - Resolved Complaints
  - Pending Complaints
- Real-time statistics from Firestore

#### 5. **AdminUsersActivity**
- Loads users using `FirebaseFirestoreService.getAllUsers()`
- Delete users with `deleteUser()`
- Search functionality via adapter filter
- Real-time user management

---

## Features Implementation Guide

### User Authentication Flow
1. **Registration**: Email → Firebase Auth → Firestore User Profile
2. **Login**: Email + Password → Firebase Auth → Home/Admin
3. **Session**: Checked via `FirebaseAuth.getCurrentUser()`
4. **Logout**: Clears Firebase auth + redirects to login

### Complaint Submission
1. User fills complaint form (Category, Location, Description)
2. `FirebaseFirestoreService.submitComplaint()` saves to Firestore
3. Location saved using `FirebaseFirestoreService.saveLocation()`
4. Admin can view and update status

### Safety Tips Display
1. `SafetyTipsActivity` calls `getSafetyTips()`
2. Fetches visible tips from Firestore
3. Filter by category using `getSafetyTipsByCategory()`
4. Admin can manage tips in `AdminSafetyTipsActivity`

### SOS Alert System
1. User holds SOS button → `FirebaseRealtimeDatabaseService.sendSOSAlert()`
2. Sends real-time alert with location to Realtime DB
3. Emergency contacts notified
4. Admin can track via Realtime DB listener

### Location Sharing
1. `LocationActivity` uses `FirebaseRealtimeDatabaseService.shareLocation()`
2. Updates location continuously
3. `stopLocationSharing()` to stop
4. Contacts can listen using `listenToLocationUpdates()`

### Emergency Contacts
1. `ContactsActivity` loads contacts: `getEmergencyContacts()`
2. Add new: `addEmergencyContact()`
3. Delete: `deleteEmergencyContact()`

---

## Firebase Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own profiles
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      allow read: if request.auth.uid != null; // Admins can view
    }
    
    // Users can read safety tips
    match /safety_tips/{document=**} {
      allow read: if request.auth.uid != null;
      allow write: if false; // Only admin via backend
    }
    
    // Users can submit and view complaints
    match /complaints/{document=**} {
      allow create: if request.auth.uid != null;
      allow read: if request.auth.uid == resource.data.userId;
      allow write: if false; // Only admin via backend
    }
    
    // Emergency contacts under user profiles
    match /users/{userId}/emergency_contacts/{contactId} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

---

## Remaining Activities to Implement

### High Priority
1. **ComplaintActivity** - Use `FirebaseFirestoreService.submitComplaint()`
2. **SafetyTipsActivity** - Use `getSafetyTips()` and `getSafetyTipsByCategory()`
3. **SosActivity** - Use `FirebaseRealtimeDatabaseService.sendSOSAlert()`
4. **LocationActivity** - Use `shareLocation()` with real GPS data
5. **ContactsActivity** - Use emergency contacts methods
6. **ProfileActivity** - Use `updateUser()` for profile editing

### Medium Priority
1. **AdminComplaintsActivity** - List and update complaints status
2. **AdminSafetyTipsActivity** - CRUD operations for safety tips
3. **AdminReportsActivity** - Generate statistics reports

### Low Priority
1. **Complaint2Activity** - Evidence upload using Firebase Storage

---

## Key Methods Reference

### Quick Start - Login Example
```java
FirebaseAuthService authService = new FirebaseAuthService();
authService.loginUser("user@example.com", "password123", 
    new FirebaseAuthService.OnAuthCallback() {
        @Override
        public void onSuccess(String message) {
            startActivity(new Intent(activity, HomeActivity.class));
        }
        
        @Override
        public void onError(String error) {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
        }
    });
```

### Quick Start - Get User Data
```java
FirebaseFirestoreService firestore = FirebaseFirestoreService.getInstance();
String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
firestore.getUser(userId, new FirebaseFirestoreService.OnUserFetchCallback() {
    @Override
    public void onSuccess(User user) {
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
    }
});
```

---

## Testing Recommendations

1. **Test Authentication**:
   - Register new user with valid/invalid data
   - Login with correct/incorrect credentials
   - Password reset flow
   - Logout functionality

2. **Test Firestore Operations**:
   - Create user profile
   - Update profile information
   - Query users list
   - Submit complaint
   - Fetch complaints

3. **Test Realtime Database**:
   - Send SOS alert
   - Share location in real-time
   - Listen for updates
   - Cancel SOS alert

4. **Test Admin Features**:
   - View all users
   - View all complaints
   - Update complaint status
   - Manage safety tips
   - View dashboard statistics

---

## Next Steps

1. Implement remaining activities using provided service methods
2. Set up Firebase Firestore security rules
3. Test all authentication flows
4. Test database CRUD operations
5. Implement image upload to Firebase Storage
6. Set up admin role verification
7. Add offline mode support with Firebase offline persistence
8. Implement push notifications for SOS alerts

---

## Support & Troubleshooting

**Common Issues**:
- **Auth Error**: Ensure Firebase project is properly configured in google-services.json
- **Firestore Timeout**: Check internet connectivity and Firebase quota
- **Location Accuracy**: Use high accuracy mode and proper permission requests
- **SOS Not Sending**: Verify Realtime Database rules allow writes

**Debug Tips**:
- Enable Firestore logging: `FirebaseFirestore.setLoggingEnabled(true)`
- Monitor Firebase Console for errors
- Use Android Logcat for service method logging
- Test with Firebase Emulator Suite for development

---

Generated: April 2, 2026

