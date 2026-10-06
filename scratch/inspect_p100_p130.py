import docx
import sys

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')
for i in range(100, 130):
    p = doc.paragraphs[i]
    has_sect = bool(p._p.xpath('./w:pPr/w:sectPr'))
    print(f"P{i:3d}: sect={has_sect} runs={len(p.runs)} text={repr(p.text[:40])}")
    for r_idx, r in enumerate(p.runs):
        if 'w:br' in r._r.xml:
            print(f"   r{r_idx} br xml: {r._r.xml}")
