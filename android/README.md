# KeypadCraft - Android app

A tiny WebView wrapper that runs the bundled web version of the game offline.

The workflow `.github/workflows/android.yml` builds it on GitHub and attaches `KeypadCraft.apk` to the release.
Build locally: copy everything inside `../play/` into `app/src/main/assets/`, then run `gradle assembleDebug` (JDK 17, Android SDK 34).
The APK is debug-signed: fine for installing on your own phone (allow "install unknown apps").

See ../COPYRIGHT.txt and ../LICENSE for credits and usage terms.
