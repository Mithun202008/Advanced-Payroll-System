import re

# Read the refined script
with open('scratch/build_pristine_pbl_report_refined.py', 'r', encoding='utf-8') as f:
    text = f.read()

# Let's enrich the paragraphs with crisp, theoretical wording and selective bolding (**term**)

# 1. Background enrichment
old_bg1 = 'add_academic_p("Payroll administration constitutes one of the most critical operational workflows in modern institutional and corporate governance. Every enterprise, regardless of its industry vertical, is legally and operationally obligated to disburse timely, accurate, and mathematically verifiable compensation to its workforce while maintaining strict compliance with statutory taxation, provident fund regulations, and internal institutional salary matrices.")'
new_bg1 = 'add_academic_p("**Payroll administration** constitutes one of the most critical operational workflows in modern institutional and corporate governance. Every enterprise, regardless of its industry vertical, is legally and operationally obligated to disburse timely, accurate, and mathematically verifiable compensation to its workforce while maintaining strict compliance with **statutory taxation**, **provident fund regulations**, and internal institutional salary matrices.")'
text = text.replace(old_bg1, new_bg1)

old_bg2 = 'add_academic_p("Historically, small-to-medium enterprises (SMEs) and academic institutions relied heavily on manual physical ledgers or fragmented spreadsheet software (such as Microsoft Excel) to log employee working days, calculate base compensation, and deduct statutory withholdings. However, empirical analysis demonstrates that spreadsheet-based payroll management suffers from severe systemic vulnerabilities. Unintentional formula overwrites, lack of referential data constraints, lack of audit trails, absence of centralized security, and concurrency conflicts frequently cause salary miscalculations, employee dissatisfaction, and regulatory penalties.")'
new_bg2 = 'add_academic_p("Historically, small-to-medium enterprises (SMEs) and academic institutions relied heavily on manual physical ledgers or fragmented spreadsheet software (such as Microsoft Excel) to record employee working days, calculate base compensation, and deduct statutory withholdings. However, empirical software engineering analysis demonstrates that spreadsheet-based payroll management suffers from severe systemic vulnerabilities. **Unintentional formula overwrites**, **lack of referential integrity constraints**, **absence of centralized security**, and **uncontrolled concurrent modifications** frequently cause salary miscalculations, employee dissatisfaction, and regulatory penalties.")'
text = text.replace(old_bg2, new_bg2)

old_bg3 = 'add_academic_p("To eliminate these human vulnerabilities and provide complete computational reproducibility, contemporary software engineering advocates for dedicated desktop-based payroll management systems. By coupling a strongly typed programming language such as Java with a robust relational database engine, organizations can enforce strict data types, referential integrity constraints, automated code generation, and transactional isolation. This project addresses the operational need for an autonomous, deterministic, and user-friendly desktop payroll platform tailored for enterprise deployment.")'
new_bg3 = 'add_academic_p("To eliminate these vulnerabilities and guarantee **computational reproducibility**, contemporary software engineering advocates for dedicated desktop-based payroll management systems. By coupling a strongly typed language such as **Java SE** with a robust relational database engine, organizations enforce **strict data typing**, **referential integrity constraints**, **automated key generation**, and **transactional isolation**. This project addresses the operational need for an autonomous, deterministic, and user-friendly desktop payroll platform tailored for institutional and enterprise deployment.")'
text = text.replace(old_bg3, new_bg3)

# 2. Driving Question
old_dq = 'add_academic_p("“How can an integrated, secure, and offline-resilient Java Swing desktop application streamline end-to-end payroll administration—from employee onboarding and position-based salary grade configuration to real-time statutory deduction calculation, dynamic payslip generation, and audit-ready reporting—while maintaining ACID transaction integrity on relational flat-file databases?”")'
new_dq = 'add_academic_p("“How can an integrated, secure, and offline-resilient **Java Swing desktop application** streamline end-to-end payroll administration—from employee onboarding and designation-based salary grade configuration to real-time statutory deduction calculation, dynamic payslip generation, and audit-ready reporting—while maintaining **ACID transaction integrity** on relational flat-file databases?”")'
text = text.replace(old_dq, new_dq)

# 3. Objectives
old_obj1 = 'add_bullet_p("1. Relational Database Modeling and Persistence Architecture: Design and deploy a 3NF relational schema in Microsoft Access (PayRoll.mdb) managed via the pure-Java UCanAccess JDBC driver bridge, providing cross-platform flat-file data persistence without requiring heavy database server installations.")'
new_obj1 = 'add_bullet_p("1. **Relational Database Modeling & Persistence Architecture**: Design and deploy a Third Normal Form (3NF) relational schema in Microsoft Access (**PayRoll.mdb**) managed via the pure-Java **UCanAccess JDBC driver bridge**, providing flat-file data persistence with full ACID compliance without requiring external database server installations.")'
text = text.replace(old_obj1, new_obj1)

