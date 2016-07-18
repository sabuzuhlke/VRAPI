package VRAPI.ResourceController;

import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.ContainerSimpleContactOrganisation.Contact;
import VRAPI.JSONContainerActivities.JSONActivity;
import VRAPI.JSONContainerProject.JSONPhase;
import VRAPI.JSONContainerProject.JSONProject;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.Exceptions.*;
import VRAPI.JSONContainerOrganisation.JSONContact;
import VRAPI.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONContainerOrganisation.JSONOrganisationList;
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
import org.w3c.dom.Element;
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
import static org.w3c.dom.Node.ELEMENT_NODE;

@RestController
@Scope("prototype")
public class OrganisationController {

    private static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    private static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;

    private final Long SALES_TEAM_IDENTIFIER = -5L;

    private RestTemplate rest;


    private DocumentBuilder documentBuilder = null;

    private final URI vertecURI;

    private final Integer BAD_REQUEST = 3;
    private final Integer UNAUTHORISED = 2;
    private final Integer FORBIDDEN = 1;
    private final Integer AUTHORISED = 0;

    public QueryBuilder queryBuilder;

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

//====================================================================================================================== GET /organisation/{id}/projects

    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/projects", method = RequestMethod.GET)
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
                .map(this::asJsonProject)
                .collect(toList());
    }

    private JSONProject asJsonProject(Project project) {
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

    private List<VRAPI.ContainerPhases.ProjectPhase> getPhasesForProject(List<Long> phaseIds) {
        return callVertec(
                queryBuilder.getProjectPhases(phaseIds),
                VRAPI.ContainerPhases.Envelope.class).getBody().getQueryResponse().getPhases();
    }

    private List<VRAPI.ContainerDetailedProjects.Project> getProjects(Collection<Long> projectIds) {
        return callVertec(
                queryBuilder.getProjectDetails(projectIds),
                VRAPI.ContainerDetailedProjects.Envelope.class).getBody().getQueryResponse().getProjects();
    }


//====================================================================================================================== GET /organisation/{id}/activities

    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/activities", method = RequestMethod.GET)
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

    private List<VRAPI.ContainerActivity.Activity> getActivities(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getActivities(ids), VRAPI.ContainerActivity.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getActivities();
        } catch (NullPointerException npe){
            throw new HttpNotFoundException("No activities exist with the following v_ids: " + ids);
        }
    }

    public List<Long> getObjrefsForOrganisationDocument(Document response) {
        NodeList activityObjrefs =  response.getElementsByTagName("objref");
        if (activityObjrefs.getLength() > 0) {
            return IntStream.range(0, activityObjrefs.getLength())
                    .mapToObj(index -> (Element) activityObjrefs.item(index))
                    .map(objrefElement -> Long.parseLong(objrefElement.getTextContent()))
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    public String getNameForOrganisationDocument(Document response) {
        return response.getElementsByTagName("name").item(0).getTextContent();
    }

//====================================================================================================================== GET /organisations/all

    //    //GET ORganisation in common represenation
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisations/all", method = RequestMethod.GET)
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

    private List<Organisation> createOrganisationList(List<VRAPI.ContainerDetailedOrganisation.Organisation> organisations) {
        return organisations.stream()
                .map(vo -> {
                    Organisation o = new Organisation(vo);
                    setOwnerAndOwnedOnVertecBy(o, vo);
                    return o;
                }).collect(toList());
    }

    private void setOwnerAndOwnedOnVertecBy(Organisation o, VRAPI.ContainerDetailedOrganisation.Organisation vo) {
        Long supervisorId = supervisorIdMap.get(vo.getPersonResponsible().getObjref());
        if (supervisorId == 0L) {
            o.setOwnedOnVertecBy("Not ZUK");
            o.setSupervisingEmail("blahdeblahdeblah");//TODO:change this
        } else if (supervisorId.longValue() == SALES_TEAM_IDENTIFIER ) {
            o.setOwnedOnVertecBy("Sales Team");
            o.setSupervisingEmail(teamIdMap.get(vo.getPersonResponsible().getObjref()));
        } else {
            o.setOwnedOnVertecBy("ZUK Sub Team");
            o.setSupervisingEmail(getSupervisingEmailForSubTeamMember(vo));
        }

    }

    private String getSupervisingEmailForSubTeamMember(VRAPI.ContainerDetailedOrganisation.Organisation vo) {
        //assumes vo.supervisor id is sub team member;
        Long supervisorId = 0L;
        Long idForMapGet = vo.getPersonResponsible().getObjref();
        while (supervisorId.longValue() != SALES_TEAM_IDENTIFIER) {
            supervisorId = supervisorIdMap.get(idForMapGet);
            if (supervisorId.longValue() == SALES_TEAM_IDENTIFIER) {
                return teamIdMap.get(idForMapGet);
            }
            idForMapGet = supervisorId;
        }
        return "OH DAMN SOMETHING WENT WRONG";
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getOrganisationDetails(ids), VRAPI.ContainerDetailedOrganisation.Envelope.class).getBody().getQueryResponse().getOrganisationList().stream()
                    .filter(VRAPI.ContainerDetailedOrganisation.Organisation::getActive)
                    .collect(toList());
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("Did not find any of the listed organisations:" + ids );
        }
    }

    public List<List<Long>> getSimpleContactsandOrgs(Collection<Long> contactIds) {
        List<Long> cIds = new ArrayList<>();
        List<Long> oIds = new ArrayList<>();
        List<List<Long>> rIds = new ArrayList<>();
        VRAPI.ContainerSimpleContactOrganisation.Envelope env
                = callVertec(queryBuilder.getContactAndOrganisationIds(contactIds), VRAPI.ContainerSimpleContactOrganisation.Envelope.class);

        cIds.addAll(
                env.getBody().getQueryResponse().getContacts().stream()
                        .map(Contact::getObjid)
                        .collect(toList()));
        oIds.addAll(
                env.getBody().getQueryResponse().getOrgs().stream()
                        .map(VRAPI.ContainerSimpleContactOrganisation.Organisation::getObjid)
                        .collect(toList()));
        rIds.add(cIds);
        rIds.add(oIds);
        return rIds;
    }

    public Set<Long> getAddressIdsSupervisedBy(Set<Long> employeeIds) {
        Set<Long> addressIds = new HashSet<>();
        callVertec(queryBuilder.getSupervisedAddresses(employeeIds), VRAPI.ContainerAddresses.Envelope.class)
                .getBody().getQueryResponse().getWorkers()
                .forEach(w -> addressIds.addAll(w.getAddresses().getList().getObjects()));
        return addressIds;
    }

    private Boolean toBoolean(String s) {
        return s.equals("1");
    }

    private List<Long> getZUKTeamMemberIds() {
        String xmlQuery = queryBuilder.getLeadersTeam();
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        return elementIn(response, "QueryResponse")
                .map(queryResponse -> queryResponse.getElementsByTagName("objref"))
                .map(OrganisationController::asIdList)
                .orElse(new ArrayList<>());
    }

    private static Optional<Element> elementIn(Document document, String tagname) {
        final NodeList queryResponses = document.getElementsByTagName(tagname);
        return queryResponses.getLength() == 1 && queryResponses.item(0).getNodeType() == ELEMENT_NODE
                ? Optional.of((Element) queryResponses.item(0))
                : Optional.empty();
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
    public ResponseEntity<JSONOrganisation> postOrganisation(@RequestBody JSONOrganisation orgToPost) {

        System.out.println(orgToPost.toPrettyJSON());

        //TODO: Finish this

        return new ResponseEntity<>(new JSONOrganisation(), HttpStatus.OK);
    }


//---------------------------------------------------------------------------------------------------------------------- GET /{ids}


    @ApiOperation(value = "Get organisation by list", nickname = "byList")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org/{ids}", method = RequestMethod.GET)
    public ResponseEntity<JSONOrganisationList> getOrganisationList(@PathVariable List<Long> ids)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        VRAPI.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(ids),
                             VRAPI.ContainerDetailedOrganisation.Envelope.class);


        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Some or all of the ids requested could not be found as organisations");
        }

        JSONOrganisationList jsonOrgs = new JSONOrganisationList();
        jsonOrgs.setOrganisations(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().stream()
                .map(vertecOrg -> {
                    JSONOrganisation jsonOrganisation = xml2JSON(vertecOrg);
                    jsonOrganisation.setOwner(teamIdMap.get(vertecOrg.getPersonResponsible().getObjref()));
                    return jsonOrganisation;
                })
                .collect(toList()));


        return new ResponseEntity<>(jsonOrgs, HttpStatus.OK);
    }

