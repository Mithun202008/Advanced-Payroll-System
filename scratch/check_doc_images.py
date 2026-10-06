from PIL import Image
import os

folder = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\doc_images"
for f in sorted(os.listdir(folder)):
    p = os.path.join(folder, f)
    with Image.open(p) as img:
        print(f"{f}: size={img.size} mode={img.mode} format={img.format} size_bytes={os.path.getsize(p)}")
