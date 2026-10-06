import os
import shutil
import zipfile
import xml.etree.ElementTree as ET
from PIL import Image

doc_path = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\Enterprise_Payroll_Accounting_Management_System_PBL_REPORT (2).docx"
backup_path = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\Enterprise_Payroll_Accounting_Management_System_PBL_REPORT (2)_BACKUP.docx"
work_dir = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\docx_unpack"

# 1. Backup
if not os.path.exists(backup_path):
    shutil.copy2(doc_path, backup_path)
    print(f"Created backup at {backup_path}")

# 2. Unpack docx
if os.path.exists(work_dir):
    shutil.rmtree(work_dir)
os.makedirs(work_dir, exist_ok=True)

with zipfile.ZipFile(doc_path, 'r') as z:
    z.extractall(work_dir)
print("Unpacked docx archive.")

# 3. Replace image files in word/media/
media_dir = os.path.join(work_dir, 'word', 'media')
screenshots_dir = r"C:\Users\HP\OneDrive\Documents\Advanced_Payroll_System\PayRoll\scratch\new_screenshots"

image_replacements = {
    'image9.png': 'fig5_1_login.png',
    'image10.png': 'fig5_2_dashboard.png',
    'image11.png': 'fig5_3_employees.png',
    'image12.png': 'fig5_4_add_employee.png',
    'image13.png': 'fig5_5_edit_employee.png',
    'image14.png': 'fig5_6_settings.png',
    'image15.png': 'fig5_7_payroll.png',
    'image16.png': 'fig5_8_print_slip.png',
    'image17.png': 'fig5_9_change_password.png',
    'image18.png': 'fig5_10_help.png'
}

for media_name, src_name in image_replacements.items():
    src_file = os.path.join(screenshots_dir, src_name)
    dst_file = os.path.join(media_dir, media_name)
    shutil.copy2(src_file, dst_file)
    print(f"Replaced {media_name} with {src_name}")

# Read dimensions of all images in media_dir
image_dims = {} # media_name -> (w_px, h_px)
for f in os.listdir(media_dir):
    p = os.path.join(media_dir, f)
    try:
        with Image.open(p) as img:
            image_dims[f] = img.size
    except Exception:
        pass

# 4. Map relationships: rId -> target
rels_path = os.path.join(work_dir, 'word', '_rels', 'document.xml.rels')
rels_tree = ET.parse(rels_path)
rels_root = rels_tree.getroot()
rid_to_target = {}
for rel in rels_root:
    r_id = rel.attrib.get('Id')
    target = rel.attrib.get('Target')
    if target and target.startswith('media/'):
        target_name = os.path.basename(target)
        rid_to_target[r_id] = target_name

# Target widths in inches for specific figures (printable width is 6.10 in)
target_widths = {
    'image5.png': 5.50,   # Figure 1.1 Workflow
    'image6.png': 5.50,   # Figure 4.1 Architecture
    'image7.png': 5.50,   # Figure 4.2 Class Diagram
    'image8.png': 5.50,   # Figure 4.3 ER Schema
    'image9.png': 4.80,   # Figure 5.1 LoginFrame
    'image10.png': 5.50,  # Figure 5.2 Dashboard
    'image11.png': 5.50,  # Figure 5.3 Employee Directory
    'image12.png': 4.60,  # Figure 5.4 Add Employee
    'image13.png': 4.60,  # Figure 5.5 Edit Employee
    'image14.png': 5.50,  # Figure 5.6 Settings View
    'image15.png': 5.50,  # Figure 5.7 Payroll Processor
    'image16.png': 4.50,  # Figure 5.8 Print Slip
    'image17.png': 4.60,  # Figure 5.9 Change Password
    'image18.png': 5.00   # Figure 5.10 Help Guide
}

# 5. Process word/document.xml
doc_xml_path = os.path.join(work_dir, 'word', 'document.xml')

# Register namespaces so ET doesn't generate ns0, ns1 etc.
namespaces = {
    'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main',
    'wp': 'http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing',
    'a': 'http://schemas.openxmlformats.org/drawingml/2006/main',
    'pic': 'http://schemas.openxmlformats.org/drawingml/2006/picture',
    'r': 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'
}
for prefix, uri in namespaces.items():
    ET.register_namespace(prefix, uri)

doc_tree = ET.parse(doc_xml_path)
doc_root = doc_tree.getroot()
body = doc_root.find('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}body')

w_p = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}p'
w_pPr = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}pPr'
w_jc = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}jc'
w_ind = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}ind'
w_spacing = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}spacing'
w_keepNext = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}keepNext'
w_t = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t'
w_val = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}val'

def get_para_text(p_elem):
    texts = [t.text for t in p_elem.findall(f'.//{w_t}') if t.text]
    return ''.join(texts).strip()

