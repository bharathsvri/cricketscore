# Google Play Store Production Release Guide for B47 Cricket Score

**App Name:** B47 Cricket Score  
**Package Name:** `com.b47tech.cricketscore`  
**Developer:** B47 Tech  

---

## 1. Prerequisites Checklist
- [x] Application ID set to `com.b47tech.cricketscore`
- [x] Target SDK set to 35 (Android 15 requirement)
- [x] Version code: 1, Version name: "1.0.0"
- [x] Vector Adaptive Icons configured (`ic_launcher.xml`, `ic_launcher_round.xml`)
- [x] ProGuard rules enabled (`app/proguard-rules.pro`)
- [x] Google Mobile Ads SDK configured with compliant metadata
- [x] Privacy Policy and Data Safety documentation prepared

---

## 2. Generate a Production Signing Keystore

Run the following command in PowerShell / Terminal:

```powershell
keytool -genkey -v -keystore release-keystore.jks -alias b47cricket -keyalg RSA -keysize 2048 -validity 10000
```

Store `release-keystore.jks` in the project root directory or a secure vault. **Never lose this keystore or password!**

---

## 3. Configure `keystore.properties`

Create a file named `keystore.properties` in the project root directory with:

```properties
storeFile=release-keystore.jks
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=b47cricket
keyPassword=YOUR_KEY_PASSWORD
```

---

## 4. Build the Signed Android App Bundle (AAB)

Run the Gradle bundle command:

```powershell
.\gradlew.bat bundleRelease
```

The output file will be generated at:
```
app/build/outputs/bundle/release/app-release.aab
```

This `.aab` file is the exact file to upload to the **Google Play Console** under **Production > Releases > Create new release**.

---

## 5. Google Play Console Setup Steps

1. **Create App**:
   - App Name: **B47 Cricket Score**
   - Default language: English (United States)
   - App or game: App
   - Free or paid: Free
2. **Set up Store Listing**:
   - **Short Description**: Fast, offline ball-by-ball cricket scoring with complete undo & scorecards.
   - **Full Description**: 
     > B47 Cricket Score is a modern, intuitive, and offline-first cricket scoring app built for club, street, and tournament cricket matches.
     > 
     > **Key Features:**
     > - **Live Scoring Pad:** Quick-tap runs (0, 1, 2, 3, 4, 6) with boundary highlights.
     > - **Complete Undo:** Made a mistake? Undo any ball instantly without breaking the state.
     > - **Extras & Free Hit:** Support for Wides, No-Balls, Byes, Leg Byes, and automatic Free Hit deliveries.
     > - **Comprehensive Dismissals:** Bowled, Caught, Run Out (striker/non-striker), LBW, Stumped, and Hit Wicket.
     > - **Dual-Innings Scorecard:** Full batting and bowling tables, strike rates, economy, maidens, extras, and Fall of Wickets.
     > - **Player Career Statistics:** Track aggregate runs, high scores, batting averages, wickets, and bowling averages.
     > - **Offline-First:** No login, no sign-up, no internet required to score matches.
     > 
     > Developed by B47 Tech.
3. **Store Listing Assets**:
   - App Icon: 512 x 512 px PNG
   - Feature Graphic: 1024 x 500 px JPG/PNG
   - Phone Screenshots: Minimum 2 (1080 x 1920 px or 1080 x 2400 px)
4. **App Content Questionnaire**:
   - **Privacy Policy URL**: Host `PRIVACY_POLICY.md` (e.g. GitHub Pages or website) and paste the link.
   - **Ads**: Select "Yes, my app contains ads".
   - **App Access**: All functionality is available without special access.
   - **Content Rating**: Complete the IARC questionnaire (typically rated Everyone / 3+).
   - **Target Audience**: Ages 13 and up (or general audience).
   - **Data Safety**: Refer to `PLAY_CONSOLE_DATA_SAFETY.md`.
