package VRAPI.ResourceController;
import VRAPI.ContainerActivity.Activity;
import VRAPI.ContainerActivity.Type;
import VRAPI.ContainerActivityType.ActivityType;
import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.ContainerPhases.ProjectPhase;
import VRAPI.ContainerProjectType.ProjectType;
import VRAPI.ContainerProjects.ProjectWorker;
import VRAPI.ContainerSimpleContactOrganisation.Contact;
import VRAPI.ContainerSimpleContactOrganisation.Organisation;
import VRAPI.Exceptions.*;
import VRAPI.JSONContainerActivities.JSONActivitiesResponse;
import VRAPI.JSONContainerActivities.JSONActivity;
import VRAPI.JSONContainerOrganisation.JSONContact;
import VRAPI.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONContainerOrganisation.ZUKOrganisationResponse;
import VRAPI.JSONContainerProject.JSONPhase;
import VRAPI.JSONContainerProject.JSONProject;
import VRAPI.JSONContainerProject.ZUKProjectsResponse;
import VRAPI.JSONTeam.TeamMember;
import VRAPI.JSONTeam.ZUKTeam;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.w3c.dom.Node.ELEMENT_NODE;

@SuppressWarnings("WeakerAccess")
@RestController
@Scope("prototype")
public class ResourceController {
    public static final String DEFAULT_VERTEC_SERVER_HOST = "172.18.112.31";
    public static final String DEFAULT_VERTEC_SERVER_PORT = "8095";

    private final URI vertecURI;

    private final DocumentBuilder documentBuilder;

    private String username;
    private String password;

    private RestTemplate rest;

    public ContactComparator contactComparator;
    public ActivityComparator activityComparator;

    private Map<Long, String> teamMap;
    private Map<Long, List<String>> followerMap;

    public QueryBuilder queryBuilder;


    public ResourceController() throws ParserConfigurationException {
        //set resttemplate message converters
        this.rest = new RestTemplate();
        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);
        converters.add(jaxbMC);
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        rest.setMessageConverters(converters);

        //TODO: assess need for comparators
        this.contactComparator = new ContactComparator();
        this.activityComparator = new ActivityComparator();

        this.teamMap = new HashMap<>();
        this.followerMap = new HashMap<>();

