# Everquill Android App - Setup Guide

## 🎯 Quick Start

### 1. Prerequisites

- ✅ Android Studio Hedgehog or later
- ✅ JDK 11 or higher
- ✅ Android SDK 24+ (Android 7.0)
- ✅ Backend server (`exe201_be`) running

### 2. Backend Configuration

#### Option A: Local Development (Emulator)

1. Start backend server:

   ```bash
   cd exe201_be
   npm start
   ```

2. Backend will run on `http://localhost:3000`

3. In `ApiConfig.java`, use:
   ```java
   public static final String BASE_URL = "http://10.0.2.2:3000/api/";
   ```
   **Note**: `10.0.2.2` is the special IP for Android Emulator to access host machine's `localhost`

#### Option B: Physical Device (Same WiFi)

1. Find your computer's local IP:

   - Windows: `ipconfig` (look for IPv4 Address)
   - Mac/Linux: `ifconfig` (look for inet)
   - Example: `192.168.1.100`

2. Update `ApiConfig.java`:

   ```java
   public static final String BASE_URL = "http://192.168.1.100:3000/api/";
   ```

3. Make sure both computer and phone are on same WiFi network

#### Option C: Production (Deployed Backend)

1. If backend is deployed (e.g., on Render):
   ```java
   public static final String BASE_URL = "https://your-backend.onrender.com/api/";
   ```

### 3. Build & Run

#### In Android Studio:

1. Open `exe201_mobile` folder
2. Wait for Gradle sync to complete
3. Select device/emulator
4. Click Run button (or Shift+F10)

#### Via Command Line:

```bash
cd exe201_mobile
./gradlew assembleDebug
./gradlew installDebug
```

### 4. Test Login

Use test account or create new one:

- Navigate through SplashActivity -> LoginActivity
- Register new account or login with existing credentials
- Backend should be running and accessible

## 🔧 Troubleshooting

### "Unable to resolve dependency"

**Problem**: Gradle can't download dependencies

**Solutions**:

1. Check internet connection
2. Sync Gradle: File -> Sync Project with Gradle Files
3. Clean build: Build -> Clean Project
4. Rebuild: Build -> Rebuild Project

### "Connection refused" / Network errors

**Problem**: App can't connect to backend

**Solutions**:

1. **Check backend is running**: Visit `http://localhost:3000/api/docs` in browser
2. **Emulator**: Must use `10.0.2.2` not `localhost`
3. **Physical device**:
   - Use computer's IP address
   - Both must be on same WiFi
   - Check firewall isn't blocking port 3000
4. **Backend CORS**: Make sure `CLIENT_URL` in backend `.env` allows your requests

### "cleartext traffic not permitted"

**Problem**: Android blocks HTTP (non-HTTPS) requests by default

**Solution**: Already configured in `AndroidManifest.xml`:

```xml
android:usesCleartextTraffic="true"
```

### Build errors after adding dependencies

**Solutions**:

1. Invalidate caches: File -> Invalidate Caches / Restart
2. Delete `.gradle` folder and re-sync
3. Check `settings.gradle.kts` has jitpack.io repo

## 📱 Testing Tips

### Recommended Test Flow:

1. **SplashActivity** loads -> auto navigate based on login status
2. **LoginActivity** -> Register new account
3. **MainActivity** -> See empty journal list
4. **JournalEditorActivity** -> Create first journal with mood
5. **MainActivity** -> See new journal in list
6. **JournalEditorActivity** -> Edit journal, run AI Analysis
7. **DashboardActivity** -> View stats and charts
8. **ProfileActivity** -> Check user info, try avatar upload
9. **PremiumActivity** -> View premium plans
10. **NotificationActivity** -> Check notifications
11. **ReviewsActivity** -> Leave a review

### Backend Endpoints to Test:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/journals`
- `POST /api/journals`
- `PUT /api/journals/:id`
- `DELETE /api/journals/:id`
- `POST /api/journals/analyze/:id`
- `GET /api/users/me`
- `GET /api/templates`

## 🎨 UI Customization

### Colors

Edit `app/src/main/res/values/colors.xml`:

- `primary`: Main app color
- `accent`: Secondary/accent color
- `mood_*`: Colors for different moods

### Strings

Edit `app/src/main/res/values/strings.xml` for text content

### Themes

Edit `app/src/main/res/values/themes.xml` for app-wide styling

## 🔐 Security Notes

1. **Never commit sensitive data** to git
2. **API keys** should be in `local.properties` or BuildConfig
3. **Tokens** stored in encrypted SharedPreferences
4. **HTTPS** should be used in production

## 📦 APK Build

### Debug APK (for testing):

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (for production):

1. Create keystore
2. Configure signing in `build.gradle.kts`
3. Run:
   ```bash
   ./gradlew assembleRelease
   ```

## 🐛 Known Issues & Limitations

1. **Google Sign-In**: Requires Google Services configuration
2. **Image Upload**: File path handling may need adjustment for Android 10+
3. **Rich Text Editor**: WebView-based, may have performance on low-end devices
4. **Charts**: Sample data used, need real data integration
5. **Offline Mode**: Not implemented yet (consider adding Room database)

## 📞 Support

For issues or questions, contact the development team or create an issue in the repository.

## ✨ Next Steps

After basic setup works:

1. Implement offline mode with Room database
2. Add push notifications
3. Implement image attachments for journals
4. Add search and advanced filters
5. Implement data export/backup
6. Add biometric authentication
7. Create home screen widgets

Happy coding! 🚀

