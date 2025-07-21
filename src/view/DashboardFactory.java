package view;

import model.Employee;
import model.UserRole;
import util.PositionRoleMapper;
import javax.swing.JFrame;
import java.util.logging.Logger;

/**
 * Factory class for creating position-specific dashboards
 * Creates appropriate dashboard based on employee position and role
 */
public class DashboardFactory {
    private static final Logger LOGGER = Logger.getLogger(DashboardFactory.class.getName());
    
    /**
     * Create appropriate dashboard based on employee position
     * @param employee Employee object with position information
     * @return JFrame dashboard appropriate for the employee's role
     */
    public static JFrame createDashboard(Employee employee) {
        if (employee == null) {
            LOGGER.severe("❌ Cannot create dashboard: Employee is null");
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        UserRole role = PositionRoleMapper.getUserRole(employee.getPosition());
        
        LOGGER.info(String.format("🏭 Creating dashboard for: %s | Position: %s | Role: %s | Access Level: %d", 
                employee.getFullName(), 
                employee.getPosition(), 
                role.getDisplayName(), 
                role.getAccessLevel()));
        
        try {
            JFrame dashboard = createDashboardByRole(employee, role);
            
            if (dashboard == null) {
                LOGGER.warning("⚠️ Dashboard creation returned null, creating default Employee Dashboard");
                dashboard = new EmployeeDashboard(employee);
            }
            
            LOGGER.info("✅ Successfully created " + dashboard.getClass().getSimpleName() + 
                       " for " + employee.getFullName());
            
            return dashboard;
            
        } catch (Exception e) {
            LOGGER.severe("❌ Error creating dashboard for " + employee.getFullName() + ": " + e.getMessage());
            LOGGER.warning("🔄 Falling back to Employee Dashboard");
            return new EmployeeDashboard(employee);
        }
    }
    
    /**
     * Create dashboard based on user role
     */
    private static JFrame createDashboardByRole(Employee employee, UserRole role) {
        switch (role) {
            // Executive Level Dashboards
            case CEO:
            case VP:
            case DIRECTOR:
                return new ExecutiveDashboard(employee);
                
            // HR Department Dashboards
            case HR_MANAGER:
            case HR_SPECIALIST:
            case HR_ASSISTANT:
                return new view.HRDashboard(employee);
                
            // Payroll & Finance Dashboards
            case PAYROLL_ADMIN:
                return new PayrollDashboard(employee);
                
            case ACCOUNTANT:
                return new AccountingDashboard(employee);
                
            // Management Dashboards
            case MANAGER:
            case SUPERVISOR:
                return new ManagerDashboard(employee);
                
            case TEAM_LEADER:
                return new TeamLeaderDashboard(employee);
                
            // IT Dashboard
            case IT_ADMIN:
                return new ITAdminDashboard(employee);
                
            // Employee Level Dashboards
            case SENIOR_EMPLOYEE:
                return new SeniorEmployeeDashboard(employee);
                
            case EMPLOYEE:
            case CONTRACTOR:
            case INTERN:
            default:
                return new view.EmployeeDashboard(employee);
        }
    }
    
    /**
     * Get dashboard name for display purposes
     */
    public static String getDashboardName(Employee employee) {
        if (employee == null) return "Unknown Dashboard";
        
        UserRole role = PositionRoleMapper.getUserRole(employee.getPosition());
        String dashboardType = PositionRoleMapper.getDashboardType(employee.getPosition());
        
        return String.format("%s - %s", dashboardType, role.getDisplayName());
    }
    
    /**
     * Check if employee can access specific dashboard features
     */
    public static boolean canAccessFeature(Employee employee, String feature) {
        if (employee == null) return false;
        
        UserRole role = PositionRoleMapper.getUserRole(employee.getPosition());
        
        switch (feature.toLowerCase()) {
            case "payroll_processing":
                return role.canAccessPayroll();
            case "employee_management":
                return role.canManageEmployees();
            case "hr_functions":
                return role.canAccessHR();
            case "financial_reports":
                return role.canAccessFinancialData();
            case "system_settings":
                return role.canAccessSystemSettings();
            case "leave_approval":
                return role.canApproveLeave();
            case "reports":
                return role.canAccessReports();
            default:
                return true; // Basic features accessible to all
        }
    }
}