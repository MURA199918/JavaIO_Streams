import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmployeePayrollServiceTest {
    @Test
    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayofEmps = {
                new EmployeePayrollData(1,"Jeff Bezos",100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayofEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3,entries);
    }

    @Test
    public void givenFileOnReadingFromShouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        long entries = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3,entries);
    }

    @Test
    public void givenEmployeePayrollInDB_whenRetrieved_ShouldMatchEmployeeCount() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(3,employeePayrollData.size());
        System.out.println("Answer found");
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws PayrollServiceException{
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 5000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
        System.out.println("Answer found");
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws PayrollServiceException{
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018,01,01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService
                .readEmployeePayrollForDateRange(EmployeePayrollService.IOService.DB_IO, startDate, endDate);
        Assert.assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrieveByGender_ShouldReturnProperValue() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00));
        Assert.assertTrue(averageSalaryByGender.get("F").equals(5000000.00));
    }

    @Test
    public void givenPayrollData_whenCountRetrieveByGender_ShouldReturnProperValue() throws PayrollServiceException{
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readCountByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(2.0) && countByGender.get("F").equals(1.0));
    }

    @Test
    public void givenPayrollData_whenCountRetrieveByGender_ShouldReturnProperMinimumValue() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readMinimumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(1000000.00) && countByGender.get("F").equals(5000000.00));
    }

    @Test
    public void givenPayrollData_whenCountRetrieveByGender_ShouldReturnProperMaximumValue() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readMaximumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(3000000.00) && countByGender.get("F").equals(5000000.00));
    }

    @Test
    public void givenPayrollData_whenCountRetrieveByGender_ShouldReturnProperSumValue() throws PayrollServiceException{
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readSumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(4000000.00) && countByGender.get("F").equals(5000000.00));
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00, LocalDate.now(), "M");
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }

    @Test
    public void givenPayrollData_ShouldRetrieveData_FromAllTables() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        int noOfEntries_Table1 = employeePayrollService.readEmployeePayrollDatafromTables(EmployeePayrollService.IOService.DB_IO,"employee_payroll");
        Assert.assertEquals(4, noOfEntries_Table1);
        int noOfEntries_Table2 = employeePayrollService.readEmployeePayrollDatafromTables(EmployeePayrollService.IOService.DB_IO,"payroll_details");
        Assert.assertEquals(1, noOfEntries_Table2);
        int noOfEntries_Table3 = employeePayrollService.readEmployeePayrollDatafromTables(EmployeePayrollService.IOService.DB_IO, "department");
        Assert.assertEquals(1,noOfEntries_Table3);
    }
}
