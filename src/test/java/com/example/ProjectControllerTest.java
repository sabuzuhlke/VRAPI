package com.example;

import VRAPI.Application;
import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class ProjectControllerTest extends ControllerTests {


    @Test
    public void canUpdateOrgLinkOfProject() {
        Long projId = 1263652L; //Vodafone IT support -- acutally exists so careful

        String uri1 = baseURI + "/projects/" + projId;
        JSONProject project = getFromVertec(uri1, JSONProject.class).getBody();

        assertEquals(projId, project.getV_id());

        Long orgId = project.getClientRef();
        System.out.println("Clientref of project: " + orgId);

        String uri = baseURI + "/project/" + projId + "/setOrganisationLink/" + TESTVertecOrganisation1; // re-point to test organisation
        Long res = putToVertec(uri, Long.class).getBody();

        project = getFromVertec(uri1, JSONProject.class).getBody();
        assertEquals("OrganisationLink did not get set", TESTVertecOrganisation1, project.getClientRef()); //assert function works

        uri = baseURI + "/project/" + projId + "/setOrganisationLink/" + orgId; //re-point to original organisation, to conserver state of project
        res = putToVertec(uri, Long.class).getBody();

        project = getFromVertec(uri1, JSONProject.class).getBody();
        assertEquals("OrganisationLink did not get set back to original value", orgId, project.getClientRef()); //assert that organisation has been set back to its original state

    }

    @Test
    public void doesNotSetOrganisationLinkOfNonProject() {
        try {
            String uri = baseURI + "/project/" + TESTRandomID + "/setOrganisationLink/" + TESTVertecOrganisation1;
            Long orgId = putToVertec(uri, Long.class).getBody();
            TestCase.assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception) {
            TestCase.assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void doesNotSetOrganisationLinkToNonOrganisation() {
        Long projId = 1263652L;
        try {
            String uri = baseURI + "/project/" + projId + "/setOrganisationLink/" + TESTRandomID;
            Long orgId = putToVertec(uri, Long.class).getBody();
            TestCase.assertTrue("No not found exception thrown", false);
        } catch (HttpClientErrorException exception) {
            TestCase.assertEquals(exception.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

}
