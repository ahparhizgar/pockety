# Debug launcher artwork

`debug-badge.svg` is the editable badge artwork. Adaptive foreground and
monochrome vectors live in `../res/drawable` and
`../res/drawable-v24`; both foreground qualifiers are intentionally present
because the main source set has a `drawable-v24` foreground.

The legacy square and round PNGs are generated from the matching production
PNG, then overlaid with the badge using only Python's standard library:

```bash
python3 app/androidApp/src/debug/icons/generate_debug_icons.py
```

This writes only to `app/androidApp/src/debug/res/mipmap-*`. Review
`preview.html` in a browser for native-density, launcher-mask, and
light/dark monochrome samples. Production resources are the raster base and
remain unchanged.