        this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    }

    //TODO: only used in tests, maybe change
    public void setTeamMap(Map<Long, String> teamMap) {
        this.teamMap = teamMap;
    }

    //------------------------------------------------------------------------------------------------------------Paths
    @Autowired
    private HttpServletRequest request;


    @ApiOperation(value = "Get Team details", nickname = "team")
    @RequestMapping(value = "/ZUKTeam", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ZUKTeam> createTeamResponse()  {

        checkUserAndPW();

        List<Long> ids = getZUKTeamMemberIds();
//        String xmlQuery = getXMLQuery_TeamIdsAndEmails(ids);
        String xmlQuery = StaticMaps.INSTANCE.getTeamQuery(ids);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        NodeList teamMembers = response.getElementsByTagName("Projektbearbeiter");
        ZUKTeam team = new ZUKTeam();
        IntStream.range(0, teamMembers.getLength())
                .mapToObj(index -> (Element) teamMembers.item(index))
                .forEach(teamMemberElement -> {
                    final NodeList active = teamMemberElement.getElementsByTagName("aktiv");
                    if (toBoolean(active.item(0).getTextContent())) {
                        final NodeList briefEmail = teamMemberElement.getElementsByTagName("briefEmail");
                        String email = briefEmail.item(0).getTextContent();
                        if(briefEmail.getLength(
                        ) != 1) {
                            throw new RuntimeException("XML Document parse for briefEmail went wrong"); //for debugging
                        }
                        final NodeList objid = teamMemberElement.getElementsByTagName("objid");
                        Long id = Long.parseLong(objid.item(0).getTextContent());
                        if (! email.isEmpty()) {
                            team.members.add(new TeamMember(email.toLowerCase(), id));
                        }
                    }
                });

        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    private Boolean toBoolean(String s) {
        return s.equals("1");
    }

    @ApiOperation(value = "Test access", nickname = "notping")
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "text/plain")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    public String ping() {

        checkUserAndPW();
        getZUKTeamMemberIds();

        return "Success!";
    }

    @ApiOperation(value = "Get organisations and nested contacts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/organisations/ZUK", method = RequestMethod.GET, produces = "application/json")
    public ZUKOrganisationResponse getZUKOrganisations()  {
        checkUserAndPW();

        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();
        this.followerMap = StaticMaps.INSTANCE.getFollowerMap();


        List<List<Long>> contactIdsAndOrgsIds = getSimpleContactsandOrgs(getAddressIdsSupervisedBy(getZUKTeamMemberIds()));

        System.out.println(contactIdsAndOrgsIds.get(1).size());
        ZUKOrganisationResponse res = buildZUKOrganisationsResponse(
                getActiveDetailedContacts(contactIdsAndOrgsIds.get(0)),
                getOrganisations(contactIdsAndOrgsIds.get(1)));

        try {
            FileWriter file = new FileWriter("orgOutput.txt");
            file.write(res.toPrettyString());
            file.close();
        } catch (Exception e) {
            System.out.println("Failed to output to file");
        }
       //System.out.println(res.toPrettyString());
        return res;
    }

    //TODO Write get OrgnisationList
    //TODO write getContactList

    @ApiOperation(value = "Get an organisation by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/organisations/{id}", method = RequestMethod.GET, produces = "application/json")
    public JSONOrganisation getOrganisationById(@PathVariable Long id)  {
        List<Long> ids = new ArrayList<>();
        ids.add(id);

        checkUserAndPW();

        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();
        this.followerMap = StaticMaps.INSTANCE.getFollowerMap();


        VRAPI.ContainerDetailedOrganisation.Organisation org = getOrganisationsWithInactive(ids).get(0);


        JSONOrganisation jOrg = new JSONOrganisation(org);

        jOrg.setOwner(getUserEmail(org.getPersonResponsible().getObjref()));

        jOrg.setContacts(getContactsAsJSONContact(org));

       // System.out.println(jOrg.toPrettyJSON());

        return jOrg;
    }

    @ApiOperation(value = "Get an contact by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/contacts/{id}", method = RequestMethod.GET, produces = "application/json")
    public JSONContact getContactbyId(@PathVariable Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        checkUserAndPW();
        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();
        this.followerMap = StaticMaps.INSTANCE.getFollowerMap();

        VRAPI.ContainerDetailedContact.Contact cont = getDetailedContacts(ids).get(0);

        JSONContact jc = new JSONContact(cont);
        jc.setOwner(getUserEmail(cont.getPersonResponsible().getObjref()));
        //Followers
        if(followerMap.get(jc.getObjid()) != null){ //None of the followers might be in our follower map, in that case we don't care

        jc.setFollowers(followerMap.get(jc.getObjid()));

        } else {
            jc.setFollowers(new ArrayList<>());
        }
        System.out.println("Getting contact " + jc.getFirstName() + " " + jc.getSurname() + " Active on vertec: " + cont.getActive() + ", and as a JSON " + jc.getActive());

        //System.out.println(jc.toPrettyJSON());

        return jc;
    }

    @ApiOperation(value = "Get an contact/org/etc... by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/addressEntry/{id}", method = RequestMethod.GET, produces = "application/json")
    public String getAddressEntryById(@PathVariable Long id){

        String res;
        try{
            res = getOrganisationById(id).toPrettyJSON();
            //System.out.println("Single Org: " + res);
            return res;
        } catch (HttpNotFoundException nfe){
            res = getContactbyId(id).toPrettyJSON();
            //System.out.println("Single cont: " + res);
            return res;
        }
    }

    @ApiOperation(value = "Get projects and nested phases")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/projects/ZUK", method = RequestMethod.GET, produces = "application/json")
    public ZUKProjectsResponse getZUKProjects() {
            checkUserAndPW();
        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();
            final ZUKProjectsResponse response = new ZUKProjectsResponse();
            response.setProjects(projectsForTeam(getZUKTeamMemberIds()));

        try {
            FileWriter file = new FileWriter("projOutput.txt");
            file.write(response.toPrettyJSON());
            file.close();
        } catch (Exception e) {
            System.out.println("Failed to output to file");
        }
        //System.out.println(response.toPrettyJSON());
            return response;
    }

    @ApiOperation(value = "Get project and nested phases, by specifying a project code or vertec id in the url")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/projects/{code}", method = RequestMethod.GET, produces = "application/json")
    public JSONProject getSingleProject(@PathVariable String code){
//                                                                  This endpoint accepts both, queries by project v_id
//                                                                  And by project code
        checkUserAndPW();
        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();
        try{
            Long id = Long.parseLong(code);

            return getProjectById(id);

        } catch (NumberFormatException e){
            return getProjectByCode(code);
        }
    }

    private JSONProject getProjectById(Long id) {
        Set<Long> ids = new HashSet<>();
        ids.add(id);
        VRAPI.ContainerDetailedProjects.Project project = null;
        try{
                 project = callVertec(this.queryBuilder.getProjectDetails(ids),
                    VRAPI.ContainerDetailedProjects.Envelope.class).getBody().getQueryResponse().getProjects().get(0);


        } catch (NullPointerException e){
            throw new HttpNotFoundException("Project with id " + id + "does not exist");
        }

        return asJsonProject((fromProject(project)));
    }

    public JSONProject getProjectByCode(String code) {
        VRAPI.ContainerDetailedProjects.Project project = null;

        try{
            project = callVertec(this.queryBuilder.getProjectByCode(code),
                    VRAPI.ContainerDetailedProjects.Envelope.class)
                    .getBody().getQueryResponse().getProjects().get(0);

        } catch (NullPointerException e){
            throw new HttpNotFoundException("Project with code " + code + " does not exist");
        }


        return asJsonProject(fromProject(project));

    }

    @ApiOperation(value = "Get Activities")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/activities/ZUK", method = RequestMethod.GET, produces = "application/json")
    public JSONActivitiesResponse getZUKActivities() {
        checkUserAndPW();
        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();

        final List<Activity> activities = getActivities(getActivityIds(getZUKTeamMemberIds()));

        JSONActivitiesResponse res = buildJSONActivitiesResponse(activities, getActivityTypes(activities));
        System.out.println(res.toPrettyJSON());
        try {
            FileWriter file = new FileWriter("actOutput.txt");
            file.write(res.toPrettyJSON());
            file.close();
        } catch (Exception e) {
            System.out.println("Failed to output to file");
        }

        return res;

    }

    @ApiOperation(value = "Get Activity by ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/activities/{v_id}", method = RequestMethod.GET, produces = "application/json")
    public JSONActivitiesResponse getActivityById(@PathVariable Long v_id){
        checkUserAndPW();
        this.teamMap = StaticMaps.INSTANCE.getTeamIDMap();

        final List<Activity> activities = getActivities(singletonList(v_id));

        JSONActivitiesResponse res = buildJSONActivitiesResponse(activities, getActivityTypes(activities));
        //System.out.println(res.toPrettyJSON());

        return res;
    }


    private List<JSONProject> projectsForTeam(List<Long> teamMemberIDs) {
        List<ProjectWithType> projectsBeforePhasesAssigned = getDetailedProjects(getProjectsTeamAreWorkingOn(teamMemberIDs)).stream()
                .map(this::fromProject)
                .filter(ProjectWithType::isAfterCutOffDate)
                .filter(ProjectWithType::isInUK)
                .filter(ProjectWithType::isExternal)
                .collect(toList());
//
//        List<ProjectPhase> phaseList = getPhasesList(projectsBeforePhasesAssigned).stream()
//                .filter(phase -> !phase.getCode().contains("00_INTERN"))
//                .filter(phase -> !phase.getCode().contains("00_BID"))
//                .collect(toList());
        return projectsBeforePhasesAssigned.stream()
//                .map(proj -> asJsonProject(proj, phaseList))
                .map(this::asJsonProject)
                .collect(toList());
    }

    private List<ProjectPhase> getPhasesList(List<ProjectWithType> projectsBeforePhasesAssigned) {
        List<Long> allPhaseIds = projectsBeforePhasesAssigned.stream()
                .map(p -> p.project)
                .map(p -> p.getPhases().getObjlist().getObjrefs())
                .flatMap(Collection::stream)
                .collect(toList());
        return callVertec(queryBuilder.getProjectPhases(allPhaseIds), VRAPI.ContainerPhases.Envelope.class).getBody().getQueryResponse().getPhases();
    }

    private class ProjectWithType {
        private final Project project;
        private final ProjectType projectType;

        public ProjectWithType(VRAPI.ContainerDetailedProjects.Project project, ProjectType projectType) {
            this.project = project;
            this.projectType = projectType;
        }

        private boolean isInUK() {
            final String descripton = projectType.getDescripton();
            return descripton.contains("SGB_") || descripton.contains("EMS") || descripton.contains("DSI") || descripton.contains("CAP");
        }

        private boolean isExternal(){
            final String code = project.getCode();
            return code.contains("C");
        }

        private Long currencyId() {
            return project.getCurrency().getObjref();
        }

        public boolean isAfterCutOffDate() {
            DateTimeFormatter v = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime vt = LocalDateTime.from(v.parse(project.getCreationDate()));
            LocalDateTime cutoff = LocalDateTime.from(v.parse("2008-01-01T00:00:00"));
            return vt.isAfter(cutoff);
        }
    }

//    private JSONProject asJsonProject(ProjectWithType pwt, List<ProjectPhase> phases) {
//        String leaderEmail = teamMap.get(pwt.project.getLeader().getObjref());
//
//        String aManagerEmail = null;
//        if(pwt.project.getAccountManager() != null){
//            System.out.println("Account Manager added: " + pwt.project.getAccountManager().getObjref());
//            aManagerEmail = teamMap.get(pwt.project.getAccountManager().getObjref());
//        }
//        JSONProject proj = new JSONProject(pwt.project, leaderEmail, aManagerEmail);
//        proj.setPhases(phasesFor(pwt.project, phases));
//        proj.setType(pwt.projectType.getDescripton());
//        proj.setCurrency(getCurrency(pwt.currencyId()).getName());
//        return proj;
//    }

    private JSONProject asJsonProject(ProjectWithType pwt) {
        String aManagerEmail = null;

        String leaderEmail = teamMap.get(pwt.project.getLeader().getObjref());

        if(pwt.project.getAccountManager() != null){
            System.out.println("Account Manager added: " + pwt.project.getAccountManager().getObjref());
            aManagerEmail = teamMap.get(pwt.project.getAccountManager().getObjref());
        }

        JSONProject proj = new JSONProject(pwt.project, leaderEmail, aManagerEmail);
        proj.setPhases(phasesFor(pwt.project));
        proj.setType(pwt.projectType.getDescripton());
        proj.setCurrency(getCurrency(pwt.currencyId()).getName());
        return proj;
    }

    public ProjectWithType fromProject(VRAPI.ContainerDetailedProjects.Project project) {
        return new ProjectWithType(project, getProjectType(project.getType().getObjref()));
    }

//    private List<JSONPhase> phasesFor(Project project, List<ProjectPhase> phases) {
//        return phases.stream()
//                .filter(phase -> project.getPhases().getObjlist().getObjrefs().contains(phase.getObjid()))
//                .map(phase -> {
//                    String responsibleEmail = teamMap.get(project.getLeader().getObjref());
//                    return new JSONPhase(phase, responsibleEmail);
//                })
//                .collect(toList());
//
//
////        return getPhasesForProject(project.getPhases().getObjlist().getObjrefs()).stream()
////                .filter(phase -> !phase.getCode().contains("00_INTERN"))
////                .map(phase -> {
////                    String responsibleEmail = teamMap.get(project.getLeader().getObjref());
////                    return new JSONPhase(phase, responsibleEmail);
////                })
////                .collect(toList());
//    }

    private List<JSONPhase> phasesFor(Project project) {
        return getPhasesForProject(project.getPhases().getObjlist().getObjrefs()).stream()
                .filter(phase -> !phase.getCode().contains("00_INTERN"))
                .filter(phase -> !phase.getCode().contains("00_BID"))
                .map(phase -> {
                    String responsibleEmail = teamMap.get(project.getLeader().getObjref());
                    return new JSONPhase(phase, responsibleEmail);
                })
                .collect(toList());
    }

    private void checkUserAndPW() {
        final String[] nameAndPassword = request.getHeader("Authorization").split(":");
        if (nameAndPassword.length != 2) {
            throw new HttpBadRequest("Misssing name or password");
        }
        this.username = nameAndPassword[0];
        this.password = nameAndPassword[1];
        this.queryBuilder = new QueryBuilder(this.username, this.password);
    }

    //------------------------------------------------------------------------------------------------------------Helper Methods

    private List<JSONContact> getContactsAsJSONContact(VRAPI.ContainerDetailedOrganisation.Organisation org) {
       try {
           if (org.getContacts() == null )

           if (org.getContacts().getObjlist() == null) {
               System.out.println("Objlist null: " + org);
               return new ArrayList<>();
           }
           if (org.getContacts().getObjlist().getObjref() == null) {
               System.out.println("Objref null: " + org);
               return new ArrayList<>();
           }
           List<VRAPI.ContainerDetailedContact.Contact> conts = getActiveDetailedContacts(org.getContacts().getObjlist().getObjref());
           if (conts == null || conts.isEmpty()) {
               return new ArrayList<>();
           }
           return conts.stream()
                   .map(contact -> {
                       JSONContact jCont = new JSONContact(contact);
                       jCont.setOwner(getUserEmail(contact.getPersonResponsible().getObjref()));
                       return jCont;
                   })
                   .collect(toList());
       } catch (Exception e) {
           System.out.println("Organisation not behaving itself: " + org);
           System.out.println("Cause: " +  e);
       }
        return new ArrayList<>();
    }

    public String getUserEmail(Long id){
        try {
            return callVertec(queryBuilder.getUserEmail(id),
                    VRAPI.ContainerAddresses.Envelope.class)
                    .getBody().getQueryResponse().getWorkers().get(0).getEmail();
        } catch (Exception iobe){
            System.out.println("Could not find email address for v_id: " + id);
            return null;
        }
    }

    public List<VRAPI.ContainerDetailedProjects.Project> getDetailedProjects(Set<Long> projectIds) {
        return callVertec(
                queryBuilder.getProjectDetails(projectIds),
                VRAPI.ContainerDetailedProjects.Envelope.class).getBody().getQueryResponse().getProjects();
    }

    public List<VRAPI.ContainerPhases.ProjectPhase> getPhasesForProject(List<Long> phaseIds) {
        return callVertec(
                queryBuilder.getProjectPhases(phaseIds),
                VRAPI.ContainerPhases.Envelope.class).getBody().getQueryResponse().getPhases();
    }

    public ProjectType getProjectType(Long projectID) {
        return callVertec(
                queryBuilder.getProjectTypes(singletonList(projectID)),
                VRAPI.ContainerProjectType.Envelope.class).getBody().getQueryResponse().getProjectTypes().get(0);

    }

    public VRAPI.ContainerCurrency.Currency getCurrency(Long id) {
        return callVertec(
                queryBuilder.getCurrency(id),
                VRAPI.ContainerCurrency.Envelope.class).getBody().getQueryResponse().getCurrency();

    }

    public Set<Long> getProjectsTeamAreWorkingOn(Collection<Long> teamIds) {
        return callVertec(queryBuilder.getProjectIds(Lists.newArrayList(teamIds)),
                VRAPI.ContainerProjects.Envelope.class)
                .getBody().getQueryResponse().getProjectWorkers().stream()
                .filter(ProjectWorker::getActive)
                .filter(worker ->  worker.getProjectsList().
                        getObjList()
                        .getObjrefs() != null) //Ugly but Thal Bryan has got no projects, nor betreute addressen
                .flatMap(worker ->  worker.getProjectsList().
                        getObjList()
                        .getObjrefs()
                        .stream())
                .collect(toSet());
    }


    public List<Long> getZUKTeamMemberIds() {

        String xmlQuery = queryBuilder.getLeadersTeam();

        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> list = elementIn(response, "QueryResponse")
                .map(queryResponse -> queryResponse.getElementsByTagName("objref"))
                .map(ResourceController::asIdList)
                .orElse(new ArrayList<>());
        if (list.size() == 0) {
            failureFrom(response);
        }
        return list;
    }

    private void failureFrom(Document document) {
        elementIn(document, "Fault")
                .map(fault -> fault.getElementsByTagName("detailitem"))
                .map(detailItems -> {
                    asFailure(asStream(detailItems).findFirst());
                    return 0;
                })
                .orElseThrow(() -> new HttpInternalServerError("no detailItem"));
    }

    @SuppressWarnings("all")
    private void asFailure(Optional<String> maybeItem) {
         maybeItem
                .map(item -> {
                            if (item.contains("read access denied")) {
                                throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
                            } else if (item.contains("Authentication failure")) {
                                throw new HttpUnauthorisedException("Wrong username or password");
                            } else {
                                throw new HttpInternalServerError(item);
                            }
                        }
                ).orElseThrow(() ->  new HttpInternalServerError("missing fault"));
    }

    private static List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    private static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    private static Optional<Element> elementIn(Document document, String tagname) {
        final NodeList queryResponses = document.getElementsByTagName(tagname);
        return queryResponses.getLength() == 1 && queryResponses.item(0).getNodeType() == ELEMENT_NODE
                ? Optional.of((Element) queryResponses.item(0))
                : Optional.empty();
    }

    private Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException| IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    public List<Long> getAddressIdsSupervisedBy(List<Long> supervisorIds) {
        List<Long> ids = new ArrayList<>();
        Set<Long> uniqueIds = new HashSet<>();
        callVertec(queryBuilder.getSupervisedAddresses(supervisorIds), VRAPI.ContainerAddresses.Envelope.class)
                .getBody().getQueryResponse().getWorkers().stream()
                //.filter(VRAPI.ContainerAddresses.ProjectWorker::getActive) // removed filter as inactive user emails now mapped to active members
                .forEach(w -> {
                    ids.addAll(w.getAddresses().getList().getObjects());
                    //teamMap.put(w.getObjid(), w.getEmail().toLowerCase());
                });
        uniqueIds.addAll(ids);
        ids.clear();
        ids.addAll(uniqueIds);
        return ids;
    }

    public List<List<Long>> getSimpleContactsandOrgs(List<Long> contactIds) {
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
                        .map(Organisation::getObjid)
                        .collect(toList()));
        rIds.add(cIds);
        rIds.add(oIds);
        return rIds;
    }

    public List<VRAPI.ContainerDetailedContact.Contact> getActiveDetailedContacts(List<Long> ids){
        return getDetailedContacts(ids).stream()
                .filter(VRAPI.ContainerDetailedContact.Contact::getActive)
                .collect(toList());
    }

    public List<VRAPI.ContainerDetailedContact.Contact> getDetailedContacts(List<Long> ids) {
    try {
        return callVertec(queryBuilder.getContactDetails(ids), VRAPI.ContainerDetailedContact.Envelope.class)
                .getBody()
                .getQueryResponse()
                .getContactList();
        } catch (NullPointerException npe){
        throw new HttpNotFoundException("None of the given contacts exsist " + ids);
    }

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

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisationsWithInactive(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getOrganisationDetails(ids), VRAPI.ContainerDetailedOrganisation.Envelope.class).getBody().getQueryResponse().getOrganisationList().stream()
                    .collect(toList());
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("Did not find any of the listed organisations:" + ids );
        }
    }

    public ZUKOrganisationResponse buildZUKOrganisationsResponse(List<VRAPI.ContainerDetailedContact.Contact> contacts, List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs) {
        ZUKOrganisationResponse res = new ZUKOrganisationResponse();
        List<JSONContact> dangle = new ArrayList<>();


        List<JSONOrganisation> jsonOrgs = new ArrayList<>();
        for (VRAPI.ContainerDetailedOrganisation.Organisation vo : orgs) {

            JSONOrganisation org = new JSONOrganisation(vo);

            org.setOwner(teamMap.get(vo.getPersonResponsible().getObjref()));

            List<JSONContact> orgContacts = new ArrayList<>();

            for (Iterator<VRAPI.ContainerDetailedContact.Contact> vc = contacts.listIterator(); vc.hasNext(); ) {
                VRAPI.ContainerDetailedContact.Contact a = vc.next();

                if (a.getOrganisation() == null) continue;
                if (a.getOrganisation().getObjref() == null) continue;

                if (vo.getObjId().longValue() == a.getOrganisation().getObjref().longValue()) {
                    JSONContact c = new JSONContact(a);
                    c.setOwner(teamMap.get(a.getPersonResponsible().getObjref()));
                    c.setFollowers(followerMap.get(c.getObjid()));
                    if (c.getFollowers() == null) c.setFollowers(new ArrayList<>());

                    orgContacts.add(c);
                    vc.remove();
                }
            }

            org.setContacts(orgContacts);

            jsonOrgs.add(org);

        }

        for (VRAPI.ContainerDetailedContact.Contact a : contacts) {
            JSONContact c = new JSONContact(a);
            c.setOwner(teamMap.get(a.getPersonResponsible().getObjref()));
            c.setFollowers(followerMap.get(c.getObjid()));
            if (c.getFollowers() == null) c.setFollowers(new ArrayList<>());
            dangle.add(c);
        }

        res.setDanglingContacts(dangle);
        res.setOrganisationList(jsonOrgs);

        return res;
    }

    public List<Long> getActivityIds(List<Long> teamIds) {
        return callVertec(queryBuilder.getActivityIds(teamIds), VRAPI.ContainerAddresses.Envelope.class)
                .getBody().getQueryResponse().getWorkers().stream()
                .filter(VRAPI.ContainerAddresses.ProjectWorker::getActive)
                .map(VRAPI.ContainerAddresses.ProjectWorker::getActivities)
                .map(VRAPI.ContainerAddresses.Activities::getObjlist)
                .map(VRAPI.ContainerAddresses.Objlist::getObjects)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public List<VRAPI.ContainerActivity.Activity> getActivities(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getActivities(ids), VRAPI.ContainerActivity.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getActivities();
        } catch (NullPointerException npe){
            throw new HttpNotFoundException("No activities exist with the following v_ids: " + ids);
        }
    }

    public List<ActivityType> getActivityTypes(List<Activity> activities) {
        Set<Long> typeSet = activities.stream()
                .filter(a -> a.getType() != null)
                .map(Activity::getType)
                .map(Type::getObjref)
                .collect(toSet());

        List<Long> typeIds = new ArrayList<>();
        typeIds.addAll(typeSet);

        return callVertec(queryBuilder.getActivityTypes(typeIds), VRAPI.ContainerActivityType.Envelope.class)
                .getBody()
                .getQueryResponse()
                .getActivityTypes();

    }

    public JSONActivitiesResponse buildJSONActivitiesResponse(List<Activity> activities, List<ActivityType> types) {
        Map<Long, String> typeMap = new HashMap<>();

        for (ActivityType t : types) {
            typeMap.put(t.getObjid(), t.getTypename());
        }


        return new JSONActivitiesResponse(
                activities.stream()
                        .map(activity ->{
                               JSONActivity act =  new JSONActivity(
                                        activity,
                                        teamMap.get(activity.getAssignee().getObjref()),
                                        typeMap.get(activity.getType().getObjref()));

                            if(act.getCreation_date_time() == null) act.setCreation_date_time("");

                            return act;
                        })
                        .filter(activity -> (
                                activity.getCustomer_link() != null
                                || activity.getPhase_link() != null
                                || activity.getProject_link() != null))
                        .filter(activity ->
                                ofNullable(activity.getType())
                                .map(type -> !(type.contains("Document")
                                        || type.contains("Organizational Chart")
                                        || type.contains("Order Confirmation")
                                        || type.contains("Offer")))
                                        //TODO see whether this is how Wolfgang wants it to be
                                .orElse(true))
                        .collect(toList()));
    }

    public <T> T callVertec(String query, Class<T> responseType) {
        System.out.println("Calling vertec, querying for: " + responseType.getName());
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }


