package com.example;

import VRAPI.Application;
import VRAPI.Entities.Contact;
import VRAPI.Entities.ContactDetails;
import VRAPI.Entities.ContactList;
import VRAPI.Entities.Organisation;
import VRAPI.JSONClasses.JSONContainerOrganisation.JSONContact;
import VRAPI.Keys.TestVertecKeys;
import VRAPI.ResourceControllers.ContactController;
import VRAPI.Util.QueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class ContactControllerTest extends ControllerTests {

    private ContactController contactController;
    /**
     * As other tests might modify the organisation link of the given contact,
     * this test has to put twice, to make sure that the orgLink changes
     */
    @Before
    public void setup(){
        QueryBuilder queryBuilder = new QueryBuilder(TestVertecKeys.usr, TestVertecKeys.pwd);
        this.contactController = new ContactController(queryBuilder);
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
            getFromVertec(uri, Contact.class);
            assertTrue(false);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong status code returned", HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

  //======================================================================================================================//
 // PUT /contact                                                                                                         //
//======================================================================================================================//

    @Test
    public void canSetOrganisationLinks( ){
        String uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation1;

        Long orgId = putToVertec(uri, Long.class).getBody();

        String uri1 = baseURI + "/oldcontact/" +  TESTVertecContact;
        JSONContact contact = getFromVertec(uri1,JSONContact.class).getBody();
        assertEquals("Did not set organisationLink", TESTVertecOrganisation1, contact.getOrganisation());

        assertEquals("Could not modify orglink",TESTVertecOrganisation1, orgId);

        uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation2;
        orgId = putToVertec(uri,Long.class).getBody();


        contact = getFromVertec(uri1,JSONContact.class).getBody();
        assertEquals("Did not set organisationLink back to what it was", TESTVertecOrganisation2, contact.getOrganisation());

        assertEquals("Could not modify orglink",TESTVertecOrganisation2, orgId);
    }

    @Test
    public void doesNotSetOrganisationLinksOfNonContact(){
        tryRequestWithId(TESTRandomID);
    }

    @Test
    public  void setOrganisationLinksDoesNotSetNonOrgLink(){
        tryRequestWithId(TESTVertecContact);
    }

    public void tryRequestWithId(Long id) {
        try{
            String uri = baseURI + "/contact/" + id + "/setOrganisationLink/" + TESTRandomID;
            Long orgId = putToVertec(uri,Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception){
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void canContactDetails(){
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

    @Test
    public void canSetContactToActiveAndInactive() {

        String uri = baseURI + "/contact/" + TESTVertecContact + "/activate";

        Long id = putToVertec(uri, Long.class).getBody();

        Assert.assertEquals("Could not activate Contact before setting it to inactive again!", TESTVertecContact, id);

        uri = baseURI + "/contact/" + TESTVertecContact;

        Contact contact = getFromVertec(uri,ContactList.class).getBody().getContacts().get(0);

        Assert.assertTrue("Contact did not get set to active",contact.getActive());

        id = 0L;

        id =  deleteFromVertec(uri, Long.class).getBody();

        Assert.assertEquals("Could not deactivate Contact", TESTVertecContact, id);

        contact = getFromVertec(uri,ContactList.class).getBody().getContacts().get(0);

        assertFalse("Contact did not get set to inactive",contact.getActive());

    }

    @Test
    public void cannotSetRandomIdToActive(){
        Long id = TESTRandomID;
        String uri = baseURI + "/contact/" + id + "/activate";

        try{

            id = putToVertec(uri, Long.class).getBody();
            Assert.assertTrue("Found Contact with random id",false);
        } catch (HttpStatusCodeException e){
            Assert.assertEquals( HttpStatus.NOT_FOUND, e.getStatusCode());
        }

    }

    @Test
    public void cannotSetRandomIdToInactive(){
        Long id = TESTRandomID;
        String uri = baseURI + "/contact/" + id;

        try{

            id = deleteFromVertec(uri, Long.class).getBody();
            Assert.assertTrue("Found Contact with random id",false);
        } catch (HttpStatusCodeException e){
            Assert.assertEquals( HttpStatus.NOT_FOUND, e.getStatusCode());
        }


    }



}
