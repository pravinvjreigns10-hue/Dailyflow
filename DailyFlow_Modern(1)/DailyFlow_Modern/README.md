# DailyFlow

Modern, offline-first Android task planner with a custom Flow Orbit home screen.

## Included
- Custom home interface and local scenic illustration
- Flow Orbit progress ring
- Add, complete and delete tasks
- Local task persistence with SharedPreferences + JSON
- Home, Tasks, Calendar, Insights and Settings tabs
- No network permission and no runtime internet dependency
- Custom DailyFlow launcher icon
- GitHub Actions workflow that builds a debug APK

## GitHub build
Upload this project to a GitHub repository. Commit to the `main` branch. The Actions workflow builds `app-debug.apk` and publishes it under **Artifacts**.

## Install
After the workflow succeeds: GitHub → Actions → Build DailyFlow APK → successful run → Artifacts → DailyFlow-debug → download ZIP → extract `app-debug.apk` → install it on Android.

Android may ask you to allow installation from the browser/file manager that opened the APK. Only install APKs you built or trust.
