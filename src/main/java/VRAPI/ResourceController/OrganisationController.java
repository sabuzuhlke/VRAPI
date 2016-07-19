package VRAPI.ResourceController;

import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import VRAPI.XMLClasses.ContainerDetailedProjects.Project;
import VRAPI.XMLClasses.ContainerSimpleContactOrganisation.Contact;
import VRAPI.JSONClasses.JSONContainerActivities.JSONActivity;
import VRAPI.JSONClasses.JSONContainerProject.JSONPhase;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.Exceptions.*;
import VRAPI.JSONClasses.JSONContainerOrganisation.JSONContact;
import VRAPI.MergeClasses.ProjectsForOrganisation;
import VRAPI.VertecServerInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

@RestController
@Scope("prototype") //This enforces that an organisation controller is created per request
public class OrganisationController {

    private static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    private static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;

    private RestTemplate rest;

    private DocumentBuilder documentBuilder = null;

    private final URI vertecURI;

    private QueryBuilder queryBuilder;

    private Map<Long, String> teamIdMap;
    private Map<Long, List<String>> contactFollowerMap;
    private Map<Long, Long> supervisorIdMap;
    private Map<Long, String> activityTypeMap;

    @Autowired
    private HttpServletRequest request;

    public OrganisationController() {

        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

        this.rest = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);
        converters.add(jaxbMC);
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        rest.setMessageConverters(converters);

        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

//======================================================================================================================
// GET /organisations
//======================================================================================================================

//---------------------------------------------------------------------------------------------------------------------- /{id}/projects
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/projects", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<ProjectsForOrganisation> getProjectsForOrganisation(@PathVariable Long id)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        String xmlQuery = queryBuilder.getProjectsForOrganisation(id);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> projectIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<JSONProject> projects = getDetailedProjects(projectIdsForOrg);
        ProjectsForOrganisation res = new ProjectsForOrganisation(id, organisationName);
        res.setProjects(projects);

        return new ResponseEntity<>(res, HttpStatus.OK);
        //TODO: include type and currency if we need them
    }

    private List<JSONProject> getDetailedProjects(List<Long> projectIdsForOrg) {
        return getProjects(projectIdsForOrg).stream()
                .map(this::asJSONProject)
                .collect(toList());
    }
    //TODO: add support for projectType and currency map
    private JSONProject asJSONProject(Project project) {
        String accountManager = "";
        if (project.getAccountManager() != null && project.getAccountManager().getObjref() != null) {
            accountManager = project.getAccountManager().getObjref().toString();
        }
        String leader = "";
        if (project.getLeader() != null && project.getLeader().getObjref() != null) {
            leader = project.getLeader().getObjref().toString();
        }
        JSONProject proj = new JSONProject(project,
                leader,
                accountManager);
        proj.setPhases(phasesFor(project));
        return proj;
    }

    private List<JSONPhase> phasesFor(Project project) {
        return getPhasesForProject(project.getPhases().getObjlist().getObjrefs()).stream()
                .map(phase -> {
                    String leader = "";
                    if (project.getLeader() != null && project.getLeader().getObjref() != null) {
                        leader = project.getLeader().getObjref().toString();
                    }
                    return new JSONPhase(phase, leader);
                })
                .collect(toList());
    }

    private List<VRAPI.XMLClasses.ContainerPhases.ProjectPhase> getPhasesForProject(List<Long> phaseIds) {
        return callVertec(
                queryBuilder.getProjectPhases(phaseIds),
                VRAPI.XMLClasses.ContainerPhases.Envelope.class).getBody().getQueryResponse().getPhases();
    }

    private List<VRAPI.XMLClasses.ContainerDetailedProjects.Project> getProjects(Collection<Long> projectIds) {
        return callVertec(
                queryBuilder.getProjectDetails(projectIds),
                VRAPI.XMLClasses.ContainerDetailedProjects.Envelope.class).getBody().getQueryResponse().getProjects();
    }

