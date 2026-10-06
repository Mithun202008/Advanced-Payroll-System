import re

with open('build_pristine_pbl_report.py', 'r', encoding='utf-8') as f:
    code = f.read()
code = code.replace('from docx.oxml.ns import nsdecls', 'from docx.oxml.ns import nsdecls, qn')

# 1. Update setup_centered_page_number to include explicit w:jc center
old_setup_pnum = """    def setup_centered_page_number(p):
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.left_indent = Inches(0)
        p.paragraph_format.right_indent = Inches(0)
        p.paragraph_format.first_line_indent = Inches(0)
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(0)
        p.text = \"\""""

new_setup_pnum = """    def setup_centered_page_number(p):
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        pPr = p._p.get_or_add_pPr()
        jc = pPr.find(qn('w:jc'))
        if jc is None:
            pPr.append(parse_xml(f'<w:jc {nsdecls(\"w\")} w:val=\"center\"/>'))
        else:
            jc.set(qn('w:val'), 'center')
        p.paragraph_format.left_indent = Inches(0)
        p.paragraph_format.right_indent = Inches(0)
        p.paragraph_format.first_line_indent = Inches(0)
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(0)
        p.text = \"\""""

code = code.replace(old_setup_pnum, new_setup_pnum)

# 2. Update Section 8 margins
old_sec8_margins = """    # Section 8 (Body Section): uniform 1.0 inch margins and Bottom Center page numbers
    body_sec = doc.sections[8]
    body_sec.different_first_page_header_footer = False
    body_sec.top_margin = Inches(1.0)
    body_sec.bottom_margin = Inches(1.0)
    body_sec.left_margin = Inches(1.0)
    body_sec.right_margin = Inches(1.0)"""

new_sec8_margins = """    # Section 8 (Body Section): Margins matching template geometry and Bottom Center page numbers
    body_sec = doc.sections[8]
    body_sec.different_first_page_header_footer = False
    body_sec.top_margin = Inches(0.85)
    body_sec.bottom_margin = Inches(0.65)
    body_sec.left_margin = Inches(1.0)
    body_sec.right_margin = Inches(0.65)
    body_sec.footer_distance = Inches(0.35)"""

code = code.replace(old_sec8_margins, new_sec8_margins)

# 3. Update heading functions with explicit w:jc center and zero indent
old_headings = """    def add_chapter_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(18)
        p.paragraph_format.space_after = Pt(8)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(14)
        run.font.color.rgb = COLOR_BLACK
        return p

    def add_section_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(14)
        run.font.color.rgb = COLOR_BLACK
        return p

    def add_subsection_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(12)
        run.font.color.rgb = COLOR_BLACK
        return p"""

new_headings = """    def add_chapter_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        pPr = p._p.get_or_add_pPr()
        jc = pPr.find(qn('w:jc'))
        if jc is None:
            pPr.append(parse_xml(f'<w:jc {nsdecls(\"w\")} w:val=\"center\"/>'))
        else:
            jc.set(qn('w:val'), 'center')
        p.paragraph_format.left_indent = Inches(0)
        p.paragraph_format.right_indent = Inches(0)
        p.paragraph_format.first_line_indent = Inches(0)
        p.paragraph_format.space_before = Pt(18)
        p.paragraph_format.space_after = Pt(8)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(14)
        run.font.color.rgb = COLOR_BLACK
        return p

    def add_section_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        pPr = p._p.get_or_add_pPr()
        jc = pPr.find(qn('w:jc'))
        if jc is None:
            pPr.append(parse_xml(f'<w:jc {nsdecls(\"w\")} w:val=\"center\"/>'))
        else:
            jc.set(qn('w:val'), 'center')
        p.paragraph_format.left_indent = Inches(0)
        p.paragraph_format.right_indent = Inches(0)
        p.paragraph_format.first_line_indent = Inches(0)
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(13)
        run.font.color.rgb = COLOR_BLACK
        return p

    def add_subsection_heading(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        pPr = p._p.get_or_add_pPr()
        jc = pPr.find(qn('w:jc'))
        if jc is None:
            pPr.append(parse_xml(f'<w:jc {nsdecls(\"w\")} w:val=\"center\"/>'))
        else:
            jc.set(qn('w:val'), 'center')
        p.paragraph_format.left_indent = Inches(0)
        p.paragraph_format.right_indent = Inches(0)
        p.paragraph_format.first_line_indent = Inches(0)
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = "Times New Roman"
        run.font.size = Pt(12)
        run.font.color.rgb = COLOR_BLACK
        return p"""

code = code.replace(old_headings, new_headings)

