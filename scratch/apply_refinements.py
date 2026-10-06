import re
import sys

with open('build_pristine_pbl_report.py', 'r', encoding='utf-8') as f:
    code = f.read()

# =========================================================================
# 1. HEADING FONT SIZES
# =========================================================================
# Chapter heading -> 16 pt bold
code = code.replace(
    'run.font.size = Pt(14)\n        run.font.color.rgb = COLOR_BLACK\n        return p\n\n    def add_section_heading',
    'run.font.size = Pt(16)\n        run.font.color.rgb = COLOR_BLACK\n        return p\n\n    def add_section_heading'
)

# Section heading -> 14 pt bold
code = code.replace(
    'run.font.size = Pt(13)\n        run.font.color.rgb = COLOR_BLACK\n        return p\n\n    def add_subsection_heading',
    'run.font.size = Pt(14)\n        run.font.color.rgb = COLOR_BLACK\n        return p\n\n    def add_subsection_heading'
)

# Also check References and Appendix main headings
code = code.replace(
    'r_ref_h.font.size = Pt(14)',
    'r_ref_h.font.size = Pt(16)'
)
code = code.replace(
    'r_app_h.font.size = Pt(14)',
    'r_app_h.font.size = Pt(16)'
)

# =========================================================================
# 2. TABLE FORMATTING: Bold the left-most column
# =========================================================================
# In add_aligned_table: add bold_col0 parameter (default True)
old_table_func = """    def add_aligned_table(headers, rows, col_widths, col_alignments, cell_top=80, cell_bottom=80, font_sz=10.5, trailing_p=True):
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

new_table_func = """    def add_aligned_table(headers, rows, col_widths, col_alignments, cell_top=80, cell_bottom=80, font_sz=10.5, trailing_p=True, bold_col0=True):
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
                # Bold the left-most column when it represents labels, categories, parameters, or identifiers
                if bold_col0 and c_idx == 0:
                    run.bold = True

        # Explicit column widths
        for row in table.rows:
            for i, w in enumerate(col_widths):
                row.cells[i].width = Inches(w)

        if trailing_p:
            p_after = doc.add_paragraph()
            p_after.paragraph_format.space_before = Pt(0)
            p_after.paragraph_format.space_after = Pt(4)
        return table"""

if old_table_func in code:
    code = code.replace(old_table_func, new_table_func)

# =========================================================================
# 3. SELECTIVE BOLDING HELPER
# =========================================================================
# Enhance add_academic_p to support selective bold markup e.g. **term**
old_academic_p = """    def add_academic_p(text, bold=False, italic=False, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=0, space_after=6, line_spacing=1.15):
        p = doc.add_paragraph()
        p.alignment = align
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = line_spacing
        run = p.add_run(text)
        run.bold = bold
        run.italic = italic
        run.font.name = "Times New Roman"
        run.font.size = Pt(12)
        run.font.color.rgb = COLOR_BLACK
        return p"""

new_academic_p = """    def add_academic_p(text, bold=False, italic=False, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=0, space_after=6, line_spacing=1.15):
        p = doc.add_paragraph()
        p.alignment = align
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = line_spacing
        
        # Parse **bold** markers if present
        if "**" in text and not bold:
            parts = re.split(r'(\*\*.*?\*\*)', text)
            for part in parts:
                if part.startswith("**") and part.endswith("**"):
                    r = p.add_run(part[2:-2])
                    r.bold = True
                else:
                    r = p.add_run(part)
                    r.bold = False
                r.italic = italic
                r.font.name = "Times New Roman"
                r.font.size = Pt(12)
                r.font.color.rgb = COLOR_BLACK
        else:
            run = p.add_run(text)
            run.bold = bold
            run.italic = italic
            run.font.name = "Times New Roman"
            run.font.size = Pt(12)
            run.font.color.rgb = COLOR_BLACK
        return p"""

if old_academic_p in code:
    code = code.replace(old_academic_p, new_academic_p)

# Also support **bold** in add_bullet_p
old_bullet_p = """    def add_bullet_p(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.left_indent = Inches(0.4)
        p.paragraph_format.first_line_indent = Inches(-0.25)
        p.paragraph_format.space_before = Pt(1)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.line_spacing = 1.15
        
        r_bullet = p.add_run("•  ")
        r_bullet.bold = True
        r_bullet.font.name = "Times New Roman"
        r_bullet.font.size = Pt(12)
        r_bullet.font.color.rgb = COLOR_BLACK
        
        run = p.add_run(text)
        run.font.name = "Times New Roman"
        run.font.size = Pt(12)
        run.font.color.rgb = COLOR_BLACK
        return p"""

new_bullet_p = """    def add_bullet_p(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.left_indent = Inches(0.4)
        p.paragraph_format.first_line_indent = Inches(-0.25)
        p.paragraph_format.space_before = Pt(1)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.line_spacing = 1.15
        
        r_bullet = p.add_run("•  ")
        r_bullet.bold = True
        r_bullet.font.name = "Times New Roman"
        r_bullet.font.size = Pt(12)
        r_bullet.font.color.rgb = COLOR_BLACK
        
        if "**" in text:
            parts = re.split(r'(\*\*.*?\*\*)', text)
            for part in parts:
                if part.startswith("**") and part.endswith("**"):
                    r = p.add_run(part[2:-2])
                    r.bold = True
                else:
                    r = p.add_run(part)
                    r.bold = False
                r.font.name = "Times New Roman"
                r.font.size = Pt(12)
                r.font.color.rgb = COLOR_BLACK
        else:
            run = p.add_run(text)
            run.font.name = "Times New Roman"
            run.font.size = Pt(12)
            run.font.color.rgb = COLOR_BLACK
        return p"""

if old_bullet_p in code:
    code = code.replace(old_bullet_p, new_bullet_p)

with open('scratch/build_pristine_pbl_report_refined.py', 'w', encoding='utf-8') as f:
    f.write(code)

print("Created scratch/build_pristine_pbl_report_refined.py with heading sizes, bold columns, and markdown bold parsing.")
