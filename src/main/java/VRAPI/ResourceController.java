package VRAPI;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
public class ResourceController {

    public static final String VERTEX_SERVER_HOST = "172.18.10.54";
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
        this.VportNr = "8095";

        this.OwnIpAddress = "localhost";
        this.OwnPortNr = "9999";

        //set resttemplate message converters
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

        RequestEntity<String> req;
        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
        try {
            authorize();
            String xmlQuery = getXMLQuery_ping();
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerTeam.Envelope> res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);

            checkResHasInfo(res.getBody());

        } catch (Exception e) {
            //hopefully will only happen when response returns Fault from XML Interface, then test to see whether incorret username and pwd, or limited access
            System.out.println("Did not recieve Team, attempting to recieve error message");
            try {

                authorize();
                req = new RequestEntity<>(getXMLQuery_ping(), HttpMethod.POST, new URI(uri));
                ResponseEntity<VRAPI.ContainerError.Envelope> res = this.rest.exchange(req, VRAPI.ContainerError.Envelope.class);

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
                getOrganisations(contactIdsAndOrgsIds.get(1))).toString();
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
        List<VRAPI.ContainerPhases.ProjectPhase> phasesForProject;
        List<Long> typeIds;
        List<ProjectType> projectTypes;

        System.out.println("Populating team Map");
        populateTeamMap();

        List<Long> teamIDs;
        Set<Long> projectIds;

        try {
            authorize();
            teamIDs = getZUKTeamMemberIds();

        } catch (Exception e) {
            return e.toString();
        }

        projectIds = getProjectsTeamAreWorkingOn(teamIDs);

        Set<Long> projIdSet = new HashSet<>();
        projIdSet.addAll(projectIds);

        List<VRAPI.ContainerDetailedProjects.Project> projects = getDetailedProjects(projectIds);
        System.out.println("Got " + projects.size() + " projects");

        List<JSONProject> projectList = new ArrayList<>();

        int phasecounter = 0;

        for(VRAPI.ContainerDetailedProjects.Project project : projects) {
            List<JSONPhase> phases = new ArrayList<>();
            phasesForProject = getPhasesForProject(project.getPhases().getObjlist().getObjrefs());

            if (phasesForProject != null) {
                for (VRAPI.ContainerPhases.ProjectPhase ph : phasesForProject) {
                    JSONPhase phaseToAdd = new JSONPhase(ph);
                    phaseToAdd.setPersonResponsible(teamMap.get(ph.getPersonResponsible().getObjref()));
                    phasecounter++;

                    //ONLY ADD PHASE IF IT IS NOT "00_INTERN"
                    if( ! phaseToAdd.getCode().contains("00_INTERN")){

                        phases.add(phaseToAdd);
                    }
                }
            }
            //GET TYPE OF PROJECT
            typeIds = new ArrayList<>();
            typeIds.add(project.getType().getObjref());

            projectTypes = getProjectTypes(typeIds);

            //following if statement makes sure we only send projects done in the UK
            if(projectTypes.get(0).getDescripton().contains("SGB_") || projectTypes.get(0).getDescripton().contains("EMS") || projectTypes.get(0).getDescripton().contains("DSI") || projectTypes.get(0).getDescripton().contains("CAP")){

                //GET CURRENCY OF PROJECT
                VRAPI.ContainerCurrency.Currency currency = getCurrency(project.getCurrency().getObjref());

                JSONProject proj = new JSONProject(project);
                proj.setPhases(phases);
                proj.setType(projectTypes.get(0).getDescripton());
                proj.setCurrency(currency.getName());

                projectList.add(proj);
            }
        }

        System.out.println("Got " + projectList.size() + " ZUK projects");
        System.out.println("GOt " + phasecounter + " phases in total");

        ZUKProjectsResponse response = new ZUKProjectsResponse();
        response.setProjects(projectList);

        return response.toString();

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

        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerDetailedProjects.Envelope> res;
        List<VRAPI.ContainerDetailedProjects.Project> projectList = new ArrayList<>();
        String xmlQuery = getXMLQuery_GetProjectDetails(projectIds);
        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";

        try {
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = rest.exchange(req, VRAPI.ContainerDetailedProjects.Envelope.class);
            //TODO: filter inactive projects, if necessary
            projectList = res.getBody().getBody().getQueryResponse().getProjects();

        } catch (Exception e) {
            System.out.println("EXCEPTION GETTING DETAILED PROJECTS: " +  e);
        }

        return projectList;
    }

    public List<VRAPI.ContainerPhases.ProjectPhase> getPhasesForProject(List<Long> phaseIds){
        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerPhases.Envelope> res;
        List<VRAPI.ContainerPhases.ProjectPhase> phaseList = new ArrayList<>();
        String xmlQuery = getXMLQuery_GetProjectPhases(phaseIds);


        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
        try {

            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = rest.exchange(req, VRAPI.ContainerPhases.Envelope.class);

            //TODO: filter inactive projects, if necessary
            phaseList = res.getBody().getBody().getQueryResponse().getPhases();

        } catch (Exception e) {
            System.out.println("EXCEPTION GETTING DETAILED PROJECT_PHASES: " +  e);
        }

        return phaseList;
    }

    public List<ProjectType> getProjectTypes(List<Long> ids){
        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerProjectType.Envelope> res;
        String xmlQuery = getXMLQuery_GetProjectTypes(ids);

        List<ProjectType> projectTypes = new ArrayList<>();

        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
        try{
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = rest.exchange(req, VRAPI.ContainerProjectType.Envelope.class);
            projectTypes.addAll(res.getBody().getBody().getQueryResponse().getProjectTypes());
        } catch (Exception e){
            System.out.println("Exception in getting Project Types");
        }
        return projectTypes;
    }

    public VRAPI.ContainerCurrency.Currency getCurrency(Long id) {
        String xmlQuery = getXMLQuery_GetCurrency(id);
        VRAPI.ContainerCurrency.Currency currency = null;

        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
        try {
            final RequestEntity<String> req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            final ResponseEntity<VRAPI.ContainerCurrency.Envelope>  res = rest.exchange(req, VRAPI.ContainerCurrency.Envelope.class);

            currency = res.getBody().getBody().getQueryResponse().getCurrency();
        } catch (Exception e) {
            System.out.println("Exception in getting Currency");
        }
        return currency;
    }

    public Set<Long> getProjectsTeamAreWorkingOn(Collection<Long> teamIds)  {

        String xmlQuery = getXMLQuery_GetProjectIds(Lists.newArrayList(teamIds));
        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";

        final RequestEntity<String> req = new RequestEntity<>(xmlQuery, HttpMethod.POST, URI.create(uri));
        final ResponseEntity<VRAPI.ContainerProjects.Envelope> res = rest.exchange(req, VRAPI.ContainerProjects.Envelope.class);

        return res.getBody().getBody().getQueryResponse().getProjectWorkers().stream()
                .filter(ProjectWorker::getActive)
                .flatMap(worker -> worker.getProjectsList().getObjList().getObjrefs().stream())
                .collect(toSet());
    }

    public List<Long> getZUKTeamMemberIds() throws Exception {
        RequestEntity<String> req;
        List<Long> ids = new ArrayList<>();
        ResponseEntity<VRAPI.ContainerTeam.Envelope> res;

        String xmlQuery = getXMLQuery_LeadersTeam();
        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";

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
        this.teamMap = new HashMap<>();
        try {

            String xmlQuery = getXMLQuery_SupervisedAddresses(supervisorIds);
            String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
            RequestEntity<String> req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerAddresses.Envelope> res = rest.exchange(req, VRAPI.ContainerAddresses.Envelope.class);
            VRAPI.ContainerAddresses.Envelope env = res.getBody();

            env.getBody().getQueryResponse().getWorkers().stream()
                    .filter(VRAPI.ContainerAddresses.ProjectWorker::getActive)
                    .forEach(w -> { ids.addAll(w.getAddresses().getList().getObjects());

                teamMap.put(w.getObjid(), w.getEmail());
            });


            uniqueIds.addAll(ids);

        } catch (Exception e) {
            System.out.println("ERROR IN GETTING Supervised Addresses" + e);
        }

        ids.clear();
        ids.addAll(uniqueIds);

        return ids;
    }

    public List<List<Long>> getSimpleContactsandOrgs(List<Long> contactIds) {
        RequestEntity<String> req;
        //no need for set as well as list as objids queried from set
        List<Long> cIds = new ArrayList<>();
        List<Long> oIds = new ArrayList<>();
        List<List<Long>> rIds = new ArrayList<>();
        try {

            String xmlQuery = getXMLQuery_GetContacts(contactIds);
            String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerSimpleContactOrganisation.Envelope> res = this.rest.exchange(req, VRAPI.ContainerSimpleContactOrganisation.Envelope.class);
            VRAPI.ContainerSimpleContactOrganisation.Envelope env = res.getBody();

            cIds.addAll(
                    env.getBody().getQueryResponse().getContacts().stream()
                    .map(Contact::getObjid)
                    .collect(toList()));

            oIds.addAll(
                    env.getBody().getQueryResponse().getOrgs().stream()
                    .map(Organisation::getObjid)
                    .collect(toList()));

        } catch (Exception e){
            System.out.println("ERROR IN GETTING SIMPLE CONTACTS: " + e);
        }

        rIds.add(cIds);
        rIds.add(oIds);

        return rIds;
    }

    public List<VRAPI.ContainerDetailedContact.Contact> getDetailedContacts(List<Long> ids) {
        RequestEntity<String> req;
        List<VRAPI.ContainerDetailedContact.Contact> contacts = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetContactDetails(ids);
            String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerDetailedContact.Envelope> res = this.rest.exchange(req, VRAPI.ContainerDetailedContact.Envelope.class);

            contacts.addAll(
                    res.getBody().getBody().getQueryResponse().getContactList().stream()
                    .filter(VRAPI.ContainerDetailedContact.Contact::getActive)
                    .collect(toList()));


        } catch ( Exception e){
            System.out.println("Exception in getDetailedContacts: " + e);
        }

        Collections.sort(contacts, this.comparator);
        return contacts;
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerDetailedOrganisation.Envelope> res = null;
        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetOrganisationDetails(ids);
            String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = this.rest.exchange(req, VRAPI.ContainerDetailedOrganisation.Envelope.class);

            orgs.addAll(res.getBody().getBody().getQueryResponse().getOrganisationList().stream()
                    .filter(VRAPI.ContainerDetailedOrganisation.Organisation::getActive)
                    .collect(toList()));


        } catch ( Exception e){
            System.out.println("Exception in getDetailed Organisations: " + e);
            System.out.println("Exception in getDetailed Organisations: " + res);
        }
        return orgs;
    }

    public Map<Long, List<String>> createFollowerMap(List<Long> teamIds) {
        Map<Long, List<String>> map = new HashMap<>();
        ResponseEntity<VRAPI.ContainerFollower.Envelope> leader;

        for (Long id : teamIds) {

            //#2 query for  project leaders aktiv and fromlink
            leader = getGenericLinkContainers(id);


            if (leader.getBody().getBody().getQueryResponse().getProjectWorker().getActive()) {
                ResponseEntity<VRAPI.FromContainer.Envelope> resFromContainer;

                //#3 query for generic Link Containers
                resFromContainer = getFromContainer(leader.getBody().getBody().getQueryResponse().getProjectWorker().getFromLinks().getObjlist().getObjref());

                List<GenericLinkContainer> genericLinkContainers = resFromContainer.getBody().getBody().getQueryResponse().getGenericLinkContainers();

                for(GenericLinkContainer glc : genericLinkContainers){
                    Long objref = glc.getFromContainer().getObjref();
                    try {

                        List<String> idsFollowing = map.get(objref);
                        idsFollowing.add(leader.getBody().getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.replace(objref, idsFollowing);

                    } catch (Exception e) {

                        List<String> idsFollowing = new ArrayList<>();
                        idsFollowing.add(leader.getBody().getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.put(objref, idsFollowing);

                    }
                }
            }
        }

        return map;

    }

    private ResponseEntity<VRAPI.FromContainer.Envelope> getFromContainer(List<Long> ids) {
        String xmlQuery = getXMLQuery_FromContainers(ids);
        RequestEntity<String> req;
        ResponseEntity<VRAPI.FromContainer.Envelope> res = null;
        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";

        try {

            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = this.rest.exchange(req, VRAPI.FromContainer.Envelope.class);
        } catch (Exception e) {
            System.out.println("ERROR IN GETTING FROM CONTAINERS: " + e);
        }

        return res;
    }

    private ResponseEntity<VRAPI.ContainerFollower.Envelope> getGenericLinkContainers(Long id) {
        String xmlQuery = getXMLQuery_LeadersFromLinks(id);

        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerFollower.Envelope> res = null;

        String uri = "http://" + VERTEX_SERVER_HOST + ":" + VportNr + "/xml";
        try {

            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = this.rest.exchange(req, VRAPI.ContainerFollower.Envelope.class);

        } catch (Exception e) {
            System.out.println("EXCEPTION IN GETTING GENERIC LIST CONTAINER: " +  e);
        }

        return res;

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

//------------------------------------------------------------------------------------------------------------Comparator

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
