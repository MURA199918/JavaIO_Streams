import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {

    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO}
    private List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(){
        employeePayrollDBService  = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData>
                                  employeePayrollList){
        this();
        this.employeePayrollList = employeePayrollList;
    }
    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);

    }

    public void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee ID: ");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Employee Name: ");
        String name = consoleInputReader.next();
        System.out.println("Enter Employee Salary: ");
        double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id,name,salary));
    }

    public List<EmployeePayrollData> readEmployeePayrollServiceData(IOService ioService) throws PayrollServiceException {
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) throws PayrollServiceException{
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollDateRange(startDate, endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }

    public Map<String, Double> readCountByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getCountByGender();
        return null;
    }

    public Map<String, Double> readMinimumSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMinimumSalaryByGender();
        return null;
    }

    public Map<String, Double> readMaximumSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMaximumSalaryByGender();
        return null;
    }

    public Map<String, Double> readSumSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getSumSalaryByGender();
        return null;
    }

    public int readEmployeePayrollDatafromTables(IOService ioService, String tableName) {
        int noOfEntries = 0;
        if(ioService.equals(IOService.DB_IO))
            noOfEntries =  employeePayrollDBService.readDataFromAllTables(tableName);
        return noOfEntries;
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if(result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null) employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream()
                   .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                   .findFirst()
                   .orElse(null);
    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee being Added: "+Thread.currentThread().getName());
                this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
                                          employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee Added: "+Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false)){
            try{
                Thread.sleep(10);
            }catch (InterruptedException e){ }
        }
        System.out.println(employeePayrollDataList);
    }

    public void UpdateEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeUpdateStatus = new HashMap<>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeUpdateStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee being Updated: "+Thread.currentThread().getName());
                this.updateEmployeeSalary(employeePayrollData.name, employeePayrollData.salary);
                employeeUpdateStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee Updated: "+Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeUpdateStatus.containsValue(false)){
            try{
                Thread.sleep(10);
            }catch (InterruptedException e){ }
        }
        System.out.println(employeePayrollDataList);
    }

    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData->{
            System.out.println("Employee being Added: "+employeePayrollData.name);
            this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary, employeePayrollData.startDate, employeePayrollData.gender);
            System.out.println("Employee Added: "+employeePayrollData.name);
        });
        System.out.println(this.employeePayrollList);
    }

    public void UpdateEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee being updated: "+employeePayrollData.name);
            this.updateEmployeeSalary(employeePayrollData.name, employeePayrollData.salary);
            System.out.println("Employee Updated: "+employeePayrollData.name);
        });
        System.out.println(this.employeePayrollList);
    }

    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
    }

    public void removeEmployeePayrollFromDB(String name) throws PayrollServiceException {
        employeePayrollDBService.removeEmployeeFromDB(name);
    }

    public int getNoOfActiveEmployees() throws PayrollServiceException {
        return employeePayrollDBService.getNoOfActiveEmployeefromDB();
    }

    public void writeEmployeePayrollData(EmployeePayrollService.IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("Writing Employee Payroll Roaster to Console "+employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    public long readEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
        return employeePayrollList.size();
    }

    public void printData(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
        else System.out.println(employeePayrollList);
    }

    public long countEntries(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }
}
