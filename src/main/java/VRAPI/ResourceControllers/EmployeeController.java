package VRAPI.ResourceControllers;

import VRAPI.Entities.Employee;
import VRAPI.Entities.EmployeeList;
import VRAPI.Util.QueryBuilder;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerEmployees.Envelope;
import VRAPI.XMLClasses.ContainerEmployees.ProjectWorker;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@Scope("prototype")
public class EmployeeController extends Controller {

    public EmployeeController() {
        super();
    }

    public EmployeeController(QueryBuilder queryBuilder){
        super(queryBuilder);
    }

//======================================================================================================================
// GET /employees/pipedrive
//======================================================================================================================

    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/employees/pipedrive", method = RequestMethod.GET)
    public ResponseEntity<EmployeeList> getSalesTeamDetailsEndpoint() throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();
        VertecServerInfo.log.info("Recieved Request to Pipedrive Team from " + request.getHeader("Authorization").split(":")[0]);

        return getSalesTeamDetails();
    }

    //returns response entity containing list of employees directly supervised by wolfgang, including wolfgang
    public ResponseEntity<EmployeeList> getSalesTeamDetails() {
        String xmlQuery = queryBuilder.getLeadersTeam();
        final Document teamIdsResponse = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> teamIds = getObjrefsForOrganisationDocument(teamIdsResponse);

        EmployeeList employees = getEmployees(teamIds);

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    //returns list of employees directly supervised by wolfgang, including wolfgang
    private EmployeeList getEmployees(List<Long> teamIds) {
        EmployeeList employees = new EmployeeList();
        employees.setEmployees(employeesFromIds(teamIds));
        return employees;
    }

    //returns list of employees directly supervised by wolfgang, including wolfgang
    private List<Employee> employeesFromIds(List<Long> teamIds) {
        return getEmployessFromVertec(teamIds).stream()
                .map(Employee::new)
                .collect(toList());
    }

    //gets list of project workers from vertec by ids
    private List<ProjectWorker> getEmployessFromVertec(List<Long> teamIds) {
        List<ProjectWorker> employees = callVertec(queryBuilder.getTeamDetails(teamIds), Envelope.class).getBody().getQueryResponse().getWorkers();
        for(ProjectWorker pw : employees){
            pw.setEmail(pw.getEmail().toLowerCase());
        }
        return employees;
    }

}
