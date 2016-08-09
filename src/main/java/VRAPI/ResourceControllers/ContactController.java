package VRAPI.ResourceControllers;


import VRAPI.Entities.*;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.MergeClasses.ActivitiesForAddressEntry;
import VRAPI.Util.QueryBuilder;
import VRAPI.Util.StaticMaps;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerDetailedContact.Envelope;
import VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;
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
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;


@RestController
@Scope("prototype")
public class ContactController extends Controller {

    public ContactController() {
        super();
    }

    public ContactController(QueryBuilder queryBuilder) {
        super(queryBuilder);
    }

    //======================================================================================================================//
    // GET /contact                                                                                                         //
//======================================================================================================================//
    @ApiOperation(value = "Get a list of contacts", nickname = "Contact List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{ids}", method = RequestMethod.GET)
    public ResponseEntity<ContactList> getContactListEndpoint(@PathVariable List<Long> ids) throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return new ResponseEntity<>(getContactList(ids), HttpStatus.OK);
    }

    @ApiOperation(value = "Get activities for contact", nickname = "activities")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{id}/activities", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<ActivitiesForAddressEntry> getActivitiesForActivityEndpoint(@PathVariable Long id)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return getActivitiesForAddressEntry(id);
    }


    //======================================================================================================================//
    // PUT /contact                                                                                                         //
//======================================================================================================================//

    /**
     * @return: Id returned is that of the organisation the contact is now linked to
     */
    @ApiOperation(value = "Change the organisation the contact works at", nickname = "Organisation Link")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{id}/setOrganisationLink/{orgID}", method = RequestMethod.PUT)
    public ResponseEntity<Long> setOrganisationLinkEndpoint(@PathVariable Long id, @PathVariable Long orgID)
            throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return setOrgLink(id, orgID);
    }

    @ApiOperation(value = "Merge two vertec Contacts", nickname = "merge")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{mergingId}/mergeInto/{survivingId}", method = RequestMethod.PUT)
    public ResponseEntity<String> mergeContactsEndpoint(@PathVariable Long mergingId, @PathVariable Long survivingId)
            throws ParserConfigurationException, IOException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();

        return null;
        //return mergeContacts(mergingId, survivingId);
    }

    @ApiOperation(value = "Set Contact to inactive", nickname = "activities")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{id}", method = RequestMethod.DELETE)

    public ResponseEntity<Long> setInactiveEndpoint(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return setActiveField(id, false);

    }

    @ApiOperation(value = "Set Contact to active", nickname = "activities")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{id}/activate", method = RequestMethod.PUT)
    public ResponseEntity<Long> setActiveEndpoint(@PathVariable Long id) throws ParserConfigurationException {

        queryBuilder = AuthenticateThenReturnQueryBuilder();
        return setActiveField(id, true);
    }


//=======================================METHODS========================================================================

