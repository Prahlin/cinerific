[x] Verify that the copilot-instructions.md file in the .github directory is created.
[x] Clarify Project Requirements
[x] Scaffold the Project
[x] Customize the Project
[x] Install Required Extensions (no mandatory extensions; Android Studio/Gradle wrapper verified)
[x] Compile the Project
[x] Create and Run Task
[x] Launch the Project
[x] Ensure Documentation is Complete

Project notes:
- Local workspace path: `/Users/prahlin/cinerific`
- GitHub remote repo: `prahlin/cinerific`
- App type: Android tablet app
- Language/framework: Kotlin + Jetpack Compose
- Figma source is connected through the live Figma Desktop MCP endpoint on `127.0.0.1:3845`
- Gradle uses Android Studio's bundled JBR via `org.gradle.java.home` so local command-line builds avoid the unsupported system JDK 26.
- Verified debug build, install, and launch on the local `Pixel_Tablet` AVD.
