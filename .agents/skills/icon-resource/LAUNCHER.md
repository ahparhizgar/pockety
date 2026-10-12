# Android Launcher Icon Specification & Guide

This document covers everything required to implement complete Android app launcher icon suites across all API levels (from legacy API 24 to modern Android 16).

---

## 1. Directory & File Layout

Android projects require launcher icons in both `drawable` (vector) and `mipmap` (configurations & raster) directories:

```
app/androidApp/src/main/res/
├── drawable/
│   ├── ic_launcher_background.xml      # 108dp solid/vector background
│   ├── ic_launcher_foreground.xml      # 108dp centered glyph vector
│   └── ic_launcher_monochrome.xml      # 108dp single-color vector (API 33+)
├── drawable-v24/
│   └── ic_launcher_foreground.xml      # (Important: Shadows drawable/ on API 24+)
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml                 # <adaptive-icon> declaration
│   └── ic_launcher_round.xml           # <adaptive-icon> declaration
├── mipmap-mdpi/
│   ├── ic_launcher.png                 # 48x48 squircle raster
│   └── ic_launcher_round.png           # 48x48 circular raster
├── mipmap-hdpi/
│   ├── ic_launcher.png                 # 72x72 squircle raster
│   └── ic_launcher_round.png           # 72x72 circular raster
├── mipmap-xhdpi/
│   ├── ic_launcher.png                 # 96x96 squircle raster
│   └── ic_launcher_round.png           # 96x96 circular raster
├── mipmap-xxhdpi/
│   ├── ic_launcher.png                 # 144x144 squircle raster
│   └── ic_launcher_round.png           # 144x144 circular raster
└── mipmap-xxxhdpi/
    ├── ic_launcher.png                 # 192x192 squircle raster
    └── ic_launcher_round.png           # 192x192 circular raster
```

---

## 2. Adaptive Icons (API 26+)

### 2.1 Keylines & Dimensions
- **Canvas Size**: `108dp x 108dp`.
- **Visible Region**: The system displays the inner `72dp x 72dp` (radius `36dp`), masked by the device launcher (circle, squircle, pebble, rounded rect).
- **Safe Zone**: The inner `66dp x 66dp` (radius `33dp`). The core emblem must fit inside this zone to guarantee no clipping under any vendor mask.
- **Glyph Proportion**: The foreground glyph should occupy roughly `45% - 55%` of the visible area (~`34dp - 40dp` width/height).

### 2.2 Background Layer (`ic_launcher_background.xml`)
The background must cover the full `108dp x 108dp` canvas. **Never use transparency or pre-clipped shapes in the background layer.**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#1B3022"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```

### 2.3 Foreground Layer (`ic_launcher_foreground.xml`)
Center the glyph using a translated `<group>`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:translateX="4"
        android:translateY="4">
        <!-- Glyph paths -->
    </group>
</vector>
```

> **Trap Warning**: If `drawable-v24/ic_launcher_foreground.xml` exists in the codebase, any changes only made to `drawable/ic_launcher_foreground.xml` will be ignored on API 24+. Always maintain both files or delete `drawable-v24/` if minSdk >= 24.

---

## 3. Themed / Monochrome Icons (Material You, API 33+)

In `res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
```

### In `res/drawable/ic_launcher_monochrome.xml`:
- Draw the glyph in `#000000`.
- Use `android:fillType="evenOdd"` to cut out interior accents or line details into transparent negative space.
- Do not include any background rect.

---

## 4. Legacy Raster Mipmaps (API <= 25)

Older launchers require pre-rendered PNGs with transparency in density buckets.

### Keyline Rules:
1. **Squircle / Square (`ic_launcher.png`)**:
   - Bounds occupy ~`80%` of canvas (e.g. `152x152` inside `192x192` for `xxxhdpi`).
   - Leave ~`10%` transparent margin around the edges.
2. **Circular (`ic_launcher_round.png`)**:
   - Circle occupies ~`95%` of canvas (e.g. `184x184` diameter inside `192x192` for `xxxhdpi`).

### ViewBox Keyline Technique:
If the source SVG has a 68x68 container on a 100x100 canvas:
- Render using `viewBox="6 6 88 88"`:
  - Width: $88$ units.
  - Container width: $68$ units $\rightarrow 68 / 88 \approx 77.3\%$.
  - Margin: $10$ units on each side $\rightarrow 10 / 88 \approx 11.4\%$.
- This mathematically hits the exact Google Android legacy icon keyline.

---

## 5. Build & Verification Checklist

1. **Verify Resource Merging & Compilation**:
   ```bash
   unset ANDROID_PREFS_ROOT; ./gradlew :app:androidApp:processDebugResources
   ```
2. **Check AndroidManifest.xml**:
   Ensure application tags declare both icons:
   ```xml
   android:icon="@mipmap/ic_launcher"
   android:roundIcon="@mipmap/ic_launcher_round"
   ```
3. **Verify Raster Bounds with Python**:
   ```python
   from PIL import Image
   for name in ['ic_launcher.png', 'ic_launcher_round.png']:
       im = Image.open(f'app/androidApp/src/main/res/mipmap-xxxhdpi/{name}')
       print(name, im.size, im.getbbox())
   ```