//------------------------------------------------------------------------------------------------------------Comparator

    @SuppressWarnings("WeakerAccess")
    public class ContactComparator implements Comparator<VRAPI.ContainerDetailedContact.Contact> {

        @Override
        public int compare(VRAPI.ContainerDetailedContact.Contact a, VRAPI.ContainerDetailedContact.Contact b) {
            if ((a.getOrganisation() == null || a.getOrganisation().getObjref() == null)
                    && (b.getOrganisation() == null || b.getOrganisation().getObjref() == null)) return 0;
            if ((a.getOrganisation() == null) || (a.getOrganisation().getObjref() == null)) return -1;
            if ((b.getOrganisation() == null) || (b.getOrganisation().getObjref() == null)) return 1;

            Long aref = a.getOrganisation().getObjref();
            Long bref = b.getOrganisation().getObjref();
            return aref < bref ? -1 : (aref.longValue() == bref.longValue() ? 0 : 1);
        }

    }


    public class ActivityComparator implements Comparator<VRAPI.ContainerActivity.Activity> {

        @Override
        public int compare(VRAPI.ContainerActivity.Activity a, VRAPI.ContainerActivity.Activity b) {
            if ((a.getType() == null || a.getType().getObjref() == null)
                    && (b.getType() == null || b.getType().getObjref() == null)) return 0;
            if ((a.getType() == null) || (a.getType().getObjref() == null)) return -1;
            if ((b.getType() == null) || (b.getType().getObjref() == null)) return 1;

            Long atype = a.getType().getObjref();
            Long btype = b.getType().getObjref();
            return atype < btype ? -1 : (atype.longValue() == btype.longValue() ? 0 : 1);
        }
    }

    public void setFollowerMap(Map<Long, List<String>> followerMap) {
        this.followerMap = followerMap;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
