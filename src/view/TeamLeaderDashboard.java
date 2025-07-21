package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;

import javax.swing.*;
import java.awt.*;

/**
 * Team Leader Dashboard for team leadership roles
 * Provides team coordination and basic management capabilities
 */
public class TeamLeaderDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    
    // Color scheme for team leader interface
    private static final Color LEADER_PRIMARY = new Color(52, 152, 219);
    private static final Color LEADER_SECONDARY = new Color(41, 128, 185);
    private static final Color BACKGROUND_LIGHT = new Color(248, 252, 255);
    
    public TeamLeaderDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        initializeTeamLeaderInterface();
    }
    
    private void initializeTeamLeaderInterface() {
        setTitle("MotorPH Team Leadership Portal - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(LEADER_PRIMARY);
        
        // Team Coordination Menu
        JMenu teamMenu = createStyledMenu("👥 Team Coordination", "Coordinate team activities");
        teamMenu.add(createStyledMenuItem("Team Overview", "View team status", this::viewTeamOverview));
        teamMenu.add(createStyledMenuItem("Task Assignment", "Assign team tasks", this::assignTasks));
        teamMenu.add(createStyledMenuItem("Team Communication", "Team messaging", this::teamCommunication));
        
        // Basic Approvals Menu
        JMenu approvalsMenu = createStyledMenu("✅ Approvals", "Basic approval functions");
        approvalsMenu.add(createStyledMenuItem("Leave Requests", "Review leave requests", this::reviewLeaveRequests));
        approvalsMenu.add(createStyledMenuItem("Schedule Changes", "Approve schedule changes", this::approveScheduleChanges));
        
        menuBar.add(teamMenu);
        menuBar.add(approvalsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createStyledMenu("🚪 Logout", "Sign out"));
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        JLabel titleLabel = new JLabel("Team Leadership Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(LEADER_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea contentArea = new JTextArea("Team leadership features will be implemented here.\n\n" +
                "Available Features:\n" +
                "• Team coordination\n" +
                "• Basic approvals\n" +
                "• Team communication\n" +
                "• Task management");
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
    private void viewTeamOverview() {
        showMessage("Team Overview", "Loading team overview...");
    }
    
    private void assignTasks() {
        showMessage("Task Assignment", "Opening task assignment interface...");
    }
    
    private void teamCommunication() {
        showMessage("Team Communication", "Opening team communication tools...");
    }
    
    private void reviewLeaveRequests() {
        showMessage("Leave Requests", "Reviewing team leave requests...");
    }
    
    private void approveScheduleChanges() {
        showMessage("Schedule Changes", "Reviewing schedule change requests...");
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}