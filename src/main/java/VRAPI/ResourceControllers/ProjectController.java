package VRAPI.ResourceControllers;

import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.MergeClasses.ProjectsForAddressEntry;
import VRAPI.Util.QueryBuilder;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope;
import VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation;
import VRAPI.XMLClasses.ContainerDetailedProjects.Project;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

import static java.util.Collections.*;

@RestController
@Scope("prototype")
public class ProjectController extends Controller {
    public ProjectController() {
        super();
    }

    public ProjectController(QueryBuilder queryBuilder) {
        super(queryBuilder);
    }

    //======================================================================================================================//
    // PUT /project   endpoint to set the organisation link of a project to a differnece organsation                                                                                                      //
//======================================================================================================================//
    @ApiOperation(value = "Re-Link Project (id) to an organisation (orgID)", nickname = "setOrgLink")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/project/{id}/setOrganisationLink/{orgID}", method = RequestMethod.PUT)
    public ResponseEntity<Long> setOrgLinkEndpoint(@PathVariable Long id, @PathVariable Long orgID) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return setOrgLink(id, orgID);
    }

//=======================================METHODS========================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================

    ResponseEntity<Long> setOrgLink(Long id, Long orgID) {

        //checks to make sure the ids provided represent that correct entity types
        VertecServerInfo.log.info("--------------- Setting Organisation Link of Project ---------------------------->");
        if (!isIdOfType(id, "Projekt")) {
            VertecServerInfo.log.info("--------------- Project with id: " + id + " does not exist ------------------>");
            throw new HttpNotFoundException("Project with id: " + id + " does not exist");
        }
        if (!isIdOfType(orgID, "Firma")) {
            VertecServerInfo.log.info("---------- Organisation with id: " + orgID + " does not exist ----------------->");
            throw new HttpNotFoundException("Organisation with id: " + orgID + " does not exist");
        }

        //Get project first
        Project project = callVertec(queryBuilder.getProjectDetails(singletonList(id))
                , VRAPI.XMLClasses.ContainerDetailedProjects.Envelope.class)
                .getBody().getQueryResponse().getProjects().get(0); //TODO see whether refactor is possible

        Organisation organisation = callVertec(queryBuilder.getOrganisationDetails(singletonList(orgID))
                , Envelope.class)
                .getBody().getQueryResponse().getOrganisationList().get(0);

        VertecServerInfo.log.info("Request seems OK, about to re-point project " + project.getCode() + " " + project.getTitle() + "(v_id: " + project.getId() + ")" +
                " to Organisation " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")");

        if (project.getClient() != null) {
            Long clientref = project.getClient().getObjref();
            if (isIdOfType(clientref, "Firma")) {
                Organisation org = callVertec(queryBuilder.getOrganisationDetails(singletonList(clientref))
                        , Envelope.class)
                        .getBody().getQueryResponse().getOrganisationList().get(0);


                VertecServerInfo.log.info("Clientref pointed to Organisation " + org.getName() +
                        "(v_id: " + org.getObjId() + ") this would be overwritten" +
                        " to point to Organisation: " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")!!");
            }
        }

        if (project.getCustomer() != null) {
            Long custommerRef = project.getCustomer().getObjref();
            if (isIdOfType(custommerRef, "Firma")) {
                Organisation org = callVertec(queryBuilder.getOrganisationDetails(singletonList(custommerRef))
                        , Envelope.class)
                        .getBody().getQueryResponse().getOrganisationList().get(0);

                VertecServerInfo.log.info("custommerRef points to Organisation " + org.getName() +
                        "(v_id: " + org.getObjId() + ") this would be kept as is!");
            }
        }


        String putQuery = queryBuilder.setProjectOrgLink(id, orgID);

        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {

            VertecServerInfo.log.info("Project now linked to Organisation: " + orgID);
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            return new ResponseEntity<>(orgID, HttpStatus.OK);

        } else {
            VertecServerInfo.log.info("Could not re-link project, Unknown response from Vertec");
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
    }

}
