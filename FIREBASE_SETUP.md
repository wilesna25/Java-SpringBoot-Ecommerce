# Firebase Authentication Setup Guide

This guide explains how to set up Firebase Authentication for the NiceCommerce Spring Boot application.

## Prerequisites

1. Google Cloud Platform account
2. Firebase project created
3. Firebase Admin SDK service account key

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `nicecommerce` (or your preferred name)
4. Follow the setup wizard
5. Enable Google Analytics (optional)

## Step 2: Enable Authentication

1. In Firebase Console, go to **Authentication**
2. Click **Get Started**
3. Enable **Email/Password** provider
4. Enable **Google** provider (for Google Sign-In)
   - Add your OAuth consent screen
   - Add authorized domains

## Step 3: Generate Service Account Key

1. Go to **Project Settings** → **Service Accounts**
2. Click **Generate New Private Key**
3. Download the JSON file
4. Save it as `firebase-service-account.json` in `src/main/resources/`

**⚠️ Security Note**: Never commit this file to version control!

## Step 4: Configure Application

### Option 1: Using Credentials File (Recommended for Development)

1. Place `firebase-service-account.json` in `src/main/resources/`
2. Update `application.yml`:
```yaml
firebase:
  credentials:
    json: firebase-service-account.json
  project-id: your-project-id
```

### Option 2: Using Environment Variables (Recommended for Production)

```bash
export FIREBASE_CREDENTIALS_PATH=/path/to/firebase-service-account.json
export FIREBASE_PROJECT_ID=your-project-id
```

### Option 3: Using GCP Default Credentials (For GCP Environments)

If running on Google Cloud (Cloud Run, App Engine, etc.), Firebase will use default credentials automatically.

## Step 5: Update .gitignore

Make sure `firebase-service-account.json` is in `.gitignore`:

```
firebase-service-account.json
**/firebase-service-account.json
```

## Step 6: Test the Setup

1. Start the application:
```bash
mvn spring-boot:run
```

2. Check logs for:
```
Firebase initialized successfully
```

## API Endpoints

Once configured, you can use these endpoints:

### Sign Up
```bash
POST /api/auth/signup
{
  "email": "user@example.com",
  "password": "password123",
  "displayName": "John Doe"
}
```

### Sign In (Client gets ID token from Firebase SDK)
```bash
POST /api/auth/signin
{
  "idToken": "firebase-id-token-from-client"
}
```

### Update User
```bash
PUT /api/auth/users/{uid}
{
  "displayName": "Updated Name",
  "phoneNumber": "+1234567890"
}
```

### Sign Out
```bash
POST /api/auth/signout/{uid}
```

### Password Reset
```bash
POST /api/auth/password/reset
{
  "email": "user@example.com"
}
```

## Client-Side Integration

### Web (JavaScript)

```javascript
import { initializeApp } from 'firebase/app';
import { getAuth, signInWithEmailAndPassword, createUserWithEmailAndPassword } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "your-api-key",
  authDomain: "your-project.firebaseapp.com",
  projectId: "your-project-id"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

// Sign up
const userCredential = await createUserWithEmailAndPassword(auth, email, password);
const idToken = await userCredential.user.getIdToken();

// Send to backend
fetch('/api/auth/signin', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ idToken })
});
```

### Android (Kotlin)

```kotlin
import com.google.firebase.auth.FirebaseAuth

val auth = FirebaseAuth.getInstance()

// Sign up
auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = task.result?.user
            user?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
                val idToken = tokenResult.token
                // Send to backend
            }
        }
    }
```

### iOS (Swift)

```swift
import FirebaseAuth

// Sign up
Auth.auth().createUser(withEmail: email, password: password) { result, error in
    if let user = result?.user {
        user.getIDToken { idToken, error in
            // Send to backend
        }
    }
}
```

## Security Best Practices

1. **Never expose service account key** in client-side code
2. **Use environment variables** for credentials in production
3. **Enable Firebase App Check** to prevent abuse
4. **Set up Firebase Security Rules** for Firestore/Realtime Database
5. **Use HTTPS** in production
6. **Implement rate limiting** on authentication endpoints
7. **Log authentication events** for security monitoring

## Troubleshooting

### Error: "FirebaseApp already initialized"
- Firebase is initialized multiple times
- Solution: Check that `@PostConstruct` only runs once

### Error: "Failed to initialize Firebase"
- Credentials file not found or invalid
- Solution: Verify file path and JSON format

### Error: "Invalid token"
- Token expired or invalid
- Solution: Client should refresh token using Firebase SDK

## Testing

Run tests with:
```bash
mvn test
```

Tests use mocked Firebase services, so no actual Firebase connection is needed.

## Production Deployment

For production on GCP:

1. Store service account key in **Secret Manager**
2. Mount as volume in Cloud Run/App Engine
3. Or use **Workload Identity** for automatic authentication

Example Cloud Run deployment:
```bash
gcloud run deploy nicecommerce \
  --set-env-vars="FIREBASE_PROJECT_ID=your-project-id" \
  --set-secrets="FIREBASE_CREDENTIALS=firebase-key:latest"
```

---

For more information, see:
- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin/setup)
- [Firebase Authentication Documentation](https://firebase.google.com/docs/auth)

