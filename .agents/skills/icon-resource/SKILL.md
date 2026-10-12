---
name: icon-resource
description: Convert SVG designs to Android Vector Drawable XML resources and generate complete Android launcher icons (adaptive, monochrome/themed, and legacy mipmaps). Use when converting SVGs to Android XML drawables, updating or generating Android app launcher icons, or handling icon resources.
---

# Android Icon & Resource System

Guides and deterministic scripts for converting SVG vectors to Android Vector Drawable XMLs and generating complete Android launcher icon suites.

## Quick Start

### 1. Convert SVG to Android Vector Drawable XML
Use the conversion script or manual rules in [CONVERSION.md](CONVERSION.md):
```bash
python3 .agents/skills/icon-resource/scripts/svg_to_vector_drawable.py \
  --input path/to/icon.svg \
  --output app/androidApp/src/main/res/drawable/ic_custom.xml
```

### 2. Generate Complete Android Launcher Icons
Use the launcher generator script or follow [LAUNCHER.md](LAUNCHER.md):
```bash
python3 .agents/skills/icon-resource/scripts/generate_launcher_icons.py \
  --svg path/to/app_icon.svg \
  --res-dir app/androidApp/src/main/res
```
This produces:
- `res/drawable/ic_launcher_background.xml` (Full bleed 108dp vector)
- `res/drawable/ic_launcher_foreground.xml` (Centered 108dp glyph vector)
- `res/drawable/ic_launcher_monochrome.xml` (API 33+ Themed icon with cutout)
- `res/mipmap-anydpi-v26/ic_launcher.xml` & `ic_launcher_round.xml`
- `res/mipmap-{m,h,xh,xxh,xxxh}dpi/` raster PNGs (Square and Round)

---

## Capabilities & Navigation

- **[CONVERSION.md](CONVERSION.md)**:
  - SVG to Android Vector Drawable XML mapping rules (`<path>`, `<rect>`, `<circle>`, `<g>`).
  - Viewport, scale, and translation math.
  - Converting multi-tone paths to single-tone evenOdd cutouts.
- **[LAUNCHER.md](LAUNCHER.md)**:
  - Adaptive Icon architecture (108dp canvas, 72dp mask, 66dp safe zone).
  - Material You Themed/Monochrome icons.
  - Legacy raster mipmaps keylines and resolutions.
  - Resource qualifier shadowing rules (`drawable-v24` vs `drawable`).
- **[scripts/](scripts/)**:
  - `svg_to_vector_drawable.py`: General SVG to VectorDrawable XML converter.
  - `generate_launcher_icons.py`: End-to-end Android launcher asset generator.
