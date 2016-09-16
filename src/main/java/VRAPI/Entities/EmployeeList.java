package VRAPI.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * POJO for returning list of zuhlke employees
 */
public class EmployeeList {

    protected List<Employee> employees;

    public EmployeeList() {
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String toJSONString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
