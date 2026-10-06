import docx
import sys

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')

print(f"Total sections: {len(doc.sections)}")
sec_idx = 0
for i, p in enumerate(doc.paragraphs):
    sectPr = p._p.xpath('./w:pPr/w:sectPr')
    if sectPr:
        print(f"Paragraph {i} has sectPr -> ends Section {sec_idx}")
        s = doc.sections[sec_idx]
        print(f"   Section {sec_idx}: start={s.start_type}, top={s.top_margin.inches if s.top_margin else None}, btm={s.bottom_margin.inches if s.bottom_margin else None}, left={s.left_margin.inches if s.left_margin else None}, right={s.right_margin.inches if s.right_margin else None}")
        sec_idx += 1
if sec_idx < len(doc.sections):
    s = doc.sections[sec_idx]
    print(f"Final Section {sec_idx}: start={s.start_type}, top={s.top_margin.inches if s.top_margin else None}, btm={s.bottom_margin.inches if s.bottom_margin else None}")
