package VRAPI;
import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.ContainerOrganisationJSON.JSONContact;
import VRAPI.ContainerOrganisationJSON.JSONOrganisation;
import VRAPI.ContainerOrganisationJSON.ZUKOrganisationResponse;
import VRAPI.ContainerProjectJSON.JSONPhase;
import VRAPI.ContainerProjectJSON.JSONProject;
import VRAPI.ContainerProjectJSON.ZUKProjectsResponse;
import VRAPI.ContainerProjectType.ProjectType;
import VRAPI.ContainerProjects.ProjectWorker;
import VRAPI.ContainerSimpleContactOrganisation.Contact;
import VRAPI.ContainerSimpleContactOrganisation.Organisation;
import VRAPI.FromContainer.GenericLinkContainer;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("WeakerAccess")
@RestController
public class ResourceController {
    public static final String DEFAULT_VERTEC_SERVER_HOST = "172.18.10.54";
    public static final String DEFAULT_VERTEC_SERVER_PORT = "8095";

    final private String VipAddress;
    private final URI vertecURI;
    private String VportNr;
    private String OwnIpAddress;
    private String OwnPortNr; //To be used for querying Vertec --XML Marshaller
    private String username;
    private String password;
    private RestTemplate rest;
    public ContactComparator comparator;
    private Map<Long,String> teamMap;

    public ResourceController() {
        //IpAddress:portNum of VertecServer
        this.VipAddress = DEFAULT_VERTEC_SERVER_HOST;
        this.VportNr = DEFAULT_VERTEC_SERVER_PORT;

        this.OwnIpAddress = "localhost";
        this.OwnPortNr = "9999";


        //set resttemplate message converters
        this.rest = new RestTemplate();
        vertecURI =   URI.create("http://" + VipAddress + ":" + VportNr + "/xml");

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);
        converters.add(jaxbMC);
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        rest.setMessageConverters(converters);

        //TODO: replace with proper authenitcation
        MyAccessCredentials creds = new MyAccessCredentials();

        this.username = creds.getUserName();
        this.password = creds.getPass();

        this.comparator = new ContactComparator();
        this.teamMap = new HashMap<>();
    }

    public void setTeamMap(Map<Long, String> teamMap) {
        this.teamMap = teamMap;
    }

    //------------------------------------------------------------------------------------------------------------Paths
    @Autowired
    private HttpServletRequest request;

    //TODO: add appropriate response codes

    @ApiOperation(value = "Test access", nickname = "notping")
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "text/plain")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 401, message = "Insufficient Access Credentials"),
//            @ApiResponse(code = 403, message = "Forbidden")
//    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    public String ping() {

        final RequestEntity<String> req = new RequestEntity<>(getXMLQuery_ping(), HttpMethod.POST, vertecURI);
        try {
            authorize();
            final ResponseEntity<VRAPI.ContainerTeam.Envelope> res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);

            checkResHasInfo(res.getBody());

        } catch (Exception e) {
            //hopefully will only happen when response returns Fault from XML Interface, then test to see whether incorret username and pwd, or limited access
            System.out.println("Did not recieve Team, attempting to recieve error message");
            try {

                authorize();
                final ResponseEntity<VRAPI.ContainerError.Envelope> res = this.rest.exchange(req, VRAPI.ContainerError.Envelope.class);

                String errorDetail = res.getBody().getBody().getFault().getDetails().getDetailitem().get(0);
                System.out.println(errorDetail);
                if (errorDetail.contains("Error: Authentication failure. Wrong User Name or Password")) {
                    return "Ping Failed: Wrong Username or Password recieved in request header";
                } else {
                    return "Partial Failure: Username and Password provided do not have sufficient permissions to access all Vertec Data. Some queries may return missing or no information";
                }

            } catch (Exception newe) {
                return "Unhandled Error in server: " + newe;
            }
        }

        return "Success!";
    }

    @ApiOperation(value = "Get organisations and nested contacts")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 401, message = "Insufficient Access Credentials"),
