from PIL import Image
import os

media_dir = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\extracted_doc_images\word\media"

for f in sorted(os.listdir(media_dir)):
    p = os.path.join(media_dir, f)
    with Image.open(p) as img:
        print(f"{f}: size={img.size} mode={img.mode} format={img.format} file_size={os.path.getsize(p)} bytes")
