with open('scratch/prepare_test_v2.py', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace(
    "code = f.read()",
    "code = f.read()\ncode = code.replace('from docx.oxml.ns import nsdecls', 'from docx.oxml.ns import nsdecls, qn')"
)

with open('scratch/prepare_test_v2.py', 'w', encoding='utf-8') as f:
    f.write(text)

print("Updated prepare_test_v2.py")
