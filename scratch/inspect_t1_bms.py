import docx
import re

doc = docx.Document('Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx')
t1 = doc.tables[1]

print("=== TABLE 1 (TOC) BOOKMARK REFERENCES ===")
for i, row in enumerate(t1.rows):
    cell = row.cells[2]
    xml = cell._tc.xml
    m = re.findall(r'PAGEREF\s+([^\s\\]+)', xml)
    bm = m[0] if m else "NO_PAGEREF"
    title = row.cells[1].text.strip()
    ch_no = row.cells[0].text.strip()
    val = cell.text.strip()
    print(f"Row {i:2d} | {ch_no:10s} | {title:40s} | bm={bm:15s} | text={val}")