def has_drawing(p_elem):
    return len(p_elem.findall('.//{http://schemas.openxmlformats.org/wordprocessingml/2006/main}drawing')) > 0

paragraphs = list(body)
paras_to_remove = set()

for i, p_elem in enumerate(paragraphs):
    if p_elem.tag != w_p:
        continue
    
    if has_drawing(p_elem):
        # Find the blip and extent
        blips = p_elem.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/main}blip')
        if blips:
            r_id = blips[0].attrib.get('{http://schemas.openxmlformats.org/officeDocument/2006/relationships}embed')
            target_media = rid_to_target.get(r_id)
            if target_media and target_media in target_widths and target_media in image_dims:
                w_px, h_px = image_dims[target_media]
                target_w_in = target_widths[target_media]
                target_h_in = target_w_in * (h_px / float(w_px))
                cx = str(int(round(target_w_in * 914400)))
                cy = str(int(round(target_h_in * 914400)))
                
                # Update wp:extent
                for ext in p_elem.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing}extent'):
                    ext.attrib['cx'] = cx
                    ext.attrib['cy'] = cy
                # Update a:ext
                for ext in p_elem.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/main}ext'):
                    ext.attrib['cx'] = cx
                    ext.attrib['cy'] = cy
                
                print(f"Updated drawing for {target_media}: {target_w_in:.2f}in x {target_h_in:.2f}in (cx={cx}, cy={cy})")

        # Set proper paragraph properties: center, keep with next, zero indents, 6pt before, 3pt after
        p_pr = p_elem.find(w_pPr)
        if p_pr is None:
            p_pr = ET.Element(w_pPr)
            p_elem.insert(0, p_pr)
        
        # Center align
        jc = p_pr.find(w_jc)
        if jc is None:
            jc = ET.SubElement(p_pr, w_jc)
        jc.attrib[w_val] = 'center'
        
        # Zero indent
        ind = p_pr.find(w_ind)
        if ind is None:
            ind = ET.SubElement(p_pr, w_ind)
        ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}left'] = '0'
        ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}right'] = '0'
        ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}firstLine'] = '0'
        
        # Spacing: before=120 (6pt), after=60 (3pt)
        spacing = p_pr.find(w_spacing)
        if spacing is None:
            spacing = ET.SubElement(p_pr, w_spacing)
        spacing.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}before'] = '120'
        spacing.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}after'] = '60'
        
        # Keep with next so image & caption stay on same page
        if p_pr.find(w_keepNext) is None:
            p_pr.append(ET.Element(w_keepNext))
        
        # Check following paragraphs: remove blank ones until caption
        for j in range(i + 1, min(i + 5, len(paragraphs))):
            next_p = paragraphs[j]
            if next_p.tag != w_p:
                continue
            next_text = get_para_text(next_p)
            if not next_text and not has_drawing(next_p):
                paras_to_remove.add(next_p)
            elif next_text.startswith("Figure "):
                # Caption found: format it cleanly
                c_pr = next_p.find(w_pPr)
                if c_pr is None:
                    c_pr = ET.Element(w_pPr)
                    next_p.insert(0, c_pr)
                
                c_jc = c_pr.find(w_jc)
                if c_jc is None:
                    c_jc = ET.SubElement(c_pr, w_jc)
                c_jc.attrib[w_val] = 'center'
                
                c_ind = c_pr.find(w_ind)
                if c_ind is None:
                    c_ind = ET.SubElement(c_pr, w_ind)
                c_ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}left'] = '0'
                c_ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}right'] = '0'
                c_ind.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}firstLine'] = '0'
                
                c_sp = c_pr.find(w_spacing)
                if c_sp is None:
                    c_sp = ET.SubElement(c_pr, w_spacing)
                c_sp.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}before'] = '40'
                c_sp.attrib['{http://schemas.openxmlformats.org/wordprocessingml/2006/main}after'] = '160'
                break
            else:
                break

# Remove empty separator paragraphs between images and captions
removed_count = 0
for p_rem in paras_to_remove:
    body.remove(p_rem)
    removed_count += 1
print(f"Removed {removed_count} redundant blank gap paragraphs.")

# Save modified document.xml
doc_tree.write(doc_xml_path, xml_declaration=True, encoding='utf-8')
print("Saved modified document.xml")

# 6. Re-zip into updated docx
updated_docx_path = doc_path
temp_zip = doc_path + ".tmp.zip"

with zipfile.ZipFile(temp_zip, 'w', zipfile.ZIP_DEFLATED) as z_out:
    for root, dirs, files in os.walk(work_dir):
        for f in files:
            full_p = os.path.join(root, f)
            arc_name = os.path.relpath(full_p, work_dir)
            z_out.write(full_p, arc_name)

# Replace target docx
if os.path.exists(doc_path):
    os.remove(doc_path)
os.rename(temp_zip, doc_path)
print(f"Successfully repacked and updated {doc_path}!")
