package com.example;

import VRAPI.Application;
import VRAPI.Entities.Employee;
import VRAPI.Entities.EmployeeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
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
