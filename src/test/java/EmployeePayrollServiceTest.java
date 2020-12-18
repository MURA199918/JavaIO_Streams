import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
    public void givenEmployeePayrollInDB_whenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollServiceData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(4,employeePayrollData.size());
        System.out.println("Answer found");
    }
}
