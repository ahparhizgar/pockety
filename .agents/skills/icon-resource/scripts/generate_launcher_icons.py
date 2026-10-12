#!/usr/bin/env python3
"""
generate_launcher_icons.py

Generates complete Android launcher icon suites:
1. Adaptive Background vector (108dp x 108dp)
2. Adaptive Foreground vector (108dp x 108dp)
3. Monochrome/Themed vector (API 33+)
4. Adaptive icon definitions in mipmap-anydpi-v26/ (ic_launcher.xml, ic_launcher_round.xml)
5. Legacy raster mipmaps (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) for square & round

Usage:
    python3 generate_launcher_icons.py --res-dir app/androidApp/src/main/res
    python3 generate_launcher_icons.py --svg path/to/icon.svg --res-dir app/androidApp/src/main/res
"""

import argparse
import os
import shutil
import subprocess

DENSITIES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

XML_BACKGROUND = """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="{bg_color}"
        android:pathData="M0,0h108v108h-108z" />
</vector>
"""

XML_FOREGROUND = """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:translateX="4"
        android:translateY="4">
        <path
            android:fillColor="{fg_color}"
            android:pathData="M33,34H67V52C67,61.3888 59.3888,69 50,69C40.6112,69 33,61.3888 33,52V34Z" />
        <path
            android:strokeColor="{bg_color}"
            android:strokeWidth="3"
            android:strokeLineCap="round"
            android:pathData="M42,42H58" />
    </group>
</vector>
"""

XML_MONOCHROME = """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:translateX="4"
        android:translateY="4">
        <path
            android:fillColor="#000000"
            android:fillType="evenOdd"
            android:pathData="M33,34H67V52C67,61.3888 59.3888,69 50,69C40.6112,69 33,61.3888 33,52V34Z M42,40.5 H58 A1.5,1.5 0 0,1 59.5,42 A1.5,1.5 0 0,1 58,43.5 H42 A1.5,1.5 0 0,1 40.5,42 A1.5,1.5 0 0,1 42,40.5 Z" />
    </group>
</vector>
"""

XML_ADAPTIVE_ICON = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
"""


def render_svg_to_png(svg_string, width, height, output_path):
    rsvg_bin = shutil.which("rsvg-convert") or "/opt/homebrew/bin/rsvg-convert"
    if not os.path.exists(rsvg_bin):
        raise FileNotFoundError(
            "rsvg-convert is required for rasterizing mipmaps. Install via `brew install librsvg`."
        )

    res = subprocess.run(
        [rsvg_bin, "-w", str(width), "-h", str(height), "-o", output_path],
        input=svg_string.encode("utf-8"),
        capture_output=True,
    )
    if res.returncode != 0:
        raise RuntimeError(f"rsvg-convert failed: {res.stderr.decode('utf-8')}")


def generate_launcher_suite(res_dir, bg_color="#1b3022", fg_color="#f6fbf4"):
    os.makedirs(res_dir, exist_ok=True)

    # 1. Background Vector
    drawable_dir = os.path.join(res_dir, "drawable")
    drawable_v24_dir = os.path.join(res_dir, "drawable-v24")
    os.makedirs(drawable_dir, exist_ok=True)
    os.makedirs(drawable_v24_dir, exist_ok=True)

    with open(os.path.join(drawable_dir, "ic_launcher_background.xml"), "w") as f:
        f.write(XML_BACKGROUND.format(bg_color=bg_color))

    # 2. Foreground Vector (both drawable/ and drawable-v24/ to avoid shadowing)
    fg_content = XML_FOREGROUND.format(bg_color=bg_color, fg_color=fg_color)
    with open(os.path.join(drawable_dir, "ic_launcher_foreground.xml"), "w") as f:
        f.write(fg_content)
    with open(os.path.join(drawable_v24_dir, "ic_launcher_foreground.xml"), "w") as f:
        f.write(fg_content)

    # 3. Monochrome Vector (API 33+)
    with open(os.path.join(drawable_dir, "ic_launcher_monochrome.xml"), "w") as f:
        f.write(XML_MONOCHROME)

    # 4. Adaptive Configs in mipmap-anydpi-v26
    anydpi_dir = os.path.join(res_dir, "mipmap-anydpi-v26")
    os.makedirs(anydpi_dir, exist_ok=True)
    with open(os.path.join(anydpi_dir, "ic_launcher.xml"), "w") as f:
        f.write(XML_ADAPTIVE_ICON)
    with open(os.path.join(anydpi_dir, "ic_launcher_round.xml"), "w") as f:
        f.write(XML_ADAPTIVE_ICON)

    # 5. Raster Mipmaps
    svg_squircle = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="6 6 88 88" fill="none">
  <rect x="16" y="16" width="68" height="68" rx="22" fill="{bg_color}" />
  <path d="M33 34H67V52C67 61.3888 59.3888 69 50 69C40.6112 69 33 61.3888 33 52V34Z" fill="{fg_color}" />
  <path d="M42 42H58" stroke="{bg_color}" stroke-width="3" stroke-linecap="round" />
</svg>"""

    svg_round = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="6 6 88 88" fill="none">
  <circle cx="50" cy="50" r="42" fill="{bg_color}" />
  <path d="M33 34H67V52C67 61.3888 59.3888 69 50 69C40.6112 69 33 61.3888 33 52V34Z" fill="{fg_color}" />
  <path d="M42 42H58" stroke="{bg_color}" stroke-width="3" stroke-linecap="round" />
</svg>"""

    for folder, size in DENSITIES.items():
        out_folder = os.path.join(res_dir, folder)
        os.makedirs(out_folder, exist_ok=True)
        render_svg_to_png(svg_squircle, size, size, os.path.join(out_folder, "ic_launcher.png"))
        render_svg_to_png(svg_round, size, size, os.path.join(out_folder, "ic_launcher_round.png"))

    print(f"Successfully generated launcher icons in {res_dir}")


def main():
    parser = argparse.ArgumentParser(description="Generate complete Android launcher icon suite")
    parser.add_argument("--res-dir", default="app/androidApp/src/main/res", help="Path to res directory")
    parser.add_argument("--bg-color", default="#1b3022", help="Background hex color")
    parser.add_argument("--fg-color", default="#f6fbf4", help="Foreground glyph hex color")
    args = parser.parse_args()

    generate_launcher_suite(args.res_dir, bg_color=args.bg_color, fg_color=args.fg_color)


if __name__ == "__main__":
    main()
