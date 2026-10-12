# SVG to Android Vector Drawable Conversion

This document specifies the rules and patterns for converting Scalable Vector Graphics (SVG) into Android Vector Drawable XML files (`<vector>`).

---

## 1. Top-Level Structure

An Android Vector Drawable replaces `<svg>` with `<vector>`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Paths and Groups -->
</vector>
```

### Attribute Mappings
| SVG Attribute | Android Vector Attribute | Notes |
|---|---|---|
| `width="W"` | `android:width="Wdp"` | Display width (always include dp/px unit) |
| `height="H"` | `android:height="Hdp"` | Display height (always include dp/px unit) |
| `viewBox="minX minY W H"` | `android:viewportWidth="W"` | Coordinate width (unitless float) |
| `viewBox="minX minY W H"` | `android:viewportHeight="H"` | Coordinate height (unitless float) |

> **Note on `minX` / `minY`**: Android Vector Drawable `viewportWidth` and `viewportHeight` always assume an origin of `(0, 0)`. If the SVG `viewBox` has a non-zero origin (e.g. `viewBox="6 6 88 88"`), wrap the root contents in a `<group android:translateX="-6" android:translateY="-6">`.

---

## 2. Shape to Path Conversion

Android Vector Drawables only support `<path>` and `<group>` elements. Basic SVG shapes (`<rect>`, `<circle>`, `<line>`, `<polygon>`) must be converted to standard SVG path data (`android:pathData`).

### 2.1 Rectangles (`<rect>`)
- Plain rectangle `<rect x="X" y="Y" width="W" height="H" />`:
  $$\text{pathData} = \text{"M}X,Y\text{ H}(X+W)\text{ V}(Y+H)\text{ H}X\text{ Z"}$$
- Rounded rectangle `<rect x="X" y="Y" width="W" height="H" rx="R" />`:
  ```
  M(X+R),Y 
  H(X+W-R) 
  A R,R 0 0,1 (X+W),(Y+R) 
  V(Y+H-R) 
  A R,R 0 0,1 (X+W-R),(Y+H) 
  H(X+R) 
  A R,R 0 0,1 X,(Y+H-R) 
  V(Y+R) 
  A R,R 0 0,1 (X+R),Y 
  Z
  ```

### 2.2 Circles (`<circle>`)
- Circle `<circle cx="CX" cy="CY" r="R" />`:
  $$\text{pathData} = \text{"M}(CX-R),CY\text{ A }R,R\text{ 0 1,0 }(CX+R),CY\text{ A }R,R\text{ 0 1,0 }(CX-R),CY\text{ Z"}$$

### 2.3 Lines (`<line>`)
- `<line x1="X1" y1="Y1" x2="X2" y2="Y2" />`:
  $$\text{pathData} = \text{"M}X1,Y1\text{ L}X2,Y2\text{"}$$

---

## 3. Path Attributes Mapping

| SVG Property | Android Attribute | Allowed Values / Conversion |
|---|---|---|
| `d="..."` | `android:pathData="..."` | Standard SVG path commands (`M, L, H, V, C, S, Q, T, A, Z`) |
| `fill="#RRGGBB"` | `android:fillColor="#RRGGBB"` | Hex color or `@color/...` |
| `fill="none"` | *(omit attribute)* | If no fill, omit `fillColor` |
| `fill-opacity="0.5"` | `android:fillAlpha="0.5"` | Float 0.0 to 1.0 |
| `stroke="#RRGGBB"` | `android:strokeColor="#RRGGBB"` | Hex color or `@color/...` |
| `stroke-width="3"` | `android:strokeWidth="3"` | Unitless float in viewport units |
| `stroke-opacity="0.5"` | `android:strokeAlpha="0.5"` | Float 0.0 to 1.0 |
| `stroke-linecap="round"` | `android:strokeLineCap="round"` | `butt`, `round`, `square` |
| `stroke-linejoin="round"` | `android:strokeLineJoin="round"` | `miter`, `round`, `bevel` |
| `stroke-miterlimit="4"` | `android:strokeMiterLimit="4"` | Float |
| `fill-rule="evenodd"` | `android:fillType="evenOdd"` | `nonZero` (default), `evenOdd` |

---

## 4. Groups and Transformations

SVG `<g transform="...">` translates directly to Android `<group>`:

```xml
<group
    android:name="group_name"
    android:translateX="4"
    android:translateY="4"
    android:scaleX="1.2"
    android:scaleY="1.2"
    android:pivotX="54"
    android:pivotY="54"
    android:rotation="45">
    <!-- Nested paths -->
</group>
```

### Scale & Centering Formula
To center an SVG glyph designed in $W_{in} \times H_{in}$ (center at $(C_x, C_y)$) onto an Android canvas $W_{out} \times H_{out}$ (center $(O_x, O_y)$) scaled by factor $S$:

$$\text{translateX} = O_x - (C_x \times S)$$
$$\text{translateY} = O_y - (C_y \times S)$$

Example: $100\times100$ glyph (center $(50, 50)$) placed onto $108\times108$ canvas (center $(54, 54)$) at scale $1.0$:
$$\text{translateX} = 54 - 50 = 4, \quad \text{translateY} = 54 - 50 = 4$$

---

## 5. Converting Multi-Tone Glyphs to Single-Tone Cutouts (EvenOdd)

When an icon design has a background glyph and an inner accent stroke (e.g., a pocket with a stitch line, or an icon with an inner groove):
- In standard SVG, the accent is often a stroked `<path>`.
- In monochrome or tinted vectors, all paths get tinted with the **same** color, causing the stroke to blend into the shape.

### Solution: Transparent Negative-Space Cutout
1. Expand the stroke into a closed contour path:
   - For a horizontal line from $(X_1, Y)$ to $(X_2, Y)$ with stroke width $W$ and round caps:
     $$R = W / 2$$
     $$\text{M}X_1,(Y-R)\text{ H}X_2\text{ A}R,R\text{ 0 0,1 }(X_2+R),Y\text{ A}R,R\text{ 0 0,1 }X_2,(Y+R)\text{ H}X_1\text{ A}R,R\text{ 0 0,1 }(X_1-R),Y\text{ A}R,R\text{ 0 0,1 }X_1,(Y-R)\text{ Z}$$
2. Combine the outer shape path and the cutout path in a single `<path>` with `android:fillType="evenOdd"`.
3. The inner region becomes transparent negative space, rendering perfectly under dynamic tinting.
