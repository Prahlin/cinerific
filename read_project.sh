#!/bin/bash

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_FILE="$PROJECT_DIR/cinerific_PROJECT_SNAPSHOT.txt"

echo "Generating Cinerific project snapshot..."
echo "Project directory: $PROJECT_DIR"
echo "Snapshot: $OUTPUT_FILE"
echo ""

write_header() {
  cat > "$OUTPUT_FILE" <<EOF
Cinerific Android PROJECT SNAPSHOT
Generated: $(date)
Project directory: $PROJECT_DIR

IMPORTANT INSTRUCTIONS FOR CHATGPT & CODEX:
1. Read this entire snapshot before giving code edits.
2. Preserve all assets, spacing, alignment, fonts, images, layout, animations, and relative positioning unless specifically asked to change them.
3. Do not remove unrelated code.
4. Do not rewrite large sections unless requested.
5. When giving code changes, include exact line/row numbers whenever possible.
6. Print full scripts when requested.
7. Treat this project structure as the source of truth.
8. If you don't understand something, ask for clarification instead of making assumptions.
9. Warn me, or at least caution me, if I ask for something that would break the project or cause it to not work as intended.


============================================================
SNAPSHOT IGNORE POLICY
============================================================

This file is rebuilt from the current Cinerific workspace every time ./read_project.sh runs.
Ignored paths are omitted from the project tree and file dumps unless they appear inside an included text file such as .gitignore or read_project.sh.

The snapshot intentionally ignores:
- .git
- .gradle
- .idea
- app/build
- build
- .kotlin
- .tmp
- .cache
- .DS_Store
- local.properties
- *_PROJECT_SNAPSHOT.txt
- *.jks
- *.keystore

Binary image and screenshot files are inventoried by path, size, and modified time only. Raw binary bytes are not printed.


============================================================
PROJECT FILE TREE
============================================================

EOF
}

print_file() {
  local file="$1"

  if [ -f "$file" ]; then
    {
      echo ""
      echo ""
      echo "============================================================"
      echo "FILE: ${file#$PROJECT_DIR/}"
      echo "============================================================"
      echo ""
      nl -ba "$file"
    } >> "$OUTPUT_FILE"
  fi
}

print_binary_inventory() {
  local title="$1"
  local dir="$2"

  {
    echo ""
    echo ""
    echo "============================================================"
    echo "$title"
    echo "============================================================"
    echo ""
  } >> "$OUTPUT_FILE"

  if [ ! -d "$dir" ]; then
    echo "Directory not present: ${dir#$PROJECT_DIR/}" >> "$OUTPUT_FILE"
    return
  fi

  find "$dir" -type f \
    ! -name ".DS_Store" \
    ! -name "*_PROJECT_SNAPSHOT.txt" \
    ! -name "*.jks" \
    ! -name "*.keystore" \
    | sort | while IFS= read -r file; do
    local size
    local modified
    size="$(stat -f "%z" "$file" 2>/dev/null || stat -c "%s" "$file" 2>/dev/null || echo "unknown")"
    modified="$(stat -f "%Sm" -t "%Y-%m-%d %H:%M:%S" "$file" 2>/dev/null || stat -c "%y" "$file" 2>/dev/null || echo "unknown")"
    printf '%s | %s bytes | modified %s\n' "${file#$PROJECT_DIR/}" "$size" "$modified" >> "$OUTPUT_FILE"
  done
}

