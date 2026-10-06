import docx
from docx.enum.text import WD_ALIGN_PARAGRAPH
import sys

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx')

print("=== DOCX HEADING ALIGNMENT AUDIT ===")
all_passed = True
checked = 0

for i, p in enumerate(doc.paragraphs):
    txt = p.text.strip()
    is_heading = False
    if any(txt.startswith(x) for x in ['CHAPTER ', 'REFERENCES', 'APPENDIX']):
        is_heading = True
    elif any(txt.startswith(f"{ch}.") for ch in range(1, 9)) and not txt[3:4].isdigit() and len(txt) < 80:
        is_heading = True
    elif any(txt.startswith(f"A.{num}") for num in range(1, 4)):
        is_heading = True
    elif txt in ['Scope', 'Limitations', 'Mathematical Formulation of Employee Compensation', 'Automated Primary Key Generation Algorithm', 'ABSTRACT', 'TABLE OF CONTENTS', 'LIST OF FIGURES', 'LIST OF TABLES', 'LIST OF ABBREVIATIONS']:
        is_heading = True
    elif any(txt.startswith(x) for x in ['Reflection by ', 'Code Snippet ']):
        is_heading = True

    if is_heading:
        checked += 1
        jc = p._p.xpath('./w:pPr/w:jc/@w:val')
        is_center = (p.alignment == WD_ALIGN_PARAGRAPH.CENTER) or ('center' in jc)
        if not is_center:
            print(f"FAILED: P{i:3d}: align={p.alignment} jc={jc} | {txt[:60]}")
            all_passed = False

if all_passed:
    print(f"ALL {checked} HEADINGS ACROSS CHAPTERS, SUB-DIVISIONS, AND FRONT-MATTER ARE 100% CENTERED! [PASS]")
else:
    print("Some headings failed centering check.")
