package VRAPI.ResourceControllers;

import VRAPI.Entities.Contact;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.Exceptions.HttpUnprocessableEntityException;
import VRAPI.JSONClasses.JSONContainerProject.JSONPhase;
import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import VRAPI.MergeClasses.ActivitiesForAddressEntry;
import VRAPI.MergeClasses.ContactsForOrganisation;
import VRAPI.MergeClasses.ProjectsForAddressEntry;
import VRAPI.Util.NoIdSuppliedException;
import VRAPI.Util.QueryBuilder;
import VRAPI.Util.StaticMaps;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerDetailedProjects.Project;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@Scope("prototype") //This enforces that an organisation controller is created per request
public class OrganisationController extends Controller {

    public OrganisationController() {
        super();
    }

    public OrganisationController(QueryBuilder queryBuilder) {
        super(queryBuilder);
    }

    //======================================================================================================================
// POST /organisations
//======================================================================================================================
    @ApiOperation(value = "Create new organisation", nickname = "create")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation", method = RequestMethod.POST)
    public ResponseEntity<Long> createOrganisationEndpoint(@RequestBody Organisation organisation) throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return createOrganisation(organisation);
    }



    //======================================================================================================================
// PUT /organisations
//======================================================================================================================
    @ApiOperation(value = "Update Organisation", nickname = "Update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateEndpoint(@PathVariable Long id, @RequestBody Organisation organisation) throws ParserConfigurationException {
        //makes response faster
        if(organisation.getVertecId() == null) throw new HttpUnprocessableEntityException("Please supply an Id with the organisation to update");

        queryBuilder = AuthenticateThenReturnQueryBuilder();

        if(!isIdOfType(id, "Firma")) throw new HttpNotFoundException("No organisation with id: " + id + " exists");

        try {
            return update(id, organisation);
        } catch (NoIdSuppliedException e) {
            throw new HttpUnprocessableEntityException("Please supply an Id with the organisation to update");
        }
    }



    @ApiOperation(value = "Set Organisation to inactive", nickname = "deActivate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Long> setInactiveEndpoint(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return setActiveField(id, false);

    }


    @ApiOperation(value = "Set Organisation to active", nickname = "Activate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/activate", method = RequestMethod.PUT)
    public ResponseEntity<Long> setActiveEndpoint(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return setActiveField(id, true);
    }

    //====================================================================================================================
    // MERGE /organisations
//======================================================================================================================
    @ApiOperation(value = "Merge two vertec organisations", nickname = "merge")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{mergingId}/mergeInto/{survivingId}", method = RequestMethod.GET)
    public ResponseEntity<String> mergeOrganisationsEndpoint(@PathVariable Long mergingId, @PathVariable Long survivingId)
            throws ParserConfigurationException, IOException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return mergeOrganisations(mergingId, survivingId);
    }


