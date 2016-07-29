package com.example;

import VRAPI.Application;
import VRAPI.Entities.ContactDetails;
import VRAPI.JSONClasses.JSONContainerOrganisation.JSONContact;
import VRAPI.Keys.TestVertecKeys;
import VRAPI.ResourceControllers.ContactController;
import VRAPI.Util.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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
    @Test
    public void canSetOrganisationLinks( ){



        String uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation1;

        Long orgId = putToVertec(uri, Long.class).getBody();

        String uri1 = baseURI + "/contact/" + TESTVertecContact;
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
        try{
            String uri = baseURI + "/contact/" + TESTRandomID + "/setOrganisationLink/" + TESTVertecOrganisation1;
            Long orgId = putToVertec(uri,Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception){
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }

    @Test
    public  void setOrganisationLinksDoesNotSetNonOrgLink(){
        try{
            String uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTRandomID;
            Long orgId = putToVertec(uri,Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception){
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void canAllContactDetailsForContact(){
        Long contact = 13111329L; //a contact on vertec - should find a better one to test (one that has more than 3 kommmittels ideally

        ContactDetails km = contactController.getContactDetailsForContact(contact);

        assertEquals(1, km.getEmails().size());
        assertEquals(1, km.getPhones().size());
        assertEquals(1, km.getMobiles().size());

        assertTrue(km.getEmails().get(0).getValue().equals("stuart.mills@laterooms.com"));
        assertTrue(km.getPhones().get(0).getValue().equals("+44 161 650 1356"));
        assertTrue(km.getMobiles().get(0).getValue().equals("+44 7432 717173"));

    }

}