//---------------------------------------------------------------------------------------------------------------------- GET /{id}

    @ApiOperation(value = "GET organisation by id", nickname = "byId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONOrganisation> getOrganisation(@PathVariable Long id)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        List<Long> idAsList = new ArrayList<>();
        idAsList.add(id);
        VRAPI.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(idAsList),
                             VRAPI.ContainerDetailedOrganisation.Envelope.class);

        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Organisation with id: " + id + " could not be found");
        }

        JSONOrganisation jsonOrganisation = xml2JSON(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0));
        Long orgId = organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0).getPersonResponsible().getObjref();
        jsonOrganisation.setOwner(teamIdMap.get(orgId));

        return new ResponseEntity<>(jsonOrganisation, HttpStatus.OK);

    }

    private JSONOrganisation xml2JSON(VRAPI.ContainerDetailedOrganisation.Organisation vertecOrg) {
        JSONOrganisation org = new JSONOrganisation(vertecOrg);
        org.setContacts(getContactsForOrganisation(vertecOrg.getContacts().getObjlist().getObjref()));
        return org;
    }

    private List<JSONContact> getContactsForOrganisation(List<Long> objref) {
        List<VRAPI.ContainerDetailedContact.Contact> xmlContacts =
                callVertec(queryBuilder.getContactDetails(objref), VRAPI.ContainerDetailedContact.Envelope.class)
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

//---------------------------------------------------------------------------------------------------------------------- UTIL

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
        if (authLevel.longValue() == BAD_REQUEST) {
            throw new HttpBadRequest("Username and password not correctly set in header");
        } else if (authLevel.longValue() == UNAUTHORISED) {
            throw new HttpUnauthorisedException("Wrong username or password");
        } else if (authLevel.longValue() == FORBIDDEN) {
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
