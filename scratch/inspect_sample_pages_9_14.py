import pypdf
import sys

sys.stdout.reconfigure(encoding='utf-8')
reader = pypdf.PdfReader('scratch/sample_original.pdf')

for p_num in range(9, 15):
    page = reader.pages[p_num - 1]
    print(f"==================== SAMPLE PAGE {p_num} ====================")
    print(page.extract_text())
