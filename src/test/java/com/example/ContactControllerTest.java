package com.example;

import VRAPI.Entities.Contact;
import VRAPI.Entities.ContactDetails;
import VRAPI.Entities.ContactList;
import VRAPI.JSONClasses.JSONContainerOrganisation.JSONContact;
import VRAPI.Keys.TestVertecKeys;
import VRAPI.ResourceControllers.ContactController;
import VRAPI.Util.QueryBuilder;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class ContactControllerTest extends ControllerTests {

    private ContactController contactController;

    /**
     * As other tests might modify the organisation link of the given contact,
     * this test has to put twice, to make sure that the orgLink changes
     */
    @Before
    public void setup() {
        QueryBuilder queryBuilder = new QueryBuilder(TestVertecKeys.usr, TestVertecKeys.pwd);
        this.contactController = new ContactController(queryBuilder);
    }

    @Test @Ignore("Used as a convenient way of running this body of code")
    public void deleteContacts(){
        List<Long> idsToDel = new ArrayList<>();

        //Anthony Brigginshaw
        idsToDel.add(736855L);
        //Stella Bryant
        idsToDel.add(862588L);
        idsToDel.add(13822657L); //Mark L Busson
        idsToDel.add(18892337L); //Chetan Chomber
        idsToDel.add(788298L); //Ila Neustadt
        idsToDel.add(859691L); //Toby King
        idsToDel.add(20729464L); //Jurgen gainz
        idsToDel.add(22341015L); //Magnus Falk
        idsToDel.add(15315419L); //Paul Krisman
        idsToDel.add(15315645L);
        idsToDel.add(15639865L);
        idsToDel.add(16517417L);
        idsToDel.add(17092298L);
        idsToDel.add(12597026L);
        idsToDel.add(11399332L);
        idsToDel.add(7878333L);
        idsToDel.add(8807762L);
        idsToDel.add(9039509L);
        idsToDel.add(9662175L);
        idsToDel.add(9662101L);
        idsToDel.add(20063881L);

        for(Long id : idsToDel){
            contactController.setActiveField(id,false);
        }
    }

    @Test @Ignore("Used as a convenient way of running the contact merging")
    public void mergeContacts() throws IOException {
        List<Long> toMerge = new ArrayList<>();
        List<Long> survivors = new ArrayList<>();

        //Damien Charles
        toMerge.add(24069157L);
        survivors.add(23471089L);

        //Martin Carpenter
        toMerge.add(24093304L);
        survivors.add(23464579L);
        //Guido Mengelkamp
        toMerge.add(19440283L);
        survivors.add(20067417L);
        //Duncan Robins
        toMerge.add(10741596L);
        survivors.add(10703664L);
        //Hayden Ian
        toMerge.add(11860698L);
        survivors.add(12005407L);
        //Rauli Hantikainen
        toMerge.add(12526150L);
        survivors.add(16027111L);
        //Gaelle Aotore
        toMerge.add(19534447L);
        survivors.add(19534537L);
        //Ian Spencer
        toMerge.add(25892964L);
        survivors.add(13030700L);

        for(int i = 0; i < toMerge.size(); i ++){
            contactController.mergeContacts(toMerge.get(i), survivors.get(i));
        }


    }

    //======================================================================================================================//
    // GET /contact                                                                                                         //
//======================================================================================================================//

    @Test
    public void canGetContactById() {
        String uri = baseURI + "/contact/" + TESTVertecContact;

        Contact contactRecieved = getFromVertec(uri, ContactList.class).getBody().getContacts().get(0);

        assertEquals("Wrong contact recieved", TESTVertecContact, contactRecieved.getVertecId());
        assertNotNull(contactRecieved.getActive());
        assertNotNull(contactRecieved.getCreationTime());
        assertNotNull(contactRecieved.getModifiedTime());
        assertNotNull(contactRecieved.getOwnedOnVertecBy());
        assertNotNull(contactRecieved.getOwnerId());
        assertNotNull(contactRecieved.getSurname());
        assertNotNull(contactRecieved.getFirstName());
        assertNotNull(contactRecieved.getEmails());
        assertNotNull(contactRecieved.getPhones());
        assertNotNull(contactRecieved.getPosition());

    }

    @Test
    public void canNotGetContactByInvalidId() {
        try {
            String uri = baseURI + "/contact/10934085";
            getFromVertec(uri, Contact.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong status code returned", HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void canGetListOfContactByIds() {
        String uri = baseURI + "/contact/" + idsAsString(Arrays.asList(TESTVertecContact, 28055081L));
        ContactList contacts = getFromVertec(uri, ContactList.class).getBody();

        contacts.getContacts().forEach(contactRecieved -> {
            assertNotNull(contactRecieved.getActive());
            assertNotNull(contactRecieved.getCreationTime());
            assertNotNull(contactRecieved.getModifiedTime());
            assertNotNull(contactRecieved.getOwnedOnVertecBy());
            assertNotNull(contactRecieved.getOwnerId());
            assertNotNull(contactRecieved.getSurname());
            assertNotNull(contactRecieved.getFirstName());
            assertNotNull(contactRecieved.getEmails());
            assertNotNull(contactRecieved.getPhones());
            assertNotNull(contactRecieved.getPosition());
        });
    }

    @Test
    public void canNotGetListOfContactsIfAnyAreInvalid() {
        try {
            String uri = baseURI + "/contact/" + idsAsString(Arrays.asList(TESTVertecContact, 280550821L, 1287245L));
            ContactList cl = getFromVertec(uri, ContactList.class).getBody();
            assertEquals(1, cl.getContacts().size());
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong status code returned", HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    //======================================================================================================================//
    // PUT /contact                                                                                                         //
//======================================================================================================================//

    @Test @Ignore("Only to run on test vertec instance")
    public void canSetOrganisationLinks() {
        String uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation1;

        Long orgId = putToVertec(uri, Long.class).getBody();

        String uri1 = baseURI + "/oldcontact/" + TESTVertecContact;
        JSONContact contact = getFromVertec(uri1, JSONContact.class).getBody();
        assertEquals("Did not set organisationLink", TESTVertecOrganisation1, contact.getOrganisation());

        assertEquals("Could not modify orglink", TESTVertecOrganisation1, orgId);

        uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation2;
        orgId = putToVertec(uri, Long.class).getBody();


        contact = getFromVertec(uri1, JSONContact.class).getBody();
        assertEquals("Did not set organisationLink back to what it was", TESTVertecOrganisation2, contact.getOrganisation());

        assertEquals("Could not modify orglink", TESTVertecOrganisation2, orgId);
    }

    @Test
    public void doesNotSetOrganisationLinksOfNonContact() {
        tryRequestWithId(TESTRandomID);
    }

    @Test @Ignore("Only to run on test vertec instance")
    public void setOrganisationLinksDoesNotSetNonOrgLink() {
        tryRequestWithId(TESTVertecContact);
    }

    public void tryRequestWithId(Long id) {
        try {
            String uri = baseURI + "/contact/" + id + "/setOrganisationLink/" + TESTRandomID;
            Long orgId = putToVertec(uri, Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception) {
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void canContactDetails() {
        Long contact = 13111339L; //a contact on vertec - should find a better one to test (one that has more than 3 kommmittels ideally
        List<Long> kommMittel = new ArrayList<>();
        kommMittel.add(13111348L);
        kommMittel.add(13111351L);
        kommMittel.add(13111352L);

        ContactDetails km = contactController.getContactDetails(kommMittel);

        assertEquals(1, km.getEmails().size());
        assertEquals(2, km.getPhones().size());

        assertTrue(km.getEmails().get(0).getValue().equals("stuart.mills@laterooms.com"));
        assertTrue(km.getPhones().get(0).getValue().equals("+44 161 650 1356"));
        assertTrue(km.getPhones().get(0).getLabel().equals("Phone"));
        assertTrue(km.getPhones().get(1).getValue().equals("+44 7432 717173"));
        assertTrue(km.getPhones().get(1).getLabel().equals("Mobile"));
    }

    @Test @Ignore("Only to run on test vertec instance")
    public void canSetContactToActiveAndInactive() {

        String uri = baseURI + "/contact/" + TESTVertecContact + "/activate";

        Long id = putToVertec(uri, Long.class).getBody();

        Assert.assertEquals("Could not activate Contact before setting it to inactive again!", TESTVertecContact, id);

        uri = baseURI + "/contact/" + TESTVertecContact;

        Contact contact = getFromVertec(uri, ContactList.class).getBody().getContacts().get(0);

        Assert.assertTrue("Contact did not get set to active", contact.getActive());

        id = 0L;

        id = deleteFromVertec(uri, Long.class).getBody();

        Assert.assertEquals("Could not deactivate Contact", TESTVertecContact, id);

        contact = getFromVertec(uri, ContactList.class).getBody().getContacts().get(0);

        assertFalse("Contact did not get set to inactive", contact.getActive());

    }

    @Test @Ignore("Only to run on test vertec instance")
    public void cannotSetRandomIdToActive() {
        Long id = TESTRandomID;
        String uri = baseURI + "/contact/" + id + "/activate";

        try {

            id = putToVertec(uri, Long.class).getBody();
            Assert.assertTrue("Found Contact with random id", false);
        } catch (HttpStatusCodeException e) {
            Assert.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }

    }

    @Test @Ignore("Only to run on test vertec instance")
    public void cannotSetRandomIdToInactive() {
        Long id = TESTRandomID;
        String uri = baseURI + "/contact/" + id;

        try {

            id = deleteFromVertec(uri, Long.class).getBody();
            Assert.assertTrue("Found Contact with random id", false);
        } catch (HttpStatusCodeException e) {
            Assert.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }


    }

    @Test @Ignore("Only to run on test vertec instance")
    public void canSetfromContainers() {
        List<Long> glcIds = Arrays.asList(958149L, 8110400L, 20066431L);

        List<Long> originalgenericContainers = contactController.getGenericLinkContainers(glcIds).stream()
                .map(glc -> glc.getFromContainer().getObjref())
                .collect(toList());

        Long survivor = TESTVertecContact;

        //set all glcs to point to test contact
        Long newID = contactController.setFromContainerOfGLC(survivor, glcIds).getBody();

        assertEquals("setgenericContainers did not return correctly", TESTVertecContact, newID);

        //check whether changes have been applied
        List<Long> newgenericContainers = contactController.getGenericLinkContainers(glcIds).stream()
                .map(glc -> glc.getFromContainer().getObjref())
                .collect(toList());

        for (Long id : newgenericContainers) {
            assertEquals("One of the ids didnt get set", TESTVertecContact, id);
        }

        //set links of glc-s back to original based on original genericContainers one-by-one
        for (int i = 0; i < glcIds.size(); i++) {

            Long id1 = 0L;
            id1 = contactController.setFromContainerOfGLC(originalgenericContainers.get(i), Collections.singletonList(glcIds.get(i))).getBody();
            assertEquals("set fromlinck back to original did not return correctly", originalgenericContainers.get(i), id1);
        }

        //check that they have been set back to the original ones
        newgenericContainers = contactController.getGenericLinkContainers(glcIds).stream()
                .map(glc -> glc.getFromContainer().getObjref())
                .collect(toList());

        assertEquals("Not all links have been returned", glcIds.size(), newgenericContainers.size());

        for (int j = 0; j < newgenericContainers.size(); j++) {
            assertEquals("Did not set link" + j + " back to original", originalgenericContainers.get(j), newgenericContainers.get(j));
        }
    }


    @Test @Ignore("Only to run on test vertec instance")
    public void canSeLinks() {
        Long newId = TESTVertecContact;
        Long oldId = 1881841L; //actual contact on vertec take care

        List<Long> glcIds = Arrays.asList(1807337L, 8662018L); //these glcs are linked to oldId
        List<GenericLinkContainer> glcs = contactController.getGenericLinkContainers(glcIds);

        //set links to point to surviving contact
        Long id = contactController.replaceLinks(newId, oldId, glcs).getBody();

        assertEquals("replaceLinks did not return correctly", newId, id);

        List<GenericLinkContainer> updatedGLCs = contactController.getGenericLinkContainers(glcIds);

        //check its been done
        for (GenericLinkContainer glc : updatedGLCs) {
            assertTrue("Did not update new link", glc.getLinks().getObjlist().getObjref().contains(newId));
        }

        //reset to point to original
        Long revertedId = contactController.replaceLinks(oldId, newId, updatedGLCs).getBody();

        assertEquals("Resetting replaceLinks did not return correctly", oldId, revertedId);

        List<GenericLinkContainer> revertedGLCs = contactController.getGenericLinkContainers(glcIds);

        //check it worked
        for (GenericLinkContainer glc : revertedGLCs) {
            assertTrue("Reseting did not update new link", glc.getLinks().getObjlist().getObjref().contains(oldId));
        }


    }


}
