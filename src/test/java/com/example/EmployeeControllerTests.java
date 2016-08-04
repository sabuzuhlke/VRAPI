package com.example;

import VRAPI.Entities.Employee;
import VRAPI.Entities.EmployeeList;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class EmployeeControllerTests extends ControllerTests {

    @Test
    public void getSalesTeamReturnsAllMembersOfTeam() {
        String uri = baseURI + "/employees/pipedrive";
        ResponseEntity<EmployeeList> employees = getFromVertec(uri, EmployeeList.class);

        assertEquals(HttpStatus.OK, employees.getStatusCode());

        assertNotNull(employees.getBody());
        assertNotNull(employees.getBody().getEmployees());
        assertFalse(employees.getBody().getEmployees().isEmpty());

        employees.getBody().getEmployees().forEach(
                employee -> {
                    assertNotNull(employee.getId());
                    assertNotNull(employee.getName());
                    assertNotNull(employee.getEmail());
                    assertNotNull(employee.getActive());
                }
        );

        List<Long> employeeIds = employees.getBody().getEmployees().stream().map(Employee::getId).collect(toList());

        assertTrue(employeeIds.contains(5295L));
        assertTrue(employeeIds.contains(5726L));
        assertTrue(employeeIds.contains(24907657L));

        System.out.println(employees.getBody().toJSONString());
    }

}
