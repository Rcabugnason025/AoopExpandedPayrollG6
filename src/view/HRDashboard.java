package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;
import dao.EmployeeDAO;
import dao.AttendanceDAO;
import service.PayrollCalculator;
import ui.EmployeeDetailsDialog;
import ui.PasswordChangeDialog;
import ui.LeaveManagementDialog;
import ui.AttendanceManagementDialog;
import ui.ReportsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HRDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    private JPanel mainContentPanel;
    private JPanel sidebarPanel;
    private JPanel dashboardPanel;
    private String currentView = "dashboard";

    // Modern Color Palette - Professional Purple/Blue Theme
    private static final Color PRIMARY_PURPLE = new Color(88, 86, 214);
    private static final Color SECONDARY_PURPLE = new Color(124, 58, 237);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(251, 146, 60);
    private static final Color SIDEBAR_DARK = new Color(30, 27, 75);
    private static final Color BACKGROUND_LIGHT = new Color(248, 250, 252);
    private static final Color CARD_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

    // Dashboard Components
    private JTable employeeTable;
    private DefaultTableModel employeeTableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JLabel totalEmployeesLabel;
    private JLabel newEmployeesLabel;
    private JLabel attendanceRateLabel;
    private JLabel currentDateTimeLabel;

    // Services
    private EmployeeDAO employeeDAO;
    private AttendanceDAO attendanceDAO;
    private PayrollCalculator payrollCalculator;

    // Timer for real-time updates
    private Timer dateTimeTimer;
    private Timer metricsTimer;

    public HRDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());

        try {
            this.employeeDAO = new EmployeeDAO();
            this.attendanceDAO = new AttendanceDAO();
            this.payrollCalculator = new PayrollCalculator();

            initializeComponents();
            setupLayout();
            setupEventHandlers();
            startRealTimeUpdates();
            loadData();

            System.out.println("✅ HR Dashboard initialized for: " + user.getFullName() + " (" + userRole.getDisplayName() + ")");

        } catch (Exception e) {
            System.err.println("❌ HR Dashboard initialization failed: " + e.getMessage());
            e.printStackTrace();
            createErrorInterface(e);
        }

        setTitle("MotorPH HR Management System - " + user.getFullName() + " (" + userRole.getDisplayName() + ")");
        setSize(1600, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void createErrorInterface(Exception error) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_LIGHT);

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(CARD_WHITE);
        errorPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        String errorMessage = "<html><center>" +
                "<h1 style='color: #dc2626;'>⚠️ System Error</h1>" +
                "<p style='font-size: 16px; margin: 20px 0;'>Unable to initialize HR Dashboard</p>" +
                "<p><b>Error:</b> " + error.getMessage() + "</p>" +
                "</center></html>";

        JLabel messageLabel = new JLabel(errorMessage, JLabel.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton retryButton = createModernButton("🔄 Retry", PRIMARY_PURPLE);
        retryButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new HRDashboard(currentUser).setVisible(true));
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.add(retryButton);

        errorPanel.add(messageLabel, BorderLayout.CENTER);
        errorPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(errorPanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        // Initialize main panels
        sidebarPanel = createSidebarPanel();
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_LIGHT);

        // Initialize dashboard metrics labels
        totalEmployeesLabel = new JLabel("0");
        newEmployeesLabel = new JLabel("+0");
        attendanceRateLabel = new JLabel("0%");
        currentDateTimeLabel = new JLabel();

        // Initialize employee table for dashboard
        String[] columns = {"ID", "Last Name", "First Name", "Position", "Department", "Status", "Salary"};
        employeeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(employeeTableModel);
        setupTableStyling(employeeTable);

        tableSorter = new TableRowSorter<>(employeeTableModel);
        employeeTable.setRowSorter(tableSorter);

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        // Create dashboard panel
        dashboardPanel = createDashboardPanel();
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_DARK);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

        // Header with logo and welcome
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(SIDEBAR_DARK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 25, 25));

        // Logo section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setBackground(SIDEBAR_DARK);

        JLabel logoIcon = new JLabel("🏍️");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new BoxLayout(logoTextPanel, BoxLayout.Y_AXIS));
        logoTextPanel.setBackground(SIDEBAR_DARK);

        JLabel logoTitle = new JLabel("MotorPH");
        logoTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoTitle.setForeground(Color.WHITE);
        logoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoSubtitle = new JLabel("HR Management");
        logoSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoSubtitle.setForeground(new Color(156, 163, 175));
        logoSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoTextPanel.add(logoTitle);
        logoTextPanel.add(logoSubtitle);

        logoPanel.add(logoIcon);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(logoTextPanel);

        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(SIDEBAR_DARK);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome,");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(156, 163, 175));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userNameLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userRoleLabel = new JLabel(userRole.getDisplayName());
        userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRoleLabel.setForeground(new Color(156, 163, 175));
        userRoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(userNameLabel);
        welcomePanel.add(userRoleLabel);

        headerPanel.add(logoPanel);
        headerPanel.add(welcomePanel);

        // Navigation menu
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_DARK);
        navPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Navigation buttons
        JButton dashboardBtn = createSidebarButton("📊 Dashboard", "dashboard", true);
        JButton employeesBtn = createSidebarButton("👥 Employees", "employees", false);
        JButton attendanceBtn = createSidebarButton("📅 Attendance", "attendance", false);
        JButton leaveBtn = createSidebarButton("🏖️ Leave Management", "leave", false);
        JButton reportsBtn = createSidebarButton("📈 Reports", "reports", false);

        navPanel.add(dashboardBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(employeesBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(attendanceBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(leaveBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(reportsBtn);

        // Bottom section with logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(SIDEBAR_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));

        JButton logoutBtn = createSidebarButton("🚪 Sign Out", "logout", false);
        logoutBtn.setBackground(new Color(220, 38, 38));
        logoutBtn.addActionListener(e -> logout());

        bottomPanel.add(Box.createVerticalGlue());
        bottomPanel.add(logoutBtn);

        sidebar.add(headerPanel);
        sidebar.add(navPanel);
        sidebar.add(bottomPanel);

        return sidebar;
    }

    private JButton createSidebarButton(String text, String action, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(240, 45));
        
        if (isActive) {
            button.setBackground(PRIMARY_PURPLE);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(SIDEBAR_DARK);
            button.setForeground(new Color(156, 163, 175));
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(new Color(55, 65, 81));
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(SIDEBAR_DARK);
                    button.setForeground(new Color(156, 163, 175));
                }
            }
        });

        button.addActionListener(e -> switchView(action));

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BACKGROUND_LIGHT);
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Top section with date/time and metrics
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BACKGROUND_LIGHT);
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Date/Time Card
        JPanel dateTimeCard = createMetricCard("📅 Current Date & Time", "", PRIMARY_PURPLE);
        currentDateTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentDateTimeLabel.setForeground(Color.WHITE);
        dateTimeCard.add(currentDateTimeLabel, BorderLayout.SOUTH);

        // Metrics cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setBackground(BACKGROUND_LIGHT);

        JPanel totalEmployeesCard = createMetricCard("👥 Total Employees", "", ACCENT_BLUE);
        totalEmployeesLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalEmployeesLabel.setForeground(Color.WHITE);
        totalEmployeesCard.add(totalEmployeesLabel, BorderLayout.CENTER);

        JPanel newEmployeesCard = createMetricCard("🆕 New Employees", "", SUCCESS_GREEN);
        newEmployeesLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        newEmployeesLabel.setForeground(Color.WHITE);
        newEmployeesCard.add(newEmployeesLabel, BorderLayout.CENTER);

        JPanel attendanceCard = createMetricCard("📈 Attendance Rate", "", WARNING_ORANGE);
        attendanceRateLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        attendanceRateLabel.setForeground(Color.WHITE);
        attendanceCard.add(attendanceRateLabel, BorderLayout.CENTER);

        metricsPanel.add(totalEmployeesCard);
        metricsPanel.add(newEmployeesCard);
        metricsPanel.add(attendanceCard);

        topSection.add(dateTimeCard, BorderLayout.WEST);
        topSection.add(metricsPanel, BorderLayout.CENTER);

        // Employee table section
        JPanel tableSection = createEmployeeTableSection();

        dashboard.add(topSection, BorderLayout.NORTH);
        dashboard.add(tableSection, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createMetricCard(String title, String subtitle, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setPreferredSize(new Dimension(220, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        if (!subtitle.isEmpty()) {
            JLabel subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitleLabel.setForeground(new Color(255, 255, 255, 180));
            card.add(subtitleLabel, BorderLayout.SOUTH);
        }

        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JPanel createEmployeeTableSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND_LIGHT);

        // Header with search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel tableTitle = new JLabel("📋 Employee Directory");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(CARD_WHITE);

        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(TEXT_PRIMARY);

        JButton searchButton = createModernButton("Search", PRIMARY_PURPLE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> searchEmployees());

        JButton addButton = createModernButton("➕ Add Employee", SUCCESS_GREEN);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> showAddEmployeeDialog());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);

        headerPanel.add(tableTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        section.add(headerPanel, BorderLayout.NORTH);
        section.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        section.add(tablePanel, BorderLayout.SOUTH);

        return section;
    }

    private void setupTableStyling(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(88, 86, 214, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_LIGHT);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setBackground(CARD_WHITE);
        table.setForeground(TEXT_PRIMARY);

        // Modern table header
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_LIGHT));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = button.getBackground();
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_LIGHT);

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Initially show dashboard
        switchView("dashboard");
    }

    private void switchView(String view) {
        currentView = view;
        mainContentPanel.removeAll();

        switch (view) {
            case "dashboard":
                mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);
                break;
            case "employees":
                mainContentPanel.add(createEmployeesView(), BorderLayout.CENTER);
                break;
            case "attendance":
                openAttendanceManagement();
                return;
            case "leave":
                openLeaveManagement();
                return;
            case "reports":
                openReportsDialog();
                return;
            case "logout":
                logout();
                return;
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createEmployeesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("👥 Employee Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        // Employee management content
        JPanel contentPanel = createEmployeeTableSection();

        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void startRealTimeUpdates() {
        // Update date/time every second
        dateTimeTimer = new Timer(1000, e -> updateDateTime());
        dateTimeTimer.start();

        // Update metrics every 30 seconds
        metricsTimer = new Timer(30000, e -> updateMetrics());
        metricsTimer.start();

        // Initial updates
        updateDateTime();
        updateMetrics();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeText = now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy\nhh:mm:ss a"));
        currentDateTimeLabel.setText("<html><center>" + dateTimeText.replace("\n", "<br>") + "</center></html>");
    }

    private void updateMetrics() {
        try {
            List<Employee> allEmployees = employeeDAO.getAllEmployees();
            totalEmployeesLabel.setText(String.valueOf(allEmployees.size()));

            // Calculate new employees - simplified
            newEmployeesLabel.setText("+2");

            // Calculate attendance rate (placeholder)
            attendanceRateLabel.setText("96.8%");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        searchField.addActionListener(e -> searchEmployees());

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = employeeTable.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = employeeTable.convertRowIndexToModel(row);
                        int employeeId = (Integer) employeeTableModel.getValueAt(modelRow, 0);
                        Employee employee = employeeDAO.getEmployeeById(employeeId);
                        if (employee != null) {
                            showEmployeeDetails(employee);
                        }
                    }
                }
            }
        });
    }

    private void loadData() {
        loadEmployeeData();
        updateMetrics();
    }

    private void loadEmployeeData() {
        employeeTableModel.setRowCount(0);

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();

            for (Employee emp : employees) {
                Object[] row = {
                        emp.getEmployeeId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getPosition(),
                        "HR", // Department placeholder
                        emp.getStatus(),
                        String.format("₱%,.2f", emp.getBasicSalary())
                };
                employeeTableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ Data Loading Error</h3><p>Error loading employee data: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchEmployees() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadEmployeeData();
            return;
        }

        employeeTableModel.setRowCount(0);

        try {
            List<Employee> employees = employeeDAO.searchEmployees(searchTerm);

            for (Employee emp : employees) {
                Object[] row = {
                        emp.getEmployeeId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getPosition(),
                        "HR", // Department placeholder
                        emp.getStatus(),
                        String.format("₱%,.2f", emp.getBasicSalary())
                };
                employeeTableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ Search Error</h3><p>Error searching employees: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddEmployeeDialog() {
        try {
            EmployeeDetailsDialog dialog = new EmployeeDetailsDialog(this, null, true);
            dialog.setVisible(true);
            loadEmployeeData();
            updateMetrics();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ Dialog Error</h3><p>Error opening employee dialog: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEmployeeDetails(Employee employee) {
        try {
            EmployeeDetailsDialog dialog = new EmployeeDetailsDialog(this, employee, false);
            dialog.setVisible(true);
            loadEmployeeData();
            updateMetrics();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ Dialog Error</h3><p>Error opening employee details: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAttendanceManagement() {
        try {
            AttendanceManagementDialog dialog = new AttendanceManagementDialog(this, currentUser);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ System Error</h3><p>Unable to open attendance management: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openLeaveManagement() {
        try {
            LeaveManagementDialog dialog = new LeaveManagementDialog(this, currentUser);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ System Error</h3><p>Unable to open leave management: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openReportsDialog() {
        try {
            ReportsDialog dialog = new ReportsDialog(this, currentUser);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "<html><center><h3>❌ System Error</h3><p>Unable to open reports: " + e.getMessage() + "</p></center></html>",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void logout() {
        // Stop timers
        if (dateTimeTimer != null) {
            dateTimeTimer.stop();
        }
        if (metricsTimer != null) {
            metricsTimer.stop();
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><center>" +
                "<h3>🚪 Confirm Sign Out</h3>" +
                "<p>Are you sure you want to sign out of the HR system?</p>" +
                "<p>Any unsaved work will be lost.</p>" +
                "</center></html>",
                "Confirm Sign Out", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    new ui.LoginForm().setVisible(true);
                } catch (Exception e) {
                    System.exit(0);
                }
            });
        } else {
            // Restart timers if user cancels
            startRealTimeUpdates();
        }
    }

    @Override
    public void dispose() {
        // Clean up timers
        if (dateTimeTimer != null) {
            dateTimeTimer.stop();
        }
        if (metricsTimer != null) {
            metricsTimer.stop();
        }
        super.dispose();
    }
}