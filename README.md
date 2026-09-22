# Cinerific

Local Android project for the `prahlin/cinerific` GitHub repo.

This is a first Kotlin / Jetpack Compose scaffold based on the Figma intro and sign-in frames. The current app opens as a tablet-friendly full-screen frame preview with:

- Intro frame 1
- Animated intro frame 2
- Promo/logo intro frame 3
- Sign-in/loading frame 4

## Stack

- Kotlin
- Jetpack Compose Material 3
- Android Gradle Plugin 8.5.x
- Gradle 8.7

## Run it

Open the project in Android Studio and run `app`, or use the Gradle wrapper:

```bash
./gradlew :app:assembleDebug
```

The project pins Gradle to Android Studio's bundled JBR in `gradle.properties`, so command-line builds do not accidentally use an unsupported system JDK.

From VS Code, run the default build task `Android: assemble debug`. There are also tasks for `Android: install debug` and `Android: launch Cinerific` when an emulator or device is connected.

### Standard tablet emulator

Use the guarded launcher for the persistent low-load QA profile:

```bash
./start_cinerific_android_standard.sh
```

The launcher requests and verifies `1280x800` at `160 dpi`, two guest CPU cores,
host GPU acceleration, and the repository-owned rounded tablet bezel before
opening Cinerific. It refuses to continue if the running AVD does not match.
Emulator logs are written to
`~/Library/Logs/Cinerific/standard-emulator.log`.

## Notes for the first Kotlin pass

- The layout is intentionally simple and adaptive for tablets.
- The current visuals are Compose approximations of the Figma frames and are meant to be replaced with production assets screen by screen.
- The local project folder is the same path as the GitHub remote: `prahlin/cinerific`.
