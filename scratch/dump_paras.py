import docx

doc = docx.Document('Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx')

with open('scratch/all_paragraphs.txt', 'w', encoding='utf-8') as f:
    for i, p in enumerate(doc.paragraphs):
        txt = p.text.strip()
        if txt:
            f.write(f"P[{i}]: {txt}\n\n")

print("Dumped all paragraphs to scratch/all_paragraphs.txt")
