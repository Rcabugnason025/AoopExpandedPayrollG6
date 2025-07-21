package util;

import model.UserRole;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Maps employee positions to user roles for access control
 * Based on your actual database positions
 */
public class PositionRoleMapper {
    private static final Logger LOGGER = Logger.getLogger(PositionRoleMapper.class.getName());
    private static final Map<String, UserRole> POSITION_ROLE_MAP = new HashMap<>();
    
    static {
        // Executive Level - C-Suite
        POSITION_ROLE_MAP.put("chief executive officer", UserRole.CEO);
        POSITION_ROLE_MAP.put("chief operating officer", UserRole.VP);
        POSITION_ROLE_MAP.put("chief finance officer", UserRole.VP);
        POSITION_ROLE_MAP.put("chief marketing officer", UserRole.VP);
        
        // IT Department
        POSITION_ROLE_MAP.put("it operations and systems", UserRole.IT_ADMIN);
        
        // HR Department Hierarchy
        POSITION_ROLE_MAP.put("hr manager", UserRole.HR_MANAGER);
        POSITION_ROLE_MAP.put("hr team leader", UserRole.HR_SPECIALIST);
        POSITION_ROLE_MAP.put("hr rank and file", UserRole.HR_ASSISTANT);
        
        // Finance & Accounting Department
        POSITION_ROLE_MAP.put("accounting head", UserRole.MANAGER);
        POSITION_ROLE_MAP.put("payroll manager", UserRole.PAYROLL_ADMIN);
        POSITION_ROLE_MAP.put("payroll team leader", UserRole.PAYROLL_ADMIN);
        POSITION_ROLE_MAP.put("payroll rank and file", UserRole.ACCOUNTANT);
        
        // Account Management Department
        POSITION_ROLE_MAP.put("account manager", UserRole.MANAGER);
        POSITION_ROLE_MAP.put("account team leader", UserRole.TEAM_LEADER);
        POSITION_ROLE_MAP.put("account rank and file", UserRole.EMPLOYEE);
        
        // Other Departments - General Employees
        POSITION_ROLE_MAP.put("sales & marketing", UserRole.EMPLOYEE);
        POSITION_ROLE_MAP.put("supply chain and logistics", UserRole.EMPLOYEE);
        POSITION_ROLE_MAP.put("customer service and relations", UserRole.EMPLOYEE);
        
        LOGGER.info("✅ Position-Role mapping initialized with " + POSITION_ROLE_MAP.size() + " mappings");
    }
    
    /**
     * Get user role based on employee position
     * @param position Employee position from database
     * @return Corresponding UserRole
     */
    public static UserRole getUserRole(String position) {
        if (position == null || position.trim().isEmpty()) {
            LOGGER.warning("⚠️ Empty position provided, defaulting to EMPLOYEE role");
            return UserRole.EMPLOYEE;
        }
        
        String normalizedPosition = position.toLowerCase().trim();
        UserRole role = POSITION_ROLE_MAP.get(normalizedPosition);
        
        if (role == null) {
            LOGGER.warning("⚠️ Unknown position: '" + position + "', defaulting to EMPLOYEE role");
            return UserRole.EMPLOYEE;
        }
        
        LOGGER.fine("🎯 Position '" + position + "' mapped to role: " + role.getDisplayName());
        return role;
    }
    
    /**
     * Check if position has HR access
     */
    public static boolean hasHRAccess(String position) {
        UserRole role = getUserRole(position);
        return role.canAccessHR();
    }
    
    /**
     * Check if position has payroll access
     */
    public static boolean hasPayrollAccess(String position) {
        UserRole role = getUserRole(position);
        return role.canAccessPayroll();
    }
    
    /**
     * Check if position has executive access
     */
    public static boolean hasExecutiveAccess(String position) {
        UserRole role = getUserRole(position);
        return role.isExecutiveLevel();
    }
    
    /**
     * Check if position can manage employees
     */
    public static boolean canManageEmployees(String position) {
        UserRole role = getUserRole(position);
        return role.canManageEmployees();
    }
    
    /**
     * Check if position can approve leave requests
     */
    public static boolean canApproveLeave(String position) {
        UserRole role = getUserRole(position);
        return role.canApproveLeave();
    }
    
    /**
     * Check if position can access financial data
     */
    public static boolean canAccessFinancialData(String position) {
        UserRole role = getUserRole(position);
        return role.canAccessFinancialData();
    }
    
    /**
     * Get access level for position
     */
    public static int getAccessLevel(String position) {
        UserRole role = getUserRole(position);
        return role.getAccessLevel();
    }
    
    /**
     * Get all mapped positions for debugging
     */
    public static Map<String, UserRole> getAllMappings() {
        return new HashMap<>(POSITION_ROLE_MAP);
    }
    
    /**
     * Check if a position is mapped
     */
    public static boolean isPositionMapped(String position) {
        if (position == null || position.trim().isEmpty()) {
            return false;
        }
        return POSITION_ROLE_MAP.containsKey(position.toLowerCase().trim());
    }
    
    /**
     * Get dashboard type for position
     */
    public static String getDashboardType(String position) {
        UserRole role = getUserRole(position);
        
        if (role.isExecutiveLevel()) {
            return "Executive Dashboard";
        } else if (role.canAccessHR()) {
            return "HR Dashboard";
        } else if (role.canAccessPayroll()) {
            return "Payroll Dashboard";
        } else if (role.isManagementLevel()) {
            return "Management Dashboard";
        } else {
            return "Employee Dashboard";
        }
    }
}