old_obj2 = 'add_bullet_p("2. Secure Role-Based Desktop Interface: Construct a responsive, modern Multiple Document Interface (MDI) utilizing Java Foundation Classes (Swing / AWT) with centralized authentication, session tracking, and runtime administrative password management.")'
new_obj2 = 'add_bullet_p("2. **Secure Role-Based Desktop Interface**: Construct a responsive, modern **Multiple Document Interface (MDI)** utilizing **Java Foundation Classes (Swing / AWT)** with centralized authentication, session tracking, and runtime administrative password management.")'
text = text.replace(old_obj2, new_obj2)

old_obj3 = 'add_bullet_p("3. Automated Personnel Lifecycle Management: Implement full CRUD (Create, Read, Update, Delete) employee record administration featuring automated alphanumeric identifier generation (emp_xxx) and real-time searchable personnel directories with multi-column sorting and CSV export functionality.")'
new_obj3 = 'add_bullet_p("3. **Automated Personnel Lifecycle Management**: Implement full **CRUD employee record administration** featuring automated alphanumeric identifier generation (**emp_xxx**) and real-time searchable personnel directories with multi-column sorting (**TableRowSorter**) and CSV export functionality.")'
text = text.replace(old_obj3, new_obj3)

old_obj4 = 'add_bullet_p("4. Dual-Mode Mathematical Salary Engine: Develop a deterministic compensation calculation engine capable of computing allowances (DA, HRA, WA) and statutory deductions (GPF, IT, GIS, PF, LIC) using dynamically configurable position-based percentages or absolute currency values.")'
new_obj4 = 'add_bullet_p("4. **Dual-Mode Mathematical Salary Engine**: Develop a deterministic compensation calculation engine capable of computing allowances (**DA, HRA, WA**) and statutory deductions (**GPF, IT, GIS, PF, LIC**) using dynamically configurable position-based percentages or absolute currency values.")'
text = text.replace(old_obj4, new_obj4)

old_obj5 = 'add_bullet_p("5. High-Fidelity Printable Reporting Subsystem: Build an in-memory document rendering engine implementing the java.awt.print.Printable interface to deliver formatted salary slip previews, hardcopy printing, and portable documentation.")'
new_obj5 = 'add_bullet_p("5. **High-Fidelity Printable Reporting Subsystem**: Build an in-memory document rendering engine implementing the **java.awt.print.Printable** interface to deliver formatted salary slip previews, physical hardcopy printing, and verifiable audit records.")'
text = text.replace(old_obj5, new_obj5)

# 4. Scope and Limitations
old_scope_list = [
    ('add_bullet_p("Standalone desktop workstation deployment with complete offline functionality.")', 'add_bullet_p("**Standalone Workstation Deployment**: Complete operational capability in offline, air-gapped institutional environments.")'),
    ('add_bullet_p("Administrative authentication and session tracking.")', 'add_bullet_p("**Administrative Authentication**: Credential verification, session identity tracking, and runtime password encryption.")'),
    ('add_bullet_p("Employee onboarding, demographic records, and job designation assignment.")', 'add_bullet_p("**Personnel Lifecycle Administration**: Structured onboarding, contact records, and job designation assignment.")'),
    ('add_bullet_p("Designation-based compensation matrix configuration (Basic Pay, DA, HRA, WA, GPF, IT, GIS, PF, LIC).")', 'add_bullet_p("**Designation-Based Salary Matrices**: Dynamic configuration of basic pay, allowance percentages, and statutory deduction policies.")'),
    ('add_bullet_p("Real-time employee directory filtering via regex and TableRowSorter.")', 'add_bullet_p("**Interactive Directory Filtering**: Real-time multi-column search via **TableRowSorter** and case-insensitive regex.")'),
    ('add_bullet_p("Instant CSV export of tabular personnel records for spreadsheet auditing.")', 'add_bullet_p("**Tabular Data Serialization**: Instant CSV export conforming to standard RFC-4180 formats for external auditing.")'),
    ('add_bullet_p("Dynamic payslip generation and automated net compensation reconciliation.")', 'add_bullet_p("**Deterministic Compensation Calculation**: Real-time mathematical reconciliation of gross earnings against statutory withholdings.")'),
    ('add_bullet_p("Hardware printer integration and print preview using Java 2D vector drawing.")', 'add_bullet_p("**Hardware Printing & Preview**: High-fidelity 2D vector rendering via **Printable API** with zero-defect pagination.")'),
    ('add_bullet_p("Local database isolation and transactional ACID compliance via UCanAccess.")', 'add_bullet_p("**Embedded Database Persistence**: Local relational storage isolation with **ACID transaction guarantees** via UCanAccess.")')
]
for old_s, new_s in old_scope_list:
    text = text.replace(old_s, new_s)

