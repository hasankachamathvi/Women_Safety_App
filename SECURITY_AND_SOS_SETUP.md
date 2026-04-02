# Security, Admin Role, GPS, and SOS Contact Alert Setup

## Implemented in app code

- Role-based admin checks in login routing and `BaseAdminActivity`
- Live GPS coordinates for SOS and location sharing
- Google Map embedded in `LocationActivity`
- SOS post-action dialog to notify emergency contacts via SMS/call intents

## 1) Set admin role in Firestore

In `users/{uid}`:

```json
{
  "role": "admin",
  "status": "Active"
}
```

Regular users:

```json
{
  "role": "user",
  "status": "Active"
}
```

## 2) Add Google Maps key

Set API key in `app/src/main/res/values/strings.xml`:

```xml
<string name="google_maps_key">YOUR_REAL_MAPS_API_KEY</string>
```

## 3) Publish rules

- Firestore -> Rules: use `firestore.rules`
- Realtime Database -> Rules: use `database.rules.json`

## 4) SOS emergency contact alert flow

After SOS activation:

1. App writes SOS record to Realtime DB with coordinates.
2. App loads `users/{uid}/emergency_contacts`.
3. App prompts:
   - `Open SMS`: opens default SMS app with prefilled emergency message
   - If no contacts: `Call 119`

## 5) Quick verification

1. Login as non-admin -> admin pages should redirect.
2. Login as admin -> admin pages should open.
3. Open location -> map and current coordinates appear.
4. Press SOS -> alert written + SMS/call prompt appears.

## Notes

- SMS is opened through the SMS app for explicit user consent.
- Replace the placeholder Maps API key before testing map rendering.

