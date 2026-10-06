import docx
import sys

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('scratch/test_build.docx')

print("Checking all headings in body (from paragraph 207 onwards):")
not_centered = []
centered_count = 0

for i, p in enumerate(doc.paragraphs[207:], start=207):
    txt = p.text.strip()
    # Check if this paragraph is a heading
    is_heading = False
    if any(txt.startswith(x) for x in ['CHAPTER ', 'REFERENCES', 'APPENDIX']):
        is_heading = True
    elif any(txt.startswith(f"{ch}.") for ch in range(1, 9)):
        is_heading = True
    elif any(txt.startswith(f"A.{num}") for num in range(1, 4)):
        is_heading = True
    elif txt in ['Scope', 'Limitations', 'Mathematical Formulation of Employee Compensation', 'Automated Primary Key Generation Algorithm']:
        is_heading = True
    elif any(txt.startswith(x) for x in ['Reflection by ', 'Code Snippet ']):
        is_heading = True

    if is_heading:
        align = p.alignment
        align_str = str(align)
        jc = p._p.xpath('./w:pPr/w:jc/@w:val')
        if align != docx.enum.text.WD_ALIGN_PARAGRAPH.CENTER and 'center' not in jc:
            not_centered.append((i, txt, align_str, jc))
        else:
            centered_count += 1

print(f"Total centered headings found: {centered_count}")
print(f"Total non-centered headings found: {len(not_centered)}")
for item in not_centered:
    print("   NON-CENTERED:", item)