//======================================================================================================================

    //TODO merge Organisation pointed out in tomerge.txt
    public ResponseEntity<String> mergeContacts(Long mergingId, Long survivingId) throws IOException {

        if (!isIdOfType(mergingId, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + mergingId + " does not exist");
        }
        if (!isIdOfType(survivingId, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + survivingId + " does not exist");
        }

        VRAPI.Entities.Contact mergingCont = getContactList(singletonList(mergingId)).getContacts().get(0);
        VRAPI.Entities.Contact survivingContact = getContactList(singletonList(survivingId)).getContacts().get(0);

        VertecServerInfo.log.info("=============================== START MERGE FOR CONTACT  " + mergingCont.getFullName() + "(v_id: " + mergingCont.getVertecId()
                + ") INTO : " + survivingContact.getFullName() + "(v_id: " + survivingContact.getVertecId() + "===============================");


        //Set Activity Links
        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING ACTIVITIES =========================");
        ActivityController activityController = new ActivityController(queryBuilder);
        ActivitiesForAddressEntry afa = activityController.getActivitiesForAddressEntry(mergingId).getBody();
        for (Activity a : afa.getActivities()) {
            VertecServerInfo.log.info("------------------------------------------------------>");
            VertecServerInfo.log.info("Setting Activity(v_id: " + a.getVertecId() + ") to point to contact " + survivingId
                    + " (used to point to: " + mergingCont.getVertecId() + ")");
            try {

                activityController.setContactLink(a.getVertecId(), survivingId);
                VertecServerInfo.log.info("--------------------SUCCESS--------------------------->\n");
            } catch (Exception e) {
                VertecServerInfo.log.info("FAILURE:: " + e.toString());
            }
        }

        //set Followers
        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING LINKS =========================");
        //setGenericLink containers
        List<Long> genericContainersIds = mergingCont.getGenericContainers();
        VertecServerInfo.log.info("About to repoint from Contact " + mergingCont.getFullName() + "(v_id: " + mergingCont.getVertecId()
                + ") to " + survivingContact.getFullName() + "(v_id: " + survivingContact.getVertecId() +
                ") the following genericContainers: " + genericContainersIds);

        try {

            setFromContainerOfGLC(survivingId, genericContainersIds);
            VertecServerInfo.log.info("--------------------SUCCESS--------------------------->\n");

        } catch (Exception e) {
            VertecServerInfo.log.info("FAILURE:: " + e.toString());
        }

        List<Long> fromLinks = mergingCont.getFromLinks();

        VertecServerInfo.log.info("About to repoint from Contact " + mergingCont.getFullName() + "(v_id: " + mergingCont.getVertecId()
                + ") to " + survivingContact.getFullName() + "(v_id: " + survivingContact.getVertecId() +
                ") the following fromLinks: " + fromLinks);

        try {
            replaceLinks(survivingId, mergingId, getGenericLinkContainers(fromLinks));
            VertecServerInfo.log.info("--------------------SUCCESS--------------------------->\n");

        } catch (Exception e) {
            VertecServerInfo.log.info("FAILURE:: " + e.toString());
        }


        try{

        //Log in log and to file
        File mergedIds = new File("mergedConts");
        PrintWriter out = new PrintWriter(new FileWriter(mergedIds, true));
        out.write(mergingId + "," + survivingId + "\n");
        out.close();
        } catch (Exception e) {
            System.out.println(" Caught exception tryin to write to file: " + e.getMessage());
        }

        //Set merged Contact ot inactive
        try {
            VertecServerInfo.log.info("------------------------------------------------------>");
            VertecServerInfo.log.info("Setting contact " + mergingCont.getFullName() + "(v_id: " + mergingId + ") to inactive");

            setActiveField(mergingId, false);

            VertecServerInfo.log.info("--------------------SUCCESS--------------------------->\n");
        } catch (Exception e) {
            VertecServerInfo.log.info("FAILURE:: " + e.toString());
        }

        VertecServerInfo.log.info("===================================================================================\n\n");
        return null;
    }

    public ResponseEntity<Long> setActiveField(Long contId, boolean active) {
        if (!isIdOfType(contId, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + contId + " does not exist");
        }
        String putQuery = queryBuilder.setContactActive(active, contId);
        //send put request to vertec

        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {
            return new ResponseEntity<>(contId, HttpStatus.OK);
        } else {
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
    }

    public ResponseEntity<Long> setOrgLink(Long id, Long orgID) {
        VertecServerInfo.log.info("--------------- Setting Organisation Link of Contact ---------------------------->");

        if (!isIdOfType(id, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + id + " does not exist");
        }
        if (!isIdOfType(orgID, "Firma")) {
            throw new HttpNotFoundException("Organisation with id" + orgID + " does not exist");
        }

        String xmlQuery = queryBuilder.setContactOrganisationLink(id, orgID);

        //Get contact first
        VRAPI.XMLClasses.ContainerDetailedContact.Contact contact = callVertec(queryBuilder.getDetailedContact(singletonList(id))
                , Envelope.class)
                .getBody().getQueryResponse().getContactList().get(0); //TODO see whether refactor is possible

        Organisation organisation = callVertec(queryBuilder.getOrganisationDetails(singletonList(orgID))
                , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                .getBody().getQueryResponse().getOrganisationList().get(0);

        VertecServerInfo.log.info("Request seems OK, about to re-point contact " + contact.getFirstName() + " "
                + contact.getSurnname() + "(v_id: " + contact.getObjId() + ")" +
                " to Organisation " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")");

        if (contact.getOrganisation() != null) {
            Long clientref = contact.getOrganisation().getObjref();
            if (isIdOfType(clientref, "Firma")) {
                Organisation org = callVertec(queryBuilder.getOrganisationDetails(singletonList(clientref))
                        , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                        .getBody().getQueryResponse().getOrganisationList().get(0);


                VertecServerInfo.log.info("Clientref pointed to Organisation " + org.getName() +
                        "(v_id: " + org.getObjId() + ") this would be overwritten" +
                        " to point to Organisation: " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")!!");
            }
        }
        Document res = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {

            VertecServerInfo.log.info("Contact now works at to Organisation: " + orgID);
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            return new ResponseEntity<>(orgID, HttpStatus.OK);

        } else {
            VertecServerInfo.log.info("Failed to make contact change Organisation, Unknown response from Vertec");
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }

    }


    public ContactDetails getContactDetails(List<Long> kommMittel) {

        String query = queryBuilder.getContactMediumDetails(kommMittel);
        List<VRAPI.XMLClasses.ContainerContactDetails.KommMittel> kommMittelList =
                callVertec(query, VRAPI.XMLClasses.ContainerContactDetails.Envelope.class)
                        .getBody()
                        .getQueryResponse()
                        .getContactDetails();

        ContactDetails details = new ContactDetails();
        kommMittelList.forEach(detail -> {

            ContactDetail cd = new ContactDetail();
            cd.setPrimary(detail.getPriority());
            cd.setValue(detail.getValue());

            if (detail.getTyp().getObjref() == 6L) {
                cd.setLabel("Phone");
                details.getPhones().add(cd);
            }
            if (detail.getTyp().getObjref() == 7L) {
                cd.setLabel("Mobile");
                details.getPhones().add(cd);
            }
            if (detail.getTyp().getObjref() == 9L) {
                cd.setLabel("Email");
                details.getEmails().add(cd);
            }
        });

        return details;
    }

    public ContactList getContactList(List<Long> ids) {

        VRAPI.XMLClasses.ContainerDetailedContact.Envelope contactEnvelope
                = callVertec(queryBuilder.getDetailedContact(ids),
                VRAPI.XMLClasses.ContainerDetailedContact.Envelope.class);

        if (contactEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Some or all of the ids requested are not contacts");
        }

        this.supervisorIdMap = StaticMaps.INSTANCE.getSupervisorMap();
        ContactList res = new ContactList();
        res.setContacts(contactEnvelope.getBody().getQueryResponse().getContactList().stream().map(super::asContact).collect(toList()));

        return res;

    }

    /**
     * Gerneric Link Containers are a Superset of Followers
     * ---> This is the first half of the necessary actions for dealing with Generic Link Containers
     * /fromContainer/
     */
    public ResponseEntity<Long> repointFollowedGenericLinkContainers(Contact merging, Contact survivor) {
        //get GenericContainers of contact
        List<GenericLinkContainer> mergingContainers = getGenericLinkContainers(merging.getFromLinks());
        //set  fromContainer to new Id
        List<Long> glcids = mergingContainers.stream()
                .map(GenericLinkContainer::getObjid)
                .collect(toList());

        return setFromContainerOfGLC(survivor.getVertecId(), glcids);
    }

    public ResponseEntity<Long> repointFromLinksOfGenericLinkContainers(Contact merging, Contact survivor) {
        //get fromLinks of merging contact
        List<GenericLinkContainer> mergingContainers = getGenericLinkContainers(merging.getGenericContainers());

        return replaceLinks(survivor.getVertecId(), merging.getVertecId(), mergingContainers);

    }

//
//    /**
//     * Gerneric Link Containers are a Superset of Followers
//     * ---> This is the second half of the necessary actions for dealing with Generic Link Containers
//     * /links/
//     */
//    public void repointFollowingGenericLinkContainers(Contact merging, Long survivor){
//        //TODO research how these Containers are Linked to the Contact
//    }


}
