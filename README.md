# B47 Cricket Score

A production-ready, offline-first Android cricket scoring and match management application built with **Jetpack Compose**, **Material 3**, **Room Database**, and **Clean Architecture**.

Designed and developed by **B47 Tech** for publication on the **Google Play Store**.

---

## 🌟 Features

* **Match Setup & Configuration**:
  - Custom team names and editable player rosters.
  - Flexible over settings (5, 10, 15, 20, 50, or custom).
  - Configurable players per team (2 to 15 players).
  - Ball type selection (Tennis / Leather).
* **Toss & Inning Management**:
  - Toss winner selection & Bat/Bowl decision.
  - Dynamic strike & bowler assignment for 1st and 2nd innings.
* **Ball-by-Ball Live Scoring Engine**:
  - One-tap scoring for 0 (Dot), 1, 2, 3, 4 (Four), 6 (Six).
  - Boundary styling and animated highlights.
  - Comprehensive extras: Wides (+ extra runs), No-Balls (+ runs off bat), Byes, Leg Byes.
  - **Free Hit Delivery Support**: Automatic Free Hit rule enforcement following No-Balls.
  - Strike rotation on odd runs and over ends.
  - Comprehensive dismissals: Bowled, Caught, Run Out (striker or non-striker), LBW, Stumped, Hit Wicket, Retired Hurt.
  - Automatic bowler change enforcement (no bowler can bowl back-to-back overs).
* **Instant Ball-by-Ball Undo**:
  - Flawlessly roll back any delivery, boundary, extra, wicket, or over transition with zero state corruption.
* **Real-time Metrics**:
  - Live Scoreboard: Total runs, wickets, overs, legal balls.
  - CRR (Current Run Rate) and RRR (Required Run Rate).
  - Target tracking, balls remaining, and runs needed in the second innings.
  - Automatic match winner and victory margin evaluation (won by runs, won by wickets, tie).
* **Dual-Innings Detailed Scorecards**:
  - Batter stats: Runs, balls faced, 4s, 6s, strike rate, dismissal text.
  - Bowler stats: Overs, maidens, runs conceded, wickets, economy rate, dot balls.
  - Fall of Wickets timeline.
  - Extras breakdown.
* **Player Career Statistics**:
  - Aggregated career records: matches, innings, runs, highest score, 50s, 100s, batting average, strike rate, wickets taken, maidens, economy, and best bowling figures.
* **Match History & Offline Persistence**:
  - Every ball is immediately persisted to the local Room database.
  - Resume any unfinished match right from where you stopped.
  - View full scorecards of past matches anytime.
* **Play Store Production-Ready**:
  - Target SDK 35 (Android 15 compliant).
  - Google Mobile Ads SDK (AdMob) integrated with test banner units and production placeholders.
  - ProGuard/R8 optimizations enabled.
  - Adaptive launcher vector icons.
  - 100% offline-first, zero login required.

---

## 🏗 Architecture & Technologies

* **Language**: Kotlin 2.0.20
* **UI Framework**: Jetpack Compose with Material 3
* **Design Pattern**: MVVM / Clean Architecture
* **Decoupled Scoring Engine**: Pure Kotlin unit-testable engine (`com.b47tech.cricketscore.core.engine.CricketEngine`)
* **Local Database**: Android Jetpack Room with Kotlin Coroutines Flow
* **Serialization**: Kotlinx Serialization JSON
* **Navigation**: Navigation Compose
* **Monetization**: Google Mobile Ads (AdMob)
* **SDK Compatibility**: `minSdk = 26`, `compileSdk = 35`, `targetSdk = 35`

---

## 📁 Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/b47tech/cricketscore/
│   ├── CricketApplication.kt
│   ├── MainActivity.kt
│   ├── core/
│   │   └── engine/
│   │       ├── CricketEngine.kt      # Standalone Pure Scoring Engine
│   │       └── MatchModels.kt        # Cricket domain models
│   ├── data/
│   │   ├── local/
│   │   │   ├── CricketDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── MatchDao.kt
│   │   │   │   └── PlayerCareerStatsDao.kt
│   │   │   └── entity/
│   │   │       ├── MatchEntity.kt
│   │   │       └── PlayerCareerStatsEntity.kt
│   │   └── repository/
│   │       ├── CricketRepository.kt
│   │       └── CricketRepositoryImpl.kt
│   └── ui/
│       ├── components/              # ScoreBoard, Batsmen, Bowler, Timeline, Keypad, Dialogs
│       ├── navigation/              # NavHost, Screen sealed class
│       ├── screens/
│       │   ├── home/                # Dashboard & active match card
│       │   ├── matchsetup/          # Match configuration & player roster editor
│       │   ├── toss/                # Toss winner & decision
│       │   ├── openers/             # Striker, non-striker & opening bowler selector
│       │   ├── scoring/             # Full live scoring UI
│       │   ├── scorecard/           # Tabbed dual-innings scorecard
│       │   ├── history/             # Past matches list & management
│       │   ├── stats/               # Career batting & bowling leaderboards
│       │   └── settings/            # App identity & privacy
│       └── theme/                   # Cricket stadium color palette & typography
└── res/
    ├── drawable/                    # Vector adaptive icon foreground & background
    ├── mipmap-anydpi-v26/           # Adaptive launcher icons
    └── values/                      # strings.xml, colors.xml, themes.xml
```

---

## 🚀 Building & Releasing

### Debug Build
```powershell
.\gradlew.bat assembleDebug
```

### Run Unit Tests
```powershell
.\gradlew.bat testDebugUnitTest
```

### Production Release Bundle (AAB)
```powershell
.\gradlew.bat bundleRelease
```

---

## 📄 Documentation
* [Google Play Store Release Guide](file:///D:/Learning/Android%20application%20development/cricketscore/PLAY_STORE_RELEASE_GUIDE.md)
* [Play Console Data Safety Guide](file:///D:/Learning/Android%20application%20development/cricketscore/PLAY_CONSOLE_DATA_SAFETY.md)
* [Privacy Policy](file:///D:/Learning/Android%20application%20development/cricketscore/PRIVACY_POLICY.md)
"# cricketscore" 
