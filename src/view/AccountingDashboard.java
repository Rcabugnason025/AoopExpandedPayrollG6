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
 * Accounting Dashboard for accounting personnel
 * Specialized interface for financial and accounting operations
 */
public class AccountingDashboard extends JFrame {
    private Employee currentUser;
    private UserRole userRole;
    private JLabel timeLabel;
    private Timer clockTimer;
    
    // Color scheme for accounting interface
    private static final Color ACCOUNTING_PRIMARY = new Color(39, 174, 96);
    private static final Color ACCOUNTING_SECONDARY = new Color(46, 204, 113);
    private static final Color ACCOUNTING_ACCENT = new Color(22, 160, 133);
    private static final Color BACKGROUND_LIGHT = new Color(248, 255, 248);
    
    public AccountingDashboard(Employee user) {
        this.currentUser = user;
        this.userRole = PositionRoleMapper.getUserRole(user.getPosition());
        
        initializeAccountingInterface();
        startClock();
    }
    
    private void initializeAccountingInterface() {
        setTitle("MotorPH Accounting System - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        createMenuBar();
        createMainContent();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(ACCOUNTING_PRIMARY);
        
        // Financial Reports Menu
        JMenu reportsMenu = createStyledMenu("📊 Financial Reports", "Generate financial reports");
        reportsMenu.add(createStyledMenuItem("Income Statement", "P&L reports", this::generateIncomeStatement));
        reportsMenu.add(createStyledMenuItem("Balance Sheet", "Financial position", this::generateBalanceSheet));
        reportsMenu.add(createStyledMenuItem("Cash Flow", "Cash flow analysis", this::generateCashFlow));
        
        // Payroll Accounting Menu
        JMenu payrollMenu = createStyledMenu("💰 Payroll Accounting", "Payroll financial operations");
        payrollMenu.add(createStyledMenuItem("Payroll Journal", "Payroll journal entries", this::managePayrollJournal));
        payrollMenu.add(createStyledMenuItem("Expense Allocation", "Allocate payroll expenses", this::allocateExpenses));
        
        menuBar.add(reportsMenu);
        menuBar.add(payrollMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createStyledMenu("🚪 Logout", "Sign out"));
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        JLabel titleLabel = new JLabel("Accounting Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCOUNTING_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea contentArea = new JTextArea("Accounting features will be implemented here.\n\n" +
                "Available Features:\n" +
                "• Financial reporting\n" +
                "• Payroll accounting\n" +
                "• Expense management\n" +
                "• Budget tracking");
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
    private void generateIncomeStatement() {
        showMessage("Income Statement", "Generating income statement...");
    }
    
    private void generateBalanceSheet() {
        showMessage("Balance Sheet", "Generating balance sheet...");
    }
    
    private void generateCashFlow() {
        showMessage("Cash Flow", "Generating cash flow statement...");
    }
    
    private void managePayrollJournal() {
        showMessage("Payroll Journal", "Opening payroll journal entries...");
    }
    
    private void allocateExpenses() {
        showMessage("Expense Allocation", "Opening expense allocation tool...");
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}