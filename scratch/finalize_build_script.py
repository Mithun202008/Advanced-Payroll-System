import sys

with open('build_pristine_pbl_report.py', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. Ensure qn is imported
if 'from docx.oxml.ns import nsdecls, qn' not in code:
    code = code.replace('from docx.oxml.ns import nsdecls', 'from docx.oxml.ns import nsdecls, qn')

# 2. Update setup_centered_page_number
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

if old_setup_pnum in code:
    code = code.replace(old_setup_pnum, new_setup_pnum)

# 3. Update Section 8 margins
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

if old_sec8_margins in code:
    code = code.replace(old_sec8_margins, new_sec8_margins)

# 4. Update heading functions with explicit w:jc center and zero indent
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

if old_headings in code:
    code = code.replace(old_headings, new_headings)

# 5. Update add_aligned_table with cell_top, cell_bottom, font_sz, trailing_p
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

if old_aligned_table in code:
    code = code.replace(old_aligned_table, new_aligned_table)

# 6. Update TOC tab stop position to 9533 dxa
code = code.replace('tabs = parse_xml(f\'<w:tabs {nsdecls("w")}><w:tab w:val="right" w:leader="dot" w:pos="9028"/></w:tabs>\')', 'tabs = parse_xml(f\'<w:tabs {nsdecls("w")}><w:tab w:val="right" w:leader="dot" w:pos="9533"/></w:tabs>\')')

# 7. Update front-matter centered headings with explicit w:jc center
for var_name in ['p_abs', 'p_toc', 'p_lf', 'p_lt', 'p_la', 'p_ref_h', 'p_app_h']:
    old_p_align = f"{var_name}.alignment = WD_ALIGN_PARAGRAPH.CENTER"
    new_p_align = f"""{var_name}.alignment = WD_ALIGN_PARAGRAPH.CENTER
    _pPr_{var_name} = {var_name}._p.get_or_add_pPr()
    _jc_{var_name} = _pPr_{var_name}.find(qn('w:jc'))
    if _jc_{var_name} is None:
        _pPr_{var_name}.append(parse_xml(f'<w:jc {{nsdecls(\"w\")}} w:val=\"center\"/>'))
    else:
        _jc_{var_name}.set(qn('w:val'), 'center')"""
    if old_p_align in code:
        code = code.replace(old_p_align, new_p_align, 1)

# 8. Update Table of Contents entries with synchronized page numbers
old_toc_entries = """    toc_entries = [
        ("CHAPTER 1: INTRODUCTION", "14", True, False),
        ("1.1 Background", "14", False, True),
        ("1.2 Driving Question", "14", False, True),
        ("1.3 Objectives", "14", False, True),
        ("1.4 Scope and Limitations", "15", False, True),
        ("1.5 Project Overview", "15", False, True),
        ("1.6 Organisation of the Report", "16", False, True),
        ("CHAPTER 2: CONCEPT EXPLORATION", "17", True, False),
        ("2.1 Related Approaches", "17", False, True),
        ("2.2 Summary Table", "17", False, True),
        ("2.3 What This Told Us", "18", False, True),
        ("CHAPTER 3: PROJECT PLANNING AND TEAM ORGANISATION", "19", True, False),
        ("3.1 Weekly PBL Progress Log", "19", False, True),
        ("3.2 Requirements", "19", False, True),
        ("3.3 Feasibility", "20", False, True),
        ("CHAPTER 4: SYSTEM DESIGN AND DEVELOPMENT", "21", True, False),
        ("4.1 System Architecture", "21", False, True),
        ("4.2 Component Descriptions", "21", False, True),
        ("4.3 Modules and Responsibilities", "22", False, True),
        ("4.4 Class Design", "23", False, True),
        ("4.5 Calculation Engine and Math Formulation", "24", False, True),
        ("4.6 Data Flow and Relational Data Dictionary", "24", False, True),
        ("4.7 Security Design", "25", False, True),
        ("CHAPTER 5: IMPLEMENTATION", "26", True, False),
        ("5.1 Core Java Classes", "26", False, True),
        ("5.2 Object-Oriented Concepts Used", "27", False, True),
        ("5.3 Key Code Snippets", "28", False, True),
        ("5.4 User Interface / Demo", "31", False, True),
        ("CHAPTER 6: RESULTS AND DISCUSSION", "38", True, False),
        ("6.1 Evaluation Metrics", "38", False, True),
        ("6.2 Results Across Iterations", "38", False, True),
        ("6.3 Discussion", "39", False, True),
        ("6.4 Limitations", "39", False, True),
        ("6.5 Note on Machine Learning Metrics", "40", False, True),
        ("CHAPTER 7: TEAM REFLECTION AND LEARNING OUTCOMES", "41", True, False),
        ("7.1 Individual Reflections", "41", False, True),
        ("7.2 Team Learning", "41", False, True),
        ("7.3 Course Outcomes — Evidence Summary", "42", False, True),
        ("CHAPTER 8: CONCLUSION AND FUTURE SCOPE", "43", True, False),
        ("8.1 Conclusion", "43", False, True),
        ("8.2 Future Scope", "43", False, True),
        ("REFERENCES", "44", True, False),
        ("APPENDIX", "45", True, False),
        ("A.1 Full source code", "45", False, True),
        ("A.2 Complete Weekly PBL Log and Mentor Sign-offs", "45", False, True),
        ("A.3 Self and Peer Assessment", "45", False, True),
    ]"""

new_toc_entries = """    toc_entries = [
        ("CHAPTER 1: INTRODUCTION", "14", True, False),
        ("1.1 Background", "14", False, True),
        ("1.2 Driving Question", "14", False, True),
        ("1.3 Objectives", "14", False, True),
        ("1.4 Scope and Limitations", "15", False, True),
        ("1.5 Project Overview", "15", False, True),
        ("1.6 Organisation of the Report", "16", False, True),
        ("CHAPTER 2: CONCEPT EXPLORATION", "17", True, False),
        ("2.1 Related Approaches", "17", False, True),
        ("2.2 Summary Table", "17", False, True),
        ("2.3 What This Told Us", "18", False, True),
        ("CHAPTER 3: PROJECT PLANNING AND TEAM ORGANISATION", "19", True, False),
        ("3.1 Weekly PBL Progress Log", "19", False, True),
        ("3.2 Requirements", "19", False, True),
        ("3.3 Feasibility", "20", False, True),
        ("CHAPTER 4: SYSTEM DESIGN AND DEVELOPMENT", "22", True, False),
        ("4.1 System Architecture", "22", False, True),
        ("4.2 Component Descriptions", "22", False, True),
        ("4.3 Modules and Responsibilities", "23", False, True),
        ("4.4 Class Design", "24", False, True),
        ("4.5 Calculation Engine and Math Formulation", "24", False, True),
        ("4.6 Data Flow and Relational Data Dictionary", "25", False, True),
        ("4.7 Security Design", "27", False, True),
        ("CHAPTER 5: IMPLEMENTATION", "28", True, False),
        ("5.1 Core Java Classes", "28", False, True),
        ("5.2 Object-Oriented Concepts Used", "29", False, True),
        ("5.3 Key Code Snippets", "29", False, True),
        ("5.4 User Interface / Demo", "32", False, True),
        ("CHAPTER 6: RESULTS AND DISCUSSION", "39", True, False),
        ("6.1 Evaluation Metrics", "39", False, True),
        ("6.2 Results Across Iterations", "39", False, True),
        ("6.3 Discussion", "40", False, True),
        ("6.4 Limitations", "40", False, True),
        ("6.5 Note on Machine Learning Metrics", "41", False, True),
        ("CHAPTER 7: TEAM REFLECTION AND LEARNING OUTCOMES", "42", True, False),
        ("7.1 Individual Reflections", "42", False, True),
        ("7.2 Team Learning", "42", False, True),
        ("7.3 Course Outcomes — Evidence Summary", "42", False, True),
        ("CHAPTER 8: CONCLUSION AND FUTURE SCOPE", "44", True, False),
        ("8.1 Conclusion", "44", False, True),
        ("8.2 Future Scope", "44", False, True),
        ("REFERENCES", "45", True, False),
        ("APPENDIX", "46", True, False),
        ("A.1 Full source code", "46", False, True),
        ("A.2 Complete Weekly PBL Log and Mentor Sign-offs", "46", False, True),
        ("A.3 Self and Peer Assessment", "46", False, True),
    ]"""

if old_toc_entries in code:
    code = code.replace(old_toc_entries, new_toc_entries)

# 9. Update fig_list_data with synchronized page numbers
old_fig_list = """    fig_list_data = [
        ["1.1", "Enterprise Payroll Overall Workflow", "20"],
        ["4.1", "Enterprise Payroll Desktop System Architecture", "26"],
        ["4.2", "Object-Oriented Domain Class Diagram", "28"],
        ["4.3", "Relational Database Schema & Entity-Relationship Model", "30"],
        ["5.1", "System Authentication & Security Gateway (LoginFrame)", "38"],
        ["5.2", "Executive Desktop Dashboard & MDI Ribbon Interface (MainMenu)", "39"],
        ["5.3", "Employee Personnel Directory with Search and CSV Export", "39"],
        ["5.4", "Employee Registration Form with Automated Code Generation", "40"],
        ["5.5", "Employee Record Modification & Persistence Interface", "41"],
        ["5.6", "Compensation Matrix & Statutory Rule Configuration", "41"],
        ["5.7", "Automated Payroll Calculation & Payslip Generator", "42"],
        ["5.8", "Printable Salary Slip Document & Hardcopy Generator", "43"],
        ["5.9", "Administrative Security & Password Management", "43"],
        ["5.10", "Integrated User Documentation & Operational Help", "44"],
    ]
    # Total width = 1.0 + 4.47 + 0.8 = 6.27 in
    add_aligned_table(
        ["Figure No.", "Title", "Page"], 
        fig_list_data, 
        col_widths=[1.0, 4.47, 0.8], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER]
    )"""

new_fig_list = """    fig_list_data = [
        ["1.1", "Enterprise Payroll Overall Workflow", "15"],
        ["4.1", "Enterprise Payroll Desktop System Architecture", "22"],
        ["4.2", "Object-Oriented Domain Class Diagram", "24"],
        ["4.3", "Relational Database Schema & Entity-Relationship Model", "25"],
        ["5.1", "System Authentication & Security Gateway (LoginFrame)", "33"],
        ["5.2", "Executive Desktop Dashboard & MDI Ribbon Interface (MainMenu)", "33"],
        ["5.3", "Employee Personnel Directory with Search and CSV Export", "34"],
        ["5.4", "Employee Registration Form with Automated Code Generation", "34"],
        ["5.5", "Employee Record Modification & Persistence Interface", "35"],
        ["5.6", "Compensation Matrix & Statutory Rule Configuration", "36"],
        ["5.7", "Automated Payroll Calculation & Payslip Generator", "36"],
        ["5.8", "Printable Salary Slip Document & Hardcopy Generator", "37"],
        ["5.9", "Administrative Security & Password Management", "37"],
        ["5.10", "Integrated User Documentation & Operational Help", "38"],
    ]
    add_aligned_table(
        ["Figure No.", "Title", "Page"], 
        fig_list_data, 
        col_widths=[1.0, 4.82, 0.8], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER],
        trailing_p=False
    )"""

if old_fig_list in code:
    code = code.replace(old_fig_list, new_fig_list)

# 10. Update tbl_list_data with synchronized page numbers
old_tbl_list = """    tbl_list_data = [
        ["2.1", "Comparative Evaluation of Payroll Management Approaches", "21"],
        ["3.1", "Weekly PBL Progress Log", "23"],
        ["3.2", "Hardware and Software Requirements", "23"],
        ["4.1", "Architectural Modules and Operational Responsibilities", "27"],
        ["4.2", "Relational Data Dictionary (EMPLOYEE, Settings, Login)", "30"],
        ["5.1", "Core Java Classes and Responsibilities", "33"],
        ["5.2", "OOP Concepts Used in Enterprise Payroll System", "34"],
        ["6.1", "System Verification and Functional Quality Metrics", "45"],
        ["6.2", "Evolutionary Milestone Trajectory Across Project Iterations", "45"],
        ["6.3", "Technical Limitations and Architectural Boundaries", "46"],
        ["7.1", "Mapping of Course Outcomes (POs/PSOs) to Project Evidence", "49"],
        ["A.1", "Self and Peer Assessment", "52"],
    ]
    add_aligned_table(
        ["Table No.", "Title", "Page"], 
        tbl_list_data, 
        col_widths=[1.0, 4.47, 0.8], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER]
    )"""

new_tbl_list = """    tbl_list_data = [
        ["2.1", "Comparative Evaluation of Payroll Management Approaches", "17"],
        ["3.1", "Weekly PBL Progress Log", "19"],
        ["3.2", "Hardware and Software Requirements", "19"],
        ["4.1", "Architectural Modules and Operational Responsibilities", "23"],
        ["4.2", "Relational Data Dictionary (EMPLOYEE, Settings, Login)", "26"],
        ["5.1", "Core Java Classes and Responsibilities", "28"],
        ["5.2", "OOP Concepts Used in Enterprise Payroll System", "29"],
        ["6.1", "System Verification and Functional Quality Metrics", "39"],
        ["6.2", "Evolutionary Milestone Trajectory Across Project Iterations", "39"],
        ["6.3", "Technical Limitations and Architectural Boundaries", "40"],
        ["7.1", "Mapping of Course Outcomes (POs/PSOs) to Project Evidence", "42"],
        ["A.1", "Self and Peer Assessment", "46"],
    ]
    add_aligned_table(
        ["Table No.", "Title", "Page"], 
        tbl_list_data, 
        col_widths=[1.0, 4.82, 0.8], 
        col_alignments=[WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER],
        trailing_p=False
    )"""

if old_tbl_list in code:
    code = code.replace(old_tbl_list, new_tbl_list)

# 11. Update Abbreviations to fit Page 13 perfectly
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

new_abbr_block = """    abbr_data = [
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

if old_abbr_block in code:
    code = code.replace(old_abbr_block, new_abbr_block)

# Ensure target output path is Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx
code = code.replace('output_path = "scratch/test_build.docx"', 'output_path = "Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx"')
code = code.replace('output_path = "scratch/test_build_v2.docx"', 'output_path = "Enterprise_Payroll_Accounting_Management_System_PBL_REPORT.docx"')

with open('build_pristine_pbl_report.py', 'w', encoding='utf-8') as f:
    f.write(code)

print("build_pristine_pbl_report.py successfully updated and finalized!")
