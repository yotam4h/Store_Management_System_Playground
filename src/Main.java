import java.sql.SQLException;
import java.util.List;

import com.storeManagement.dataAccessObject.EmployeeDao;
import com.storeManagement.model.Employee;
import com.storeManagement.utils.Constants;

public class Main
{
    public static void main(String[] args)
    {
        Employee emp = new Employee("John Doe", "1234567890", Constants.EmployeeRole.MANAGER, 11);
        EmployeeDao empDao = new EmployeeDao();

        try
        {
            // add
            System.out.println("Adding employee...");
            empDao.add(emp);
            // read
            System.out.println("Reading employee...");
            Employee temp = empDao.get(emp.getId());
            System.out.println(temp);
            // read all
            System.out.println("Reading all employees...");
            List<Employee> employees = empDao.getList();
            for (Employee e : employees)
            {
                System.out.println(e);
            }
            // update
            System.out.println("Updating employee...");
            temp.setRole(Constants.EmployeeRole.EMPLOYEE);
            empDao.update(temp);
            // delete
            System.out.println("Deleting employee...");
            empDao.delete(temp.getId());

        }
        catch (SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}