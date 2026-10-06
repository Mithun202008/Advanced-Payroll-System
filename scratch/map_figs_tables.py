import pypdf
import sys

sys.stdout.reconfigure(encoding='utf-8')
reader = pypdf.PdfReader('scratch/test_build_v2.pdf')

figs = [
    ("1.1", "Enterprise Payroll Overall Workflow"),
    ("4.1", "Enterprise Payroll Desktop System Architecture"),
    ("4.2", "Object-Oriented Domain Class Diagram"),
    ("4.3", "Relational Database Schema & Entity-Relationship Model"),
    ("5.1", "System Authentication & Security Gateway"),
    ("5.2", "Executive Desktop Dashboard"),
    ("5.3", "Employee Personnel Directory"),
    ("5.4", "Employee Registration Form"),
    ("5.5", "Employee Record Modification"),
    ("5.6", "Compensation Matrix"),
    ("5.7", "Automated Payroll Calculation"),
    ("5.8", "Printable Salary Slip"),
    ("5.9", "Administrative Security"),
    ("5.10", "Integrated User Documentation"),
]

tbls = [
    ("2.1", "Comparative Evaluation of Payroll Management Approaches"),
    ("3.1", "Weekly PBL Progress Log"),
    ("3.2", "Hardware and Software Requirements"),
    ("4.1", "Architectural Modules and Operational Responsibilities"),
    ("4.2", "Relational Data Dictionary"),
    ("5.1", "Core Java Classes and Responsibilities"),
    ("5.2", "OOP Concepts Used in Enterprise Payroll System"),
    ("6.1", "System Verification and Functional Quality Metrics"),
    ("6.2", "Evolutionary Milestone Trajectory Across Project Iterations"),
    ("6.3", "Technical Limitations and Architectural Boundaries"),
    ("7.1", "Mapping of Course Outcomes"),
    ("A.1", "Self and Peer Assessment"),
]

print("=== LOCATING FIGURES IN BODY ===")
for num, title in figs:
    found_pg = None
    for p_idx in range(13, len(reader.pages)):
        txt = reader.pages[p_idx].extract_text()
        if f"Figure {num}" in txt:
            found_pg = p_idx + 1
            break
    print(f"Figure {num}: {found_pg}")

print("\n=== LOCATING TABLES IN BODY ===")
for num, title in tbls:
    found_pg = None
    for p_idx in range(13, len(reader.pages)):
        txt = reader.pages[p_idx].extract_text()
        if f"Table {num}" in txt:
            found_pg = p_idx + 1
            break
    print(f"Table {num}: {found_pg}")