//            @ApiResponse(code = 403, message = "Forbidden")
//    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/organisations/ZUK", method = RequestMethod.GET, produces = "application/json")
    public String getZUKOrganisations() {
        List<Long> teamIds;

        try {
            authorize();
            teamIds = getZUKTeamMemberIds();

        } catch (Exception e) {
            //hopefully will only happen when response returns Fault from XML Interface, then test to see whether incorret username and pwd, or limited access
            return e.toString();
        }

        List<List<Long>> contactIdsAndOrgsIds = getSimpleContactsandOrgs(getAddressIdsSupervisedBy(teamIds));

        return buildZUKOrganisationsResponse(
                createFollowerMap(teamIds),
                getDetailedContacts(contactIdsAndOrgsIds.get(0)),
                getOrganisations(contactIdsAndOrgsIds.get(1)))
                .toString();
    }

    @ApiOperation(value = "Get projects and nested phases")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 401, message = "Insufficient Access Credentials"),
//            @ApiResponse(code = 403, message = "Forbidden")
//    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "username:password", required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/projects/ZUK", method = RequestMethod.GET, produces = "application/json")
    public String getZUKProjects() {
        populateTeamMap();

        try {
            authorize();

            final ZUKProjectsResponse response = new ZUKProjectsResponse();
            response.setProjects(projectsForTeam(getZUKTeamMemberIds()));
            return response.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    private List<JSONProject> projectsForTeam(List<Long> teamMemberIDs) {
        return getDetailedProjects(getProjectsTeamAreWorkingOn(teamMemberIDs)).stream()
        .map(this::fromProject)
        .filter(ProjectWithType::isInUK)
        .map(this::asJsonProject)
        .collect(toList());
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

        private Long currencyId() {
            return project.getCurrency().getObjref();
        }
    }

    private JSONProject asJsonProject(ProjectWithType pwt) {
        JSONProject proj = new JSONProject(pwt.project);
        proj.setPhases(phasesFor(pwt.project));
        proj.setType(pwt.projectType.getDescripton());
        proj.setCurrency(getCurrency(pwt.currencyId()).getName());
        return proj;
    }

    public ProjectWithType fromProject(VRAPI.ContainerDetailedProjects.Project project) {
        return new ProjectWithType(project, getProjectType(project.getType().getObjref()));
    }

    private List<JSONPhase> phasesFor(Project project) {
        return getPhasesForProject(project.getPhases().getObjlist().getObjrefs()).stream()
                .filter(phase -> ! phase.getCode().contains("00_INTERN"))
                .map(phase -> new JSONPhase(phase, teamMap.get(phase.getPersonResponsible().getObjref())))
                .collect(toList());
    }

    private void authorize() throws Exception {
        try {
            String userpwd = request.getHeader("Authorization");
            String[] both = userpwd.split(":");
            this.username = both[0];
            this.password = both[1];
        } catch (Exception e) {
            throw new Exception("Request failed: Authorization header incorrectly set");
        }
    }

    //------------------------------------------------------------------------------------------------------------Helper Methods
    //TODO: make xml access methods private, adjust tests: http://stackoverflow.com/questions/34571/how-to-test-a-class-that-has-private-methods-fields-or-inner-classes

    private void populateTeamMap() {
        List<Long> teamIds = new ArrayList<>();
        try {
            authorize();
            teamIds = getZUKTeamMemberIds();
        } catch (Exception e) {
            System.out.println("EXCEPTION POPULATING TEAM MAP: " + e);
        }
        getAddressIdsSupervisedBy(teamIds);

    }


    public List<VRAPI.ContainerDetailedProjects.Project> getDetailedProjects(Set<Long> projectIds) {
        return callVertec(
                getXMLQuery_GetProjectDetails(projectIds),
                VRAPI.ContainerDetailedProjects.Envelope.class).getBody().getQueryResponse().getProjects();
    }

    public List<VRAPI.ContainerPhases.ProjectPhase> getPhasesForProject(List<Long> phaseIds){
        // TODO filter out inactive projects?
        return callVertec(
                getXMLQuery_GetProjectPhases(phaseIds),
                VRAPI.ContainerPhases.Envelope.class).getBody().getQueryResponse().getPhases();
    }

    public ProjectType getProjectType(Long projectID){
        return callVertec(
                getXMLQuery_GetProjectTypes(singletonList(projectID)),
                VRAPI.ContainerProjectType.Envelope.class).getBody().getQueryResponse().getProjectTypes().get(0);

    }

    public VRAPI.ContainerCurrency.Currency getCurrency(Long id) {
        return callVertec(
                getXMLQuery_GetCurrency(id),
                VRAPI.ContainerCurrency.Envelope.class).getBody().getQueryResponse().getCurrency();

    }

    public Set<Long> getProjectsTeamAreWorkingOn(Collection<Long> teamIds)  {
        return callVertec(getXMLQuery_GetProjectIds(Lists.newArrayList(teamIds)),
                VRAPI.ContainerProjects.Envelope.class)
                .getBody().getQueryResponse().getProjectWorkers().stream()
                .filter(ProjectWorker::getActive)
                .flatMap(worker -> worker.getProjectsList().getObjList().getObjrefs().stream())
                .collect(toSet());
    }

    public List<Long> getZUKTeamMemberIds() throws Exception {
        RequestEntity<String> req;
        List<Long> ids = new ArrayList<>();
        ResponseEntity<VRAPI.ContainerTeam.Envelope> res;

        String xmlQuery = getXMLQuery_LeadersTeam();
        String uri = "http://" + VipAddress + ":" + VportNr + "/xml";

        req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));

        Exception exception = null;

        try {
            res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);
            checkResHasInfo(res.getBody());
            ids = res.getBody().getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects();

        } catch (XMLFailureException e) {
            System.out.println("XMLException thrown by getZUKTeamMemberIds");
            System.out.println("Did not recieve Team, attempting to recieve error message");
            try {
                ResponseEntity<VRAPI.ContainerError.Envelope> rese = this.rest.exchange(req, VRAPI.ContainerError.Envelope.class);

                String errorDetail = rese.getBody().getBody().getFault().getDetails().getDetailitem().get(0);
                System.out.println(errorDetail);

                if (errorDetail.contains("Error: Authentication failure. Wrong User Name or Password")) {
                    exception = new XMLFailureException("Ping Failed: Wrong Username or Password recieved in request header");
                } else {
                    exception =  new XMLFailureException("Partial Failure: Username and Password provided do not have sufficient permissions to access all Vertec Data. Some queries may return missing or no information");
                }

            } catch (Exception newe) {
                exception = new Exception("Unhandled Error in server: " + newe.toString());
            }
        } catch (Exception e) {
            exception = new Exception("Unhandled Error in server" + e);
        }

        if (exception != null) {
            throw exception;
        }

        return ids;
    }

    public List<Long> getAddressIdsSupervisedBy(List<Long> supervisorIds){
        List<Long> ids = new ArrayList<>();
        Set<Long> uniqueIds = new HashSet<>();
        callVertec(getXMLQuery_SupervisedAddresses(supervisorIds), VRAPI.ContainerAddresses.Envelope.class)
                .getBody().getQueryResponse().getWorkers().stream()
                .filter(VRAPI.ContainerAddresses.ProjectWorker::getActive)
                .forEach(w -> {
                    ids.addAll(w.getAddresses().getList().getObjects());
                    teamMap.put(w.getObjid(), w.getEmail());
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
                = callVertec(getXMLQuery_GetContacts(contactIds), VRAPI.ContainerSimpleContactOrganisation.Envelope.class);

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

    public List<VRAPI.ContainerDetailedContact.Contact> getDetailedContacts(List<Long> ids) {
        List<VRAPI.ContainerDetailedContact.Contact> contacts = new ArrayList<>();
        contacts.addAll(
                callVertec(getXMLQuery_GetContactDetails(ids), VRAPI.ContainerDetailedContact.Envelope.class).getBody().getQueryResponse().getContactList().stream()
                        .filter(VRAPI.ContainerDetailedContact.Contact::getActive)
                        .collect(toList()));

        Collections.sort(contacts, this.comparator);
        return contacts;
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        return callVertec(getXMLQuery_GetOrganisationDetails(ids), VRAPI.ContainerDetailedOrganisation.Envelope.class).getBody().getQueryResponse().getOrganisationList().stream()
                .filter(VRAPI.ContainerDetailedOrganisation.Organisation::getActive)
                .collect(toList());
    }

    public Map<Long, List<String>> createFollowerMap(List<Long> teamIds) {
        Map<Long, List<String>> map = new HashMap<>();
        VRAPI.ContainerFollower.Envelope leader;

        for (Long id : teamIds) {

            //#2 query for  project leaders aktiv and fromlink
            leader = getGenericLinkContainers(id);


            if (leader.getBody().getQueryResponse().getProjectWorker().getActive()) {
                VRAPI.FromContainer.Envelope resFromContainer;

                //#3 query for generic Link Containers
                resFromContainer = getFromContainer(leader.getBody().getQueryResponse().getProjectWorker().getFromLinks().getObjlist().getObjref());

                List<GenericLinkContainer> genericLinkContainers = resFromContainer.getBody().getQueryResponse().getGenericLinkContainers();

                for(GenericLinkContainer glc : genericLinkContainers){
                    Long objref = glc.getFromContainer().getObjref();
                    try {

                        List<String> idsFollowing = map.get(objref);
                        idsFollowing.add(leader.getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.replace(objref, idsFollowing);

                    } catch (Exception e) {

                        List<String> idsFollowing = new ArrayList<>();
                        idsFollowing.add(leader.getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.put(objref, idsFollowing);

                    }
                }
            }
        }

        return map;

    }

    private VRAPI.FromContainer.Envelope getFromContainer(List<Long> ids) {
        return callVertec(getXMLQuery_FromContainers(ids), VRAPI.FromContainer.Envelope.class);
    }

    private VRAPI.ContainerFollower.Envelope getGenericLinkContainers(Long id) {
        return callVertec(getXMLQuery_LeadersFromLinks(id), VRAPI.ContainerFollower.Envelope.class);
    }

    public ZUKOrganisationResponse buildZUKOrganisationsResponse(Map<Long, List<String>> followerMap, List<VRAPI.ContainerDetailedContact.Contact> contacts, List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs){
        ZUKOrganisationResponse res = new ZUKOrganisationResponse();
        List<JSONContact> dangle = new ArrayList<>();


        List<JSONOrganisation> jsonOrgs = new ArrayList<>();
        for(VRAPI.ContainerDetailedOrganisation.Organisation vo : orgs) {

            JSONOrganisation org = new JSONOrganisation(vo);

            org.setOwner(teamMap.get(vo.getPersonResponsible().getObjref()));

            List<JSONContact> orgContacts = new ArrayList<>();

            for(Iterator<VRAPI.ContainerDetailedContact.Contact> vc = contacts.listIterator();vc.hasNext();){
                VRAPI.ContainerDetailedContact.Contact a = vc.next();

                if(a.getOrganisation() == null) continue;
                if(a.getOrganisation().getObjref() == null) continue;

                if(vo.getObjId().longValue() == a.getOrganisation().getObjref().longValue()) {
                    JSONContact c = new JSONContact(a);
                    c.setOwner(teamMap.get(a.getPersonResponsible().getObjref()).toLowerCase());
                    c.setFollowers(followerMap.get(c.getObjid()));
                    if(c.getFollowers() == null) c.setFollowers(new ArrayList<>());

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

    private String getXMLQuery_FromContainers(List<Long> containerIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        if(containerIds != null){
            for (Long id : containerIds) {
                bodyStart += "<objref>" + id + "</objref>\n";
            }
        }
        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>fromContainer</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_LeadersFromLinks(Long id) {
        return "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>" + id + "</objref>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>fromlinks</member>\n" +
                "        <member>aktiv</member>\n" +
                "        <member>briefEmail</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private String getXMLQuery_ping() {
        return getXMLQuery_LeadersTeam();
    }

    private String getXMLQuery_LeadersTeam() {
        return "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <ocl>projektBearbeiter->select(loginName='wje')</ocl>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" + //will return objref for each member of team
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private String getXMLQuery_SupervisedAddresses(List<Long> memberIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for(Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>BetreuteAdressen</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "        <member>briefEmail</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetContacts(List<Long> contactIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username+ "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : contactIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetContactDetails(List<Long> contactIds){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : contactIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>Firma</member>\n" + //will return objref to parent firma
                "        <member>StandardEMail</member>\n" +
                "        <member>StandardTelefon</member>\n" +
                "        <member>StandardMobile</member>\n" +
                "        <member>Vorname</member>\n" +
                "        <member>betreuer</member>\n" +
                "        <member>ModifiedDateTime</member>\n" +
                "        <member>creationDateTime</member>\n" +
                "        <member>aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;

    }

    private String getXMLQuery_GetOrganisationDetails(List<Long> ids){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>betreuer</member>\n" +
                "        <member>StandardAdresse</member>\n" +
                "        <member>StandardLand</member>\n" +
                "        <member>StandardOrt</member>\n" +
                "        <member>StandardPLZ</member>\n" +
                "        <member>zusatz</member>\n" +
                "        <member>aktiv</member>\n" +
                "        <member>ModifiedDateTime</member>\n" +
                "        <member>creationDateTime</member>\n" +
                "        <member>mutterfirma</member>\n" +
                "        <member>tochterfirmen</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetProjectDetails(Set<Long> ids){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Kunde</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "        <member>Phasen</member>\n" +
                "        <member>projektnummer</member>\n" +
                "        <member>projektleiter</member>\n" +
                "        <member>code</member>\n" +
                "        <member>auftraggeber</member>\n" +
                "        <member>typ</member>\n" +
                "        <member>waehrung</member>\n" +
                "        <member>modifieddatetime</member>\n" +
                "        <member>creationdatetime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetProjectIds(List<Long> memberIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for(Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>bearbProjekte</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetProjectPhases(List<Long> ids){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>aktiv</member>\n" +
                "           <member>SumWertInt</member>\n" +
                "           <member>SumWertExt</member>\n" +
                "           <member>Status</member>\n" +
                "           <member>Code</member>\n" +
                "           <member>Beschreibung</member>\n" +
                "           <member>AbschlussDatum</member>\n" +
                "           <member>Verantwortlicher</member>\n" +
                "           <member>offertdatum</member>\n" +
                "           <member>startDatum</member>\n" +
                "           <member>endDatum</member>\n" +
                "           <member>verkaufsstatus</member>\n" +
                "           <member>absagegrundtext</member>\n" +
                "           <member>creationdatetime</member>\n" +
                "           <member>modifieddatetime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetProjectTypes(List<Long> ids){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetCurrency(Long id){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
                    bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    private void checkResHasInfo(VRAPI.ContainerTeam.Envelope envelope) throws Exception {
        if (envelope.getBody().getQueryResponse() == null) {
            throw new XMLFailureException();
        }
    }

    private class XMLFailureException extends Exception {
        private XMLFailureException() {

        }

        XMLFailureException(String message) {
            super(message);
        }
    }

    private <T> T callVertec(String query, Class<T> responseType) {
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }


//------------------------------------------------------------------------------------------------------------Comparator

    @SuppressWarnings("WeakerAccess")
    public class ContactComparator implements Comparator<VRAPI.ContainerDetailedContact.Contact> {

        @Override
        public int compare(VRAPI.ContainerDetailedContact.Contact a, VRAPI.ContainerDetailedContact.Contact b) {
            if((a.getOrganisation() == null || a.getOrganisation().getObjref() == null)
                    && (b.getOrganisation() == null || b.getOrganisation().getObjref() == null)) return 0;
            if((a.getOrganisation() == null) || (a.getOrganisation().getObjref() == null)) return -1;
            if((b.getOrganisation() == null) || (b.getOrganisation().getObjref() == null)) return 1;

            Long aref = a.getOrganisation().getObjref();
            Long bref = b.getOrganisation().getObjref();
            return aref < bref ? -1 : (aref.longValue() == bref.longValue() ? 0 : 1);
        }

    }

    public String getOwnPortNr() {
        return OwnPortNr;
    }

    public String getOwnIpAddress() {
        return OwnIpAddress;
    }

}
