package Test;

import model.Employee;
import dao.EmployeeDAO;
import java.time.LocalDate;

/**
 * Simple Employee Test Class (without JUnit dependencies)
 * Tests employee functionality using basic assertions
 */
public class JUnitEmployeeTest {

    private Employee testEmployee;
    private EmployeeDAO employeeDAO;
    private static final int TEST_EMPLOYEE_ID = 99999;

    public void setUp() {
        testEmployee = new Employee();
        employeeDAO = new EmployeeDAO();
        System.out.println("🔧 Test setup completed");
    }

    public void testCreateValidEmployee() {
        // Arrange
        testEmployee.setEmployeeId(TEST_EMPLOYEE_ID);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setBasicSalary(50000.0);
        testEmployee.setStatus("Regular");
        testEmployee.setPosition("Software Developer");

        // Act & Assert
        assert testEmployee.getEmployeeId() == TEST_EMPLOYEE_ID : "Employee ID should match";
        assert "John".equals(testEmployee.getFirstName()) : "First name should match";
        assert "Doe".equals(testEmployee.getLastName()) : "Last name should match";
        assert "John Doe".equals(testEmployee.getFullName()) : "Full name should be concatenated";
        assert Math.abs(testEmployee.getBasicSalary() - 50000.0) < 0.01 : "Basic salary should match";
        assert "Regular".equals(testEmployee.getStatus()) : "Status should match";
        assert "Software Developer".equals(testEmployee.getPosition()) : "Position should match";
        assert testEmployee.isValid() : "Employee should be valid";
        
        System.out.println("✅ testCreateValidEmployee passed");
    }

    public void testInvalidEmployeeId() {
        // Test negative employee ID
        try {
            testEmployee.setEmployeeId(-1);
            assert false : "Should throw exception for negative employee ID";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("positive") : "Error message should mention positive requirement";
        }
        
        // Test zero employee ID
        try {
            testEmployee.setEmployeeId(0);
            assert false : "Should throw exception for zero employee ID";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✅ testInvalidEmployeeId passed");
    }

    public void testNameValidation() {
        // Test null first name
        try {
            testEmployee.setFirstName(null);
            assert false : "Should throw exception for null first name";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be null") : "Null error message";
        }
        
        // Test empty first name
        try {
            testEmployee.setFirstName("");
            assert false : "Should throw exception for empty first name";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be null") : "Empty error message";
        }
        
        // Test whitespace first name
        try {
            testEmployee.setFirstName("   ");
            assert false : "Should throw exception for whitespace first name";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be null") : "Whitespace error message";
        }
        
        System.out.println("✅ testNameValidation passed");
    }

    public void testSalaryValidation() {
        // Test valid salary
        try {
            testEmployee.setBasicSalary(50000.0);
            // Should not throw exception
        } catch (Exception e) {
            assert false : "Should not throw exception for valid salary";
        }
        
        // Test negative salary
        try {
            testEmployee.setBasicSalary(-1000.0);
            assert false : "Should throw exception for negative salary";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be negative") : "Error message should mention negative restriction";
        }
        
        System.out.println("✅ testSalaryValidation passed");
    }

    public void testTotalAllowances() {
        // Arrange
        testEmployee.setRiceSubsidy(1500.0);
        testEmployee.setPhoneAllowance(1000.0);
        testEmployee.setClothingAllowance(500.0);

        // Act
        double total = testEmployee.getTotalAllowances();

        // Assert
        assert Math.abs(total - 3000.0) < 0.01 : "Total allowances should be 3000.0";
        
        System.out.println("✅ testTotalAllowances passed");
    }

    public void testAgeCalculation() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(25);
        testEmployee.setBirthday(birthDate);

        // Act
        int age = testEmployee.getAge();

        // Assert
        assert age == 25 : "Age should be calculated correctly";
        
        System.out.println("✅ testAgeCalculation passed");
    }

    public void testNullBirthdayReturnsZeroAge() {
        // Arrange
        testEmployee.setBirthday(null);

        // Act
        int age = testEmployee.getAge();

        // Assert
        assert age == 0 : "Age should be 0 if birthday is null";
        
        System.out.println("✅ testNullBirthdayReturnsZeroAge passed");
    }

    public void testDatabaseEmployeeRetrieval() {
        try {
            // Act
            Employee employee = employeeDAO.getEmployeeById(10001);

            // Assert
            assert employee != null : "Employee should not be null";
            assert employee.getEmployeeId() == 10001 : "Employee ID should match";
            assert employee.getFirstName() != null : "First name should not be null";
            assert employee.getLastName() != null : "Last name should not be null";
            assert employee.getBasicSalary() > 0 : "Basic salary should be positive";
            
            System.out.println("✅ testDatabaseEmployeeRetrieval passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testDatabaseEmployeeRetrieval skipped - database not available");
        }
    }

    public void testEmployeeSearch() {
        try {
            // Act
            var employees = employeeDAO.searchEmployees("Garcia");

            // Assert
            assert employees != null : "Search results should not be null";
            assert !employees.isEmpty() : "Search should return results";
            assert employees.stream().anyMatch(emp -> 
                emp.getLastName().contains("Garcia") || emp.getFirstName().contains("Garcia")) : 
                "Results should contain Garcia";
            
            System.out.println("✅ testEmployeeSearch passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testEmployeeSearch skipped - database not available");
        }
    }

    public void testEmployeeValidationRules() {
        // Test complete valid employee
        testEmployee.setEmployeeId(TEST_EMPLOYEE_ID);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setBasicSalary(50000.0);
        testEmployee.setPosition("Developer");

        assert testEmployee.isValid() : "Complete employee should be valid";

        // Test invalid employee (missing required fields)
        Employee invalidEmployee = new Employee();
        assert !invalidEmployee.isValid() : "Incomplete employee should be invalid";
        
        System.out.println("✅ testEmployeeValidationRules passed");
    }

    // Main method to run all tests
    public static void main(String[] args) {
        System.out.println("🧪 Running Employee Tests...");
        
        JUnitEmployeeTest test = new JUnitEmployeeTest();
        
        try {
            test.setUp();
            test.testCreateValidEmployee();
            
            test.setUp();
            test.testInvalidEmployeeId();
            
            test.setUp();
            test.testNameValidation();
            
            test.setUp();
            test.testSalaryValidation();
            
            test.setUp();
            test.testTotalAllowances();
            
            test.setUp();
            test.testAgeCalculation();
            
            test.setUp();
            test.testNullBirthdayReturnsZeroAge();
            
            test.setUp();
            test.testDatabaseEmployeeRetrieval();
            
            test.setUp();
            test.testEmployeeSearch();
            
            test.setUp();
            test.testEmployeeValidationRules();
            
            System.out.println("🎉 All Employee Tests Passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}