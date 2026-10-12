#!/usr/bin/env python3
"""
svg_to_vector_drawable.py

Converts SVG files or snippet paths into Android Vector Drawable XML format.
Supports shape conversion (rect, circle, line -> pathData), attribute mapping,
and group translations/scaling.

Usage:
    python3 svg_to_vector_drawable.py --input icon.svg --output ic_icon.xml
    python3 svg_to_vector_drawable.py --input icon.svg --width 108 --height 108 --tx 4 --ty 4
"""

import argparse
import re
import xml.etree.ElementTree as ET


def rect_to_path(x, y, w, h, rx=0.0):
    if rx <= 0.0:
        return f"M{x},{y} H{x + w} V{y + h} H{x} Z"
    return (
        f"M{x + rx},{y} "
        f"H{x + w - rx} "
        f"A {rx},{rx} 0 0,1 {x + w},{y + rx} "
        f"V{y + h - rx} "
        f"A {rx},{rx} 0 0,1 {x + w - rx},{y + h} "
        f"H{x + rx} "
        f"A {rx},{rx} 0 0,1 {x},{y + h - rx} "
        f"V{y + rx} "
        f"A {rx},{rx} 0 0,1 {x + rx},{y} "
        f"Z"
    )


def circle_to_path(cx, cy, r):
    return (
        f"M{cx - r},{cy} "
        f"A {r},{r} 0 1,0 {cx + r},{cy} "
        f"A {r},{r} 0 1,0 {cx - r},{cy} "
        f"Z"
    )


def line_to_path(x1, y1, x2, y2):
    return f"M{x1},{y1} L{x2},{y2}"


def strip_namespace(tag):
    if "}" in tag:
        return tag.split("}", 1)[1]
    return tag


def parse_float(val, default=0.0):
    if val is None:
        return default
    m = re.match(r"[-+]?[0-9]*\.?[0-9]+", str(val).strip())
    if m:
        return float(m.group(0))
    return default


def convert_svg_to_vector(svg_content, width=None, height=None, tx=0.0, ty=0.0, scale=1.0):
    root = ET.fromstring(svg_content)

    # Determine viewBox and dimensions
    viewbox_str = root.attrib.get("viewBox")
    if viewbox_str:
        vb_parts = [float(p) for p in re.split(r"[,\s]+", viewbox_str.strip()) if p]
        vb_w = vb_parts[2]
        vb_h = vb_parts[3]
    else:
        vb_w = parse_float(root.attrib.get("width", 24))
        vb_h = parse_float(root.attrib.get("height", 24))

    out_w = width if width is not None else int(vb_w)
    out_h = height if height is not None else int(vb_h)

    out_lines = [
        '<?xml version="1.0" encoding="utf-8"?>',
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{out_w}dp"',
        f'    android:height="{out_h}dp"',
        f'    android:viewportWidth="{vb_w}"',
        f'    android:viewportHeight="{vb_h}">',
    ]

    use_group = (tx != 0.0 or ty != 0.0 or scale != 1.0)
    indent = "    "
    if use_group:
        out_lines.append(f'{indent}<group')
        if tx != 0.0:
            out_lines.append(f'{indent}    android:translateX="{tx}"')
        if ty != 0.0:
            out_lines.append(f'{indent}    android:translateY="{ty}"')
        if scale != 1.0:
            out_lines.append(f'{indent}    android:scaleX="{scale}"')
            out_lines.append(f'{indent}    android:scaleY="{scale}"')
        out_lines.append(f'{indent}>')
        indent = "        "

    for elem in root.iter():
        tag = strip_namespace(elem.tag).lower()
        if tag not in ("path", "rect", "circle", "line"):
            continue

        path_data = None
        if tag == "path":
            path_data = elem.attrib.get("d")
        elif tag == "rect":
            rx = parse_float(elem.attrib.get("rx", 0))
            x = parse_float(elem.attrib.get("x", 0))
            y = parse_float(elem.attrib.get("y", 0))
            w = parse_float(elem.attrib.get("width", 0))
            h = parse_float(elem.attrib.get("height", 0))
            path_data = rect_to_path(x, y, w, h, rx)
        elif tag == "circle":
            cx = parse_float(elem.attrib.get("cx", 0))
            cy = parse_float(elem.attrib.get("cy", 0))
            r = parse_float(elem.attrib.get("r", 0))
            path_data = circle_to_path(cx, cy, r)
        elif tag == "line":
            x1 = parse_float(elem.attrib.get("x1", 0))
            y1 = parse_float(elem.attrib.get("y1", 0))
            x2 = parse_float(elem.attrib.get("x2", 0))
            y2 = parse_float(elem.attrib.get("y2", 0))
            path_data = line_to_path(x1, y1, x2, y2)

        if not path_data:
            continue

        fill = elem.attrib.get("fill")
        stroke = elem.attrib.get("stroke")
        stroke_w = elem.attrib.get("stroke-width")
        stroke_cap = elem.attrib.get("stroke-linecap")
        fill_rule = elem.attrib.get("fill-rule")

        elem_lines = [f"{indent}<path"]
        if fill and fill.lower() != "none":
            elem_lines.append(f'{indent}    android:fillColor="{fill}"')
        if fill_rule and fill_rule.lower() == "evenodd":
            elem_lines.append(f'{indent}    android:fillType="evenOdd"')
        if stroke and stroke.lower() != "none":
            elem_lines.append(f'{indent}    android:strokeColor="{stroke}"')
            if stroke_w:
                elem_lines.append(f'{indent}    android:strokeWidth="{stroke_w}"')
            if stroke_cap:
                elem_lines.append(f'{indent}    android:strokeLineCap="{stroke_cap}"')

        elem_lines.append(f'{indent}    android:pathData="{path_data}" />')
        out_lines.extend(elem_lines)

    if use_group:
        out_lines.append("    </group>")

    out_lines.append("</vector>\n")
    return "\n".join(out_lines)


def main():
    parser = argparse.ArgumentParser(description="Convert SVG to Android Vector Drawable XML")
    parser.add_argument("--input", "-i", required=True, help="Input SVG file path")
    parser.add_argument("--output", "-o", help="Output XML file path (prints to stdout if omitted)")
    parser.add_argument("--width", type=int, help="Output vector width in dp")
    parser.add_argument("--height", type=int, help="Output vector height in dp")
    parser.add_argument("--tx", type=float, default=0.0, help="Translate X offset")
    parser.add_argument("--ty", type=float, default=0.0, help="Translate Y offset")
    parser.add_argument("--scale", type=float, default=1.0, help="Scale factor")
    args = parser.parse_args()

    with open(args.input, "r", encoding="utf-8") as f:
        svg_content = f.read()

    xml_content = convert_svg_to_vector(
        svg_content,
        width=args.width,
        height=args.height,
        tx=args.tx,
        ty=args.ty,
        scale=args.scale,
    )

    if args.output:
        with open(args.output, "w", encoding="utf-8") as f:
            f.write(xml_content)
        print(f"Wrote Vector Drawable to {args.output}")
    else:
        print(xml_content)


if __name__ == "__main__":
    main()
