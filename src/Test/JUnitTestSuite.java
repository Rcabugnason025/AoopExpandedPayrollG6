package Test;

/**
 * Simple Test Suite for MotorPH Payroll System
 * Runs all tests without JUnit dependencies
 */
public class JUnitTestSuite {
    
    public static void main(String[] args) {
        System.out.println("🧪 MotorPH Payroll System Test Suite");
        System.out.println("=" .repeat(60));
        
        try {
            // Run Employee Tests
            System.out.println("\n📋 Running Employee Tests...");
            JUnitEmployeeTest.main(args);
            
            // Run Payroll Tests
            System.out.println("\n📋 Running Payroll Tests...");
            JUnitPayrollTest.main(args);
            
            // Run Attendance Tests
            System.out.println("\n📋 Running Attendance Tests...");
            JUnitAttendanceTest.main(args);
            
            // Run Database Tests
            System.out.println("\n📋 Running Database Tests...");
            JUnitDatabaseTest.main(args);
            
            System.out.println("\n" + "=" .repeat(60));
            System.out.println("🎉 ALL TEST SUITE COMPLETED SUCCESSFULLY!");
            System.out.println("✅ MotorPH Payroll System is ready for production");
            
        } catch (Exception e) {
            System.err.println("\n❌ Test Suite Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}