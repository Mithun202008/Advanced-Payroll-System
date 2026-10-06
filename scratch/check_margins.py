import docx

doc = docx.Document('Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx')
for i, s in enumerate(doc.sections):
    pw = s.page_width / 1440.0 if s.page_width else 8.5
    lm = s.left_margin / 1440.0 if s.left_margin else 1.0
    rm = s.right_margin / 1440.0 if s.right_margin else 1.0
    tm = s.top_margin / 1440.0 if s.top_margin else 1.0
    bm = s.bottom_margin / 1440.0 if s.bottom_margin else 1.0
    usable_w = pw - lm - rm
    print(f'Section {i}: page_w={pw:.2f}", margins: L={lm:.2f}", R={rm:.2f}", T={tm:.2f}", B={bm:.2f}" -> usable_w={usable_w:.2f}"')
