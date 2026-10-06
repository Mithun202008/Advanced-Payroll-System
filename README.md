# Enterprise Payroll & Attendance Management System

A comprehensive, full-featured Payroll, Attendance, and Human Resource Management System featuring both a modernized **Java Desktop GUI (Swing)** and an embedded **Web SaaS Portal (HTTP Server)** with real-time analytics, automated salary computation, role-based access control, and comprehensive reporting.

---

## 🚀 Key Features

- **Multi-Modal Interface**:
  - **Modern Desktop Client**: Sleek Java Swing dashboard with custom components, KPI cards, sidebar navigation, dark/light modern themes, and interactive charts.
  - **Web SaaS Portal**: Embedded zero-dependency HTTP server delivering an interactive web dashboard accessible via browser (`http://localhost:8080`).
- **Core HR & Employee Management**:
  - Full CRUD lifecycle for employee records (personal details, departments, designations, basic pay, contact info).
  - Employee list viewer with search, filtering, and detailed profile inspectors.
- **Automated Payroll Computation Engine**:
  - Configurable allowances (DA, HRA, Medical, Conveyance) and deductions (PF, ESI, Professional Tax, TDS).
  - Net salary auto-calculation with slip generation, batch processing, and payment logging.
- **Attendance & Leave Management**:
  - Daily attendance tracking (Present, Absent, Half-Day, On-Leave).
  - Leave approval workflows and balance tracking across departments.
- **Security & Role-Based Access Control (RBAC)**:
  - Multi-tier roles (`ADMIN`, `HR_MANAGER`, `PAYROLL_OFFICER`, `EMPLOYEE`).
  - Secure credential management, audit logging, and activity monitoring.
- **Reporting & Analytics**:
  - In-app printable salary slips and departmental payroll summaries.
  - Interactive distribution charts and department breakdowns.
  - Python-powered automated PBL report generators for Word (.docx) and PDF.

---

## 🛠️ Technology Stack

- **Core Application**: Java (JDK 8+)
- **Desktop UI**: Java Swing with custom modernized UI components (`ModernTheme`, `DashboardCharts`, `ModernSidebar`)
- **Web Portal**: Java HTTP Server (`com.sun.net.httpserver`) with embedded REST APIs and dynamic HTML/CSS dashboards
- **Database Engine**: Microsoft Access (`PayRoll.mdb`) powered by `UCanAccess` / `Jackcess` JDBC drivers
- **Reporting Utilities**: Python (python-docx, docx2pdf) for automated documentation and report generation

---

## 📁 Project Structure

```text
├── images/               # Application assets, icons, and logos
├── lib/
│   └── ucanaccess-uber.jar # All-in-one UCanAccess JDBC database driver
├── pbl/                  # Project-based learning documentation & reports
├── scratch/              # Automation, testing scripts, and report builders
├── .gitignore            # Git exclusion rules
├── MakeDB.ini            # JDBC connection configuration
├── PayRoll.mdb           # Relational Access database
├── README.md             # Project documentation
├── LoginFrame.java       # Desktop GUI authentication entry point
├── MainMenu.java         # Main dashboard & desktop hub
├── PayrollWebServer.java # Embedded Web SaaS portal & API server
├── run.bat / run.ps1     # One-click desktop launcher scripts
└── run_web.bat / run_web.ps1 # One-click web portal launcher scripts
```

---

## ⚡ Getting Started

### Prerequisites

1. **Java Development Kit (JDK)**: Java 8 or higher installed and configured in your `PATH`.
2. *(Optional)* **Python 3.x**: Required only if generating or building custom Word/PDF reports.

---

### Running the Desktop Application

Execute the batch script or PowerShell script:

```bash
# Windows Command Prompt / Double-click
run.bat

# Or using PowerShell
.\run.ps1
```

Or compile and run manually via Java CLI:

```bash
javac -cp ".;lib/ucanaccess-uber.jar" *.java
java -cp ".;lib/ucanaccess-uber.jar" LoginFrame
```

---

### Running the Web SaaS Portal

Launch the embedded web server:

```bash
# Windows Command Prompt / Double-click
run_web.bat

# Or using PowerShell
.\run_web.ps1
```

Then open your browser at:
```text
http://localhost:8080
```

---

## 📜 Database Configuration

The application reads database connection parameters from `MakeDB.ini`:

```ini
jdbc.drivers=net.ucanaccess.jdbc.UcanaccessDriver
jdbc.url=jdbc:ucanaccess://PayRoll.mdb
jdbc.username=
jdbc.password=
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