# 4. Update add_aligned_table with cell_top, cell_bottom, font_sz, trailing_p
old_aligned_table = """    def add_aligned_table(headers, rows, col_widths, col_alignments):
        table = doc.add_table(rows=len(rows) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        set_table_borders_black(table)

        # Header Row: Bold, perfectly aligned over data
        hdr_row = table.rows[0]
        for i, h in enumerate(headers):
            cell = hdr_row.cells[i]
            set_cell_margins(cell, top=100, bottom=100, left=120, right=120)
            p = cell.paragraphs[0]
            p.alignment = col_alignments[i]
            p.paragraph_format.space_before = Pt(0)
            p.paragraph_format.space_after = Pt(0)
            run = p.add_run(h)
            run.bold = True
            run.font.name = "Times New Roman"
            run.font.size = Pt(11)
            run.font.color.rgb = COLOR_BLACK

        # Data Rows: Identical alignment, zero shading, clean margins
        for r_idx, row_data in enumerate(rows):
            row = table.rows[r_idx + 1]
            for c_idx, val in enumerate(row_data):
                cell = row.cells[c_idx]
                set_cell_margins(cell, top=80, bottom=80, left=120, right=120)
                cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
                p = cell.paragraphs[0]
                p.alignment = col_alignments[c_idx]
                p.paragraph_format.space_before = Pt(0)
                p.paragraph_format.space_after = Pt(0)
                p.paragraph_format.line_spacing = 1.15
                run = p.add_run(str(val))
                run.font.name = "Times New Roman"
                run.font.size = Pt(10.5)
                run.font.color.rgb = COLOR_BLACK

        # Explicit column widths matching exactly 6.27 inches total
        for row in table.rows:
            for i, w in enumerate(col_widths):
                row.cells[i].width = Inches(w)

        p_after = doc.add_paragraph()
        p_after.paragraph_format.space_before = Pt(0)
        p_after.paragraph_format.space_after = Pt(4)
        return table"""

new_aligned_table = """    def add_aligned_table(headers, rows, col_widths, col_alignments, cell_top=80, cell_bottom=80, font_sz=10.5, trailing_p=True):
        table = doc.add_table(rows=len(rows) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        set_table_borders_black(table)

        # Header Row: Bold, perfectly aligned over data
        hdr_row = table.rows[0]
        for i, h in enumerate(headers):
            cell = hdr_row.cells[i]
            set_cell_margins(cell, top=max(cell_top, 80), bottom=max(cell_bottom, 80), left=120, right=120)
            p = cell.paragraphs[0]
            p.alignment = col_alignments[i]
            p.paragraph_format.space_before = Pt(0)
            p.paragraph_format.space_after = Pt(0)
            run = p.add_run(h)
            run.bold = True
            run.font.name = "Times New Roman"
            run.font.size = Pt(11)
            run.font.color.rgb = COLOR_BLACK

        # Data Rows: Identical alignment, zero shading, clean margins
        for r_idx, row_data in enumerate(rows):
            row = table.rows[r_idx + 1]
            for c_idx, val in enumerate(row_data):
                cell = row.cells[c_idx]
                set_cell_margins(cell, top=cell_top, bottom=cell_bottom, left=120, right=120)
                cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
                p = cell.paragraphs[0]
                p.alignment = col_alignments[c_idx]
                p.paragraph_format.space_before = Pt(0)
                p.paragraph_format.space_after = Pt(0)
                p.paragraph_format.line_spacing = 1.15
                run = p.add_run(str(val))
                run.font.name = "Times New Roman"
                run.font.size = Pt(font_sz)
                run.font.color.rgb = COLOR_BLACK

        # Explicit column widths
        for row in table.rows:
            for i, w in enumerate(col_widths):
                row.cells[i].width = Inches(w)

        if trailing_p:
            p_after = doc.add_paragraph()
            p_after.paragraph_format.space_before = Pt(0)
            p_after.paragraph_format.space_after = Pt(4)
        return table"""

code = code.replace(old_aligned_table, new_aligned_table)

# 5. Update TOC tab stop position to 9533 dxa (6.62 in) and ensure center jc on p_toc
code = code.replace('tabs = parse_xml(f\'<w:tabs {nsdecls("w")}><w:tab w:val="right" w:leader="dot" w:pos="9028"/></w:tabs>\')', 'tabs = parse_xml(f\'<w:tabs {nsdecls("w")}><w:tab w:val="right" w:leader="dot" w:pos="9533"/></w:tabs>\')')

# 6. Ensure p_abs, p_toc, p_lf, p_lt, p_la, p_ref_h, p_app_h have explicit w:jc center
for var_name in ['p_abs', 'p_toc', 'p_lf', 'p_lt', 'p_la', 'p_ref_h', 'p_app_h']:
    old_p_align = f"{var_name}.alignment = WD_ALIGN_PARAGRAPH.CENTER"
    new_p_align = f"""{var_name}.alignment = WD_ALIGN_PARAGRAPH.CENTER
    _pPr_{var_name} = {var_name}._p.get_or_add_pPr()
    _jc_{var_name} = _pPr_{var_name}.find(qn('w:jc'))
    if _jc_{var_name} is None:
        _pPr_{var_name}.append(parse_xml(f'<w:jc {{nsdecls(\"w\")}} w:val=\"center\"/>'))
    else:
        _jc_{var_name}.set(qn('w:val'), 'center')"""
    code = code.replace(old_p_align, new_p_align, 1)

