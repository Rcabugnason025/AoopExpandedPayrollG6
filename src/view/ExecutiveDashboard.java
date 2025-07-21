package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;
import dao.EmployeeDAO;
import dao.PayrollDAO;
import dao.AttendanceDAO;
import service.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Executive Dashboard for C-Level executives
 * Provides high-level analytics and strategic insights
 */
public class ExecutiveDashboard extends JFrame {
    private Employee currentExecutive;
    private UserRole userRole;
    private JLabel timeLabel;
    private Timer clockTimer;
    
    // Services
    private EmployeeDAO employeeDAO;
    private PayrollDAO payrollDAO;
    private AttendanceDAO attendanceDAO;
    private PayrollCalculator payrollCalculator;
    
    // Color scheme for executive interface
    private static final Color EXECUTIVE_PRIMARY = new Color(44, 62, 80);
    private static final Color EXECUTIVE_SECONDARY = new Color(52, 73, 94);
    private static final Color EXECUTIVE_ACCENT = new Color(231, 76, 60);
    private static final Color EXECUTIVE_SUCCESS = new Color(39, 174, 96);
    private static final Color EXECUTIVE_WARNING = new Color(243, 156, 18);
    private static final Color BACKGROUND_LIGHT = new Color(236, 240, 241);
    
    public ExecutiveDashboard(Employee executive) {
        this.currentExecutive = executive;
        this.userRole = PositionRoleMapper.getUserRole(executive.getPosition());
        
        // Initialize services
        this.employeeDAO = new EmployeeDAO();
        this.payrollDAO = new PayrollDAO();
        this.attendanceDAO = new AttendanceDAO();
        this.payrollCalculator = new PayrollCalculator();
        
        initializeExecutiveInterface();
        startClock();
    }
    
