import docx
import xml.etree.ElementTree as ET

docx_path = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\Enterprise_Payroll_Accounting_Management_System_PBL_REPORT (2).docx"
doc = docx.Document(docx_path)

for i in [198, 249, 255, 260, 400, 405, 409, 414, 419, 422, 427, 432, 437, 441]:
    p = doc.paragraphs[i]
    xml = p._p.xml
    root = ET.fromstring(xml)
    extents = root.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing}extent')
    blips = root.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/main}blip')
    
    cx = extents[0].attrib.get('cx') if extents else None
    cy = extents[0].attrib.get('cy') if extents else None
    r_id = blips[0].attrib.get('{http://schemas.openxmlformats.org/officeDocument/2006/relationships}embed') if blips else None
    target = doc.part.rels[r_id].target_ref if r_id in doc.part.rels else None
    
    print(f"Para [{i}]: rId={r_id} -> {target} | cx={cx} ({int(cx)/914400:.2f} in), cy={cy} ({int(cy)/914400:.2f} in)")
