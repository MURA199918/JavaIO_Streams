import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double salary;
    public double basic_pay;
    public LocalDate startDate;

    public EmployeePayrollData(Integer id, String name, Double salary){
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeePayrollData(int id, String name, double basic_pay, LocalDate startDate) {
        this(id, name, basic_pay);
        this.startDate = startDate;
    }

    public String toString(){
        return "id=" + id + ", name='" + name + ", salary=" + salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                Double.compare(that.basic_pay, basic_pay) == 0 &&
                name.equals(that.name);
    }

}