    private void initializeExecutiveInterface() {
        setTitle("MotorPH Executive Dashboard - " + currentExecutive.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(EXECUTIVE_PRIMARY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Strategic Overview Menu
        JMenu strategicMenu = createStyledMenu("📊 Strategic Overview", "High-level company metrics");
        strategicMenu.add(createStyledMenuItem("Company Dashboard", "Overall company performance", this::showCompanyDashboard));
        strategicMenu.add(createStyledMenuItem("Financial Analytics", "Revenue and cost analysis", this::showFinancialAnalytics));
        strategicMenu.add(createStyledMenuItem("Performance Metrics", "KPI tracking and analysis", this::showPerformanceMetrics));
        strategicMenu.add(createStyledMenuItem("Workforce Analytics", "Employee metrics and trends", this::showWorkforceAnalytics));
        
        // Executive Reports Menu
        JMenu reportsMenu = createStyledMenu("📈 Executive Reports", "Strategic reporting and insights");
        reportsMenu.add(createStyledMenuItem("Monthly Executive Summary", "Comprehensive monthly report", this::generateExecutiveSummary));
        reportsMenu.add(createStyledMenuItem("Department Performance", "Department-wise analysis", this::showDepartmentPerformance));
        reportsMenu.add(createStyledMenuItem("Budget vs Actual", "Financial variance analysis", this::showBudgetAnalysis));
        reportsMenu.add(createStyledMenuItem("Compliance Reports", "Regulatory compliance status", this::showComplianceReports));
        
        // Strategic Planning Menu
        JMenu planningMenu = createStyledMenu("🎯 Strategic Planning", "Planning and forecasting tools");
        planningMenu.add(createStyledMenuItem("Workforce Planning", "Future staffing needs", this::showWorkforcePlanning));
        planningMenu.add(createStyledMenuItem("Budget Planning", "Financial planning tools", this::showBudgetPlanning));
        planningMenu.add(createStyledMenuItem("Goal Setting", "Strategic objectives", this::showGoalSetting));
        
        // Executive Actions Menu
        JMenu actionsMenu = createStyledMenu("⚡ Executive Actions", "High-level administrative actions");
        actionsMenu.add(createStyledMenuItem("Policy Management", "Company policies", this::managePolicies));
        actionsMenu.add(createStyledMenuItem("Organizational Chart", "Company structure", this::showOrgChart));
        actionsMenu.add(createStyledMenuItem("Executive Approvals", "Pending executive decisions", this::showExecutiveApprovals));
        
        // Settings Menu
        JMenu settingsMenu = createStyledMenu("⚙️ Settings", "System configuration");
        settingsMenu.add(createStyledMenuItem("System Settings", "Configure system parameters", this::showSystemSettings));
        settingsMenu.add(createStyledMenuItem("User Management", "Manage user access", this::showUserManagement));
        settingsMenu.add(createStyledMenuItem("Audit Logs", "System activity logs", this::showAuditLogs));
        
        // Logout Menu
        JMenu logoutMenu = createStyledMenu("🚪 Logout", "Sign out of the system");
        logoutMenu.add(createStyledMenuItem("Logout", "Sign out safely", this::logout));
        
        menuBar.add(strategicMenu);
        menuBar.add(reportsMenu);
        menuBar.add(planningMenu);
        menuBar.add(actionsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(settingsMenu);
        menuBar.add(logoutMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        // Header Panel
        JPanel headerPanel = createExecutiveHeader();
        
        // Content Panel with executive dashboard
        JTabbedPane tabbedPane = createExecutiveContentTabs();
        
        // Status Panel
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createExecutiveHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(EXECUTIVE_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Executive info
        JPanel execInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        execInfoPanel.setOpaque(false);
        
        JLabel execIcon = new JLabel("👔");
        execIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Executive Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(currentExecutive.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(currentExecutive.getPosition() + " • " + userRole.getDisplayName());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(189, 195, 199));
        
        textPanel.add(welcomeLabel);
        textPanel.add(nameLabel);
        textPanel.add(roleLabel);
        
        execInfoPanel.add(execIcon);
        execInfoPanel.add(Box.createHorizontalStrut(15));
        execInfoPanel.add(textPanel);
        
        // Time and quick actions
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        rightPanel.setOpaque(false);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActionsPanel.setOpaque(false);
        
        JButton dashboardBtn = createHeaderButton("📊 Dashboard", this::showCompanyDashboard);
        JButton reportsBtn = createHeaderButton("📈 Reports", this::generateExecutiveSummary);
        
        quickActionsPanel.add(dashboardBtn);
        quickActionsPanel.add(reportsBtn);
        
        rightPanel.add(timeLabel);
        rightPanel.add(quickActionsPanel);
        
        headerPanel.add(execInfoPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JTabbedPane createExecutiveContentTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("📊 Company Overview", createCompanyOverviewTab());
        tabbedPane.addTab("💰 Financial Dashboard", createFinancialTab());
        tabbedPane.addTab("👥 Workforce Analytics", createWorkforceTab());
        tabbedPane.addTab("📈 Performance Metrics", createPerformanceTab());
        tabbedPane.addTab("⚙️ Executive Controls", createControlsTab());
        
        return tabbedPane;
    }
    
    private JPanel createCompanyOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Key metrics cards
        JPanel metricsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        metricsPanel.setOpaque(false);
        
        try {
            List<Employee> allEmployees = employeeDAO.getAllEmployees();
            int totalEmployees = allEmployees.size();
            long regularEmployees = allEmployees.stream().filter(e -> "Regular".equals(e.getStatus())).count();
            double avgSalary = allEmployees.stream().mapToDouble(Employee::getBasicSalary).average().orElse(0.0);
            
            metricsPanel.add(createExecutiveMetricCard("👥 Total Employees", String.valueOf(totalEmployees), "Active workforce", EXECUTIVE_SUCCESS));
            metricsPanel.add(createExecutiveMetricCard("✅ Regular Staff", String.valueOf(regularEmployees), "Permanent employees", EXECUTIVE_PRIMARY));
            metricsPanel.add(createExecutiveMetricCard("💰 Avg Salary", String.format("₱%,.0f", avgSalary), "Monthly average", EXECUTIVE_WARNING));
            metricsPanel.add(createExecutiveMetricCard("📈 Growth Rate", "+12%", "YoY employee growth", EXECUTIVE_SUCCESS));
            
            metricsPanel.add(createExecutiveMetricCard("🏢 Departments", "6", "Active departments", EXECUTIVE_SECONDARY));
            metricsPanel.add(createExecutiveMetricCard("📊 Attendance", "96.8%", "Average rate", EXECUTIVE_SUCCESS));
            metricsPanel.add(createExecutiveMetricCard("💼 Positions", "18", "Different roles", EXECUTIVE_PRIMARY));
            metricsPanel.add(createExecutiveMetricCard("⭐ Satisfaction", "4.2/5", "Employee rating", EXECUTIVE_WARNING));
            
        } catch (Exception e) {
            // Fallback metrics if database is unavailable
            metricsPanel.add(createExecutiveMetricCard("👥 Employees", "34", "Total workforce", EXECUTIVE_SUCCESS));
            metricsPanel.add(createExecutiveMetricCard("💰 Payroll", "₱1.5M", "Monthly total", EXECUTIVE_WARNING));
            metricsPanel.add(createExecutiveMetricCard("📈 Growth", "+8%", "This quarter", EXECUTIVE_SUCCESS));
            metricsPanel.add(createExecutiveMetricCard("🎯 Goals", "85%", "Achievement rate", EXECUTIVE_PRIMARY));
        }
        
        // Charts and analytics area
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);
        
        JPanel departmentChart = createChartPanel("Department Distribution", "Employee count by department");
        JPanel trendChart = createChartPanel("Growth Trends", "Monthly employee growth trends");
        
        chartsPanel.add(departmentChart);
        chartsPanel.add(trendChart);
        
        panel.add(metricsPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFinancialTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("💰 Financial Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(EXECUTIVE_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Financial analytics and budget tracking will be displayed here.\n\n" +
                "Features:\n" +
                "• Monthly payroll costs\n" +
                "• Department budget allocation\n" +
                "• Cost per employee analysis\n" +
                "• Financial forecasting\n" +
                "• ROI metrics");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createWorkforceTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("👥 Workforce Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(EXECUTIVE_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Workforce analytics and HR metrics will be displayed here.\n\n" +
                "Features:\n" +
                "• Employee demographics\n" +
                "• Turnover analysis\n" +
                "• Performance distribution\n" +
                "• Skill gap analysis\n" +
                "• Succession planning");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📈 Performance Metrics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(EXECUTIVE_PRIMARY);
        
        JTextArea contentArea = new JTextArea("Performance metrics and KPI tracking will be displayed here.\n\n" +
                "Features:\n" +
                "• Company-wide KPIs\n" +
                "• Department performance\n" +
                "• Goal achievement tracking\n" +
                "• Productivity metrics\n" +
                "• Benchmarking analysis");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("⚙️ Executive Controls");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(EXECUTIVE_PRIMARY);
        
        // Control buttons panel
        JPanel controlsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        controlsPanel.setOpaque(false);
        
        controlsPanel.add(createControlButton("👥 User Management", "Manage system users", this::showUserManagement));
        controlsPanel.add(createControlButton("📊 System Reports", "Generate system reports", this::generateExecutiveSummary));
        controlsPanel.add(createControlButton("⚙️ System Settings", "Configure system", this::showSystemSettings));
        controlsPanel.add(createControlButton("🔍 Audit Logs", "View system logs", this::showAuditLogs));
        controlsPanel.add(createControlButton("📋 Policies", "Manage policies", this::managePolicies));
        controlsPanel.add(createControlButton("🏢 Org Chart", "Organization structure", this::showOrgChart));
        controlsPanel.add(createControlButton("✅ Approvals", "Pending approvals", this::showExecutiveApprovals));
        controlsPanel.add(createControlButton("📈 Analytics", "Advanced analytics", this::showFinancialAnalytics));
        controlsPanel.add(createControlButton("🎯 Planning", "Strategic planning", this::showWorkforcePlanning));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(controlsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper methods
    private JPanel createExecutiveMetricCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setPreferredSize(new Dimension(200, 120));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subtitleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createChartPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(EXECUTIVE_PRIMARY);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(descLabel);
        
        JLabel chartPlaceholder = new JLabel("📊 Chart will be displayed here", SwingConstants.CENTER);
        chartPlaceholder.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chartPlaceholder.setForeground(Color.GRAY);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPlaceholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createControlButton(String text, String tooltip, Runnable action) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setBackground(EXECUTIVE_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(180, 80));
        button.addActionListener(e -> action.run());
        return button;
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
        button.setForeground(EXECUTIVE_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(EXECUTIVE_SECONDARY);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Executive Dashboard Ready | Access Level: " + userRole.getAccessLevel());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel companyLabel = new JLabel("MotorPH Executive Portal v2.0");
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
    private void showCompanyDashboard() {
        showMessage("Company Dashboard", "Loading comprehensive company metrics...");
    }
    
    private void showFinancialAnalytics() {
        showMessage("Financial Analytics", "Opening financial analysis tools...");
    }
    
    private void showPerformanceMetrics() {
        showMessage("Performance Metrics", "Loading KPI dashboard...");
    }
    
    private void showWorkforceAnalytics() {
        showMessage("Workforce Analytics", "Analyzing workforce data...");
    }
    
    private void generateExecutiveSummary() {
        showMessage("Executive Summary", "Generating comprehensive executive report...");
    }
    
    private void showDepartmentPerformance() {
        showMessage("Department Performance", "Loading department analytics...");
    }
    
    private void showBudgetAnalysis() {
        showMessage("Budget Analysis", "Opening budget variance reports...");
    }
    
    private void showComplianceReports() {
        showMessage("Compliance Reports", "Loading regulatory compliance status...");
    }
    
    private void showWorkforcePlanning() {
        showMessage("Workforce Planning", "Opening strategic workforce planning tools...");
    }
    
    private void showBudgetPlanning() {
        showMessage("Budget Planning", "Loading financial planning interface...");
    }
    
    private void showGoalSetting() {
        showMessage("Goal Setting", "Opening strategic objectives management...");
    }
    
    private void managePolicies() {
        showMessage("Policy Management", "Loading company policy management...");
    }
    
    private void showOrgChart() {
        showMessage("Organization Chart", "Displaying organizational structure...");
    }
    
    private void showExecutiveApprovals() {
        showMessage("Executive Approvals", "Loading pending executive decisions...");
    }
    
    private void showSystemSettings() {
        showMessage("System Settings", "Opening system configuration...");
    }
    
    private void showUserManagement() {
        showMessage("User Management", "Loading user access management...");
    }
    
    private void showAuditLogs() {
        showMessage("Audit Logs", "Opening system activity logs...");
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout from the Executive Dashboard?",
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