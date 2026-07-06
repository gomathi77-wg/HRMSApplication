# Human Resource Management System (HRMS)

An enterprise-grade desktop application engineered in Core Java using the Swing framework to automate essential workplace administration workflows. This platform delivers an intuitive visual interface tailored for HR managers, stepping away from standard console-based designs.

## 🚀 Key Features
* **Secure HR Access:** Credentials-restricted admin gateway (`admin` / `admin123`).
* **Employee Profiles:** Real-time data updates organized inside clean interactive tables.
* **Attendance Tracking:** Simple module to log daily employee records (Present, Absent, Leave).
* **Leave Management:** Dual-action panel to submit, track, and process leave applications.
* **Dynamic Reports:** Automatically renders synchronized breakdowns of active data.
* **File Serialization:** Persists all database metrics safely into local text logs (.txt).

## 🛠️ Installation & Setup
1. **Compile:** `javac HRMSApplication.java`
2. **Execute:** `java HRMSApplication`

## 📂 Architecture
* **Frontend:** Java Swing & AWT API (CardLayout, JTable)
* **Data Layer:** Standard Java File I/O Streams (`BufferedReader`, `PrintWriter`)
*
