with open('build_pristine_pbl_report.py', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace(
    'output_path = "Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx"',
    'output_path = "scratch/test_build.docx"'
)

with open('scratch/run_test_build.py', 'w', encoding='utf-8') as f:
    f.write(code)
print("Wrote scratch/run_test_build.py")
