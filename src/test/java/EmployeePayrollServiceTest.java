import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
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

    @Test
    public void givenEmployeeName_ShouldRemove_EmployeePayrollData_FromDataBase() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.removeEmployeePayrollFromDB("Mark");
        int noOfEmployee = employeePayrollService.getNoOfActiveEmployees();
        Assert.assertEquals(3,noOfEmployee);
    }

    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() throws PayrollServiceException {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Sundar", "M", 600000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 2000000.0, LocalDate.now())
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without Thread : "+ Duration.between(start,end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration With Threads: "+Duration.between(threadStart, threadEnd));
        employeePayrollService.printData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(13, employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));

    }

    @Test
    public void givenEmployees_WhenSalaryUpdatedToDB_ShouldMatchEmployeeEntries() throws PayrollServiceException{
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", "M", 200000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Bill Gates", "M", 100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mark Zuckerberg", "M", 200000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Sundar", "M", 500000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mukesh", "M", 2000000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 3000000.0, LocalDate.now())
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.UpdateEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without Thread : "+ Duration.between(start,end));
        Instant threadStart = Instant.now();
        employeePayrollService.UpdateEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration With Threads: "+Duration.between(threadStart, threadEnd));
        employeePayrollService.printData(EmployeePayrollService.IOService.DB_IO);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Anil");
        Assert.assertTrue(result);
        System.out.println("Answer found");
        Assert.assertEquals(13, employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }
}
