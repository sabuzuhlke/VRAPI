package VRAPI.ResourceControllers;

import VRAPI.Entities.Activity;
import VRAPI.Entities.Contact;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.JSONClasses.JSONContainerProject.JSONPhase;
import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.MergeClasses.ContactsForOrganisation;
import VRAPI.MergeClasses.ProjectsForOrganisation;
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
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@Scope("prototype") //This enforces that an organisation controller is created per request
public class OrganisationController extends Controller {

    private Map<Long, Long> supervisorIdMap;
    private Map<Long, String> activityTypeMap;

    public OrganisationController() {
        super();
    }

//======================================================================================================================
// PUT /organisations
//======================================================================================================================
    @ApiOperation(value = "Set Organisation to inactive", nickname = "activities")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Long> setInactive(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        if ( ! isIdOfType(id, "Firma")) {
            throw new HttpNotFoundException("Organisation with id: " + id + " does not exist");
        }
        String putQuery = queryBuilder.setOrganisationActive(false, id);
        //send put request to vertec
        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST,vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {
            return new ResponseEntity<>(id,HttpStatus.OK);
        } else {
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
        //if we receive "updated 1 object" then we were successful, else throw some error

    }
    @ApiOperation(value = "Set Organisation to active", nickname = "activities")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/activate", method = RequestMethod.PUT)
    public ResponseEntity<Long> setActive(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        if ( ! isIdOfType(id, "Firma")) {
            throw new HttpNotFoundException("Organisation with id: " + id + " does not exist");
        }
        String putQuery = queryBuilder.setOrganisationActive(true, id);
        //send put request to vertec
        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST,vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {
            return new ResponseEntity<>(id,HttpStatus.OK);
        } else {
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
        //if we receive "updated 1 object" then we were successful, else throw some error

    }

  //======================================================================================================================//
 // MERGE /organisations                                                                                                 //=
//======================================================================================================================//==
    @ApiOperation(value = "Merge two vertec organisations", nickname = "merge")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{mergingId}/mergeInto/{survivingId}", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<String> mergeOrganisations(@PathVariable Long mergingId, @PathVariable Long survivingId)
            throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();

        VertecServerInfo.log.info("=============================== START MERGE FOR ID: " + mergingId + " INTO ID: " + survivingId + "===============================");
        //Get Names of organisations from vertec for logging
        String mergingQuery = queryBuilder.getProjectsForOrganisation(mergingId);
        String survivingQuery = queryBuilder.getProjectsForOrganisation(survivingId);

        final Document mergeResponse = responseFor(new RequestEntity<>(mergingQuery, HttpMethod.POST, vertecURI));
        final Document suviveResponse = responseFor(new RequestEntity<>(survivingQuery, HttpMethod.POST, vertecURI));

        String orgToMerge = getNameForOrganisationDocument(mergeResponse);
        String orgToSurvive = getNameForOrganisationDocument(suviveResponse);

        VertecServerInfo.log.info("Merging organisation: '" + orgToMerge + "' into '" + orgToSurvive + "'");
        //for the mergingOrg, get all projects, get all activities, get all contacts
        ResponseEntity<ActivitiesForOrganisation> activityRes = getActivitiesForOrganisation(mergingId);
        ResponseEntity<ProjectsForOrganisation> projectRes = getProjectsForOrganisation(mergingId);
        ResponseEntity<ContactsForOrganisation> contactRes = getContactsForOrganisation(mergingId);
        //log that we plan on updating each of these to point to surviving org

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING PROJECTS =========================");

        projectRes.getBody().getProjects().forEach(project -> {
            VertecServerInfo.log.info("Updating Project name: " + project.getTitle() + ", Code: " + project.getCode() + " to be linked to Organisation ID: " + mergingId);

            //PUT Project --DONE
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING ACTIVITIES =========================");

        activityRes.getBody().getActivitiesForOrganisation().forEach(activity -> {
            VertecServerInfo.log.info("Updating Activity name: " + activity.getSubject() + ", Type: " + activity.getvType() + " , id" + activity.getVertecId()+ activity.getDoneDate() + activity.getDueDate() + " to be linked to Organisation ID: " + mergingId);
            //PUT Activity -- DONE
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING CONTACTS =========================");

        contactRes.getBody().getContacts().forEach(contact -> {
            VertecServerInfo.log.info("Updating Contact name: " + contact.getFirstName() + " " + contact.getSurname() + " Email: " + (contact.getEmails().size() > 0 ? contact.getEmails().get(0).getValue() : "null") + " to be linked to Organisation ID: " + mergingId);
            //PUT contact -- DONE now actually puts, so write new function to simply log

        });

        //PUT org to inactive -- DONE now actually puts, so write new function to simply log

        return new ResponseEntity<>("Recieved call to merge organisation with id: " + mergingId + " into organisation with id: " + survivingId, HttpStatus.OK);
    }


//======================================================================================================================
// GET /organisations
//======================================================================================================================


    //---------------------------------------------------------------------------------------------------------------------- /{id}/contacts
    @ApiOperation(value = "Get contacts for organisation", nickname = "contacts")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}/contacts", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<ContactsForOrganisation> getContactsForOrganisation(@PathVariable Long id) throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

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

    private List<Contact> getDetailedContacts(List<Long> contactIdsForOrg) {
        return getContacts(contactIdsForOrg).stream()
                .map(this::asContact)
                .collect(toList());
    }

    private Contact asContact(VRAPI.XMLClasses.ContainerDetailedContact.Contact contact) {
        Contact c = new Contact(contact);
        setOwnedOnVertecByForContact(c, contact);
        //TODO: set followers for contact here?
        return c;
    }

    private List<VRAPI.XMLClasses.ContainerDetailedContact.Contact> getContacts(List<Long> contactIdsForOrg) {
        //TODO: add support for recieving multiple contact details for contact
        return callVertec(
                queryBuilder.getContactDetails(contactIdsForOrg),
                VRAPI.XMLClasses.ContainerDetailedContact.Envelope.class).getBody().getQueryResponse().getContactList();
    }


    //---------------------------------------------------------------------------------------------------------------------- /{id}/projects
    @ApiOperation(value = "Get projects for organisation", nickname = "projects")
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
        queryBuilder = AuthenticateThenReturnQueryBuilder();

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
    @ApiOperation(value = "Get activities for organisation", nickname = "activities")
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
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        String xmlQuery = queryBuilder.getActivitiesForOrganisation(id);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        //Query vertec with organisation id for list of activities associated with it.
        List<Long> activityIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<Activity> activities = getActivityDetails(activityIdsForOrg);
        //Query vertec for details of each activity build response object
        ActivitiesForOrganisation res = new ActivitiesForOrganisation(id, organisationName);
        //set Activity list
        res.setActivitiesForOrganisation(activities);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private List<Activity> getActivityDetails(List<Long> activityIdsForOrg) {
        activityTypeMap = StaticMaps.INSTANCE.getActivityTypeMap();
        return getActivities(activityIdsForOrg).stream()
                .map(this::getJsonActivity).collect(toList());

    }

    private Activity getJsonActivity(VRAPI.XMLClasses.ContainerActivity.Activity activity) {
        Activity a = new Activity(activity);
        a.setvType(activityTypeMap.get(activity.getType() != null ? activity.getType().getObjref() : null));
        //Set Organisation link
        a.setVertecOrganisationLink(activity.getAddressEntry() != null ? activity.getAddressEntry().getObjref() : null);
        return a;
    }

    private List<VRAPI.XMLClasses.ContainerActivity.Activity> getActivities(List<Long> ids) {
        try{
            return callVertec(queryBuilder.getActivities(ids), VRAPI.XMLClasses.ContainerActivity.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getActivities();
        } catch (NullPointerException npe){
            throw new HttpNotFoundException("At leas one of the supplied Ids does not belong to an activity: " + ids);
        }
    }

    //====================================================================================================================== /all
    @ApiOperation(value = "Get all organisations owned by ZUK employees", nickname = "all")
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
        queryBuilder = AuthenticateThenReturnQueryBuilder();
        System.out.println("Authenticated request");
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
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisations/{ids}", method = RequestMethod.GET)
    public ResponseEntity<OrganisationList> getOrganisationList(@PathVariable List<Long> ids)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();


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
        queryBuilder = AuthenticateThenReturnQueryBuilder();


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

    /**
     * Returns the first 'name' field returned by vertec as String
     * @param response
     * @return
     */
    public String getNameForOrganisationDocument(Document response) {
        return response.getElementsByTagName("name").item(0).getTextContent();
    }

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

    private void setOwnedOnVertecByForContact(Contact c, VRAPI.XMLClasses.ContainerDetailedContact.Contact vc) {
        Long responsibleId = vc.getPersonResponsible().getObjref();
        c.setOwnedOnVertecBy(getOwnedOnVertecByStringForOwnerId(responsibleId));
    }

    private String getOwnedOnVertecByStringForOwnerId(Long ownerId) {
        Long supervisorId = supervisorIdMap.get(ownerId);
        Long SALES_TEAM_IDENTIFIER = -5L; //members of the top sales team, including wolfgang have their 'supervisorId' set to -5 within the map;
        if(ownerId == null) return "No Owner";

        if(supervisorId == null || supervisorId == 0L) return "Not ZUK"; // might be wrong

        if (supervisorId.longValue() == SALES_TEAM_IDENTIFIER) {
            return "Sales Team";
        } else {
            return "ZUK Sub Team";
        }
    }

}
