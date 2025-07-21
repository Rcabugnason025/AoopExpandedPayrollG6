package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;

import javax.swing.*;
import java.awt.*;

/**
 * Senior Employee Dashboard for senior-level employees
 * Enhanced employee interface with additional privileges
 */
public class SeniorEmployeeDashboard extends EmployeeDashboard {
    private Employee currentUser;
    private UserRole userRole;
    
    public SeniorEmployeeDashboard(Employee user) {
        super(user);
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        customizeForSeniorEmployee();
    }
    
    private void customizeForSeniorEmployee() {
        // Update title to reflect senior status
        setTitle("MotorPH Senior Employee Portal - " + currentUser.getFullName());
        
        // Add senior-specific features to the existing employee dashboard
        SwingUtilities.invokeLater(() -> {
            addSeniorEmployeeFeatures();
        });
    }
    
    private void addSeniorEmployeeFeatures() {
        // Add additional menu items for senior employees
        JMenuBar menuBar = getJMenuBar();
        if (menuBar != null) {
            // Add mentoring menu
            JMenu mentoringMenu = new JMenu("🎓 Mentoring");
            mentoringMenu.setForeground(Color.WHITE);
            mentoringMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            JMenuItem menteesItem = new JMenuItem("My Mentees");
            menteesItem.addActionListener(e -> showMessage("Mentoring", "Opening mentee management..."));
            mentoringMenu.add(menteesItem);
            
            JMenuItem trainingItem = new JMenuItem("Training Programs");
            trainingItem.addActionListener(e -> showMessage("Training", "Opening training programs..."));
            mentoringMenu.add(trainingItem);
            
            // Insert before help menu
            menuBar.add(mentoringMenu, menuBar.getMenuCount() - 2);
            menuBar.revalidate();
        }
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}