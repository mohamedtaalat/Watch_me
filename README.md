# 🎬 WatchMe - Android Movie Discovery & Tracking App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.13.2-blue.svg?style=flat&logo=android)](https://developer.android.com/studio/releases/gradle-plugin)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-green.svg?style=flat&logo=android)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen.svg?style=flat&logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-orange.svg?style=flat)]()
[![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-FFCA28.svg?style=flat&logo=firebase)](https://firebase.google.com)
[![Room](https://img.shields.io/badge/Database-Room%202.6.1-4285F4.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Retrofit](https://img.shields.io/badge/Networking-Retrofit%202.11.0-brightgreen.svg?style=flat)](https://square.github.io/retrofit/)

**WatchMe** is a modern, native Android application built in Kotlin that enables movie enthusiasts to search, explore, and track movies, TV shows, and series in real time. Powered by the **OMDb REST API**, **Firebase (Authentication & Cloud Firestore)**, and **Room Database**, the app delivers a fluid, responsive, and resilient offline-capable experience following Google's official Android Architecture Guidelines.

---

## 📑 Table of Contents

- [✨ Key Features](#-key-features)
- [🏛️ App Architecture](#️-app-architecture)
- [🛠️ Tech Stack & Libraries](#️-tech-stack--libraries)
- [📂 Project Structure](#-project-structure)
- [📋 Prerequisites](#-prerequisites)
- [🚀 Installation & Setup](#-installation--setup)
  - [1. Clone Repository](#1-clone-the-repository)
  - [2. OMDb API Key Setup](#2-configure-omdb-api-key)
  - [3. Firebase Configuration](#3-configure-firebase-google-services)
  - [4. Build & Sync Gradle](#4-sync-project-with-gradle-files)
- [📱 How to Run the App](#-how-to-run-the-app)
  - [Running from Android Studio](#option-a-via-android-studio-recommended)
  - [Running via Command Line (Gradle)](#option-b-via-command-line-gradlew)
- [🧪 Running Tests](#-running-tests)
- [📊 Database & API Design](#-database--api-design)
  - [Local Cache (Room Database)](#local-cache-room-database)
  - [Cloud Storage (Firebase Firestore)](#cloud-storage-firebase-firestore)
  - [OMDb API Integration](#omdb-api-endpoints)
- [🧭 Navigation & Flow](#-navigation--flow)
- [🔧 Troubleshooting & FAQ](#-troubleshooting--faq)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## ✨ Key Features

### 🔐 1. Authentication & User Management (Firebase Auth)
- **User Registration**: Create a new account with email and password validation.
- **Secure Sign In**: Real-time credential authentication via Firebase Authentication.
- **Forgot / Reset Password**: Automated password reset email dispatch with inline validation and confirmation.
- **Session Persistence**: Remembers active user sessions; automatically maintains login state.
- **Safe Sign Out**: One-tap sign out from the bottom navigation with backstack clearance.

### 🔍 2. Movie Search & Exploration (OMDb API)
- **Real-Time Movie Search**: Instant keyword querying for movies, TV series, and media titles.
- **Rich Results List**: RecyclerView with `ListAdapter` and `DiffUtil` animations showcasing poster thumbnails, titles, media type, and release years.
- **Image Caching & Error Handling**: Fast, asynchronous image loading with crossfade animations using Coil, including smart fallbacks for missing or "N/A" posters.

### 🎞️ 3. Comprehensive Movie Details
- **Detailed Overview**: High-resolution movie posters, title, genre tags, and metadata (year, MPAA rating, runtime).
- **IMDb Ratings & Engagement**: Formatted IMDb score along with total vote counts.
- **Plot & Storyline**: Full movie synopsis.
- **Deep Production Metadata**: Dynamic rows for Director, Writers, Cast/Actors, Release Date, Language, Country, Awards, Metascore, Critic Ratings (Rotten Tomatoes, Metacritic), and Season counts for TV series.

### 🕒 4. Dual-Layer History & Caching
- **Cloud Search History (Firestore)**: User searches are synced securely to Firebase Cloud Firestore under `users/{uid}/search_history`. Users can view recent searches, tap to re-execute, or delete individual search queries.
- **Local Movie Cache (Room Database)**: Inspected movies are cached locally in SQLite using Room. Offline access and instant repeat lookups without consuming unnecessary network bandwidth.

### 🎨 5. Modern UI & Edge-to-Edge Design
- **Animated Splash Screen**: Custom enter animations (logo bounce, typography slide-up, progress fade-in) before transitioning to the main experience.
- **Jetpack Navigation**: Single-Activity architecture managed by Android Navigation Graph.
- **Dynamic Bottom Navigation**: Context-aware bottom bar that adapts visibility and actions depending on authentication state.
- **Edge-to-Edge Compatibility**: Seamless layout styling supporting modern Android window insets and display cutouts.

---

## 🏛️ App Architecture

The project adheres to the **MVVM (Model-View-ViewModel)** architectural pattern coupled with the **Repository Pattern** and **Unidirectional Data Flow (UDF)**:

```
┌────────────────────────────────────────────────────────┐
│                   UI / View Layer                      │
│        Fragments (HomePage, MovieDetails, Auth)        │
│                & Jetpack ViewBinding                   │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow / Flow)
┌───────────────────────────┴────────────────────────────┐
│                    ViewModel Layer                     │
│  AuthViewModel | HomeViewModel | MovieHistoryViewModel │
└───────────────────────────▲────────────────────────────┘
                            │ (Coroutines / Suspend)
┌───────────────────────────┴────────────────────────────┐
│                   Repository Layer                     │
│  AuthRepository | SearchHistoryRepo | MovieHistoryRepo │
└─────────────▲─────────────────────────────▲────────────┘
              │                             │
┌─────────────┴─────────────┐ ┌─────────────┴────────────┐
│    Remote Data Sources    │ │    Local Data Source     │
│  - OMDb REST API (Retrofit)│ │  - Room DB (SQLite)      │
│  - Firebase Auth          │ │  - Entity & DAO          │
│  - Cloud Firestore        │ │                          │
└───────────────────────────┘ └──────────────────────────┘
```

- **View Layer**: Observes ViewModel `StateFlow` within lifecycle-aware scopes (`repeatOnLifecycle`) and renders reactive UI updates.
- **ViewModel Layer**: Manages UI state, handles user intents, launches coroutines on `viewModelScope`, and handles network/database exceptions.
- **Repository Layer**: Acts as the single source of truth, mediating between remote APIs (Retrofit / Firebase) and local persistence (Room).
- **Data Layer**: Clean models, Room entities, DAOs, and Retrofit HTTP interfaces.

---

## 🛠️ Tech Stack & Libraries

| Category | Technology / Library | Version | Description |
|:---|:---|:---|:---|
| **Language** | [Kotlin](https://kotlinlang.org/) | `2.0.21` | Modern, expressive language for Android |
| **Android Build** | [AGP (Android Gradle Plugin)](https://developer.android.com) | `8.13.2` | Gradle plugin with Kotlin DSL & KSP |
| **Compiler / Symbol Processing** | [KSP](https://github.com/google/ksp) | `2.0.21-1.0.28` | Kotlin Symbol Processing for Room |
| **Architecture Components** | [Lifecycle ViewModel & Coroutines](https://developer.android.com/topic/libraries/architecture/viewmodel) | `2.8.7` | MVVM state management with `viewModelScope` |
| **Navigation** | [Jetpack Navigation](https://developer.android.com/guide/navigation) | `2.10.2` | Single-Activity navigation component |
| **Database** | [Android Room](https://developer.android.com/training/data-storage/room) | `2.6.1` | SQLite abstraction with Flow support |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) | `2.11.0` | Type-safe HTTP client for Android & Kotlin |
| **JSON Serialization** | [Gson Converter](https://github.com/google/gson) | `2.11.0` | JSON parsing and mapping |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) | `2.7.0` | Coroutine-based image loading & caching |
| **Authentication** | [Firebase Auth](https://firebase.google.com/docs/auth) | `BOM 33.7.0` | Secure email/password authentication |
| **Cloud Storage** | [Cloud Firestore](https://firebase.google.com/docs/firestore) | `BOM 33.7.0` | Real-time NoSQL cloud database for search history |
| **UI Components** | [Material Design 3](https://m3.material.io/) | `1.10.0` | Modern UI styling, Material Buttons, TextInputs |
| **View Binding** | Android ViewBinding | Built-in | Null-safe, compile-time view references |
| **Unit Testing** | [JUnit 4](https://junit.org/junit4/) | `4.13.2` | Unit testing framework |

---

## 📂 Project Structure

```
Watchme/
├── app/
│   ├── google-services.json                  # Firebase configuration file
│   ├── build.gradle.kts                      # Module-level Gradle configuration & dependencies
│   ├── proguard-rules.pro                    # R8 / ProGuard optimization rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml           # App manifest, permissions & activities
│       │   ├── java/com/example/watchme/
│       │   │   ├── accessusers/              # Authentication Feature
│       │   │   │   ├── Login.kt              # Sign in fragment with validation
│       │   │   │   ├── Signup.kt             # User registration fragment
│       │   │   │   └── ForgetPassword.kt     # Password reset dispatch fragment
│       │   │   ├── api/                      # Networking Layer
│       │   │   │   ├── OmdbApi.kt            # Retrofit endpoint declarations
│       │   │   │   └── RetrofitClient.kt     # Retrofit singleton instance
│       │   │   ├── data/                     # Data & Repository Layer
│       │   │   │   ├── AuthRepository.kt     # Firebase Auth data operations
│       │   │   │   ├── MovieHistoryRepository.kt  # Room cache repository
│       │   │   │   ├── SearchHistoryRepository.kt # Firestore history repository
│       │   │   │   └── local/                # Room Persistence
│       │   │   │       ├── AppDatabase.kt    # Room database declaration
│       │   │   │       ├── dao/
│       │   │   │       │   └── MovieHistoryDao.kt     # SQLite queries & mutations
│       │   │   │       └── entity/
│       │   │   │           └── MovieHistoryEntity.kt  # Cached movie table schema
│       │   │   ├── home/                     # Home & Movie Details Feature
│       │   │   │   ├── HomePage.kt           # Search screen & search history list
│       │   │   │   ├── MovieDetailsFragment.kt # Comprehensive movie details viewer
│       │   │   │   ├── MovieResultAdapter.kt # RecyclerView adapter for search results
│       │   │   │   └── SearchHistoryAdapter.kt# RecyclerView adapter for recent queries
│       │   │   ├── model/                    # Data Transfer Objects & States
│       │   │   │   ├── AuthResult.kt         # Sealed class for auth outcomes
│       │   │   │   ├── OmdbMovieResponse.kt  # OMDb detailed movie schema & mapper
│       │   │   │   ├── OmdbSearchResponse.kt # OMDb search response & items
│       │   │   │   └── SearchHistoryItem.kt  # History item model
│       │   │   ├── utils/                    # Helper Extensions
│       │   │   │   └── ImageExtensions.kt    # Coil ImageView extension functions
│       │   │   ├── viewmodel/                # State & Presentation Logic
│       │   │   │   ├── AuthViewModel.kt      # ViewModel for login/signup/reset
│       │   │   │   ├── HomeViewModel.kt      # ViewModel for search & history flow
│       │   │   │   ├── MovieHistoryViewModel.kt  # ViewModel for Room movie cache
│       │   │   │   └── MovieSearchViewModel.kt   # ViewModel for single movie query
│       │   │   ├── MainActivity.kt           # Main host Activity with Bottom Navigation
│       │   │   └── SplashScreen.kt           # Launch activity with animations
│       │   └── res/                          # Android Resources
│       │       ├── anim/                     # Splash screen entrance animations
│       │       ├── drawable/                 # Vector icons & UI backgrounds
│       │       ├── layout/                   # XML UI layouts with ViewBinding
│       │       ├── navigation/app_nav.xml    # Jetpack Navigation graph
│       │       └── values/                   # Colors, strings, themes (Day/Night)
│       └── test/                             # Unit Tests
│           └── java/com/example/watchme/
│               ├── ExampleUnitTest.kt        # Basic test verification
│               └── OmdbMappingTest.kt        # Data mapping & entity sanitization tests
├── gradle/
│   ├── libs.versions.toml                    # Centralized Version Catalog
│   └── wrapper/                              # Gradle Wrapper binary & config
├── build.gradle.kts                          # Root project Gradle configuration
├── settings.gradle.kts                       # Repository & project includes
├── local.properties                          # Local SDK & private API key configuration
└── README.md                                 # Comprehensive documentation
```

---

## 📋 Prerequisites

Before setting up the project, ensure your workstation meets the following requirements:

- **Operating System**: Windows 10/11, macOS, or Linux
- **IDE**: [Android Studio](https://developer.android.com/studio) (Koala, Ladybug, or newer recommended)
- **Java Development Kit (JDK)**: JDK 11 or JDK 17 (Java 11 target compatibility)
- **Android SDK**:
  - `compileSdk`: **36**
  - `minSdk`: **24** (Android 7.0 Nougat or newer)
  - `targetSdk`: **36**
- **Hardware / Device**: An Android Virtual Device (AVD) running API 24+ or an Android physical device with USB debugging enabled.

---

## 🚀 Installation & Setup

Follow these step-by-step instructions to get a local development environment running:

### 1. Clone the Repository

Clone the project from GitHub using Git:

```bash
git clone https://github.com/mohamedtaalat/Watch_me.git
cd Watch_me
```

### 2. Configure OMDb API Key

The app searches and fetches movie metadata using the [OMDb API](https://www.omdbapi.com/).

1. Obtain a free API key from [OMDb API Key Generator](https://www.omdbapi.com/apikey.aspx).
2. Open or create the `local.properties` file in the root directory of the project:
   ```properties
   ## Location of the Android SDK
   sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk

   ## OMDb API Key
   OMDB_API_KEY=your_actual_api_key_here
   ```
3. The build system automatically reads `OMDB_API_KEY` from `local.properties` and injects it into `BuildConfig.OMDB_API_KEY` during compilation.

> [!NOTE]
> For convenience during initial testing, a fallback default API key is also configured in `OmdbApi.kt`. Supplying your personal key in `local.properties` is recommended to prevent exceeding public rate limits.

### 3. Configure Firebase (Google Services)

The application uses **Firebase Authentication** and **Cloud Firestore**.

1. An initial `google-services.json` file is included under `app/google-services.json`.
2. If you are linking your own Firebase project:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project named **WatchMe**.
   - Register an Android App with package name: `com.example.watchme`.
   - Download the generated `google-services.json` file and place it in the `app/` directory:
     ```
     Watchme/app/google-services.json
     ```
   - In the Firebase Console, enable:
     - **Authentication**: Enable the **Email/Password** sign-in provider.
     - **Cloud Firestore**: Create a Firestore Database in test mode or with authenticated access rules:
       ```javascript
       rules_version = '2';
       service cloud.firestore {
         match /databases/{database}/documents {
           match /users/{userId}/search_history/{document=**} {
             allow read, write: if request.auth != null && request.auth.uid == userId;
           }
         }
       }
       ```

### 4. Sync Project with Gradle Files

1. Open Android Studio and select **Open**, then select the `Watchme` folder.
2. Android Studio will automatically invoke the Gradle sync process.
3. If prompted to install required SDK Build Tools or platforms (API 36), accept and install them through the SDK Manager.
4. Alternatively, execute a build check via terminal:
   ```bash
   # On Windows
   .\gradlew assembleDebug

   # On macOS / Linux
   ./gradlew assembleDebug
   ```

---

## 📱 How to Run the App

### Option A: Via Android Studio (Recommended)

1. Launch Android Studio and open the project.
2. Ensure an emulator is running or connect your physical Android device via USB (with **USB Debugging** enabled in Developer Options).
3. Select the `app` run configuration in the top toolbar.
4. Click the green **Run** button (or press `Shift + F10`).
5. The app will install and open with the animated splash screen.

### Option B: Via Command Line (Gradlew)

You can build and deploy the APK directly from PowerShell or Bash:

#### 1. Assemble Debug APK:
```bash
# Windows
.\gradlew assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
*The compiled APK will be generated at:*  
`app/build/outputs/apk/debug/app-debug.apk`

#### 2. Install and Run on Connected Device:
```bash
# Windows
.\gradlew installDebug

# macOS / Linux
./gradlew installDebug
```

#### 3. Launch the App via ADB:
```bash
adb shell am start -n com.example.watchme/.SplashScreen
```

---

## 🧪 Running Tests

The project includes unit tests for verifying data models, sanitization routines, and Room entity mapping.

### Run Unit Tests:
```bash
# Windows
.\gradlew test

# macOS / Linux
./gradlew test
```

### View Test Reports:
After test completion, open the HTML test report in your browser:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 📊 Database & API Design

### Local Cache (Room Database)

- **Database Name**: `watch_me_database`
- **Table Name**: `movie_history`
- **Entity**: `MovieHistoryEntity`

| Column | Type | Description |
|:---|:---|:---|
| `imdbID` *(Primary Key)* | `TEXT` | Unique IMDb identifier (e.g., `tt0409591`) |
| `title` *(Indexed)* | `TEXT` | Title of the movie or TV show |
| `year` | `TEXT` | Release year or active years |
| `type` | `TEXT` | Type of media (`movie`, `series`, `episode`) |
| `genre` | `TEXT` (nullable) | Genres list |
| `plot` | `TEXT` (nullable) | Story overview |
| `posterUrl` | `TEXT` (nullable) | Remote poster image URL (sanitized from `"N/A"`) |
| `imdbRating` | `TEXT` (nullable) | IMDb rating score (e.g., `8.4`) |
| `timestamp` | `INTEGER` | Unix epoch millisecond of when the item was accessed |

### Cloud Storage (Firebase Firestore)

User search queries are synchronized in real-time under each user's authenticated path:
- **Collection Path**: `users/{userId}/search_history/{documentId}`
- **Document ID**: Deterministic SHA-256 hash of the lowercased search query string.
- **Fields**:
  - `query` (`string`): The clean search query.
  - `timestamp` (`number`): Timestamp in milliseconds.

### OMDb API Endpoints

The app communicates with `https://www.omdbapi.com/` using Retrofit:

1. **Search Movies**:
   `GET /?s={query}&apikey={apiKey}`
   - Returns a list of search matching items (`OmdbSearchResponse`).
2. **Movie Details by IMDb ID**:
   `GET /?i={imdbId}&apikey={apiKey}`
   - Returns comprehensive metadata for the selected movie (`OmdbMovieResponse`).
3. **Movie Details by Title**:
   `GET /?t={title}&apikey={apiKey}`
   - Queries a single movie directly by exact title match.

---

## 🧭 Navigation & Flow

The user journey is orchestrated through `app/src/main/res/navigation/app_nav.xml`:

```
                    ┌─────────────────┐
                    │  SplashScreen   │
                    └────────┬────────┘
                             │ (3s delay / Animation)
                             ▼
                    ┌─────────────────┐
                    │      Login      │◄──────────┐
                    └────┬────────┬───┘           │
         Go to Sign Up   │        │ Reset Pass    │
         ┌───────────────┘        └──────────┐    │
         ▼                                   ▼    │
  ┌──────────────┐                  ┌─────────────┴─┐
  │    Signup    │─────────►        │ ForgetPassword│
  └──────────────┘ (Success/Back)   └───────────────┘
         │
         │ (Authenticated)
         ▼
  ┌─────────────────────────────────────────────────┐
  │                    HomePage                     │
  │  - Search Input Bar                             │
  │  - Cloud Search History (Firestore)             │
  │  - Search Results (OMDb API)                    │
  └────────────────────────┬────────────────────────┘
                           │ (Click movie card)
                           ▼
  ┌─────────────────────────────────────────────────┐
  │              MovieDetailsFragment               │
  │  - Poster, IMDb Rating, Runtime, Genre          │
  │  - Synopsis & Cast Details                      │
  │  - Automatic Room Caching                       │
  └─────────────────────────────────────────────────┘
```

---

## 🔧 Troubleshooting & FAQ

<details>
<summary><b>1. Gradle Build Error: SDK location not found</b></summary>

Ensure you have a `local.properties` file in the root directory specifying your SDK path:
```properties
sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
```
*(On macOS: `sdk.dir=/Users/<YourUsername>/Library/Android/sdk`)*
</details>

<details>
<summary><b>2. OMDb API returns "Movie not found!" or Rate limit exceeded</b></summary>

The free tier of the OMDb API allows up to 1,000 requests per day. If you reach this limit or receive rate limit errors, register for a new free key at [omdbapi.com/apikey.aspx](https://www.omdbapi.com/apikey.aspx) and update `OMDB_API_KEY` in `local.properties`.
</details>

<details>
<summary><b>3. Firebase Authentication: "Password reset is disabled in Firebase Console"</b></summary>

In your Firebase Console, navigate to **Build** -> **Authentication** -> **Templates** -> **Password reset** and ensure the email provider and reset templates are enabled.
</details>

<details>
<summary><b>4. Firestore permission denied error when loading search history</b></summary>

Ensure your Cloud Firestore security rules permit authenticated users to read and write their own documents under `/users/{userId}/search_history/{document=**}`.
</details>

<details>
<summary><b>5. Room Database Schema Change Exception</b></summary>

The Room database builder is configured with `.fallbackToDestructiveMigration()`. If you modify entity structures during local development, uninstalling and reinstalling the app will clear existing tables and recreate the updated schema cleanly.
</details>

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the Project.
2. Create your Feature Branch:
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. Commit your Changes:
   ```bash
   git commit -m "Add some AmazingFeature"
   ```
4. Push to the Branch:
   ```bash
   git push origin feature/AmazingFeature
   ```
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the Apache 2.0 License - feel free to use, modify, and distribute it in accordance with the project terms.
