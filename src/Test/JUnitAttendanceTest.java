package Test;

import model.Attendance;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Simple Attendance Test Class (without JUnit dependencies)
 * Tests attendance functionality using basic assertions
 */
public class JUnitAttendanceTest {

    private Attendance testAttendance;

    public void setUp() {
        testAttendance = new Attendance();
        System.out.println("🔧 Attendance test setup completed");
    }

    public void testCreateValidAttendance() {
        // Arrange
        int employeeId = 10001;
        Date date = Date.valueOf(LocalDate.now());
        Time logIn = Time.valueOf(LocalTime.of(8, 0));
        Time logOut = Time.valueOf(LocalTime.of(17, 0));

        // Act
        testAttendance.setEmployeeId(employeeId);
        testAttendance.setDate(date);
        testAttendance.setLogIn(logIn);
        testAttendance.setLogOut(logOut);

        // Assert
        assert testAttendance.getEmployeeId() == employeeId : "Employee ID should match";
        assert testAttendance.getDate().equals(date) : "Date should match";
        assert testAttendance.getLogIn().equals(logIn) : "Log in time should match";
        assert testAttendance.getLogOut().equals(logOut) : "Log out time should match";
        assert testAttendance.isPresent() : "Should be present";
        assert Math.abs(testAttendance.getWorkHours() - 9.0) < 0.01 : "Work hours should be 9.0";
        
        System.out.println("✅ testCreateValidAttendance passed");
    }

    public void testInvalidEmployeeId() {
        try {
            testAttendance.setEmployeeId(-1);
            assert false : "Should throw exception for invalid employee ID";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("positive") : "Error message should mention positive requirement";
            System.out.println("✅ testInvalidEmployeeId passed");
        }
    }

    public void testLateArrival() {
        // Arrange
        testAttendance.setEmployeeId(10001);
        testAttendance.setDate(Date.valueOf(LocalDate.now()));
        testAttendance.setLogIn(Time.valueOf(LocalTime.of(8, 30))); // 30 minutes late
        testAttendance.setLogOut(Time.valueOf(LocalTime.of(17, 0)));

        // Act & Assert
        assert testAttendance.isLate() : "Should detect late arrival";
        assert Math.abs(testAttendance.getLateMinutes() - 30.0) < 0.01 : "Late minutes should be 30";
        
        System.out.println("✅ testLateArrival passed");
    }

    public void testUndertime() {
        // Arrange
        testAttendance.setEmployeeId(10001);
        testAttendance.setDate(Date.valueOf(LocalDate.now()));
        testAttendance.setLogIn(Time.valueOf(LocalTime.of(8, 0)));
        testAttendance.setLogOut(Time.valueOf(LocalTime.of(16, 30))); // 30 minutes early

        // Act & Assert
        assert testAttendance.hasUndertime() : "Should detect undertime";
        assert Math.abs(testAttendance.getUndertimeMinutes() - 30.0) < 0.01 : "Undertime minutes should be 30";
        
        System.out.println("✅ testUndertime passed");
    }

    public void testWorkDurationCalculation() {
        // Arrange
        testAttendance.setEmployeeId(10001);
        testAttendance.setDate(Date.valueOf(LocalDate.now()));
        testAttendance.setLogIn(Time.valueOf(LocalTime.of(8, 0)));
        testAttendance.setLogOut(Time.valueOf(LocalTime.of(17, 30)));

        // Act
        double workHours = testAttendance.getWorkHours();

        // Assert
        assert Math.abs(workHours - 9.5) < 0.01 : "Work hours should be 9.5";
        
        System.out.println("✅ testWorkDurationCalculation passed");
    }

    public void testNullLogOut() {
        // Arrange
        testAttendance.setEmployeeId(10001);
        testAttendance.setDate(Date.valueOf(LocalDate.now()));
        testAttendance.setLogIn(Time.valueOf(LocalTime.of(8, 0)));
        testAttendance.setLogOut(null);

        // Act & Assert
        assert testAttendance.isPresent() : "Should be present even with null log out";
        assert Math.abs(testAttendance.getWorkHours() - 0.0) < 0.01 : "Work hours should be 0 with null log out";
        
        System.out.println("✅ testNullLogOut passed");
    }

    public void testDateValidation() {
        // Test null date
        try {
            testAttendance.setDate(null);
            assert false : "Should throw exception for null date";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be null") : "Error message should mention null restriction";
            System.out.println("✅ testDateValidation passed");
        }
    }

    // Main method to run all tests
    public static void main(String[] args) {
        System.out.println("🧪 Running Attendance Tests...");
        
        JUnitAttendanceTest test = new JUnitAttendanceTest();
        
        try {
            test.setUp();
            test.testCreateValidAttendance();
            
            test.setUp();
            test.testInvalidEmployeeId();
            
            test.setUp();
            test.testLateArrival();
            
            test.setUp();
            test.testUndertime();
            
            test.setUp();
            test.testWorkDurationCalculation();
            
            test.setUp();
            test.testNullLogOut();
            
            test.setUp();
            test.testDateValidation();
            
            System.out.println("🎉 All Attendance Tests Passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}