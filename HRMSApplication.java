import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HRMSApplication extends JFrame {
    // Data storage files
    private static final String EMP_FILE = "employees.txt";
    private static final String ATT_FILE = "attendance.txt";
    private static final String LEAVE_FILE = "leaves.txt";

    // Data models
    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayList<Attendance> attendanceRecords = new ArrayList<>();
    private ArrayList<LeaveRequest> leaveRequests = new ArrayList<>();

    // Current logged in state
    private boolean isAuthenticated = false;

    // CardLayout for switching panels
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    public HRMSApplication() {
        setTitle("Human Resource Management System (HRMS)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load data from files
        loadData();

        // Initialize Views
        initLoginPanel();
        initDashboardPanel();

        add(mainContainer);
        cardLayout.show(mainContainer, "Login");
    }

    // --- Data Models ---
    static class Employee implements Serializable {
        String id, name, designation, department, contact;
        public Employee(String id, String name, String designation, String department, String contact) {
            this.id = id; this.name = name; this.designation = designation; this.department = department; this.contact = contact;
        }
    }

    static class Attendance implements Serializable {
        String empId, date, status;
        public Attendance(String empId, String date, String status) {
            this.empId = empId; this.date = date; this.status = status;
        }
    }

    static class LeaveRequest implements Serializable {
        String empId, leaveType, startDate, endDate, status;
        public LeaveRequest(String empId, String leaveType, String startDate, String endDate, String status) {
            this.empId = empId; this.leaveType = leaveType; this.startDate = startDate; this.endDate = endDate; this.status = status;
        }
    }

    // --- Persistence Management ---
    private void loadData() {
        try {
            File f1 = new File(EMP_FILE);
            if(f1.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f1));
                String line;
                while((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if(p.length == 5) employees.add(new Employee(p[0], p[1], p[2], p[3], p[4]));
                }
                br.close();
            }
            File f2 = new File(ATT_FILE);
            if(f2.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f2));
                String line;
                while((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if(p.length == 3) attendanceRecords.add(new Attendance(p[0], p[1], p[2]));
                }
                br.close();
            }
            File f3 = new File(LEAVE_FILE);
            if(f3.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f3));
                String line;
                while((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if(p.length == 5) leaveRequests.add(new LeaveRequest(p[0], p[1], p[2], p[3], p[4]));
                }
                br.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            PrintWriter pw1 = new PrintWriter(new FileWriter(EMP_FILE));
            for(Employee e : employees) pw1.println(e.id + "," + e.name + "," + e.designation + "," + e.department + "," + e.contact);
            pw1.close();

            PrintWriter pw2 = new PrintWriter(new FileWriter(ATT_FILE));
            for(Attendance a : attendanceRecords) pw2.println(a.empId + "," + a.date + "," + a.status);
            pw2.close();

            PrintWriter pw3 = new PrintWriter(new FileWriter(LEAVE_FILE));
            for(LeaveRequest l : leaveRequests) pw3.println(l.empId + "," + l.leaveType + "," + l.startDate + "," + l.endDate + "," + l.status);
            pw3.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data save error: " + e.getMessage());
        }
    }

    // --- GUI Components ---
    private void initLoginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 244, 248));

        JLabel lblTitle = new JLabel("HRMS Authentication", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setBounds(250, 100, 400, 40);
        panel.add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(300, 200, 100, 30);
        panel.add(lblUser);

        JTextField txtUser = new JTextField("admin");
        txtUser.setBounds(400, 200, 180, 30);
        panel.add(txtUser);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(300, 250, 100, 30);
        panel.add(lblPass);

        JPasswordField txtPass = new JPasswordField("admin123");
        txtPass.setBounds(400, 250, 180, 30);
        panel.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(400, 310, 100, 35);
        btnLogin.setBackground(new Color(33, 150, 243));
        btnLogin.setForeground(Color.WHITE);
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            if(user.equals("admin") && pass.equals("admin123")) {
                isAuthenticated = true;
                cardLayout.show(mainContainer, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid credentials! Use admin / admin123", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainContainer.add(panel, "Login");
    }

    private void initDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        // Navigation Sidebar
        JPanel sidebar = new JPanel(new GridLayout(7, 1, 5, 5));
        sidebar.setBackground(new Color(38, 50, 56));
        sidebar.setPreferredSize(new Dimension(200, 650));

        JButton btnEmp = createSidebarButton("Employees");
        JButton btnAtt = createSidebarButton("Attendance");
        JButton btnLeave = createSidebarButton("Leave Rules");
        JButton btnSearch = createSidebarButton("Search");
        JButton btnReport = createSidebarButton("Reports");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(btnEmp);
        sidebar.add(btnAtt);
        sidebar.add(btnLeave);
        sidebar.add(btnSearch);
        sidebar.add(btnReport);
        sidebar.add(new JLabel("")); // spacer
        sidebar.add(btnLogout);

        // Sub Panels Container
        CardLayout subCard = new CardLayout();
        JPanel contentArea = new JPanel(subCard);

        // 1. Employee Management Panel
        JPanel empPanel = createEmployeePanel();
        contentArea.add(empPanel, "Employees");

        // 2. Attendance Panel
        JPanel attPanel = createAttendancePanel();
        contentArea.add(attPanel, "Attendance");

        // 3. Leave Panel
        JPanel leavePanel = createLeavePanel();
        contentArea.add(leavePanel, "Leave");

        // 4. Search Panel
        JPanel searchPanel = createSearchPanel();
        contentArea.add(searchPanel, "Search");

        // 5. Reports Panel
        JPanel reportPanel = createReportsPanel();
        contentArea.add(reportPanel, "Reports");

        // Button Actions
        btnEmp.addActionListener(e -> subCard.show(contentArea, "Employees"));
        btnAtt.addActionListener(e -> subCard.show(contentArea, "Attendance"));
        btnLeave.addActionListener(e -> subCard.show(contentArea, "Leave"));
        btnSearch.addActionListener(e -> subCard.show(contentArea, "Search"));
        btnReport.addActionListener(e -> {
            contentArea.add(createReportsPanel(), "Reports"); // refresh data view
            subCard.show(contentArea, "Reports");
        });
        btnLogout.addActionListener(e -> {
            isAuthenticated = false;
            cardLayout.show(mainContainer, "Login");
        });

        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(contentArea, BorderLayout.CENTER);

        mainContainer.add(dashboardPanel, "Dashboard");
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(55, 71, 79));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    // --- Sub Panels Factories ---
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Designation", "Department", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        for(Employee e : employees) model.addRow(new Object[]{e.id, e.name, e.designation, e.department, e.contact});

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Manage Employee Data"));

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtDesig = new JTextField();
        JTextField txtDept = new JTextField();
        JTextField txtContact = new JTextField();

        inputPanel.add(new JLabel("Employee ID:")); inputPanel.add(txtId);
        inputPanel.add(new JLabel("Full Name:")); inputPanel.add(txtName);
        inputPanel.add(new JLabel("Designation:")); inputPanel.add(txtDesig);
        inputPanel.add(new JLabel("Department:")); inputPanel.add(txtDept);
        inputPanel.add(new JLabel("Contact Info:")); inputPanel.add(txtContact);

        JButton btnAdd = new JButton("Add/Update");
        JButton btnDel = new JButton("Delete Selected");
        inputPanel.add(btnAdd); inputPanel.add(btnDel);

        btnAdd.addActionListener(e -> {
            if(txtId.getText().isEmpty() || txtName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "ID and Name are required!");
                return;
            }
            // Check if exist for update
            boolean found = false;
            for(Employee emp : employees) {
                if(emp.id.equals(txtId.getText())) {
                    emp.name = txtName.getText();
                    emp.designation = txtDesig.getText();
                    emp.department = txtDept.getText();
                    emp.contact = txtContact.getText();
                    found = true;
                    break;
                }
            }
            if(!found) {
                employees.add(new Employee(txtId.getText(), txtName.getText(), txtDesig.getText(), txtDept.getText(), txtContact.getText()));
            }
            saveData();
            // Refresh table
            model.setRowCount(0);
            for(Employee emp : employees) model.addRow(new Object[]{emp.id, emp.name, emp.designation, emp.department, emp.contact});
            JOptionPane.showMessageDialog(panel, "Operation Successful!");
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                String id = model.getValueAt(row, 0).toString();
                employees.removeIf(emp -> emp.id.equals(id));
                saveData();
                model.removeRow(row);
                JOptionPane.showMessageDialog(panel, "Employee Deleted!");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Mark Daily Attendance"));

        JTextField txtId = new JTextField();
        JTextField txtDate = new JTextField("2026-07-02");
        String[] statuses = {"Present", "Absent", "On Leave"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);

        inputPanel.add(new JLabel("Employee ID:")); inputPanel.add(txtId);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):")); inputPanel.add(txtDate);
        inputPanel.add(new JLabel("Status:")); inputPanel.add(cbStatus);

        JButton btnSave = new JButton("Record Attendance");
        inputPanel.add(new JLabel("")); inputPanel.add(btnSave);

        btnSave.addActionListener(e -> {
            if(txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter Employee ID!");
                return;
            }
            attendanceRecords.add(new Attendance(txtId.getText(), txtDate.getText(), cbStatus.getSelectedItem().toString()));
            saveData();
            JOptionPane.showMessageDialog(panel, "Attendance recorded successfully!");
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLeavePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Emp ID", "Type", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for(LeaveRequest l : leaveRequests) model.addRow(new Object[]{l.empId, l.leaveType, l.startDate, l.endDate, l.status});

        JPanel actionPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Submit / Process Leave Applications"));

        JTextField txtId = new JTextField();
        JTextField txtType = new JTextField("Casual");
        JTextField txtStart = new JTextField("2026-07-05");
        JTextField txtEnd = new JTextField("2026-07-07");

        actionPanel.add(new JLabel("Employee ID:")); actionPanel.add(txtId);
        actionPanel.add(new JLabel("Leave Type:")); actionPanel.add(txtType);
        actionPanel.add(new JLabel("Start Date:")); actionPanel.add(txtStart);
        actionPanel.add(new JLabel("End Date:")); actionPanel.add(txtEnd);

        JButton btnSubmit = new JButton("Submit Request");
        JButton btnApprove = new JButton("Approve Selected");
        JButton btnReject = new JButton("Reject Selected");

        actionPanel.add(btnSubmit); actionPanel.add(btnApprove);
        actionPanel.add(btnReject);

        btnSubmit.addActionListener(e -> {
            if(txtId.getText().isEmpty()) return;
            LeaveRequest lr = new LeaveRequest(txtId.getText(), txtType.getText(), txtStart.getText(), txtEnd.getText(), "Pending");
            leaveRequests.add(lr);
            saveData();
            model.addRow(new Object[]{lr.empId, lr.leaveType, lr.startDate, lr.endDate, lr.status});
        });

        ActionListener approvalListener = e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                String action = e.getActionCommand().contains("Approve") ? "Approved" : "Rejected";
                leaveRequests.get(row).status = action;
                saveData();
                model.setValueAt(action, row, 4);
                JOptionPane.showMessageDialog(panel, "Leave status updated to: " + action);
            }
        };

        btnApprove.addActionListener(approvalListener);
        btnReject.addActionListener(approvalListener);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        
        JTextField txtQuery = new JTextField(20);
        String[] criteria = {"Name", "ID", "Department"};
        JComboBox<String> cbCriteria = new JComboBox<>(criteria);
        JButton btnSearch = new JButton("Search");

        topPanel.add(new JLabel("Search Criteria:"));
        topPanel.add(cbCriteria);
        topPanel.add(txtQuery);
        topPanel.add(btnSearch);

        String[] columns = {"ID", "Name", "Designation", "Department", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        btnSearch.addActionListener(e -> {
            model.setRowCount(0);
            String query = txtQuery.getText().toLowerCase();
            int crit = cbCriteria.getSelectedIndex(); // 0: Name, 1: ID, 2: Dept

            for(Employee emp : employees) {
                boolean match = false;
                if(crit == 0 && emp.name.toLowerCase().contains(query)) match = true;
                if(crit == 1 && emp.id.toLowerCase().contains(query)) match = true;
                if(crit == 2 && emp.department.toLowerCase().contains(query)) match = true;

                if(match) {
                    model.addRow(new Object[]{emp.id, emp.name, emp.designation, emp.department, emp.contact});
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        JTextArea txtEmpReport = new JTextArea();
        txtEmpReport.setBorder(BorderFactory.createTitledBorder("Employee Summary Report"));
        txtEmpReport.setEditable(false);
        
        StringBuilder sb = new StringBuilder("=== Total Employees registered: " + employees.size() + " ===\n\n");
        for(Employee e : employees) {
            sb.append(String.format("ID: %s | %s [%s] -> Dept: %s\n", e.id, e.name, e.designation, e.department));
        }
        txtEmpReport.setText(sb.toString());

        JTextArea txtAttReport = new JTextArea();
        txtAttReport.setBorder(BorderFactory.createTitledBorder("Attendance & Leave Logs"));
        txtAttReport.setEditable(false);

        StringBuilder sb2 = new StringBuilder("=== Attendance Log ===\n");
        for(Attendance a : attendanceRecords) {
            sb2.append(String.format("Emp ID: %s | Date: %s | Status: %s\n", a.empId, a.date, a.status));
        }
        sb2.append("\n=== Leave Decisions ===\n");
        for(LeaveRequest l : leaveRequests) {
            sb2.append(String.format("Emp ID: %s | %s (%s to %s) -> %s\n", l.empId, l.leaveType, l.startDate, l.endDate, l.status));
        }
        txtAttReport.setText(sb2.toString());

        panel.add(new JScrollPane(txtEmpReport));
        panel.add(new JScrollPane(txtAttReport));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HRMSApplication().setVisible(true));
    }
}