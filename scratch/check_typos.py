import docx

doc = docx.Document('Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx')

print("=== CHECKING FOR ENCODING ARTIFACTS AND TYPOS ===")
for i, p in enumerate(doc.paragraphs):
    txt = p.text
    if '' in txt or '\ufffd' in txt:
        print(f"P[{i}] contains : {txt[:100]}")
    # check for common typos or double spaces
    if '  ' in txt and not any(txt.strip().startswith(x) for x in ['Figure', 'Table', '1.', '2.', '3.', '4.', '5.', '6.', '7.', '8.']):
        pass

for t_idx, t in enumerate(doc.tables):
    for r_idx, r in enumerate(t.rows):
        for c_idx, c in enumerate(r.cells):
            if '' in c.text or '\ufffd' in c.text:
                print(f"Table {t_idx} R{r_idx}C{c_idx} contains : {c.text[:80]}")
