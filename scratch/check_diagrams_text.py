import docx

docx_path = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\Enterprise_Payroll_Accounting_Management_System_PBL_REPORT (2).docx"
doc = docx.Document(docx_path)

for idx in [197, 198, 199, 200, 201, 248, 249, 250, 251, 254, 255, 256, 257, 258, 259, 260, 261, 262]:
    p = doc.paragraphs[idx]
    has_drawing = 'w:drawing' in p._p.xml
    print(f"[{idx:03d}] (draw={has_drawing}, align={p.alignment}): {p.text.strip()[:100]}")
