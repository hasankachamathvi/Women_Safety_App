"""Strip UTF-8 BOM from every XML file under app/src/main/res."""
import os, glob, pathlib

root = pathlib.Path(__file__).parent / "app" / "src" / "main" / "res"
fixed = 0
for fpath in root.rglob("*.xml"):
    raw = fpath.read_bytes()
    if raw[:3] == b"\xef\xbb\xbf":
        fpath.write_bytes(raw[3:])
        fixed += 1
        print(f"FIXED BOM: {fpath.relative_to(root)}")

print(f"\nDone. Fixed {fixed} file(s).")

