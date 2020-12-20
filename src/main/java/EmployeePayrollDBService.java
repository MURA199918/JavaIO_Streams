import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

    private static EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement employeePayrollDataStatement;
    private EmployeePayrollDBService() {

    }

    public static EmployeePayrollDBService getInstance() {
        if(employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "muralis@";
        Connection connection;
        System.out.println("Connecting to database: "+jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is Successful!!!!!!!!"+connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() throws PayrollServiceException{
        String sql = "SELECT * FROM employee_payroll";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public List<EmployeePayrollData> getEmployeePayrollDateRange(LocalDate startDate, LocalDate endDate) throws PayrollServiceException {
        String sql = String.format("SELECT * FROM employee_payroll WHERE start BETWEEN '%s' AND '%s';",
                                   Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(salary) as avg_salary FROM employee_payroll GROUP BY gender;";
        return getCompileByGender("gender", "avg_salary", sql );
    }

    public Map<String, Double> getCountByGender() {
        String sql = "SELECT gender, count(salary) as count_gender from employee_payroll GROUP BY gender";
        return getCompileByGender("gender","count_gender",sql);
    }

    public Map<String, Double> getMinimumSalaryByGender() {
        String sql = "SELECT gender, min(salary) as minSalary_gender from employee_payroll GROUP BY gender";
        return getCompileByGender("gender","minSalary_gender",sql);
    }

    public Map<String, Double> getMaximumSalaryByGender() {
        String sql = "SELECT gender, max(salary) as maxSalary_gender from employee_payroll GROUP BY gender";
        return getCompileByGender("gender","maxSalary_gender",sql);
    }

    public Map<String, Double> getSumSalaryByGender() {
        String sql = "SELECT gender, sum(salary) as sumSalary_gender from employee_payroll GROUP BY gender";
        return getCompileByGender("gender","sumSalary_gender",sql);
    }

    private Map<String, Double> getCompileByGender(String gender, String compile, String sql) {
        Map<String, Double> genderCountMap = new HashMap<>();
        try(Connection connection = this.getConnection();){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                String getGender = result.getString(gender);
                Double count = result.getDouble(compile);
                genderCountMap.put(getGender, count);
            }
        }catch (SQLException e) {
            e.getMessage();
        }
        return genderCountMap;
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) throws PayrollServiceException{
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException e){
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.RETRIEVAL_PROBLEM);
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException | PayrollServiceException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws PayrollServiceException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        }catch (SQLException e){
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.RETRIEVAL_PROBLEM);
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try{
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM employee_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement(name, salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s' ;", salary, name);
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public int updateEmployeeDataUsingPreparedStatement(String name, double salary){
        try(Connection connection = this.getConnection();){
            String sql = "update employee_payroll set salary = ? where name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1,salary);
            preparedStatement.setString(2,name);
            int status =  preparedStatement.executeUpdate();
            return status;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        int employeeId = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_payroll (name, gender, salary, start) "+
                                   "VALUES ( '%s', '%s', %s, '%s' )", name, gender, salary, Date.valueOf(startDate));
        try(Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }
}
