import os
import glob

res_dir = os.path.join(os.path.dirname(__file__), "app", "src", "main", "res")

# Check all XML files under res/
xml_files = glob.glob(os.path.join(res_dir, "**", "*.xml"), recursive=True)

for fpath in xml_files:
    with open(fpath, "rb") as f:
        data = f.read()
    if data[:3] == b'\xef\xbb\xbf':
        with open(fpath, "wb") as f:
            f.write(data[3:])
        print(f"FIXED BOM: {os.path.relpath(fpath)}")
    else:
        print(f"OK: {os.path.relpath(fpath)}")

print("=== DONE ===")

