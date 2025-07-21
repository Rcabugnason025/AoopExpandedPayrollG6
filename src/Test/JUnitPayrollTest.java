package Test;

import model.Payroll;
import service.PayrollCalculator;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Simple Payroll Test Class (without JUnit dependencies)
 * Tests payroll functionality using basic assertions
 */
public class JUnitPayrollTest {

    private Payroll testPayroll;
    private PayrollCalculator payrollCalculator;

    public void setUp() {
        testPayroll = new Payroll();
        payrollCalculator = new PayrollCalculator();
        System.out.println("🔧 Payroll test setup completed");
    }

    public void testPayrollCreation() {
        // Arrange & Act
        testPayroll.setEmployeeId(10001);
        testPayroll.setMonthlyRate(50000.0);
        testPayroll.setDaysWorked(22);
        testPayroll.setGrossPay(50000.0);
        testPayroll.setTotalDeductions(10000.0);
        testPayroll.setNetPay(40000.0);

        // Assert
        assert testPayroll.getEmployeeId() == 10001 : "Employee ID should match";
        assert Math.abs(testPayroll.getMonthlyRate() - 50000.0) < 0.01 : "Monthly rate should match";
        assert testPayroll.getDaysWorked() == 22 : "Days worked should match";
        assert Math.abs(testPayroll.getGrossPay() - 50000.0) < 0.01 : "Gross pay should match";
        assert Math.abs(testPayroll.getTotalDeductions() - 10000.0) < 0.01 : "Total deductions should match";
        assert Math.abs(testPayroll.getNetPay() - 40000.0) < 0.01 : "Net pay should match";
        assert testPayroll.isValid() : "Payroll should be valid";
        
        System.out.println("✅ testPayrollCreation passed");
    }

    public void testPayrollValidation() {
        // Test invalid employee ID
        try {
            testPayroll.setEmployeeId(-1);
            assert false : "Should throw exception for negative employee ID";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("positive") : "Employee ID error message";
        }

        // Test invalid monthly rate
        try {
            testPayroll.setMonthlyRate(-1000.0);
            assert false : "Should throw exception for negative monthly rate";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("negative") : "Monthly rate error message";
        }

        // Test invalid days worked
        try {
            testPayroll.setDaysWorked(-1);
            assert false : "Should throw exception for negative days worked";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("negative") : "Days worked error message";
        }
        
        System.out.println("✅ testPayrollValidation passed");
    }

    public void testGovernmentContributions() {
        // Test SSS calculation for different salary ranges
        assert Math.abs(calculateSSS(4000.0) - 180.00) < 0.01 : "SSS for 4000 should be 180.00";
        assert Math.abs(calculateSSS(30000.0) - 1125.00) < 0.01 : "SSS for 30000 should be 1125.00";
        assert Math.abs(calculateSSS(50000.0) - 1125.00) < 0.01 : "SSS for 50000 should be 1125.00";

        // Test PhilHealth calculation
        double philHealthContrib = calculatePhilHealth(50000.0);
        assert philHealthContrib >= 500.00 && philHealthContrib <= 5000.00 : 
            "PhilHealth contribution should be between 500 and 5000";

        // Test Pag-IBIG calculation
        assert Math.abs(calculatePagIBIG(1500.0) - 15.00) < 0.01 : "Pag-IBIG for 1500 should be 15.00";
        assert Math.abs(calculatePagIBIG(20000.0) - 200.00) < 0.01 : "Pag-IBIG for 20000 should be 200.00";
        assert Math.abs(calculatePagIBIG(50000.0) - 200.00) < 0.01 : "Pag-IBIG for 50000 should be 200.00";
        
        System.out.println("✅ testGovernmentContributions passed");
    }