//======================================================================================================================
// GET /organisations
//======================================================================================================================


    //---------------------------------------------------------------------------------------------------------------------- /{id}/contacts
    @ApiOperation(value = "Get contacts for organisation", nickname = "contacts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/contacts", method = RequestMethod.GET)
    public ResponseEntity<ContactsForOrganisation> getContactsForOrganisationEndpoint(@PathVariable Long id) throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getContactsForOrganisation(id);
    }


    //---------------------------------------------------------------------------------------------------------------------- /{id}/projects
    @ApiOperation(value = "Get projects for organisation", nickname = "projects")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/projects", method = RequestMethod.GET)
    public ResponseEntity<ProjectsForAddressEntry> getProjectsForOrganisationEndpoint(@PathVariable Long id)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getProjectsForOrganisation(id);
    }


    private List<JSONProject> getDetailedProjects(List<Long> projectIdsForOrg) {
        if (projectIdsForOrg.isEmpty()) return new ArrayList<>();
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
    @ApiOperation(value = "Get activities for organisation", nickname = "activities")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/activities", method = RequestMethod.GET)
    //TODO: write test for this function
    public ResponseEntity<ActivitiesForAddressEntry> getActivitiesForOrganisationEndpoint(@PathVariable Long id)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getActivitiesForAddressEntry(id);
    }


    //====================================================================================================================== /all
    @ApiOperation(value = "Get all organisations owned by ZUK employees", nickname = "all")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisations/all", method = RequestMethod.GET)//TODO: write tests for this
    public ResponseEntity<OrganisationList> getAllOrganisationsEndpoint() throws ParserConfigurationException {

        System.out.println("Received request");
        queryBuilder = AuthenticateThenReturnQueryBuilder();
        System.out.println("Authenticated request");
        ResponseEntity<OrganisationList> res = getAllOrganisations();
        System.out.println(res.getBody());

        return res;
    }

    private List<VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        try {
            return callVertec(queryBuilder.getOrganisationDetails(ids), VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class).getBody().getQueryResponse().getOrganisationList().stream()
                    .filter(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation::getActive)
                    .collect(toList());
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("Did not find any of the listed organisations:" + ids);
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
                        .map(VRAPI.XMLClasses.ContainerSimpleContactOrganisation.Contact::getObjid)
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisations/{ids}", method = RequestMethod.GET)
    public ResponseEntity<OrganisationList> getOrganisationListEndpoint(@PathVariable List<Long> ids)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getOrganisationList(ids);
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
    public ResponseEntity<Organisation> getOrganisationEndpoint(@PathVariable Long id)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getOrganisation(id);
    }


