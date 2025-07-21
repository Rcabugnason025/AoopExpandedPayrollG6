package model;

/**
 * UserRole enum for position-based access control
 * Defines different user roles and their access levels
 */
public enum UserRole {
    // Executive Level (Highest Access)
    CEO("Chief Executive Officer", 10, "Full system access with executive controls"),
    VP("Vice President", 9, "Senior executive access with strategic oversight"),
    DIRECTOR("Director", 8, "Department-level executive access"),
    
    // Management Level
    MANAGER("Manager", 7, "Management access with team oversight"),
    SUPERVISOR("Supervisor", 6, "Supervisory access with limited management functions"),
    TEAM_LEADER("Team Leader", 5, "Team leadership with basic management tools"),
    
    // HR Department
    HR_MANAGER("HR Manager", 7, "Full HR system access"),
    HR_SPECIALIST("HR Specialist", 6, "HR operations with employee management"),
    HR_ASSISTANT("HR Assistant", 4, "Basic HR functions and record keeping"),
    
    // Finance & Payroll
    PAYROLL_ADMIN("Payroll Administrator", 6, "Payroll processing and management"),
    ACCOUNTANT("Accountant", 5, "Financial records and basic payroll"),
    
    // IT Department
    IT_ADMIN("IT Administrator", 7, "System administration and technical support"),
    
    // General Employees
    SENIOR_EMPLOYEE("Senior Employee", 3, "Enhanced employee access with seniority benefits"),
    EMPLOYEE("Employee", 2, "Basic employee access to personal information"),
    
    // Special Cases
    CONTRACTOR("Contractor", 1, "Limited access for contract workers"),
    INTERN("Intern", 1, "Minimal access for training purposes");
    
    private final String displayName;
    private final int accessLevel;
    private final String description;
    
    UserRole(String displayName, int accessLevel, String description) {
        this.displayName = displayName;
        this.accessLevel = accessLevel;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public int getAccessLevel() { return accessLevel; }
    public String getDescription() { return description; }
    
    // Access level checks
    public boolean canAccessPayroll() {
        return accessLevel >= 6; // Manager level and above
    }
    
    public boolean canAccessHR() {
        return this == HR_MANAGER || this == HR_SPECIALIST || this == HR_ASSISTANT || accessLevel >= 8;
    }
    
    public boolean canAccessReports() {
        return accessLevel >= 5; // Team Leader and above
    }
    
    public boolean canApproveLeave() {
        return accessLevel >= 5; // Team Leader and above
    }
    
    public boolean canManageEmployees() {
        return canAccessHR() || accessLevel >= 7; // Manager level for non-HR
    }
    
    public boolean canAccessFinancialData() {
        return this == PAYROLL_ADMIN || this == ACCOUNTANT || accessLevel >= 8;
    }
    
    public boolean canAccessSystemSettings() {
        return this == IT_ADMIN || accessLevel >= 9;
    }
    
    public boolean isExecutiveLevel() {
        return accessLevel >= 8;
    }
    
    public boolean isManagementLevel() {
        return accessLevel >= 5 && accessLevel < 8;
    }
    
    public boolean isEmployeeLevel() {
        return accessLevel < 5;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}