//====================================================================================================================== /{id}/activities
    //TODO: change activities to common representation
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/activities", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<ActivitiesForOrganisation> getActivitiesForOrganisation(@PathVariable Long id)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        String xmlQuery = queryBuilder.getActivitiesForOrganisation(id);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        //Query vertec with organisation id for list of activities associated with it.
        List<Long> activityIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<JSONActivity> activities = getActivityDetails(activityIdsForOrg);
        //Query vertec for details of each activity build response object
        ActivitiesForOrganisation res = new ActivitiesForOrganisation(id, organisationName);
        //set Activity list
        res.setActivitiesForOrganisation(activities);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private List<JSONActivity> getActivityDetails(List<Long> activityIdsForOrg) {

        activityTypeMap = StaticMaps.INSTANCE.getActivityTypeMap();

        return getActivities(activityIdsForOrg).stream()
                .map(activity -> {
                    JSONActivity a = new JSONActivity(activity);
                    Long ref = activity.getAssignee().getObjref();
                    a.setAssignee(ref == null ? null : ref.toString());
                    a.setType(activityTypeMap.get(activity.getType().getObjref()));
                    return a;
                }).collect(toList());

    }

    private List<VRAPI.XMLClasses.ContainerActivity.Activity> getActivities(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getActivities(ids), VRAPI.XMLClasses.ContainerActivity.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getActivities();
        } catch (NullPointerException npe){
            throw new HttpNotFoundException("No activities exist with the following v_ids: " + ids);
        }
    }

//====================================================================================================================== /all
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisations/all", method = RequestMethod.GET)//TODO: write tests for this
    public ResponseEntity<OrganisationList> getAllOrganisations() throws ParserConfigurationException {
        System.out.println("Received request");
        ifUnauthorisedThrowErrorResponse();
        System.out.println("Authenticated request");
        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();
        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        Set<Long> allEmployeeIds = supervisorIdMap.keySet();
        Set<Long> allAddressIds = getAddressIdsSupervisedBy(allEmployeeIds);
        List<List<Long>> contactsIdsAndOrgIds = getSimpleContactsandOrgs(allAddressIds);
        System.out.println(contactsIdsAndOrgIds.get(1).size());
        List<Organisation> organisations = createOrganisationList(getOrganisations(contactsIdsAndOrgIds.get(1)));

        OrganisationList res = new OrganisationList();
        res.setOrganisations(organisations);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    private List<VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getOrganisationDetails(ids), VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class).getBody().getQueryResponse().getOrganisationList().stream()
                    .filter(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation::getActive)
                    .collect(toList());
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("Did not find any of the listed organisations:" + ids );
        }
    }

    private List<List<Long>> getSimpleContactsandOrgs(Collection<Long> addressIds) {
        List<Long> cIds = new ArrayList<>();
        List<Long> oIds = new ArrayList<>();
        List<List<Long>> rIds = new ArrayList<>();
        VRAPI.XMLClasses.ContainerSimpleContactOrganisation.Envelope env
                = callVertec(queryBuilder.getContactAndOrganisationIds(addressIds), VRAPI.XMLClasses.ContainerSimpleContactOrganisation.Envelope.class);

        cIds.addAll(
                env.getBody().getQueryResponse().getContacts().stream()
                        .map(Contact::getObjid)
                        .collect(toList()));
        oIds.addAll(
                env.getBody().getQueryResponse().getOrgs().stream()
                        .map(VRAPI.XMLClasses.ContainerSimpleContactOrganisation.Organisation::getObjid)
                        .collect(toList()));
        rIds.add(cIds);
        rIds.add(oIds);
        return rIds;
    }

    private Set<Long> getAddressIdsSupervisedBy(Set<Long> employeeIds) {
        Set<Long> addressIds = new HashSet<>();
        callVertec(queryBuilder.getSupervisedAddresses(employeeIds), VRAPI.XMLClasses.ContainerAddresses.Envelope.class)
                .getBody().getQueryResponse().getWorkers()
                .forEach(w -> addressIds.addAll(w.getAddresses().getList().getObjects()));
        return addressIds;
    }

