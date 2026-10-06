import docx
import sys

sys.stdout.reconfigure(encoding='utf-8')
ref_doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')

print("=== INSPECTING P80 TO P160 IN REFERENCE DOC ===")
for i in range(80, 160):
    p = ref_doc.paragraphs[i]
    xml = p._p.xml
    txt = p.text.strip()
    pPr = p._p.find('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}pPr')
    sb = p._p.xpath('./w:pPr/w:spacing/@w:before')
    sa = p._p.xpath('./w:pPr/w:spacing/@w:after')
    line = p._p.xpath('./w:pPr/w:spacing/@w:line')
    jc = p._p.xpath('./w:pPr/w:jc/@w:val')
    has_br = 'w:br' in xml
    has_sect = 'w:sectPr' in xml
    
    font_sizes = []
    for r in p.runs:
        sz = r._r.xpath('./w:rPr/w:sz/@w:val')
        if sz: font_sizes.extend(sz)
    
    print(f"P{i:3d}: br={has_br} sect={has_sect} jc={jc} sb={sb} sa={sa} ln={line} sz={font_sizes[:2]} | {txt[:60]}")
