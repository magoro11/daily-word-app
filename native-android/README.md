# Daily Word — native Android

Open `native-android` in Android Studio (JDK 17+), sync Gradle, and run `app` on an Android 8+ (API 26) device.

## What's inside
- Kotlin + Jetpack Compose + Material 3 UI
- Room database with 50+ public-domain KJV verses and 30+ motivational quotes
- Two independent rotation engines: Bible (AlarmManager, exact alarm) and Motivation (AlarmManager with configurable 1/5/10/15/30 min intervals)
- Foreground service with low-priority persistent notification
- Home screen widget (4x1 / 4x2 resizable)
- Optional floating overlay bubble (Display over other apps)
- DataStore-backed settings with light/dark/system theme + dynamic color
- First-launch onboarding wizard
- History and Favorites screens with search, copy, and share

## Required permissions
Grant these from the in-app Settings screen or Android system settings:
- **POST_NOTIFICATIONS** (Android 13+)
- **SCHEDULE_EXACT_ALARM** (for on-time Bible rotation)
- **SYSTEM_ALERT_WINDOW** (for overlay bubble, optional)
- **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS** (optional, for reliability)

## Build
1. Open `native-android` in Android Studio.
2. Let Gradle sync (it will download the wrapper if missing).
3. Connect a device or start an emulator (API 26+).
4. Click Run.

## Project structure
- `data/` — Room entities, DAOs, DataStore settings
- `domain/` — RotationEngine with timestamp-based independent timers
- `service/` — DailyWordService, RotationReceiver, BootReceiver, CacheRefreshWorker
- `widget/` — DailyWordWidget (RemoteViews)
- `overlay/` — OverlayService + OverlayActivity
- `ui/` — Compose screens (Today, Favorites, History, Settings, Onboarding)
- `di/` — Hilt modules
