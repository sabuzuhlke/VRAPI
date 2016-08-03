package com.example;

import VRAPI.Application;
import VRAPI.JSONClasses.JSONContainerActivities.JSONActivitiesResponse;
import VRAPI.JSONClasses.JSONContainerActivities.JSONActivity;
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
public class ActivityControllerTest extends ControllerTests {

    @Test
    public void canUpdateActivityOrgLink(){
        Long activityID = 1085719L; //Actually exists-- careful

        String uri1 = baseURI + "/activities/" + activityID + "/noFilter";

        JSONActivity activity = getFromVertec(uri1,JSONActivitiesResponse.class).getBody().getActivities().get(0);

        Long orgId = activity.getCustomer_link();
        System.out.println("CustomerRef of activity: " + orgId);

        String uri = baseURI + "/activity/" + activityID + "/setOrganisationLink/" + TESTVertecOrganisation1; // re-point to test organisation
        Long res = putToVertec(uri,Long.class).getBody();

        activity = getFromVertec(uri1,JSONActivitiesResponse.class).getBody().getActivities().get(0);
        assertEquals("Did not set organisationLink", TESTVertecOrganisation1, activity.getCustomer_link()); //assert function works

        uri = baseURI + "/activity/" + activityID + "/setOrganisationLink/" + orgId; //re-point to original organisation, to conserver state of project
        res = putToVertec(uri,Long.class).getBody();

        activity = getFromVertec(uri1,JSONActivitiesResponse.class).getBody().getActivities().get(0);
        assertEquals("Did not set organisationLink back to original", orgId, activity.getCustomer_link()); //assert organisationLink set back to original -- so as to kkep sate of activity unmodified

    }
    @Test
    public void doesNotSetOrganisationLinksOfNonActivity(){
        try{
            String uri = baseURI + "/activity/" + TESTRandomID + "/setOrganisationLink/" + TESTVertecOrganisation1;
            Long orgId = putToVertec(uri,Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception){
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }

    @Test
    public  void setOrganisationLinksDoesNotSetNonOrgLink(){
        Long activityID = 1085719L; //Actually exists-- careful
        try{
            String uri = baseURI + "/activity/" + activityID + "/setOrganisationLink/" + TESTRandomID;
            Long orgId = putToVertec(uri,Long.class).getBody();
            assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception){
            assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }

    @Test
    public void canGetAllActivitiesForContact(){

    }
}