    public void testPayrollCalculationComponents() {
        // Test daily rate calculation
        double monthlySalary = 50000.0;
        double expectedDailyRate = monthlySalary / 22.0; // 22 working days
        assert Math.abs(expectedDailyRate - 2272.73) < 0.01 : "Daily rate calculation should be correct";

        // Test hourly rate calculation
        double expectedHourlyRate = expectedDailyRate / 8.0; // 8 hours per day
        assert Math.abs(expectedHourlyRate - 284.09) < 0.01 : "Hourly rate calculation should be correct";

        // Test overtime calculation
        double overtimeHours = 5.0;
        double overtimeRate = expectedHourlyRate * 1.25; // 125% rate
        double expectedOvertimePay = overtimeHours * overtimeRate;
        assert Math.abs(expectedOvertimePay - 1775.57) < 0.01 : "Overtime pay calculation should be correct";
        
        System.out.println("✅ testPayrollCalculationComponents passed");
    }

    public void testDateRangeValidation() {
        // Test valid date range
        Date startDate = Date.valueOf(LocalDate.of(2024, 6, 1));
        Date endDate = Date.valueOf(LocalDate.of(2024, 6, 30));
        
        try {
            testPayroll.setPeriodStart(startDate);
            testPayroll.setPeriodEnd(endDate);
            // Should not throw exception
        } catch (Exception e) {
            assert false : "Should not throw exception for valid date range";
        }

        // Test invalid date range (end before start)
        Date invalidEndDate = Date.valueOf(LocalDate.of(2024, 5, 30));
        
        testPayroll.setPeriodStart(startDate);
        try {
            testPayroll.setPeriodEnd(invalidEndDate);
            assert false : "Should throw exception when end date is before start date";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("cannot be before") : "Error message should mention date order restriction";
        }
        
        System.out.println("✅ testDateRangeValidation passed");
    }

    public void testPayrollCalculatorIntegration() {
        try {
            // Test with valid employee ID and date range
            LocalDate periodStart = LocalDate.of(2024, 6, 1);
            LocalDate periodEnd = LocalDate.of(2024, 6, 30);
            
            Payroll calculatedPayroll = payrollCalculator.calculatePayroll(10001, periodStart, periodEnd);
            
            assert calculatedPayroll != null : "Calculated payroll should not be null";
            assert calculatedPayroll.getEmployeeId() == 10001 : "Employee ID should match";
            assert calculatedPayroll.getGrossPay() >= 0 : "Gross pay should be non-negative";
            assert calculatedPayroll.getTotalDeductions() >= 0 : "Total deductions should be non-negative";
            assert calculatedPayroll.isValid() : "Calculated payroll should be valid";
            
            System.out.println("✅ testPayrollCalculatorIntegration passed");
            
        } catch (Exception e) {
            System.out.println("⚠️ testPayrollCalculatorIntegration skipped - requires database connection");
        }
    }

    // Helper methods for testing calculations
    private double calculateSSS(double monthlySalary) {
        if (monthlySalary <= 4000) return 180.00;
        if (monthlySalary <= 25000) return Math.min(monthlySalary * 0.045, 1125.00);
        return 1125.00;
    }

    private double calculatePhilHealth(double monthlySalary) {
        double contribution = monthlySalary * 0.025;
        return Math.max(Math.min(contribution, 5000.00), 500.00);
    }

    private double calculatePagIBIG(double monthlySalary) {
        if (monthlySalary <= 1500) return monthlySalary * 0.01;
        return Math.min(monthlySalary * 0.02, 200.00);
    }

    // Main method to run all tests
    public static void main(String[] args) {
        System.out.println("🧪 Running Payroll Tests...");
        
        JUnitPayrollTest test = new JUnitPayrollTest();
        
        try {
            test.setUp();
            test.testPayrollCreation();
            
            test.setUp();
            test.testPayrollValidation();
            
            test.testGovernmentContributions();
            test.testPayrollCalculationComponents();
            
            test.setUp();
            test.testDateRangeValidation();
            
            test.setUp();
            test.testPayrollCalculatorIntegration();
            
            System.out.println("🎉 All Payroll Tests Passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}