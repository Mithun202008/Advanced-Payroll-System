import docx
from docx.enum.text import WD_ALIGN_PARAGRAPH
import sys

sys.stdout.reconfigure(encoding='utf-8')

ref_doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')

print(f"=== REFERENCE DOC SECTIONS ({len(ref_doc.sections)}) ===")
for idx, s in enumerate(ref_doc.sections):
    tm = round(s.top_margin.inches, 2) if s.top_margin else None
    bm = round(s.bottom_margin.inches, 2) if s.bottom_margin else None
    lm = round(s.left_margin.inches, 2) if s.left_margin else None
    rm = round(s.right_margin.inches, 2) if s.right_margin else None
    pw = round(s.page_width.inches, 2) if s.page_width else None
    ph = round(s.page_height.inches, 2) if s.page_height else None
    print(f"Sec {idx}: top={tm}, btm={bm}, left={lm}, right={rm}, pw={pw}, ph={ph}, start={s.start_type}")

print(f"\n=== REFERENCE DOC HEADINGS & ALIGNMENTS ===")
for i, p in enumerate(ref_doc.paragraphs):
    txt = p.text.strip()
    if any(txt.startswith(x) for x in ['CHAPTER', '1.', '2.', '3.', '4.', '5.', '6.', '7.', '8.', 'ABSTRACT', 'TABLE OF CONTENTS', 'LIST OF', 'REFERENCES', 'APPENDIX', 'DECLARATION', 'ACKNOWLEDGEMENT', 'BONAFIDE']):
        align = p.alignment
        align_str = str(align)
        # also check w:pPr/w:jc
        jc = p._p.xpath('./w:pPr/w:jc/@w:val')
        print(f"P{i:3d}: align={align_str:25s} jc={jc} | {txt[:60]}")
