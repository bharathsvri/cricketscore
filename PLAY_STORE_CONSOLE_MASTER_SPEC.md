# Google Play Console Master Configuration & App Specification
> **Application:** B47 Cricket Score  
> **Package / Application ID:** `com.b47tech.cricketscore`  
> **Developer / Brand:** B47 Tech  
> **Target Release Window:** September 2026 (Android 16 / API Level 36 Ready)  
> **Target Audience:** 13+ (General Audience / Sports)  
> **Purpose:** Comprehensive, production-verified Google Play Console release master specification, compliance configuration, policy questionnaire answers, and architectural blueprint ready to be fed directly into ChatGPT, Claude, or engineering teams to build, configure, inspect, or publish the application.

---

## Table of Contents
1. [App Identity & Store Listing Metadata](#1-app-identity--store-listing-metadata)
2. [Technical Build Configuration & Android Toolchain](#2-technical-build-configuration--android-toolchain)
3. [Manifest Permissions & Security Hardening](#3-manifest-permissions--security-hardening)
4. [ProGuard / R8 Minification & Keep Rules](#4-proguard--r8-minification--keep-rules)
5. [Google AdMob & UMP Consent Architecture](#5-google-admob--ump-consent-architecture)
6. [Keystore Generation & Production Bundle (.AAB)](#6-keystore-generation--production-bundle-aab)
7. [Play Console App Content & Policy Declarations](#7-play-console-app-content--policy-declarations)
8. [Data Safety Form Exact Answers](#8-data-safety-form-exact-answers)
9. [Store Graphic Assets & Screenshot Specifications](#9-store-graphic-assets--screenshot-specifications)
10. [Testing Tracks & 20-Tester Policy Compliance](#10-testing-tracks--20-tester-policy-compliance)
11. [Complete Codebase & Architectural Specification](#11-complete-codebase--architectural-specification)
12. [Empirical Verification & Quality Matrix](#12-empirical-verification--quality-matrix)
13. [Prompt Instructions for ChatGPT & AI Assistants](#13-prompt-instructions-for-chatgpt--ai-assistants)

---

## 1. App Identity & Store Listing Metadata

| Field | Production Value | Character Count / Limit | Status |
| :--- | :--- | :--- | :--- |
| **App Name** | `B47 Cricket Score` | 17 / 30 chars | Verified Compliant |
| **Short Description** | `Offline-first cricket scorekeeper. Track runs, overs, wickets, and match stats.` | 79 / 80 chars | Verified Compliant |
| **Application Type** | `App` | Dropdown | Required |
| **Category** | `Sports` | Dropdown | Required |
| **Tags** | `Cricket`, `Sports`, `Sports Scoring`, `Cricket Scorer`, `Scoreboard` | Up to 5 tags | Recommended |
| **Pricing** | `Free` (No in-app purchases, no subscriptions) | Fixed | Required |
| **Developer / Brand** | `B47 Tech` | Profile field | Required |
| **Contact Email** | `privacy@b47tech.com` (or `support@b47tech.com`) | Required | Required |
| **Website URL** | `https://bharathsvri.github.io/cricketscore/` (or `https://b47tech.com`) | Optional / Recommended | Required for `app-ads.txt` |
| **Privacy Policy URL**| `https://bharathsvri.github.io/cricketscore/` | Must be a public HTTPS URL | Hosted on GitHub Pages |

### Full Store Listing Description (Copy-Paste Ready)
```text
B47 Cricket Score is an offline-first cricket scoring application designed for players, coaches, umpires, and grassroots cricket enthusiasts who need reliable, ball-by-ball scorekeeping without requiring an internet connection.

Whether you are scoring a club championship, a street gully match, a school tournament, or a weekend friendly, B47 Cricket Score delivers modern, intuitive, and accurate cricket scoring right from your pocket.

CORE FEATURES:

* FULL BALL-BY-BALL SCORING: Easily score runs, extras (wides, no-balls, byes, leg-byes), and dismissals with intuitive, responsive controls.
* OFFLINE-FIRST ENGINE: All your matches, teams, deliveries, and player records are stored securely on your local device. Score anywhere, anytime, even in remote grounds without network coverage.
* OFFICIAL CRICKET RULES: Supports standard cricket laws including Free Hit logic (with Run Out and Retired Hurt rules), automatic strike rotation, maiden over calculations, and dynamic run rate (CRR/RRR) updates.
* INSTANT UNDO & CORRECTION: Made a mistake on the field? Seamlessly undo previous deliveries and correct match events with zero data desynchronization.
* DETAILED SCORECARDS & MATCH INSIGHTS: View professional, breakdown scorecards featuring batter strike rates, bowler economy rates, boundary percentages, and fall-of-wickets timelines.
* EXPORT & BACKUP: Share PDF scorecards with teammates or export your entire match history for secure backup and device migration.
* CLEAN MATERIAL 3 INTERFACE: Built with modern Android Jetpack Compose for smooth performance, full dark mode support, and crystal-clear visibility under bright outdoor sunlight.

PRIVACY & MONETIZATION:
B47 Cricket Score does not require accounts, logins, or cloud registration. Match data remains on your device. We use Google AdMob to display non-intrusive ads, which requires internet access and respects your privacy consent choices via the Google User Messaging Platform.

Built with passion for cricket lovers worldwide by B47 Tech.
```

---

## 2. Technical Build Configuration & Android Toolchain

The project is configured in [`app/build.gradle.kts`](file:///D:/Learning/Android%20application%20development/cricketscore/app/build.gradle.kts) and adheres to 2025/2026 Google Play standards:

| Build Parameter | Value | Standard / Requirement |
| :--- | :--- | :--- |
| **Application ID** | `com.b47tech.cricketscore` | Matches package and AdMob registrations |
| **Compile SDK** | `36` | Android 16 (Vanilla Ice Cream) |
| **Target SDK** | `36` | Exceeds Google Play API 35+ requirement |
| **Min SDK** | `26` | Android 8.0 Oreo (>95% active global coverage) |
| **Version Code** | `1` | Increment by +1 for each new release |
| **Version Name** | `"1.0.0"` | SemVer release format |
| **JVM Target** | `JavaVersion.VERSION_17` | Required for modern Android Gradle Plugin & Kotlin 2.0 |
| **Kotlin Version** | `2.0.20` | Compose Compiler plugin integrated |
| **AGP Version** | `8.5.2` | Android Gradle Plugin |
| **Gradle Version** | `9.5.0` | High-performance build automation |
| **Compose BOM** | `2024.10.01` | Material 3 UI component system |
| **16 KB Page Alignment** | `p_align = 0x4000` (16,384) | Verified on all native `.so` binaries (`libandroidx.graphics.path.so`) |

---

## 3. Manifest Permissions & Security Hardening

The application manifest is hardened in [`app/src/main/AndroidManifest.xml`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/AndroidManifest.xml):

### 1. Declared Permissions
```xml
<!-- Required for Google Mobile Ads SDK (AdMob) & UMP consent -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 2. Stripped Transitive Permissions (Google Play Policy Compliance)
Google AdMob (`play-services-ads:23.6.0`) transitively includes `androidx.work:work-runtime:2.7.0`, which merges `android.permission.FOREGROUND_SERVICE`. Under Android 14+ (API 34+), undeclared Foreground Service types trigger instant Play Console rejection. The app removes this explicitly:
```xml
<uses-permission 
    android:name="android.permission.FOREGROUND_SERVICE" 
    tools:node="remove" />
```

### 3. Hardened FileProvider Configuration
To prevent directory traversal vulnerabilities and avoid requesting broad storage permissions, [`app/src/main/res/xml/file_paths.xml`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/res/xml/file_paths.xml) uses strict isolated cache subdirectories:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <cache-path name="exports" path="exports/" />
    <cache-path name="backups" path="backups/" />
</paths>
```

---

## 4. ProGuard / R8 Minification & Keep Rules

Minification and resource shrinking are enabled in release builds to reduce download footprint and prevent reverse engineering.

### Configuration in `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Hardened Rules in [`app/proguard-rules.pro`](file:///D:/Learning/Android%20application%20development/cricketscore/app/proguard-rules.pro):
```pro
# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }

# Google User Messaging Platform (UMP)
-keep class com.google.android.ump.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
```

---

## 5. Google AdMob & UMP Consent Architecture

### 1. Production AdMob Identifiers
Configured in [`AdConfig.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/core/ads/AdConfig.kt) and [`AndroidManifest.xml`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/AndroidManifest.xml):

| Parameter | Identifier |
| :--- | :--- |
| **AdMob Application ID** | `ca-app-pub-2828717457589648~1473868300` |
| **Banner Ad Unit** | `ca-app-pub-2828717457589648/3631101662` |
| **Interstitial Ad Unit** | `ca-app-pub-2828717457589648/7378774984` |
| **Rewarded Ad Unit** | `ca-app-pub-2828717457589648/4882630278` |

### 2. Automatic Debug vs. Release Ad Isolation
[`AdConfig.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/core/ads/AdConfig.kt) dynamically verifies `BuildConfig.DEBUG`:
- **Debug Builds:** Automatically uses official Google sample ad units (`ca-app-pub-3940256099942544/...`) to prevent accidental test-click policy violations.
- **Release Builds:** Serves verified production ad units.

### 3. Google User Messaging Platform (UMP) Integration
Managed via [`AdManager.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/core/ads/AdManager.kt):
- **Initialization:** Invoked in [`MainActivity.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/MainActivity.kt) via `AdManager.getInstance().gatherConsentAndInitialize(this)`.
- **Ad Request Gate:** Ad preloading (`preloadInterstitial`, `preloadRewarded`, `loadBanner`) is guarded by `canRequestAds()`.
- **In-App Revocation:** Users can modify or withdraw consent at any time via **Settings -> Privacy Choices** using `AdManager.getInstance().showPrivacyOptionsForm(activity)`.

### 4. `app-ads.txt` Deployment
Host this file at the root of your developer domain or GitHub Pages (e.g., `https://bharathsvri.github.io/cricketscore/app-ads.txt`):
```text
google.com, pub-2828717457589648, DIRECT, f08c47fec0942fa0
```

---

## 6. Keystore Generation & Production Bundle (.AAB)

### Step 1: Generate Release Keystore
```powershell
keytool -genkey -v -keystore release-keystore.jks -alias b47cricket -keyalg RSA -keysize 2048 -validity 10000
```

### Step 2: Configure `keystore.properties` (Do NOT commit to git)
```properties
storeFile=release-keystore.jks
storePassword=YOUR_SECURE_PASSWORD
keyAlias=b47cricket
keyPassword=YOUR_SECURE_PASSWORD
```

### Step 3: Build Minified Release Bundle
```powershell
# Windows
.\gradlew.bat bundleRelease

# Linux / macOS
./gradlew bundleRelease
```
The output `.aab` is generated at:
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 7. Play Console App Content & Policy Declarations

When completing **Google Play Console > App Content**, submit these exact verified answers:

### 1. Privacy Policy
- **URL:** `https://bharathsvri.github.io/cricketscore/` (or `https://bharathsvri.github.io/cricketscore/privacy-policy/`). Must be public HTTPS.

### 2. Ads Declaration
- **Does your app contain ads?**  
  👉 **Yes, my app contains ads** (Google AdMob integrated).

### 3. App Access
- **Are parts of your app restricted based on login, credentials, or membership?**  
  👉 **All functionality is available without special access** (Zero authentication, no login).

### 4. Content Ratings (IARC Questionnaire)
- **Email Address:** `privacy@b47tech.com` (or your developer email)
- **Category:** **Utility, Productivity, Communication, or Other** (or **Sports / Scorekeeping App**)
- **Questionnaire Responses:**
  - Violence: **No**
  - Sexuality / Nudity: **No**
  - Language / Profanity: **No**
  - Controlled Substance (Drugs, Alcohol, Tobacco): **No**
  - Interactive Elements (Chat, Voice, Forum): **No** (Local device only)
  - Physical Location Sharing: **No**
  - Digital Goods / In-App Purchases: **No**
- **Expected Rating Result:** **PEGI 3** / **ESRB Everyone** / **USK 0** / **IARC 3+** (Pending questionnaire submission).

### 5. Target Audience & Content
- **Target age groups:** Select **13-15**, **16-17**, and **18 and over** (**13 and older**).  
  *(Crucial: Selecting under 13 subjects the app to Google Play's Families Policy, requiring certified ad networks only and strict COPPA compliance).*
- **Could your store listing unintentionally appeal to children?**  
  👉 **No**

### 6. News Apps
- 👉 **No**, this is not a news app.

### 7. COVID-19 Contact Tracing & Status
- 👉 **My app is not a COVID-19 contact tracing or status app**.

### 8. Financial Features
- 👉 **My app doesn't provide any financial features**.

### 9. Health & Medical Apps
- 👉 **My app doesn't provide any health or medical features**.

### 10. Government Apps
- 👉 **No**, this app does not represent a government entity.

### 11. Advertising ID (AAID) Declaration
- **Does your app use an advertising ID?**  
  👉 **Yes**
- **Why does your app use an advertising ID?**  
  👉 Check **Advertising or marketing**  
  👉 Check **Analytics**  
  👉 Check **Fraud prevention, security, and compliance**

---

## 8. Data Safety Form Exact Answers

### Step 1: High-Level Declarations
| Question | Answer |
| :--- | :--- |
| Does your app collect or share any of the required user data types? | **Yes** (Due to Google AdMob SDK) |
| Is all of the user data collected by your app encrypted in transit? | **Yes** (Google AdMob uses HTTPS/TLS) |
| Do you provide a way for users to request that their data be deleted? | **Yes** (Users can reset/delete Advertising ID or clear local app storage) |

### Step 2: Specific Data Types (Google Mobile Ads SDK)
Under **Device or other IDs**:
- **Data Type:** Device or other IDs -> Advertising ID
- **Collected?** 👉 **Yes**
- **Shared?** 👉 **Yes** (Shared with third-party advertising partner: Google AdMob)
- **Processed ephemerally?** 👉 **No**
- **Is this data required or optional?** 👉 **Data collection is required** (to display ads)
- **Purposes:**
  - [x] **Advertising or marketing**
  - [x] **Analytics**
  - [x] **Fraud prevention, security, and compliance**

Under **Personal info, Financial info, Location, Photos, Health, Audio, Files**:
- **Collected?** 👉 **No** (Local-only cricket data; no backend account).

---

## 9. Store Graphic Assets & Screenshot Specifications

| Asset | Dimensions | Requirements | Placement |
| :--- | :--- | :--- | :--- |
| **App Icon** | `512 x 512 px` | 32-bit PNG with alpha, max 1024 KB | Store launcher icon |
| **Feature Graphic** | `1024 x 500 px` | JPG or 24-bit PNG (no alpha), max 15 MB | Top banner on Play Store listing |
| **Phone Screenshots** | Min 2, max 8 (`1080 x 2400 px` or `1080 x 1920 px`) | PNG or JPEG, 16:9 or 9:16 aspect ratio | Phone store listing preview |
| **7-Inch Tablet Screenshots** | `1200 x 1920 px` (Optional) | PNG or JPEG, 16:9 or 9:16 aspect ratio | Small tablet listing preview |
| **10-Inch Tablet Screenshots** | `1600 x 2560 px` (Optional) | PNG or JPEG, 16:9 or 9:16 aspect ratio | Large tablet listing preview |

### Recommended Screenshot Progression:
1. **Screenshot 1 — Scoring Pad:** Clean UI highlighting large scoring buttons (0, 1, 2, 3, 4, 6), Extras, and live over timeline.
2. **Screenshot 2 — Professional Scorecard:** Dual-innings batting table (Runs, Balls, 4s, 6s, SR) and bowling table (Overs, Maidens, Runs, Wickets, Economy).
3. **Screenshot 3 — Match Setup:** Custom teams, overs, ball type (Tennis/Leather), and toss selection.
4. **Screenshot 4 — Fall of Wickets & PDF Export:** Chronological wicket breakdown with one-tap vector PDF sharing.
5. **Screenshot 5 — Stats & Match History:** Career milestones, player averages, and JSON backup/restore.

---

## 10. Testing Tracks & 20-Tester Policy Compliance

For personal Google Play developer accounts created after **November 13, 2023**:
1. **Internal Testing Track:** Upload `app-release.aab` and verify installations internally.
2. **Closed Testing Track (Mandatory):**
   - Recruit a minimum of **20 testers**.
   - Ensure testers remain opted-in for **14 consecutive days**.
   - Gather feedback and maintain active test engagement.
3. **Production Application:** After 14 consecutive days, apply for Production release access via the Google Play Console dashboard.

---

## 11. Complete Codebase & Architectural Specification

### 1. Architectural Patterns
- **Architecture:** Clean Architecture + MVVM + Unidirectional Data Flow (UDF).
- **Presentation:** Jetpack Compose with Material 3 theming.
- **Persistence:** AndroidX Room Database with SQLite KTX.
- **Concurrency:** Kotlin Coroutines + StateFlow / SharedFlow.
- **Serialization:** Kotlinx Serialization.

### 2. Database Entities & Schemas
Located in [`app/src/main/java/com/b47tech/cricketscore/data/local/`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/data/local):
1. **`MatchEntity` (`matches` table):**
   - Primary key: `id` (Long, auto-generate)
   - Metadata: `title`, `teamAName`, `teamBName`, `totalOvers`, `playersPerTeam`, `ballType`, `tossWinner`, `electedTo`
   - State: `status` (`NOT_STARTED`, `IN_PROGRESS`, `INNINGS_BREAK`, `COMPLETED`), `currentInnings` (1 or 2), `innings1DataJson`, `innings2DataJson`, `resultSummary`
   - Timestamps: `createdAt`, `updatedAt`
2. **`PlayerCareerStatsEntity` (`player_career_stats` table):**
   - Primary key: `playerName` (String)
   - Aggregates: `matchesPlayed`, `inningsBatted`, `totalRuns`, `ballsFaced`, `highestScore`, `fours`, `sixes`, `notOuts`, `oversBowledBalls`, `maidens`, `runsConceded`, `wicketsTaken`, `catches`, `runOuts`, `stumpings`

### 3. Core Engine Mechanics ([`CricketEngine.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/core/engine/CricketEngine.kt))
- **Official Dismissal Support:** Bowled, Caught, Run Out, LBW, Stumped, Hit Wicket, Retired Hurt.
- **ICC Clause 21.19 / MCC Law 21 Free Hit Logic:** Dismissals on a Free Hit are strictly restricted to Run Out and Retired Hurt. Bowler wicket credit is prevented on Retired Hurt.
- **Team All-Out Calculation:** Evaluates `totalWicketsNow >= maxWickets || (battersAtCrease < 2 && remainingBatters == 0)`.
- **Ball-by-Ball Undo Engine:** Maintains an immutable state stack. Undo decrements balls, restores striker/non-striker positions, restores partnership tallies, and rolls back bowler figures with mathematical precision.
- **Direct State Restoration:** Provides `restoreSavedState(...)` to eliminate reflective field manipulation under R8 obfuscation.

### 4. Backup & Export Security ([`BackupManager.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/core/export/BackupManager.kt))
- **Memory Bomb Defense:** Enforces a 25 MB stream/payload safety threshold (`maxSizeBytes = 25 * 1024 * 1024`).
- **Schema Validation:** Verifies `schemaVersion <= 1` before invoking Room database deserialization.
- **Atomic Operations:** Wrapped in `database.withTransaction { ... }` in [`CricketRepositoryImpl.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/data/repository/CricketRepositoryImpl.kt).

### 5. UI Navigation Routes
- `Screen.Home` (`"home"`)
- `Screen.MatchSetup` (`"match_setup"`)
- `Screen.Toss` (`"toss"`)
- `Screen.OpeningSelection` (`"opening_selection"`)
- `Screen.Scoring` (`"scoring/{matchId}"`)
- `Screen.Scorecard` (`"scorecard/{matchId}"`)
- `Screen.MatchHistory` (`"history"`)
- `Screen.PlayerStats` (`"stats"`)
- `Screen.Settings` (`"settings"`)

---

## 12. Empirical Verification & Quality Matrix

| Test / Audit Item | Command / Tool | Result | Status |
| :--- | :--- | :--- | :--- |
| **Unit Test Suite** | `.\gradlew.bat test` | 16/16 Passed (0 failures) | Passed |
| **Release Minification (R8)** | `.\gradlew.bat bundleRelease` | BUILD SUCCESSFUL | Passed |
| **Android Lint** | `.\gradlew.bat lintRelease` | 0 Errors, 111 Warnings | Passed |
| **16 KB Page Alignment** | Custom ELF Header Parser | `p_align = 0x4000` (16,384 bytes) | 100% Compliant |
| **Merged Manifest Audit** | `bundle_manifest/.../AndroidManifest.xml` | `FOREGROUND_SERVICE` removed | Passed |
| **Reflection Audit** | `CricketRepositoryImpl.kt` | 0 reflective field calls | Passed |
| **Offline-First Copy Audit** | All listing & code resources | 0 false "100% offline" claims | Passed |

---

## 13. Prompt Instructions for ChatGPT & AI Assistants

When sharing this specification with ChatGPT or any AI assistant, copy and paste the master prompt below:

```text
I have attached the complete Google Play Console Master Specification and Technical Architecture for my Android application:

- APP NAME: B47 Cricket Score
- PACKAGE ID: com.b47tech.cricketscore
- DEVELOPER: B47 Tech
- TARGET SDK: 36 (Android 16 / 2026 Ready)
- MIN SDK: 26 (Android 8.0)
- ARCHITECTURE: Clean Architecture, Jetpack Compose, Room Database, Kotlin Coroutines, Google AdMob with UMP Consent.

This document contains verified technical configurations, exact Google Play Console Data Safety questionnaire answers, App Content policy declarations, ProGuard/R8 minification rules, AdMob ad unit IDs, and cricket scoring engine mechanics.

Please review this specification and help me with:
1. Reviewing or generating any required Android Jetpack Compose code or Room queries matching this exact architecture.
2. Formulating Google Play Store launch strategies, marketing assets, and screenshot text overlays based on Section 1 and Section 9.
3. Assisting with the 20-tester closed testing phase requirements and Google Play Console review questionnaire submissions based on Section 7 and Section 8.
4. Verifying any proposed code changes against the strict offline-first, reflection-free, and 16 KB page-aligned standards described in this document.
```