# 7. Update List of Figures and Tables to pass trailing_p=False
code = code.replace(
    'add_aligned_table(\n        ["Figure No.", "Title", "Page"], \n        fig_list_data, \n        col_widths=[1.0, 4.47, 0.8], \n        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER]\n    )',
    'add_aligned_table(\n        ["Figure No.", "Title", "Page"], \n        fig_list_data, \n        col_widths=[1.0, 4.82, 0.8], \n        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER], trailing_p=False\n    )'
)

code = code.replace(
    'add_aligned_table(\n        ["Table No.", "Title", "Page"], \n        tbl_list_data, \n        col_widths=[1.0, 4.47, 0.8], \n        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER]\n    )',
    'add_aligned_table(\n        ["Table No.", "Title", "Page"], \n        tbl_list_data, \n        col_widths=[1.0, 4.82, 0.8], \n        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER], trailing_p=False\n    )'
)

# 8. Streamline Abbreviations data so it fits on Page 13 with compact row height
abbr_replacement = """    abbr_data = [
        ["ACID", "Atomicity, Consistency, Isolation, Durability"],
        ["API", "Application Programming Interface"],
        ["AWT", "Abstract Window Toolkit"],
        ["BP", "Basic Pay"],
        ["CSV", "Comma-Separated Values"],
        ["DA", "Dearness Allowance"],
        ["DBMS", "Database Management System"],
        ["EDT", "Event Dispatch Thread"],
        ["EPF", "Employees' Provident Fund"],
        ["GIS", "Group Insurance Scheme"],
        ["GP", "Gross Pay"],
        ["GPF", "General Provident Fund"],
        ["GUI", "Graphical User Interface"],
        ["HRA", "House Rent Allowance"],
        ["IDE", "Integrated Development Environment"],
        ["IT", "Income Tax"],
        ["JDBC", "Java Database Connectivity"],
        ["JDK", "Java Development Kit"],
        ["JVM", "Java Virtual Machine"],
        ["LIC", "Life Insurance Corporation"],
        ["MDI", "Multiple Document Interface"],
        ["NP", "Net Pay"],
        ["OOP", "Object-Oriented Programming"],
        ["PBL", "Project-Based Learning"],
        ["PF", "Provident Fund"],
        ["SQL", "Structured Query Language"],
        ["UI", "User Interface"],
        ["WA", "Washing Allowance"],
    ]
    add_aligned_table(
        ["Abbreviation", "Expansion"], 
        abbr_data, 
        col_widths=[1.8, 4.82], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT],
        cell_top=35, cell_bottom=35, font_sz=9.5, trailing_p=False
    )"""

old_abbr_block = """    abbr_data = [
        ["ACID", "Atomicity, Consistency, Isolation, Durability"],
        ["API", "Application Programming Interface"],
        ["AWT", "Abstract Window Toolkit"],
        ["BP", "Basic Pay"],
        ["CSV", "Comma-Separated Values"],
        ["DA", "Dearness Allowance"],
        ["DBMS", "Database Management System"],
        ["EDT", "Event Dispatch Thread"],
        ["GIS", "Group Insurance Scheme"],
        ["GP", "Gross Pay"],
        ["GPF", "General Provident Fund"],
        ["GUI", "Graphical User Interface"],
        ["HRA", "House Rent Allowance"],
        ["HSQLDB", "HyperSQL Database (Underlying UCanAccess embedded engine)"],
        ["IDE", "Integrated Development Environment"],
        ["IT", "Income Tax"],
        ["JDBC", "Java Database Connectivity"],
        ["JDK", "Java Development Kit"],
        ["JFC", "Java Foundation Classes"],
        ["JVM", "Java Virtual Machine"],
        ["LIC", "Life Insurance Corporation"],
        ["MDB", "Microsoft Access Database File Format"],
        ["MDI", "Multiple Document Interface"],
        ["MVC", "Model-View-Controller"],
        ["NP", "Net Pay"],
        ["OOP", "Object-Oriented Programming"],
        ["PBL", "Project-Based Learning"],
        ["PF", "Provident Fund"],
        ["PO", "Program Outcome"],
        ["PSO", "Program Specific Outcome"],
        ["SQL", "Structured Query Language"],
        ["TD", "Total Deductions"],
        ["WA", "Washing Allowance"],
    ]
    # Total width = 1.8 + 4.47 = 6.27 in
    add_aligned_table(
        ["Abbreviation", "Expansion"], 
        abbr_data, 
        col_widths=[1.8, 4.47], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT]
    )"""

code = code.replace(old_abbr_block, abbr_replacement)

# Set output path to test_build_v2.docx
code = code.replace('output_path = "Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx"', 'output_path = "scratch/test_build_v2.docx"')

with open('scratch/run_test_v2.py', 'w', encoding='utf-8') as f:
    f.write(code)

print("Created scratch/run_test_v2.py")
