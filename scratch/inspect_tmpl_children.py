import docx

doc = docx.Document(r'C:\Users\HP\Downloads\PBL Report - Sample Template.docx')
body = doc._body._element
for i in range(155, min(185, len(body))):
    child = body[i]
    tag = child.tag.split('}')[-1]
    txt = child.text.strip() if child.text else ""
    print(f"{i:3d}: tag={tag:5s} | text={txt[:60]}")
