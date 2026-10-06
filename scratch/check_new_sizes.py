from PIL import Image
import os

folder = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\new_screenshots"
for f in sorted(os.listdir(folder)):
    if f.endswith('.png'):
        p = os.path.join(folder, f)
        with Image.open(p) as img:
            print(f"{f}: size={img.size} mode={img.mode}")
