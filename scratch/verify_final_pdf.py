import pypdf
import sys

sys.stdout.reconfigure(encoding='utf-8')

pdf_path = 'Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.pdf'
reader = pypdf.PdfReader(pdf_path)
total_pages = len(reader.pages)
print(f"=== FINAL AUDIT OF {pdf_path} (Total Pages: {total_pages}) ===")

# Audit Pages 1 to 15
for p in range(min(16, total_pages)):
    page = reader.pages[p]
    text = page.extract_text() or ''
    lines = [l.strip() for l in text.splitlines() if l.strip()]
    first_line = lines[0] if lines else '[EMPTY]'
    last_line = lines[-1] if lines else '[EMPTY]'
    print(f"Page {p+1:2d} ({len(lines):2d} lines):")
    print(f"   Top: {first_line[:65]}")
    print(f"   Btm: {last_line[:65]}")

print("\n=== VERIFYING FRONT-MATTER PAGE ALLOCATION ===")
checks = [
    (1, "Cover Page", lambda t: "Enterprise Payroll" in t and "October - 2026" in t),
    (2, "Vision/Mission Institute", lambda t: "Mission of the Institute:" in t),
    (3, "Vision/Mission Department", lambda t: "Vision of the Department:" in t),
    (4, "PEO & PO", lambda t: "PROGRAM EDUCATIONAL OBJECTIVES" in t and "adhere to national & international laws" in t),
    (5, "PSO", lambda t: "PROGRAM SPECIFIC OUTCOMES" in t),
    (6, "Bonafide Certificate", lambda t: "BONAFIDE CERTIFICATE" in t and "INTERNAL EXAMINER" in t),
    (7, "Declaration", lambda t: "DECLARATION" in t and "Date:" in t and "MITHUN A" in t),
    (8, "Acknowledgement", lambda t: "ACKNOWLEDGEMENT" in t and "[TEAMMATE NAME]" in t),
    (9, "Abstract", lambda t: "ABSTRACT" in t and "Keywords" in t),
    (10, "Table of Contents", lambda t: "TABLE OF CONTENTS" in t and "A.3 Self and Peer Assessment" in t),
    (11, "List of Figures", lambda t: "LIST OF FIGURES" in t),
    (12, "List of Tables", lambda t: "LIST OF TABLES" in t),
    (13, "List of Abbreviations", lambda t: "LIST OF ABBREVIATIONS" in t and "WA Washing Allowance" in t),
    (14, "Chapter 1 Introduction", lambda t: "CHAPTER 1: INTRODUCTION" in t and "1.1 Background" in t),
]

for page_no, name, cond in checks:
    txt = reader.pages[page_no - 1].extract_text() or ''
    res = cond(txt)
    status = "PASS" if res else "FAIL"
    print(f"Page {page_no:2d}: {name:30s} -> [{status}]")