write_snapshot() {
  write_header

  find "$PROJECT_DIR" \
    -path "$PROJECT_DIR/.git" -prune -o \
    -path "$PROJECT_DIR/.gradle" -prune -o \
    -path "$PROJECT_DIR/.idea" -prune -o \
    -path "$PROJECT_DIR/app/build" -prune -o \
    -path "$PROJECT_DIR/build" -prune -o \
    -path "$PROJECT_DIR/.kotlin" -prune -o \
    -path "$PROJECT_DIR/.tmp" -prune -o \
    -path "$PROJECT_DIR/.cache" -prune -o \
    -name ".DS_Store" -prune -o \
    -name "local.properties" -prune -o \
    -name "*_PROJECT_SNAPSHOT.txt" -prune -o \
    -name "*.jks" -prune -o \
    -name "*.keystore" -prune -o \
    -print | sort >> "$OUTPUT_FILE"

  cat >> "$OUTPUT_FILE" <<EOF


============================================================
FILE CONTENTS — ANDROID SOURCE
============================================================

EOF

  for file in \
    "$PROJECT_DIR/app/src/main/AndroidManifest.xml" \
    "$PROJECT_DIR/app/src/main/java/com/prahlin/cinerific/MainActivity.kt" \
    "$PROJECT_DIR/app/src/main/java/com/prahlin/cinerific/ui/CinerificApp.kt" \
    "$PROJECT_DIR/app/src/main/java/com/prahlin/cinerific/ui/CinerificIntroView.kt" \
    "$PROJECT_DIR/app/src/main/java/com/prahlin/cinerific/ui/theme/Theme.kt" \
    "$PROJECT_DIR/app/src/main/res/values/strings.xml" \
    "$PROJECT_DIR/app/src/main/res/values/themes.xml" \
    "$PROJECT_DIR/app/src/main/res/values-night/themes.xml" \
    "$PROJECT_DIR/app/src/main/res/values-v31/themes.xml" \
    "$PROJECT_DIR/app/src/main/res/drawable/splash_transparent.xml" \
    "$PROJECT_DIR/app/src/main/res/drawable/ic_launcher_background.xml" \
    "$PROJECT_DIR/app/src/main/res/drawable/ic_launcher_foreground.xml" \
    "$PROJECT_DIR/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" \
    "$PROJECT_DIR/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml"
  do
    print_file "$file"
  done

  cat >> "$OUTPUT_FILE" <<EOF


============================================================
FILE CONTENTS — ROOT / TOOLING
============================================================

EOF

  for file in \
    "$PROJECT_DIR/.gitignore" \
    "$PROJECT_DIR/.github/copilot-instructions.md" \
    "$PROJECT_DIR/.vscode/mcp.json" \
    "$PROJECT_DIR/.vscode/tasks.json" \
    "$PROJECT_DIR/README.md" \
    "$PROJECT_DIR/build.gradle.kts" \
    "$PROJECT_DIR/app/build.gradle.kts" \
    "$PROJECT_DIR/settings.gradle.kts" \
    "$PROJECT_DIR/gradle.properties" \
    "$PROJECT_DIR/gradle/wrapper/gradle-wrapper.properties" \
    "$PROJECT_DIR/read_project.sh"
  do
    print_file "$file"
  done

  print_binary_inventory "BINARY / IMAGE ASSET INVENTORY — DRAWABLE-NODPI" "$PROJECT_DIR/app/src/main/res/drawable-nodpi"
  print_binary_inventory "BINARY / SCREENSHOT INVENTORY" "$PROJECT_DIR/screenshots"

  cat >> "$OUTPUT_FILE" <<EOF


============================================================
NOT INCLUDED IN SNAPSHOT
============================================================

The Cinerific snapshot intentionally excludes generated/cache/runtime/private directories:
- .git
- .gradle
- .idea
- app/build
- build
- .kotlin
- .tmp
- .cache
- .DS_Store
- local.properties
- *_PROJECT_SNAPSHOT.txt
- *.jks
- *.keystore

The snapshot lists binary assets and screenshots by path, size, and modified time, but does not print raw PNG/JPG/PDF bytes.

EOF
}

write_snapshot

echo "Done."
echo ""
echo "Snapshot created here:"
echo "$OUTPUT_FILE"
echo ""
echo "Open it with:"
echo "open \"$OUTPUT_FILE\""
