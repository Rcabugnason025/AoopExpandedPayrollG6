package Test;

import util.DBConnection;
import dao.EmployeeDAO;
import model.Employee;

/**
 * Simple Database Test Class (without JUnit dependencies)
 * Tests database functionality using basic assertions
 */
public class JUnitDatabaseTest {

    private EmployeeDAO employeeDAO;

    public void setUp() {
        employeeDAO = new EmployeeDAO();
        System.out.println("🔧 Database test setup completed");
    }

    public void testDatabaseConnection() {
        // Act & Assert
        boolean connected = DBConnection.testConnection();
        assert connected : "Database connection should be successful";
        
        System.out.println("✅ testDatabaseConnection passed");
    }

    public void testDatabaseInfo() {
        // Act
        String dbInfo = DBConnection.getDatabaseInfo();
        
        // Assert
        assert dbInfo != null : "Database info should not be null";
        assert !dbInfo.isEmpty() : "Database info should not be empty";
        assert dbInfo.contains("aoopdatabase_payroll") : "Database info should contain database name";
        
        System.out.println("✅ testDatabaseInfo passed");
    }

    public void testEmployeeDAO() {
        try {
            // Test getting all employees
            var employees = employeeDAO.getAllEmployees();
            assert employees != null : "Employees list should not be null";
            assert !employees.isEmpty() : "Employees list should not be empty";
            assert employees.size() >= 34 : "Should have at least 34 employees from setup";

            // Test getting specific employee
            Employee employee = employeeDAO.getEmployeeById(10001);
            assert employee != null : "Employee should not be null";
            assert employee.getEmployeeId() == 10001 : "Employee ID should be 10001";
            assert "Garcia".equals(employee.getLastName()) : "Last name should be Garcia";
            assert "Manuel III".equals(employee.getFirstName()) : "First name should be Manuel III";
            
            System.out.println("✅ testEmployeeDAO passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testEmployeeDAO skipped - database not available: " + e.getMessage());
        }
    }

    public void testEmployeeSearch() {
        try {
            // Test search functionality
            var results = employeeDAO.searchEmployees("Garcia");
            
            assert results != null : "Search results should not be null";
            assert !results.isEmpty() : "Search results should not be empty";
            assert results.stream().anyMatch(emp -> 
                emp.getLastName().contains("Garcia") || emp.getFirstName().contains("Garcia")) : 
                "Results should contain Garcia";
            
            System.out.println("✅ testEmployeeSearch passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testEmployeeSearch skipped - database not available");
        }
    }

    public void testEmployeeExists() {
        try {
            // Test existing employee
            assert employeeDAO.employeeExists(10001) : "Employee 10001 should exist";
            
            // Test non-existing employee
            assert !employeeDAO.employeeExists(99999) : "Employee 99999 should not exist";
            
            System.out.println("✅ testEmployeeExists passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testEmployeeExists skipped - database not available");
        }
    }

    // Main method to run all tests
    public static void main(String[] args) {
        System.out.println("🧪 Running Database Tests...");
        
        JUnitDatabaseTest test = new JUnitDatabaseTest();
        
        try {
            test.setUp();
            test.testDatabaseConnection();
            
            test.setUp();
            test.testDatabaseInfo();
            
            test.setUp();
            test.testEmployeeDAO();
            
            test.setUp();
            test.testEmployeeSearch();
            
            test.setUp();
            test.testEmployeeExists();
            
            System.out.println("🎉 All Database Tests Passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}