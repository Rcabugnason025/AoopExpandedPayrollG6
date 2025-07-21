package view;

import model.Employee;
import util.PositionRoleMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Professional Employee Dashboard - Self-Service Portal
 * Provides employees with access to their personal information and basic HR functions
 */
public class EmployeeDashboard extends JFrame {
    private Employee currentEmployee;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer clockTimer;
    
    // Color scheme for professional look
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    public EmployeeDashboard(Employee employee) {
        this.currentEmployee = employee;
        initializeEmployeeInterface();
        startClock();
    }
    
    private void initializeEmployeeInterface() {
        setTitle("MotorPH Payroll System - Employee Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        // Set professional look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName()) || "Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default if system L&F fails
        }
        
        // Create menu bar
        createMenuBar();
        
        // Create main content
        createMainContent();
        
        // Set icon
        setIconImage(createCompanyIcon());
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Personal Information Menu
        JMenu personalMenu = createStyledMenu("👤 My Profile", "Access your personal information");
        personalMenu.add(createStyledMenuItem("View Profile", "View your complete profile", this::viewMyProfile));
        personalMenu.add(createStyledMenuItem("Edit Personal Info", "Update your personal details", this::editPersonalInfo));
        personalMenu.add(createStyledMenuItem("Change Password", "Update your login credentials", this::changePassword));
        personalMenu.addSeparator();
        personalMenu.add(createStyledMenuItem("Emergency Contacts", "Manage emergency contact information", this::manageEmergencyContacts));
        
        // Attendance Menu
        JMenu attendanceMenu = createStyledMenu("🕐 Attendance", "Manage your attendance and time tracking");
        attendanceMenu.add(createStyledMenuItem("Clock In/Out", "Record your attendance", this::clockInOut));
        attendanceMenu.add(createStyledMenuItem("My Attendance History", "View your attendance records", this::viewMyAttendance));
        attendanceMenu.add(createStyledMenuItem("Overtime Requests", "Submit overtime requests", this::requestOvertime));
        attendanceMenu.add(createStyledMenuItem("Time Correction", "Request time corrections", this::requestTimeCorrection));
        
        // Leave Management Menu
        JMenu leaveMenu = createStyledMenu("📅 Leave Management", "Manage your leave requests");
        leaveMenu.add(createStyledMenuItem("Request Leave", "Submit a new leave request", this::requestLeave));
        leaveMenu.add(createStyledMenuItem("My Leave Balance", "Check available leave credits", this::viewLeaveBalance));
        leaveMenu.add(createStyledMenuItem("Leave History", "View past leave requests", this::viewLeaveHistory));
        leaveMenu.add(createStyledMenuItem("Holiday Calendar", "View company holidays", this::viewHolidayCalendar));
        
        // Payroll Menu
        JMenu payrollMenu = createStyledMenu("💰 Payroll", "Access your salary and benefits information");
        payrollMenu.add(createStyledMenuItem("Current Payslip", "View latest payslip", this::viewCurrentPayslip));
        payrollMenu.add(createStyledMenuItem("Payslip History", "View all payslips", this::viewPayslipHistory));
        payrollMenu.add(createStyledMenuItem("Tax Documents", "Download tax certificates", this::viewTaxDocuments));
        payrollMenu.add(createStyledMenuItem("Benefits Summary", "View your benefits package", this::viewBenefitsSummary));
        
        // Documents Menu
        JMenu documentsMenu = createStyledMenu("📋 Documents", "Access important documents and forms");
        documentsMenu.add(createStyledMenuItem("Employee Handbook", "Company policies and procedures", this::viewEmployeeHandbook));
        documentsMenu.add(createStyledMenuItem("Forms Library", "Download HR forms", this::accessFormsLibrary));
        documentsMenu.add(createStyledMenuItem("Certificates", "View employment certificates", this::viewCertificates));
        
        // Help Menu
        JMenu helpMenu = createStyledMenu("❓ Help", "Get help and support");
        helpMenu.add(createStyledMenuItem("User Guide", "How to use this system", this::viewUserGuide));
        helpMenu.add(createStyledMenuItem("Contact HR", "Get in touch with HR", this::contactHR));
        helpMenu.add(createStyledMenuItem("Report Issue", "Report technical problems", this::reportIssue));
        helpMenu.addSeparator();
        helpMenu.add(createStyledMenuItem("About", "About MotorPH Payroll System", this::showAbout));
        
        // Logout Menu
        JMenu logoutMenu = createStyledMenu("🚪 Logout", "Sign out of the system");
        logoutMenu.add(createStyledMenuItem("Logout", "Sign out safely", this::logout));
        
        menuBar.add(personalMenu);
        menuBar.add(attendanceMenu);
        menuBar.add(leaveMenu);
        menuBar.add(payrollMenu);
        menuBar.add(documentsMenu);
        menuBar.add(Box.createHorizontalGlue()); // Push help and logout to the right
        menuBar.add(helpMenu);
        menuBar.add(logoutMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Content Panel with tabs
        JTabbedPane tabbedPane = createContentTabs();
        
        // Status Panel
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentEmployee.getFirstName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel("Employee Portal • " + currentEmployee.getPosition());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(welcomeLabel);
        textPanel.add(roleLabel);
        
        welcomePanel.add(textPanel);
        
        // Time and quick actions
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        rightPanel.setOpaque(false);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActionsPanel.setOpaque(false);
        
        JButton clockBtn = createHeaderButton("Clock In/Out", this::clockInOut);
        JButton profileBtn = createHeaderButton("My Profile", this::viewMyProfile);
        
        quickActionsPanel.add(clockBtn);
        quickActionsPanel.add(profileBtn);
        
        rightPanel.add(timeLabel);
        rightPanel.add(quickActionsPanel);
        
        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JTabbedPane createContentTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Dashboard Tab
        tabbedPane.addTab("🏠 Dashboard", createDashboardTab());
        
        // My Information Tab  
        tabbedPane.addTab("👤 My Information", createMyInfoTab());
        
        // Attendance Tab
        tabbedPane.addTab("🕐 Attendance", createAttendanceTab());
        
        // Payroll Tab
        tabbedPane.addTab("💰 Payroll", createPayrollTab());
        
        // Leave Tab
        tabbedPane.addTab("📅 Leave", createLeaveTab());
        
        return tabbedPane;
    }
    
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Quick Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        statsPanel.add(createStatCard("📊 This Month", "22 Days", "Days Worked", SUCCESS_COLOR));
        statsPanel.add(createStatCard("⏰ Today", "8.5 hrs", "Hours Logged", PRIMARY_COLOR));
        statsPanel.add(createStatCard("📅 Leave", "12 Days", "Available", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("💰 Salary", "₱" + String.format("%,.2f", currentEmployee.getBasicSalary()), "Monthly", new Color(230, 126, 34)));
        
        // Recent Activity Panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Recent Activity"));
        activityPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Date", "Activity", "Status"};
        Object[][] data = {
            {"Today", "Clocked In", "08:30 AM"},
            {"Yesterday", "Leave Request", "Approved"},
            {"Dec 1", "Payslip Generated", "Available"},
            {"Nov 30", "Overtime Request", "Pending"}
        };
        
        JTable activityTable = new JTable(data, columns);
        activityTable.setRowHeight(25);
        JScrollPane activityScroll = new JScrollPane(activityTable);
        activityPanel.add(activityScroll, BorderLayout.CENTER);
        
        // Announcements Panel
        JPanel announcementsPanel = new JPanel(new BorderLayout());
        announcementsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Company Announcements"));
        announcementsPanel.setBackground(Color.WHITE);
        
        JTextArea announcementsArea = new JTextArea(6, 30);
        announcementsArea.setText("📢 Holiday Notice: Christmas Party on December 20, 2024\n\n" +
                                "📋 Reminder: Submit your time sheets before month-end\n\n" +
                                "🎉 Congratulations to Employee of the Month winners!\n\n" +
                                "⚠️ System Maintenance: December 15, 10 PM - 2 AM");
        announcementsArea.setEditable(false);
        announcementsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane announcementsScroll = new JScrollPane(announcementsArea);
        announcementsPanel.add(announcementsScroll, BorderLayout.CENTER);
        
        // Layout
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(activityPanel);
        contentPanel.add(announcementsPanel);
        
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMyInfoTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Profile Card
        JPanel profileCard = new JPanel(new BorderLayout());
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        
        // Profile header
        JPanel profileHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profileHeader.setOpaque(false);
        
        JLabel profileIcon = new JLabel("👤");
        profileIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        
        JPanel profileText = new JPanel(new GridLayout(3, 1, 0, 2));
        profileText.setOpaque(false);
        
        JLabel nameLabel = new JLabel(currentEmployee.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        JLabel idLabel = new JLabel("Employee ID: " + currentEmployee.getEmployeeId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(Color.GRAY);
        
        JLabel positionLabel = new JLabel(currentEmployee.getPosition());
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        profileText.add(nameLabel);
        profileText.add(idLabel);
        profileText.add(positionLabel);
        
        profileHeader.add(profileIcon);
        profileHeader.add(Box.createHorizontalStrut(15));
        profileHeader.add(profileText);
        
        // Profile details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        addInfoField(detailsPanel, "📧 Email:", currentEmployee.getEmail() != null ? currentEmployee.getEmail() : "Not provided");
        addInfoField(detailsPanel, "📱 Phone:", currentEmployee.getPhoneNumber() != null ? currentEmployee.getPhoneNumber() : "Not provided");
        addInfoField(detailsPanel, "🏠 Address:", currentEmployee.getAddress() != null ? currentEmployee.getAddress() : "Not provided");
        addInfoField(detailsPanel, "🎂 Birthday:", currentEmployee.getBirthday() != null ? currentEmployee.getBirthday().toString() : "Not provided");
        addInfoField(detailsPanel, "📊 Status:", currentEmployee.getStatus());
        addInfoField(detailsPanel, "👔 Supervisor:", currentEmployee.getImmediateSupervisor() != null ? currentEmployee.getImmediateSupervisor() : "Not assigned");
        addInfoField(detailsPanel, "🆔 SSS No.:", currentEmployee.getSssNumber() != null ? currentEmployee.getSssNumber() : "Not provided");
        addInfoField(detailsPanel, "🏥 PhilHealth:", currentEmployee.getPhilhealthNumber() != null ? currentEmployee.getPhilhealthNumber() : "Not provided");
        
        profileCard.add(profileHeader, BorderLayout.NORTH);
        profileCard.add(detailsPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton editBtn = createActionButton("Edit Profile", "✏️", this::editPersonalInfo);
        JButton changePassBtn = createActionButton("Change Password", "🔐", this::changePassword);
        
        buttonPanel.add(editBtn);
        buttonPanel.add(changePassBtn);
        
        profileCard.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(profileCard, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Clock In/Out Panel
        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        clockPanel.setBackground(Color.WHITE);
        clockPanel.setBorder(BorderFactory.createTitledBorder("Time Clock"));
        
        JButton clockInBtn = createLargeActionButton("🕐 Clock In", "Record your arrival", SUCCESS_COLOR, this::clockInOut);
        JButton clockOutBtn = createLargeActionButton("🕕 Clock Out", "Record your departure", ACCENT_COLOR, this::clockInOut);
        
        clockPanel.add(clockInBtn);
        clockPanel.add(clockOutBtn);
        
        // Attendance Summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setOpaque(false);
        
        summaryPanel.add(createStatCard("📅 This Month", "22", "Days Present", SUCCESS_COLOR));
        summaryPanel.add(createStatCard("⏰ Avg Hours", "8.2", "Per Day", PRIMARY_COLOR));
        summaryPanel.add(createStatCard("⚡ Overtime", "15.5", "Hours Total", new Color(230, 126, 34)));
        
        // Recent Attendance Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Recent Attendance"));
        
        String[] columns = {"Date", "Day", "Time In", "Time Out", "Hours", "Status"};
        Object[][] attendanceData = {
            {"2024-12-02", "Monday", "08:30", "17:15", "8.75", "Present"},
            {"2024-12-01", "Friday", "08:45", "17:30", "8.75", "Late"},
            {"2024-11-30", "Thursday", "08:25", "17:00", "8.58", "Present"},
            {"2024-11-29", "Wednesday", "08:30", "16:45", "8.25", "Early Out"},
            {"2024-11-28", "Tuesday", "09:00", "18:00", "9.00", "Overtime"}
        };
        
        JTable attendanceTable = new JTable(attendanceData, columns);
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setBackground(PRIMARY_COLOR);
        attendanceTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setOpaque(false);
        
        actionPanel.add(createActionButton("View Full History", "📊", this::viewMyAttendance));
        actionPanel.add(createActionButton("Request Correction", "✏️", this::requestTimeCorrection));
        actionPanel.add(createActionButton("Overtime Request", "⏰", this::requestOvertime));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(clockPanel, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPayrollTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Salary Overview
        JPanel salaryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        salaryPanel.setOpaque(false);
        
        salaryPanel.add(createStatCard("💰 Basic Salary", "₱" + String.format("%,.2f", currentEmployee.getBasicSalary()), "Monthly", PRIMARY_COLOR));
        salaryPanel.add(createStatCard("🍚 Rice Subsidy", "₱" + String.format("%,.2f", currentEmployee.getRiceSubsidy()), "Monthly", SUCCESS_COLOR));
        salaryPanel.add(createStatCard("📱 Phone Allowance", "₱" + String.format("%,.2f", currentEmployee.getPhoneAllowance()), "Monthly", new Color(155, 89, 182)));
        salaryPanel.add(createStatCard("👔 Clothing Allowance", "₱" + String.format("%,.2f", currentEmployee.getClothingAllowance()), "Monthly", new Color(230, 126, 34)));
        
        // Payslip History
        JPanel payslipPanel = new JPanel(new BorderLayout());
        payslipPanel.setBackground(Color.WHITE);
        payslipPanel.setBorder(BorderFactory.createTitledBorder("Recent Payslips"));
        
        String[] payslipColumns = {"Period", "Gross Pay", "Deductions", "Net Pay", "Status", "Action"};
        Object[][] payslipData = {
            {"Dec 2024", "₱45,000.00", "₱8,500.00", "₱36,500.00", "Available", "Download"},
            {"Nov 2024", "₱45,000.00", "₱8,500.00", "₱36,500.00", "Available", "Download"},
            {"Oct 2024", "₱45,000.00", "₱8,500.00", "₱36,500.00", "Available", "Download"},
            {"Sep 2024", "₱45,000.00", "₱8,500.00", "₱36,500.00", "Available", "Download"}
        };
        
        JTable payslipTable = new JTable(payslipData, payslipColumns);
        payslipTable.setRowHeight(30);
        payslipTable.getTableHeader().setBackground(PRIMARY_COLOR);
        payslipTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane payslipScroll = new JScrollPane(payslipTable);
        payslipPanel.add(payslipScroll, BorderLayout.CENTER);
        
        // Quick Actions
        JPanel payrollActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        payrollActions.setOpaque(false);
        
        payrollActions.add(createActionButton("Current Payslip", "📄", this::viewCurrentPayslip));
        payrollActions.add(createActionButton("Tax Documents", "📋", this::viewTaxDocuments));
        payrollActions.add(createActionButton("Benefits Summary", "🎁", this::viewBenefitsSummary));
        
        panel.add(salaryPanel, BorderLayout.NORTH);
        panel.add(payslipPanel, BorderLayout.CENTER);
        panel.add(payrollActions, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLeaveTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Leave Balance Cards
        JPanel balancePanel = new JPanel(new GridLayout(1, 4, 15, 0));
        balancePanel.setOpaque(false);
        
        balancePanel.add(createStatCard("🏖️ Vacation", "12", "Days Available", new Color(52, 152, 219)));
        balancePanel.add(createStatCard("🤒 Sick Leave", "5", "Days Available", new Color(231, 76, 60)));
        balancePanel.add(createStatCard("🚨 Emergency", "3", "Days Available", new Color(230, 126, 34)));
        balancePanel.add(createStatCard("📅 Total Used", "8", "Days This Year", new Color(155, 89, 182)));
        
        // Leave Request Form
        JPanel requestPanel = new JPanel(new BorderLayout());
        requestPanel.setBackground(Color.WHITE);
        requestPanel.setBorder(BorderFactory.createTitledBorder("Quick Leave Request"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Leave Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> leaveTypeCombo = new JComboBox<>(new String[]{"Vacation Leave", "Sick Leave", "Emergency Leave", "Personal Leave"});
        formPanel.add(leaveTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        JTextField startDateField = new JTextField(15);
        startDateField.setText("YYYY-MM-DD");
        formPanel.add(startDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        JTextField endDateField = new JTextField(15);
        endDateField.setText("YYYY-MM-DD");
        formPanel.add(endDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        JTextArea reasonArea = new JTextArea(3, 15);
        reasonArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(reasonArea, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        JButton submitBtn = createActionButton("Submit Request", "📤", this::requestLeave);
        formPanel.add(submitBtn, gbc);
        
        requestPanel.add(formPanel, BorderLayout.CENTER);
        
        // Recent Leave Requests
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Recent Leave Requests"));
        
        String[] leaveColumns = {"Type", "Start Date", "End Date", "Days", "Status", "Applied"};
        Object[][] leaveData = {
            {"Vacation", "2024-12-20", "2024-12-22", "3", "Approved", "2024-12-01"},
            {"Sick", "2024-11-15", "2024-11-15", "1", "Approved", "2024-11-15"},
            {"Emergency", "2024-10-10", "2024-10-11", "2", "Pending", "2024-10-08"}
        };
        
        JTable leaveTable = new JTable(leaveData, leaveColumns);
        leaveTable.setRowHeight(25);
        leaveTable.getTableHeader().setBackground(PRIMARY_COLOR);
        leaveTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane leaveScroll = new JScrollPane(leaveTable);
        historyPanel.add(leaveScroll, BorderLayout.CENTER);
        
        // Split the content
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, requestPanel, historyPanel);
        splitPane.setDividerLocation(200);
        splitPane.setOpaque(false);
        
        panel.add(balancePanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper Methods
    private void addInfoField(JPanel parent, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        parent.add(labelComp);
        parent.add(valueComp);
    }
    
    private JPanel createStatCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subtitleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JMenu createStyledMenu(String text, String tooltip) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menu.setToolTipText(tooltip);
        return menu;
    }
    
    private JMenuItem createStyledMenuItem(String text, String tooltip, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setToolTipText(tooltip);
        item.addActionListener(e -> action.run());
        return item;
    }
    
    private JButton createHeaderButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private JButton createActionButton(String text, String icon, Runnable action) {
        JButton button = new JButton(icon + " " + text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private JButton createLargeActionButton(String text, String tooltip, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(200, 80));
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel companyLabel = new JLabel("MotorPH Payroll System v2.0");
        companyLabel.setForeground(Color.LIGHT_GRAY);
        companyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(companyLabel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                String timeText = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - HH:mm:ss"));
                if (timeLabel != null) {
                    timeLabel.setText(timeText);
                }
            }
        });
        clockTimer.start();
    }
    
    private Image createCompanyIcon() {
        // Create a simple company icon
        return new ImageIcon(new byte[0]).getImage();
    }
    
    // Action Methods
    private void viewMyProfile() {
        showMessage("Profile", "Opening your profile information...");
    }
    
    private void editPersonalInfo() {
        showMessage("Edit Profile", "Opening profile editor...");
    }
    
    private void changePassword() {
        showMessage("Change Password", "Opening password change dialog...");
    }
    
    private void manageEmergencyContacts() {
        showMessage("Emergency Contacts", "Managing emergency contacts...");
    }
    
    private void clockInOut() {
        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        showMessage("Time Clock", "Time recorded: " + timeString);
        statusLabel.setText("Last clock action: " + timeString);
    }
    
    private void viewMyAttendance() {
        showMessage("Attendance History", "Displaying your attendance records...");
    }
    
    private void requestOvertime() {
        showMessage("Overtime Request", "Opening overtime request form...");
    }
    
    private void requestTimeCorrection() {
        showMessage("Time Correction", "Opening time correction request...");
    }
    
    private void requestLeave() {
        showMessage("Leave Request", "Processing your leave request...");
    }
    
    private void viewLeaveBalance() {
        showMessage("Leave Balance", "Displaying your leave credits...");
    }
    
    private void viewLeaveHistory() {
        showMessage("Leave History", "Showing your leave request history...");
    }
    
    private void viewHolidayCalendar() {
        showMessage("Holiday Calendar", "Opening company holiday calendar...");
    }
    
    private void viewCurrentPayslip() {
        showMessage("Current Payslip", "Generating your latest payslip...");
    }
    
    private void viewPayslipHistory() {
        showMessage("Payslip History", "Loading all your payslips...");
    }
    
    private void viewTaxDocuments() {
        showMessage("Tax Documents", "Preparing tax certificates...");
    }
    
    private void viewBenefitsSummary() {
        showMessage("Benefits Summary", "Displaying your benefits package...");
    }
    
    private void viewEmployeeHandbook() {
        showMessage("Employee Handbook", "Opening company handbook...");
    }
    
    private void accessFormsLibrary() {
        showMessage("Forms Library", "Loading HR forms...");
    }
    
    private void viewCertificates() {
        showMessage("Certificates", "Displaying employment certificates...");
    }
    
    private void viewUserGuide() {
        showMessage("User Guide", "Opening system user guide...");
    }
    
    private void contactHR() {
        showMessage("Contact HR", "Opening HR contact information...");
    }
    
    private void reportIssue() {
        showMessage("Report Issue", "Opening issue reporting form...");
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "MotorPH Payroll System v2.0\n" +
            "Employee Self-Service Portal\n\n" +
            "© 2024 MotorPH Philippines\n" +
            "All rights reserved.",
            "About MotorPH Payroll System",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            if (clockTimer != null) {
                clockTimer.stop();
            }
            dispose();
            // Here you would typically return to login screen
        }
    }
    
    private void showMessage(String title, String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}