//---------------------------------------------------------------------------------------------------------------------- /{ids}
    @ApiOperation(value = "Get organisation by list", nickname = "byList")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org/{ids}", method = RequestMethod.GET)
    public ResponseEntity<OrganisationList> getOrganisationList(@PathVariable List<Long> ids)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(ids),
                             VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class);


        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Some or all of the ids requested could not be found as organisations");
        }

        OrganisationList res = new OrganisationList();
        res.setOrganisations(createOrganisationList(organisationEnvelope.getBody().getQueryResponse().getOrganisationList()));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//---------------------------------------------------------------------------------------------------------------------- /{id}
    @ApiOperation(value = "GET organisation by id", nickname = "byId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.GET)
    public ResponseEntity<Organisation> getOrganisation(@PathVariable Long id)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        List<Long> idAsList = new ArrayList<>();
        idAsList.add(id);
        VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(idAsList),
                             VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class);

        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Organisation with id: " + id + " could not be found");
        }
        //techincally we recieve a list of organisations (length 1) so we take the first item from list
        Organisation res = xml2json(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private List<JSONContact> getContactsForOrganisation(List<Long> objref) {
        List<VRAPI.XMLClasses.ContainerDetailedContact.Contact> xmlContacts =
                callVertec(queryBuilder.getContactDetails(objref), VRAPI.XMLClasses.ContainerDetailedContact.Envelope.class)
                        .getBody()
                        .getQueryResponse()
                        .getContactList();

        this.contactFollowerMap = StaticMaps.INSTANCE.getFollowerMap();

        return xmlContacts.stream()
                .map(xmlContact -> {
                    JSONContact cont = new JSONContact(xmlContact);
                    cont.setOwner(teamIdMap.get(xmlContact.getPersonResponsible().getObjref()));
                    cont.setFollowers(contactFollowerMap.get(xmlContact.getObjId()));
                    return cont;
                })
                .collect(toList());
    }

//======================================================================================================================
// POST /organisations/
//======================================================================================================================

    //---------------------------------------------------------------------------------------------------------------------- POST /{id}
    @ApiOperation(value = "Post organisation to vertec", nickname = "post")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public ResponseEntity<Organisation> postOrganisation(@RequestBody Organisation orgToPost) {

        System.out.println(orgToPost.toJsonString());

        //TODO: Finish this

        return new ResponseEntity<>(new Organisation(), HttpStatus.OK);
    }

//======================================================================================================================
// Helper Methods
//======================================================================================================================

    public List<Long> getObjrefsForOrganisationDocument(Document response) {
        NodeList activityObjrefs =  response.getElementsByTagName("objref");
        return asIdList(activityObjrefs);
    }

    public String getNameForOrganisationDocument(Document response) {
        return response.getElementsByTagName("name").item(0).getTextContent();
    }

    private static List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    private static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    private Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException| IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    private List<Organisation> createOrganisationList(List<VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation> organisations) {
        return organisations.stream()
                .map(this::xml2json).collect(toList());
    }

    private Organisation xml2json(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation vo) {
        Organisation o = new Organisation(vo);
        setOwnedOnVertecBy(o, vo);
        return o;
    }

    private void setOwnedOnVertecBy(Organisation o, VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation vo) {
        Long supervisorId = supervisorIdMap.get(vo.getPersonResponsible().getObjref());
        Long SALES_TEAM_IDENTIFIER = -5L; //members of the top sales team, including wolfgang have their 'supervisorId' set to -5 within the map;
        if (supervisorId == 0L) {
            o.setOwnedOnVertecBy("Not ZUK");
        } else if (supervisorId.longValue() == SALES_TEAM_IDENTIFIER) {
            o.setOwnedOnVertecBy("Sales Team");
        } else {
            o.setOwnedOnVertecBy("ZUK Sub Team");
        }

    }

   /* private List<Long> getZUKTeamMemberIds() {
        String xmlQuery = queryBuilder.getLeadersTeam();
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        return elementIn(response, "QueryResponse")
                .map(queryResponse -> queryResponse.getElementsByTagName("objref"))
                .map(OrganisationController::asIdList)
                .orElse(new ArrayList<>());
    }*/ //-- Currently unused within class

    /**
     * Call this function at the start of every request handler
     * This will make a request for 'ZUK TEAM' from vertec and either setUp the query builder with provided username and pwd
     * or will throw appropriate error
     * @throws ParserConfigurationException
     */
    private void ifUnauthorisedThrowErrorResponse() throws ParserConfigurationException {
        Authenticator authenticator = new Authenticator();
        String usernamePassword = request.getHeader("Authorization");
        Integer authLevel = authenticator.requestIsAuthorized(usernamePassword);
        if (authLevel.longValue() == VertecServerInfo.BAD_REQUEST) {
            throw new HttpBadRequest("Username and password not correctly set in header");
        } else if (authLevel.longValue() == VertecServerInfo.UNAUTHORISED) {
            throw new HttpUnauthorisedException("Wrong username or password");
        } else if (authLevel.longValue() == VertecServerInfo.FORBIDDEN) {
            throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
        }
        String[] usrpwd = usernamePassword.split(":");
        queryBuilder = new QueryBuilder(usrpwd[0], usrpwd[1]);
    }

    private <T> T callVertec(String query, Class<T> responseType) {
        System.out.println("Calling vertec, querying for: " + responseType.getName());
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }


}
