# Everquill Android App

Android native app for Everquill - Personal Journal & Mental Health platform.

## 🚀 Features

### ✅ Implemented (Core Features)

- **Authentication**: Login, Register with JWT token management
- **Journal Management**: Create, Edit, Delete, List journals
- **Rich Text Editor**: Format text with bold, italic, underline, headings, bullets
- **AI Analysis**: Emotion & sentiment analysis for journal entries
- **Templates**: Browse and use journal templates
- **Dashboard**: Stats and charts for journal activity
- **Profile**: View and edit user profile, avatar upload
- **Premium**: Upgrade to premium plans (monthly/yearly)
- **Notifications**: View and manage notifications
- **Reviews**: Write and view app reviews

### 🔧 Technologies Used

- **Language**: Java
- **Networking**: Retrofit 2 + OkHttp
- **Image Loading**: Glide
- **Charts**: MPAndroidChart
- **Rich Text**: RichEditor-Android
- **Auth**: JWT tokens with interceptors
- **UI**: Material Design 3

## 📱 Setup Instructions

### Prerequisites

- Android Studio (latest version)
- Android SDK 24 or higher
- JDK 11 or higher
- Backend server running (see exe201_be)

### Installation

1. **Open project in Android Studio**

   ```
   File -> Open -> select exe201_mobile folder
   ```

2. **Configure Backend URL**

   Edit `app/src/main/java/com/example/everquillapp/api/ApiConfig.java`:

   ```java
   // For Android Emulator (local backend)
   public static final String BASE_URL = "http://10.0.2.2:3000/api/";

   // For physical device (replace with your IP)
   // public static final String BASE_URL = "http://192.168.1.XXX:3000/api/";

   // For production
   // public static final String BASE_URL = "https://your-backend.onrender.com/api/";
   ```

3. **Sync Gradle**

   - Android Studio will automatically prompt to sync
   - Or: File -> Sync Project with Gradle Files

4. **Run the app**
   - Connect Android device or start emulator
   - Click Run button or press Shift+F10

## 📂 Project Structure

```
app/src/main/java/com/example/everquillapp/
├── activities/          # All Activity classes
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── MainActivity (Journal List)
│   ├── JournalEditorActivity
│   ├── ProfileActivity
│   ├── DashboardActivity
│   ├── PremiumActivity
│   ├── NotificationActivity
│   ├── ReviewsActivity
│   ├── TemplateChooserActivity
│   └── SplashActivity
├── adapters/           # RecyclerView Adapters
│   ├── JournalAdapter
│   ├── TemplateAdapter
│   ├── NotificationAdapter
│   └── ReviewAdapter
├── api/               # Retrofit API layer
│   ├── ApiService
│   ├── ApiClient
│   ├── ApiConfig
│   └── AuthInterceptor
├── models/            # Data models
│   ├── User
│   ├── Journal
│   ├── Template
│   └── ApiResponse
├── dialogs/           # Custom dialogs
│   └── AIAnalysisDialog
└── utils/            # Utility classes
    └── TokenManager
```

## 🔑 Key Features Implementation

### Authentication Flow

1. SplashActivity checks if user is logged in
2. If not logged in -> LoginActivity
3. If logged in -> MainActivity (Journal List)
4. Token stored in SharedPreferences
5. AuthInterceptor auto-adds Bearer token to requests

### Journal CRUD

1. MainActivity displays list of journals (RecyclerView)
2. FAB button creates new journal
3. Click journal card to edit
4. Long press for options (Delete, AI History)
5. Rich text editor with formatting toolbar

### AI Integration

- Analyze button in JournalEditor
- Displays sentiment, emotion, keywords, summary
- Results shown in AIAnalysisDialog
- History accessible from journal list

### Premium Features

- Payment integration with backend
- Template access based on user plan
- Upgrade flow via PremiumActivity

## 🐛 Troubleshooting

### Cannot connect to backend

- **Emulator**: Use `10.0.2.2` instead of `localhost`
- **Physical Device**: Use computer's local IP (same WiFi network)
- **Check**: Backend is running on port 3000

### Build errors

- Clean project: Build -> Clean Project
- Rebuild: Build -> Rebuild Project
- Invalidate caches: File -> Invalidate Caches / Restart

### Dependencies not resolving

- Check internet connection
- Sync Gradle files
- Check `settings.gradle.kts` has jitpack.io repo

## 📝 TODO / Future Enhancements

- [ ] Implement Google Sign-In
- [ ] Add offline support with Room database
- [ ] Implement image/photo attachments
- [ ] Add push notifications
- [ ] Implement search and filters
- [ ] Add export/import journals
- [ ] Implement voice-to-text
- [ ] Add dark theme
- [ ] Implement biometric login
- [ ] Add widgets for quick journal entry

## 👥 Contributors

Team EXE201 - FPT University

## 📄 License

Copyright © 2025 Everquill

