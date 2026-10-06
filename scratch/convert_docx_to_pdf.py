import os
import sys
import win32com.client as win32
import pypdf

docx_path = os.path.abspath(r"Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx")
pdf_path = os.path.abspath(r"Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.pdf")

print("Converting DOCX to PDF and updating fields...")
print("DOCX:", docx_path)
print("PDF:", pdf_path)

# Try killing any existing stuck WINWORD processes if needed
word = win32.DispatchEx("Word.Application")
word.Visible = False
word.DisplayAlerts = 0

try:
    doc = word.Documents.Open(docx_path)
    
    print("Saving and exporting to PDF...")
    doc.Save()
    
    print("Exporting to PDF...")
    # 17 = wdExportFormatPDF
    doc.ExportAsFixedFormat(pdf_path, 17)
    doc.Close(False)
    print("Conversion completed successfully.")
finally:
    word.Quit()

reader = pypdf.PdfReader(pdf_path)
print(f"Total PDF pages: {len(reader.pages)}")
