package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;

import javax.swing.*;
import java.awt.*;

/**
 * IT Admin Dashboard for IT administrators
 * Provides system administration and technical management capabilities
 */
public class ITAdminDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    
    // Color scheme for IT admin interface
    private static final Color IT_PRIMARY = new Color(52, 73, 94);
    private static final Color IT_SECONDARY = new Color(44, 62, 80);
    private static final Color IT_SUCCESS = new Color(39, 174, 96);
    private static final Color BACKGROUND_LIGHT = new Color(250, 250, 250);
    
    public ITAdminDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        initializeITInterface();
    }
    
    private void initializeITInterface() {
        setTitle("MotorPH IT Administration - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(IT_PRIMARY);
        
        // System Administration Menu
        JMenu systemMenu = createStyledMenu("⚙️ System Admin", "System administration tools");
        systemMenu.add(createStyledMenuItem("User Management", "Manage system users", this::manageUsers));
        systemMenu.add(createStyledMenuItem("System Settings", "Configure system", this::configureSystem));
        systemMenu.add(createStyledMenuItem("Database Admin", "Database management", this::manageDatabases));
        
        // Monitoring Menu
        JMenu monitoringMenu = createStyledMenu("📊 Monitoring", "System monitoring tools");
        monitoringMenu.add(createStyledMenuItem("System Health", "Monitor system health", this::monitorSystemHealth));
        monitoringMenu.add(createStyledMenuItem("Performance Metrics", "System performance", this::viewPerformanceMetrics));
        monitoringMenu.add(createStyledMenuItem("Error Logs", "View system errors", this::viewErrorLogs));
        
        // Security Menu
        JMenu securityMenu = createStyledMenu("🔒 Security", "Security management");
        securityMenu.add(createStyledMenuItem("Access Control", "Manage access permissions", this::manageAccessControl));
        securityMenu.add(createStyledMenuItem("Security Audit", "Security audit logs", this::viewSecurityAudit));
        securityMenu.add(createStyledMenuItem("Backup Management", "Data backup tools", this::manageBackups));
        
        menuBar.add(systemMenu);
        menuBar.add(monitoringMenu);
        menuBar.add(securityMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createStyledMenu("🚪 Logout", "Sign out"));
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        JLabel titleLabel = new JLabel("IT Administration Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(IT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea contentArea = new JTextArea("IT administration features will be implemented here.\n\n" +
                "Available Features:\n" +
                "• System administration\n" +
                "• User management\n" +
                "• Security monitoring\n" +
                "• Database management\n" +
                "• System maintenance");
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
    
    // Action methods
    private void manageUsers() {
        showMessage("User Management", "Opening user management interface...");
    }
    
    private void configureSystem() {
        showMessage("System Settings", "Opening system configuration...");
    }
    
    private void manageDatabases() {
        showMessage("Database Admin", "Opening database administration tools...");
    }
    
    private void monitorSystemHealth() {
        showMessage("System Health", "Monitoring system health status...");
    }
    
    private void viewPerformanceMetrics() {
        showMessage("Performance Metrics", "Loading system performance data...");
    }
    
    private void viewErrorLogs() {
        showMessage("Error Logs", "Opening system error logs...");
    }
    
    private void manageAccessControl() {
        showMessage("Access Control", "Managing user access permissions...");
    }
    
    private void viewSecurityAudit() {
        showMessage("Security Audit", "Opening security audit logs...");
    }
    
    private void manageBackups() {
        showMessage("Backup Management", "Opening backup management tools...");
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}