package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;
import dao.EmployeeDAO;
import dao.PayrollDAO;
import service.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payroll Dashboard for payroll administrators
 * Specialized interface for payroll processing and management
 */
public class PayrollDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    private JLabel timeLabel;
    private Timer clockTimer;
    
    // Services
    private EmployeeDAO employeeDAO;
    private PayrollDAO payrollDAO;
    private PayrollCalculator payrollCalculator;
    
    // Color scheme for payroll interface
    private static final Color PAYROLL_PRIMARY = new Color(230, 126, 34);
    private static final Color PAYROLL_SECONDARY = new Color(211, 84, 0);
    private static final Color PAYROLL_SUCCESS = new Color(39, 174, 96);
    private static final Color PAYROLL_WARNING = new Color(231, 76, 60);
    private static final Color BACKGROUND_LIGHT = new Color(253, 251, 251);
    
    public PayrollDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        // Initialize services
        this.employeeDAO = new EmployeeDAO();
        this.payrollDAO = new PayrollDAO();
        this.payrollCalculator = new PayrollCalculator();
        
        initializePayrollInterface();
        startClock();
    }
    
    private void initializePayrollInterface() {
        setTitle("MotorPH Payroll Administration - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PAYROLL_PRIMARY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Payroll Processing Menu
        JMenu processingMenu = createStyledMenu("💰 Payroll Processing", "Core payroll operations");
        processingMenu.add(createStyledMenuItem("Calculate Payroll", "Process monthly payroll", this::calculatePayroll));
        processingMenu.add(createStyledMenuItem("Generate Payslips", "Create employee payslips", this::generatePayslips));
        processingMenu.add(createStyledMenuItem("Payroll Review", "Review calculated payroll", this::reviewPayroll));
        processingMenu.add(createStyledMenuItem("Payroll Approval", "Approve payroll for payment", this::approvePayroll));
        processingMenu.addSeparator();
        processingMenu.add(createStyledMenuItem("Bulk Operations", "Mass payroll operations", this::bulkOperations));
        
        // Employee Management Menu
        JMenu employeeMenu = createStyledMenu("👥 Employee Management", "Manage employee payroll data");
        employeeMenu.add(createStyledMenuItem("Employee Salaries", "Manage salary information", this::manageEmployeeSalaries));
        employeeMenu.add(createStyledMenuItem("Allowances & Benefits", "Configure allowances", this::manageAllowances));
        employeeMenu.add(createStyledMenuItem("Deductions", "Manage deductions", this::manageDeductions));
        employeeMenu.add(createStyledMenuItem("Government IDs", "Update government numbers", this::manageGovernmentIds));
        
        // Reports Menu
        JMenu reportsMenu = createStyledMenu("📊 Payroll Reports", "Generate payroll reports");
        reportsMenu.add(createStyledMenuItem("Monthly Payroll Report", "Comprehensive monthly report", this::generateMonthlyReport));
        reportsMenu.add(createStyledMenuItem("Government Contributions", "SSS, PhilHealth, Pag-IBIG reports", this::generateGovContributions));
        reportsMenu.add(createStyledMenuItem("Tax Reports", "BIR tax reports", this::generateTaxReports));
        reportsMenu.add(createStyledMenuItem("Payroll Summary", "Executive payroll summary", this::generatePayrollSummary));
        reportsMenu.addSeparator();
        reportsMenu.add(createStyledMenuItem("Custom Reports", "Build custom reports", this::buildCustomReports));
        
        // Compliance Menu
        JMenu complianceMenu = createStyledMenu("📋 Compliance", "Regulatory compliance tools");
        complianceMenu.add(createStyledMenuItem("Government Remittances", "Track remittances", this::trackRemittances));
        complianceMenu.add(createStyledMenuItem("Tax Compliance", "Tax filing status", this::checkTaxCompliance));
        complianceMenu.add(createStyledMenuItem("Audit Trail", "Payroll audit logs", this::viewAuditTrail));
        complianceMenu.add(createStyledMenuItem("Compliance Calendar", "Important dates", this::viewComplianceCalendar));
        
        // Tools Menu
        JMenu toolsMenu = createStyledMenu("🔧 Tools", "Payroll utilities and tools");
        toolsMenu.add(createStyledMenuItem("Payroll Calculator", "Manual calculations", this::openPayrollCalculator));
        toolsMenu.add(createStyledMenuItem("Tax Calculator", "Tax computation tool", this::openTaxCalculator));
        toolsMenu.add(createStyledMenuItem("Import/Export", "Data import/export", this::openImportExport));
        toolsMenu.add(createStyledMenuItem("Backup & Restore", "Data backup tools", this::openBackupTools));
        
        // Logout Menu
        JMenu logoutMenu = createStyledMenu("🚪 Logout", "Sign out of the system");
        logoutMenu.add(createStyledMenuItem("Logout", "Sign out safely", this::logout));
        
        menuBar.add(processingMenu);
        menuBar.add(employeeMenu);
        menuBar.add(reportsMenu);
        menuBar.add(complianceMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(toolsMenu);
        menuBar.add(logoutMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        // Header Panel
        JPanel headerPanel = createPayrollHeader();
        
        // Content Panel
        JTabbedPane tabbedPane = createPayrollContentTabs();
        
        // Status Panel
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createPayrollHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PAYROLL_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Payroll admin info
        JPanel adminInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminInfoPanel.setOpaque(false);
        
        JLabel adminIcon = new JLabel("💰");
        adminIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Payroll Administration");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(currentUser.getPosition() + " • " + userRole.getDisplayName());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(255, 255, 255, 180));
        
        textPanel.add(welcomeLabel);
        textPanel.add(nameLabel);
        textPanel.add(roleLabel);
        
        adminInfoPanel.add(adminIcon);
        adminInfoPanel.add(Box.createHorizontalStrut(15));
        adminInfoPanel.add(textPanel);
        
        // Time and quick actions
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        rightPanel.setOpaque(false);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActionsPanel.setOpaque(false);
        
        JButton calculateBtn = createHeaderButton("💰 Calculate", this::calculatePayroll);
        JButton reportsBtn = createHeaderButton("📊 Reports", this::generateMonthlyReport);
        
        quickActionsPanel.add(calculateBtn);
        quickActionsPanel.add(reportsBtn);
        
        rightPanel.add(timeLabel);
        rightPanel.add(quickActionsPanel);
        
        headerPanel.add(adminInfoPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JTabbedPane createPayrollContentTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("💰 Payroll Processing", createPayrollProcessingTab());
        tabbedPane.addTab("👥 Employee Data", createEmployeeDataTab());
        tabbedPane.addTab("📊 Reports", createReportsTab());
        tabbedPane.addTab("📋 Compliance", createComplianceTab());
        tabbedPane.addTab("🔧 Tools", createToolsTab());
        
        return tabbedPane;
    }
    
    private JPanel createPayrollProcessingTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("💰 Payroll Processing Center");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PAYROLL_PRIMARY);
        
        // Processing steps panel
        JPanel stepsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        stepsPanel.setOpaque(false);
        
        stepsPanel.add(createProcessingCard("1. Calculate", "Process monthly payroll", "💰", this::calculatePayroll));
        stepsPanel.add(createProcessingCard("2. Review", "Review calculations", "🔍", this::reviewPayroll));
        stepsPanel.add(createProcessingCard("3. Generate", "Create payslips", "📄", this::generatePayslips));
        stepsPanel.add(createProcessingCard("4. Approve", "Approve for payment", "✅", this::approvePayroll));
        stepsPanel.add(createProcessingCard("5. Reports", "Generate reports", "📊", this::generateMonthlyReport));
        stepsPanel.add(createProcessingCard("6. Remit", "Government remittances", "🏛️", this::trackRemittances));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(stepsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmployeeDataTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("👥 Employee Payroll Data");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PAYROLL_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Employee payroll data management will be displayed here.\n\n" +
                "Features:\n" +
                "• Salary adjustments\n" +
                "• Allowance management\n" +
                "• Deduction setup\n" +
                "• Government ID updates\n" +
                "• Employee payroll history");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📊 Payroll Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PAYROLL_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Payroll reporting interface will be displayed here.\n\n" +
                "Available Reports:\n" +
                "• Monthly payroll summary\n" +
                "• Government contributions report\n" +
                "• Tax withholding reports\n" +
                "• Employee payroll history\n" +
                "• Custom payroll analytics");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createComplianceTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📋 Compliance Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PAYROLL_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Compliance management tools will be displayed here.\n\n" +
                "Features:\n" +
                "• Government remittance tracking\n" +
                "• Tax compliance monitoring\n" +
                "• Audit trail management\n" +
                "• Regulatory deadline calendar\n" +
                "• Compliance reporting");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createToolsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("🔧 Payroll Tools");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PAYROLL_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Payroll tools and utilities will be displayed here.\n\n" +
                "Available Tools:\n" +
                "• Payroll calculator\n" +
                "• Tax computation tool\n" +
                "• Data import/export\n" +
                "• Backup and restore\n" +
                "• System maintenance");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper methods
    private JPanel createProcessingCard(String step, String description, String icon, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PAYROLL_PRIMARY, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setPreferredSize(new Dimension(200, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JLabel stepLabel = new JLabel(step, SwingConstants.CENTER);
        stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stepLabel.setForeground(PAYROLL_PRIMARY);
        
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(stepLabel);
        textPanel.add(descLabel);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(253, 251, 251));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
        
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
        button.setForeground(PAYROLL_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(PAYROLL_SECONDARY);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Payroll Administration Ready | Access Level: " + userRole.getAccessLevel());
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
    
    // Action methods
    private void calculatePayroll() {
        showMessage("Calculate Payroll", "Opening payroll calculation interface...");
    }
    
    private void generatePayslips() {
        showMessage("Generate Payslips", "Creating employee payslips...");
    }
    
    private void reviewPayroll() {
        showMessage("Review Payroll", "Opening payroll review interface...");
    }
    
    private void approvePayroll() {
        showMessage("Approve Payroll", "Processing payroll approval...");
    }
    
    private void bulkOperations() {
        showMessage("Bulk Operations", "Opening bulk payroll operations...");
    }
    
    private void manageEmployeeSalaries() {
        showMessage("Employee Salaries", "Opening salary management interface...");
    }
    
    private void manageAllowances() {
        showMessage("Allowances & Benefits", "Managing employee allowances...");
    }
    
    private void manageDeductions() {
        showMessage("Deductions", "Opening deduction management...");
    }
    
    private void manageGovernmentIds() {
        showMessage("Government IDs", "Managing government ID numbers...");
    }
    
    private void generateMonthlyReport() {
        showMessage("Monthly Report", "Generating monthly payroll report...");
    }
    
    private void generateGovContributions() {
        showMessage("Government Contributions", "Generating government contributions report...");
    }
    
    private void generateTaxReports() {
        showMessage("Tax Reports", "Generating tax reports...");
    }
    
    private void generatePayrollSummary() {
        showMessage("Payroll Summary", "Creating payroll summary...");
    }
    
    private void buildCustomReports() {
        showMessage("Custom Reports", "Opening custom report builder...");
    }
    
    private void trackRemittances() {
        showMessage("Government Remittances", "Tracking government remittances...");
    }
    
    private void checkTaxCompliance() {
        showMessage("Tax Compliance", "Checking tax compliance status...");
    }
    
    private void viewAuditTrail() {
        showMessage("Audit Trail", "Opening payroll audit trail...");
    }
    
    private void viewComplianceCalendar() {
        showMessage("Compliance Calendar", "Opening compliance calendar...");
    }
    
    private void openPayrollCalculator() {
        showMessage("Payroll Calculator", "Opening payroll calculator tool...");
    }
    
    private void openTaxCalculator() {
        showMessage("Tax Calculator", "Opening tax calculation tool...");
    }
    
    private void openImportExport() {
        showMessage("Import/Export", "Opening data import/export tools...");
    }
    
    private void openBackupTools() {
        showMessage("Backup & Restore", "Opening backup and restore tools...");
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout from the Payroll Dashboard?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            if (clockTimer != null) {
                clockTimer.stop();
            }
            dispose();
            // Return to login
            SwingUtilities.invokeLater(() -> {
                try {
                    new ui.LoginForm().setVisible(true);
                } catch (Exception e) {
                    System.exit(0);
                }
            });
        }
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}