#!/usr/bin/env python3
"""Add the editable debug-badge.svg artwork to existing RGBA launcher PNGs.

Uses only Python's standard library. Run from the repository root:
python3 app/androidApp/src/debug/icons/generate_debug_icons.py
"""
import math
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[5]
MAIN = ROOT / "app/androidApp/src/main/res"
DEBUG = ROOT / "app/androidApp/src/debug/res"
BUCKETS = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def decode_png(path):
    data = path.read_bytes()
    pos, compressed, width, height = 8, bytearray(), 0, 0
    while pos < len(data):
        size = struct.unpack(">I", data[pos:pos + 4])[0]
        kind, chunk = data[pos + 4:pos + 8], data[pos + 8:pos + 8 + size]
        pos += size + 12
        if kind == b"IHDR":
            width, height, depth, color, compression, filtering, interlace = struct.unpack(">IIBBBBB", chunk)
            if (depth, color, compression, filtering, interlace) != (8, 6, 0, 0, 0):
                raise ValueError(f"Expected non-interlaced 8-bit RGBA PNG: {path}")
        elif kind == b"IDAT":
            compressed.extend(chunk)
        elif kind == b"IEND":
            break
    raw = zlib.decompress(compressed)
    stride, rows, offset = width * 4, [], 0
    prior = bytearray(stride)
    for _ in range(height):
        filt, scan = raw[offset], bytearray(raw[offset + 1:offset + 1 + stride])
        offset += stride + 1
        for i in range(stride):
            left = scan[i - 4] if i >= 4 else 0
            above, upper_left = prior[i], prior[i - 4] if i >= 4 else 0
            if filt == 1: scan[i] = (scan[i] + left) & 255
            elif filt == 2: scan[i] = (scan[i] + above) & 255
            elif filt == 3: scan[i] = (scan[i] + ((left + above) // 2)) & 255
            elif filt == 4:
                p = left + above - upper_left
                pa, pb, pc = abs(p - left), abs(p - above), abs(p - upper_left)
                scan[i] = (scan[i] + (left if pa <= pb and pa <= pc else above if pb <= pc else upper_left)) & 255
            elif filt != 0: raise ValueError(f"Unsupported PNG filter {filt}")
        rows.append(scan)
        prior = scan
    return width, height, rows


def paint(rows, width, height, predicate, color):
    for y in range(max(0, int(height * .06)), min(height, int(height * .48) + 1)):
        for x in range(max(0, int(width * .48)), min(width, int(width * .94) + 1)):
            coverage = sum(predicate(x + (sx + .5) / 4, y + (sy + .5) / 4) for sy in range(4) for sx in range(4)) / 16
            if not coverage: continue
            i, alpha = x * 4, coverage
            row = rows[y]
            for c in range(3): row[i + c] = round(row[i + c] * (1 - alpha) + color[c] * alpha)
            row[i + 3] = 255


def circle(cx, cy, radius):
    return lambda x, y: (x - cx) ** 2 + (y - cy) ** 2 <= radius ** 2


def line(x1, y1, x2, y2, radius):
    def hit(x, y):
        dx, dy = x2 - x1, y2 - y1
        t = max(0, min(1, ((x - x1) * dx + (y - y1) * dy) / (dx * dx + dy * dy)))
        return (x - x1 - t * dx) ** 2 + (y - y1 - t * dy) ** 2 <= radius ** 2
    return hit


def save_png(path, width, height, rows):
    raw = b"".join(b"\0" + row for row in rows)
    def chunk(kind, data):
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data) & 0xffffffff)
    data = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)


def main():
    for density, size in BUCKETS.items():
        for suffix in ("", "_round"):
            source = MAIN / f"mipmap-{density}/ic_launcher{suffix}.png"
            width, height, rows = decode_png(source)
            if (width, height) != (size, size): raise ValueError(f"Unexpected dimensions: {source}")
            s, cx, cy = size / 108, size * 69 / 108, size * 39 / 108
            paint(rows, width, height, circle(cx, cy, 10 * s), (246, 184, 63))
            paint(rows, width, height, lambda x, y: ((x - cx) / (4 * s)) ** 2 + ((y - cy - 3.5 * s) / (4.5 * s)) ** 2 <= 1, (27, 48, 34))
            for x1, y1, x2, y2 in ((66,32,64,30),(70,32,72,30),(64,34,61.5,33),(64,37,61.5,38),(72,34,74.5,33),(72,37,74.5,38)):
                paint(rows, width, height, line(x1*s, y1*s, x2*s, y2*s, .75*s), (27, 48, 34))
            save_png(DEBUG / f"mipmap-{density}/ic_launcher{suffix}.png", width, height, rows)
    print("Generated 10 debug legacy launcher PNGs from main assets.")


if __name__ == "__main__":
    main()
