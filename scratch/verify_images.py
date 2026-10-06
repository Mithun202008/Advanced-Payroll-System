from PIL import Image
import os

folder = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\new_screenshots"
for f in sorted(os.listdir(folder)):
    p = os.path.join(folder, f)
    with Image.open(p) as img:
        colors = img.getcolors(maxcolors=256)
        num_colors = len(colors) if colors else ">256"
        extrema = img.getextrema()
        print(f"{f}: size={img.size}, num_colors={num_colors}, extrema={extrema}")
