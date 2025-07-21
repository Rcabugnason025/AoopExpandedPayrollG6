package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manager Dashboard for management-level employees
 * Provides team management and oversight capabilities
 */
public class ManagerDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    private JLabel timeLabel;
    private Timer clockTimer;
    
    // Color scheme for manager interface
    private static final Color MANAGER_PRIMARY = new Color(155, 89, 182);
    private static final Color MANAGER_SECONDARY = new Color(142, 68, 173);
    private static final Color MANAGER_SUCCESS = new Color(39, 174, 96);
    private static final Color BACKGROUND_LIGHT = new Color(253, 252, 255);
    
    public ManagerDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        initializeManagerInterface();
        startClock();
    }
    
    private void initializeManagerInterface() {
        setTitle("MotorPH Management Portal - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(MANAGER_PRIMARY);
        
        // Team Management Menu
        JMenu teamMenu = createStyledMenu("👥 Team Management", "Manage your team");
        teamMenu.add(createStyledMenuItem("My Team", "View team members", this::viewMyTeam));
        teamMenu.add(createStyledMenuItem("Team Performance", "Team performance metrics", this::viewTeamPerformance));
        teamMenu.add(createStyledMenuItem("Team Schedule", "Manage team schedules", this::manageTeamSchedule));
        
        // Approvals Menu
        JMenu approvalsMenu = createStyledMenu("✅ Approvals", "Pending approvals");
        approvalsMenu.add(createStyledMenuItem("Leave Requests", "Approve leave requests", this::approveLeaveRequests));
        approvalsMenu.add(createStyledMenuItem("Overtime Requests", "Approve overtime", this::approveOvertimeRequests));
        approvalsMenu.add(createStyledMenuItem("Time Corrections", "Approve time corrections", this::approveTimeCorrections));
        
        // Reports Menu
        JMenu reportsMenu = createStyledMenu("📊 Reports", "Team and department reports");
        reportsMenu.add(createStyledMenuItem("Team Reports", "Generate team reports", this::generateTeamReports));
        reportsMenu.add(createStyledMenuItem("Department Analytics", "Department performance", this::viewDepartmentAnalytics));
        
        menuBar.add(teamMenu);
        menuBar.add(approvalsMenu);
        menuBar.add(reportsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createStyledMenu("🚪 Logout", "Sign out"));
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        JLabel titleLabel = new JLabel("Management Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(MANAGER_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea contentArea = new JTextArea("Management features will be implemented here.\n\n" +
                "Available Features:\n" +
                "• Team management\n" +
                "• Approval workflows\n" +
                "• Performance tracking\n" +
                "• Team reporting");
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        add(mainPanel);
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
    
    private void startClock() {
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                String timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                if (timeLabel != null) {
                    timeLabel.setText(timeText);
                }
            }
        });
        clockTimer.start();
    }
    
    // Action methods
    private void viewMyTeam() {
        showMessage("My Team", "Loading team member information...");
    }
    
    private void viewTeamPerformance() {
        showMessage("Team Performance", "Loading team performance metrics...");
    }
    
    private void manageTeamSchedule() {
        showMessage("Team Schedule", "Opening team schedule management...");
    }
    
    private void approveLeaveRequests() {
        showMessage("Leave Requests", "Opening leave request approvals...");
    }
    
    private void approveOvertimeRequests() {
        showMessage("Overtime Requests", "Opening overtime request approvals...");
    }
    
    private void approveTimeCorrections() {
        showMessage("Time Corrections", "Opening time correction approvals...");
    }
    
    private void generateTeamReports() {
        showMessage("Team Reports", "Generating team reports...");
    }
    
    private void viewDepartmentAnalytics() {
        showMessage("Department Analytics", "Loading department analytics...");
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}