import docx
import sys

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')
s = doc.sections[8]
print(f"Section 8 margins: top={s.top_margin.inches}, btm={s.bottom_margin.inches}, left={s.left_margin.inches}, right={s.right_margin.inches}")

for p in doc.paragraphs[305:320]:
    txt = p.text.strip()
    if txt:
        pf = p.paragraph_format
        print(f"P: align={p.alignment} left_indent={pf.left_indent.inches if pf.left_indent else None} right_indent={pf.right_indent.inches if pf.right_indent else None} | {txt[:50]}")
