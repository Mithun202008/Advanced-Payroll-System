import docx

docx_path = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\Enterprise_Payroll_Accounting_Management_System_PBL_REPORT (2).docx"
doc = docx.Document(docx_path)

for s_idx, section in enumerate(doc.sections):
    pw = section.page_width.inches
    ph = section.page_height.inches
    lm = section.left_margin.inches
    rm = section.right_margin.inches
    tm = section.top_margin.inches
    bm = section.bottom_margin.inches
    printable_w = pw - lm - rm
    print(f"Section {s_idx}: Page={pw:.2f}x{ph:.2f} in | Margins: L={lm:.2f}, R={rm:.2f}, T={tm:.2f}, B={bm:.2f} in | Usable Width = {printable_w:.2f} in")