//======================================================================================================================
// POST /organisations/
//======================================================================================================================

    //---------------------------------------------------------------------------------------------------------------------- POST /{id}
    @ApiOperation(value = "Post organisation to vertec", nickname = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public ResponseEntity<Organisation> postOrganisation(@RequestBody Organisation orgToPost) {

        System.out.println(orgToPost.toJsonString());

        //TODO: Finish this - wow!?

        return new ResponseEntity<>(new Organisation(), HttpStatus.OK);
    }
    //=======================================METHODS========================================================================

    public ResponseEntity<Organisation> getOrganisation(Long id) {
        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();

        List<Long> idAsList = new ArrayList<>();
        idAsList.add(id);
        VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(idAsList),
                VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class);

        if (organisationEnvelope.getBody().getQueryResponse() == null
                || organisationEnvelope.getBody().getQueryResponse().getOrganisationList().size() == 0) {
            throw new HttpNotFoundException("Organisation with id: " + id + " could not be found");
        }
        //techincally we recieve a list of organisations (length 1) so we take the first item from list
        Organisation res = xml2json(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<OrganisationList> getOrganisationList(List<Long> ids) {
        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();

        VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(ids),
                VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class);


        if (organisationEnvelope.getBody().getQueryResponse() == null
                || organisationEnvelope.getBody().getQueryResponse().getOrganisationList().size() == 0) {
            throw new HttpNotFoundException("Some or all of the ids requested are not organisations");
        }

        OrganisationList res = new OrganisationList();
        res.setOrganisations(createOrganisationList(organisationEnvelope.getBody().getQueryResponse().getOrganisationList()));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<OrganisationList> getAllOrganisations() {
        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();
        Set<Long> allEmployeeIds = supervisorIdMap.keySet();
        Set<Long> allAddressIds = getAddressIdsSupervisedBy(allEmployeeIds);
        List<List<Long>> contactsIdsAndOrgIds = getSimpleContactsandOrgs(allAddressIds);
        System.out.println(contactsIdsAndOrgIds.get(1).size());
        List<Organisation> organisations = createOrganisationList(getOrganisations(contactsIdsAndOrgIds.get(1)));

        OrganisationList res = new OrganisationList();
        res.setOrganisations(organisations);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<ProjectsForAddressEntry> getProjectsForOrganisation(Long id) {
        String xmlQuery = queryBuilder.getProjectsForOrganisation(id);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> projectIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<JSONProject> projects = getDetailedProjects(projectIdsForOrg);
        ProjectsForAddressEntry res = new ProjectsForAddressEntry(id, organisationName);
        res.setProjects(projects);

        return new ResponseEntity<>(res, HttpStatus.OK);
        //TODO: include type and currency if we need them
    }

    public ResponseEntity<ContactsForOrganisation> getContactsForOrganisation(Long id) {
        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();

        String xmlQuery = queryBuilder.getContactsForOrganisation(id);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> contactIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<Contact> contacts = getDetailedContacts(contactIdsForOrg);
        ContactsForOrganisation res = new ContactsForOrganisation(id, organisationName);
        res.setContacts(contacts);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<Long> setActiveField(Long id, Boolean active) {
        if (!isIdOfType(id, "Firma")) {
            throw new HttpNotFoundException("Organisation with id: " + id + " does not exist");
        }
        String putQuery = queryBuilder.setOrganisationActive(active, id);
        //send put request to vertec

        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {
            VertecServerInfo.log.info("------ Set Organisation: " + id + "-s active field to " + active + "------>\n\n");
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            VertecServerInfo.log.info("------ Failed to: " + id + "-s active field to " + active + " , Unknown response from vertec------>\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
        //if we receive "updated 1 object" then we were successful, else throw some error

    }

    public ResponseEntity<String> mergeOrganisations(Long mergingId, Long survivingId) throws IOException {
        VertecServerInfo.log.info("=============================== START MERGE FOR ID: " + mergingId + " INTO ID: " + survivingId + "===============================");

        File mergedIds = new File("mergedOrgs");
        PrintWriter out = new PrintWriter(new FileWriter(mergedIds, true));

        //Get Names of organisations from vertec for logging
        String mergingQuery = queryBuilder.getProjectsForOrganisation(mergingId);
        String survivingQuery = queryBuilder.getProjectsForOrganisation(survivingId);

        ProjectController projectController = new ProjectController(queryBuilder);
        ActivityController activityController = new ActivityController(queryBuilder);
        ContactController contactController = new ContactController(queryBuilder);


        final Document mergeResponse = responseFor(new RequestEntity<>(mergingQuery, HttpMethod.POST, vertecURI));
        final Document suviveResponse = responseFor(new RequestEntity<>(survivingQuery, HttpMethod.POST, vertecURI));

        String orgToMerge = getNameForOrganisationDocument(mergeResponse);
        String orgToSurvive = getNameForOrganisationDocument(suviveResponse);

        VertecServerInfo.log.info("Merging organisation: '" + orgToMerge + "' into '" + orgToSurvive + "'");
        //for the mergingOrg, get all projects, get all activities, get all contacts
        ResponseEntity<ActivitiesForAddressEntry> activityRes = getActivitiesForAddressEntry(mergingId);
        ResponseEntity<ProjectsForAddressEntry> projectRes = getProjectsForOrganisation(mergingId);
        ResponseEntity<ContactsForOrganisation> contactRes = getContactsForOrganisation(mergingId);
        //log that we plan on updating each of these to point to surviving org

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING PROJECTS =========================");

        projectRes.getBody().getProjects().forEach(project -> {
            VertecServerInfo.log.info("Updating Project name: " + project.getTitle() + ", Code: " + project.getCode() + " to be linked to Organisation ID: " + survivingId);

            //PUT Project
            projectController.setOrgLink(project.getV_id(), survivingId); //ONLY PRODUCES A LOG ATM
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING ACTIVITIES =========================");

        activityRes.getBody().getActivities().forEach(activity -> {
            VertecServerInfo.log.info("Updating Activity name: " + activity.getSubject() + ", Type: " + activity.getvType() + " , id " + activity.getVertecId() + " on: " + activity.getDoneDate() + activity.getDueDate() + " to be linked to Organisation ID: " + survivingId);
            //PUT Activity
            activityController.setOrgLink(activity.getVertecId(), survivingId); //ONLY PRODUCES A LOG ATM
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING CONTACTS =========================");

        contactRes.getBody().getContacts().forEach(contact -> {
            VertecServerInfo.log.info("Updating Contact name: " + contact.getFirstName() + " " + contact.getSurname() + " Email: " + (contact.getEmails().size() > 0 ? contact.getEmails().get(0).getValue() : "null") + " to be linked to Organisation ID: " + survivingId);
            //PUT contact
            contactController.setOrgLink(contact.getVertecId(), survivingId); //ONLY PRODUCES A LOG ATM

        });

        out.write(mergingId + "," + survivingId + "\n");

        out.close();

        //PUT org to inactive
        setActiveField(mergingId, false); //ONLY PRODUCES A LOG ATM

        return new ResponseEntity<>("Recieved call to merge organisation with id: " + mergingId + " into organisation with id: " + survivingId, HttpStatus.OK);
    }

    private ResponseEntity<String> update(Long id, Organisation organisation) throws NoIdSuppliedException {


        String query = queryBuilder.updateOrgansiation(organisation);
        //Temporary logging
        System.out.println(query);

        System.out.println("===========================================");


        String putResponse = callVertec(query,String.class);
        //need to assert Updates persist, as Vertec does not provide correct responses
        String assertionQuery = queryBuilder.getOrganisationDetails(Collections.singletonList(id));
        VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation xmlOrg = callVertec(assertionQuery,
                VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                .getBody().getQueryResponse().getOrganisationList().get(0);
        Organisation assertOrganisation = new Organisation(xmlOrg);


        if(assertOrganisation.equalsForUpdateAssertion(organisation))
            return new ResponseEntity<>("Success", HttpStatus.OK);

       else return new ResponseEntity<>("Not all updates have been applied\n"
                + organisation.toJsonString() + "\nResulted in:\n" + assertOrganisation.toJsonString()
        , HttpStatus.ACCEPTED);
    }

    private ResponseEntity<Long> createOrganisation(Organisation organisation) {
//get creation query
        String query = queryBuilder.createOrgansiation(organisation);
        //post org with name
        Document doc = responseFor(new RequestEntity<>(query, HttpMethod.POST, vertecURI));
        //check isvalid
               String valid = doc.getElementsByTagName("isValid").item(0).getTextContent();
        Boolean isValid = false;
        isValid = valid.equals("1");


        if(isValid){

            Long vId = Long.parseLong(doc.getElementsByTagName("objid").item(0).getTextContent());
            try {
                organisation.setVertecId(vId);
                ResponseEntity<String> updateSuccess = update(vId, organisation);
                if(updateSuccess.getStatusCode().equals(HttpStatus.OK)) return new ResponseEntity<>(vId, HttpStatus.CREATED); //All fields were created
                else if(updateSuccess.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                    System.out.println("ERROR:\n" + updateSuccess.getBody());
                }
                return new ResponseEntity<>(vId, HttpStatus.ACCEPTED); //For some reason update failed,
                // object has been created, but only name field is set
            } catch (NoIdSuppliedException e) {
                throw new HttpInternalServerError("Something went wrong during creation");
            }
        }
        else throw new HttpInternalServerError("Creation failed");

    }

//======================================================================================================================
// Helper Methods
//======================================================================================================================

    private List<Organisation> createOrganisationList(List<VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation> organisations) {
        return organisations.stream()
                .map(this::xml2json).collect(toList());
    }

    private Organisation xml2json(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation vo) {
        Organisation o = new Organisation(vo);
        setOwnedOnVertecByForOrganisation(o, vo);
        return o;
    }

    private void setOwnedOnVertecByForOrganisation(Organisation o, VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation vo) {
        Long responsibleId = vo.getPersonResponsible().getObjref();
        o.setOwnedOnVertecBy(getOwnedOnVertecByStringForOwnerId(responsibleId));
    }
}