# 5. Concept Exploration: 2.1.1 to 2.1.4
old_211 = 'add_academic_p("Traditional paper-based bookkeeping relies on physical ledgers to record employee clock-in hours, base wages, and manual deductions. While requiring no electrical power, manual bookkeeping is exceptionally slow, highly prone to human mathematical errors, susceptible to physical destruction, and entirely incapable of automated auditing.")'
new_211 = 'add_academic_p("Traditional paper-based bookkeeping relies on physical ledgers to record attendance, calculate base wages, and compute deductions. While requiring no electrical infrastructure, manual bookkeeping is exceptionally slow, highly susceptible to **human arithmetic error**, vulnerable to physical loss, and entirely incapable of automated cross-referencing or institutional auditing.")'
text = text.replace(old_211, new_211)

old_212 = 'add_academic_p("Electronic spreadsheets represent the most widespread legacy transition for small businesses. They allow users to construct tabular rows with embedded formulas. However, spreadsheets lack relational database constraints, allow arbitrary formula overwriting, offer no built-in transactional ACID isolation, and fail to prevent duplicate primary keys or unauthorized salary modifications.")'
new_212 = 'add_academic_p("Electronic spreadsheets represent the most common transitional software for small organizations. While offering basic tabular formulas, spreadsheets lack **relational schema constraints**, permit silent cell and formula corruption, provide no **ACID transactional guarantees**, and fail to prevent duplicate primary keys or unauthorized salary modifications.")'
text = text.replace(old_212, new_212)

old_213 = 'add_academic_p("Modern cloud payroll platforms provide automated tax compliance, employee self-service portals, and cloud backups. However, SaaS platforms impose steep per-employee monthly licensing fees, require continuous high-speed internet connectivity, expose sensitive employee financial records to third-party cloud hosting risks, and present rigid configuration options that small organizations cannot easily customize.")'
new_213 = 'add_academic_p("Modern cloud payroll platforms offer automated compliance and remote self-service. However, enterprise SaaS systems demand steep recurring per-seat licensing fees, mandate uninterrupted WAN connectivity, expose sensitive salary records to **third-party cloud hosting risks**, and present rigid operational workflows that cannot be tailored to specialized organizational compensation rules.")'
text = text.replace(old_213, new_213)

old_214 = 'add_academic_p("Desktop database applications built with high-performance runtime environments (such as Java SE) and local relational database files combine the data integrity of relational SQL engines with the responsive, zero-latency user experience of local native GUIs. They require zero recurring subscription fees, function completely offline, and maintain complete corporate data sovereignty.")'
new_214 = 'add_academic_p("Standalone desktop applications engineered with **Java SE** and embedded relational database engines combine the **structural data integrity of SQL-92** with the responsive, zero-latency user experience of native graphical desktop environments. They incur zero recurring subscription costs, execute completely offline, and maintain complete institutional **data sovereignty**.")'
text = text.replace(old_214, new_214)

# 6. Chapter 4 Architecture
old_arch = 'add_academic_p("The Enterprise Payroll Accounting System is designed following a robust Three-Tier Model-View-Controller (MVC) architectural pattern adapted for desktop client applications. By decoupling visual presentation from business logic and database persistence, the architecture maximizes code maintainability, simplifies unit testing, and ensures fault isolation.")'
new_arch = 'add_academic_p("The Enterprise Payroll Accounting System is architected following a classical **Three-Tier Model-View-Controller (MVC)** architectural paradigm adapted for standalone desktop environments. By cleanly decoupling visual presentation from business logic and relational database persistence, the architecture maximizes **code maintainability**, facilitates **isolated component testing**, and enforces **fault isolation** across operational modules.")'
text = text.replace(old_arch, new_arch)

# 7. Chapter 4 Math
old_m1 = 'add_academic_p("The payroll calculation engine executes a deterministic two-stage mathematical algorithm to reconcile gross earnings and statutory deductions. Let the employee\'s base compensation be denoted as Basic Pay (BP).")'
new_m1 = 'add_academic_p("The payroll calculation engine executes a deterministic two-stage mathematical algorithm to reconcile gross earnings and statutory deductions. Let the employee\'s base contractual compensation be denoted as **Basic Pay (BP)**.")'
text = text.replace(old_m1, new_m1)

# Overwrite build_pristine_pbl_report.py with refined code
with open('build_pristine_pbl_report.py', 'w', encoding='utf-8') as f:
    f.write(text)

print("build_pristine_pbl_report.py updated successfully with crisp academic content and selective bolding.")
