import pypdf
import sys

sys.stdout.reconfigure(encoding='utf-8')

reader = pypdf.PdfReader('scratch/test_build.pdf')
print(f"Total pages in scratch/test_build.pdf: {len(reader.pages)}")

for i in range(min(25, len(reader.pages))):
    text = reader.pages[i].extract_text() or ''
    lines = [l.strip() for l in text.splitlines() if l.strip()]
    first_line = lines[0] if lines else '[BLANK PAGE]'
    last_line = lines[-1] if lines else '[BLANK PAGE]'
    print(f"Page {i+1:2d} ({len(lines):2d} lines): Top: {first_line[:55]:55s} | Btm: {last_line[:55]}")
