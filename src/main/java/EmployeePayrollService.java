import java.util.Scanner;

public class EmployeePayrollService {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Employee ID: ");
        int id = sc.nextInt();
        System.out.println("Enter Employee Name: ");
        String name = sc.next();
        System.out.println("Enter Employee Salary: ");
        double salary = sc.nextDouble();
        System.out.println("Employee Details: "+id+" "+name+" "+salary);

    }
}
