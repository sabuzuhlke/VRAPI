package com.example;

import VRAPI.Application;
import VRAPI.Entities.Contact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class ContactControllerTest extends ControllerTests {
    private Long TESTVertecContact = 28055069L;
    private Long TESTVertecOrganisation1 = 28055040L;
    private Long TESTVertecOrganisation2 = 28055047L;
    private Long TESTRandomID = 9542823859193471L; //this id does not exist on vertec

    /**
     * As other tests might modify the organisation link of the given contact,
     * this test has to put twice, to make sure that the orgLink changes
     */
    @Test
    public void canSetOrganisationLinks( ){

        String uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation1;

        Long orgId = putToVertec(uri, Long.class).getBody();

        assertEquals("Could not modify orglink",TESTVertecOrganisation1, orgId);

        uri = baseURI + "/contact/" + TESTVertecContact + "/setOrganisationLink/" + TESTVertecOrganisation2;
        orgId = putToVertec(uri,Long.class).getBody();

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

}
