import docx
import sys
import xml.etree.ElementTree as ET
from lxml import etree

sys.stdout.reconfigure(encoding='utf-8')
doc = docx.Document('pbl/RiskLens ( SYAMRIT , S HARSHAVARTHANAN ).docx')
for i, s in enumerate(doc.sections):
    sectPr = s._sectPr
    cols = sectPr.xpath('./w:cols')
    col_xml = etree.tostring(cols[0]).decode('utf-8') if cols else 'None'
    print(f"Section {i}: start={s.start_type} cols={col_xml}")
