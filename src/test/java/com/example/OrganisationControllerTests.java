package com.example;

import VRAPI.Application;
import VRAPI.Entities.Activity;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.JSONClasses.JSONContainerProject.JSONPhase;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.MergeClasses.ProjectsForOrganisation;
import VRAPI.ResourceControllers.OrganisationController;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class OrganisationControllerTests extends ControllerTests {
    

    @Test @Ignore("Unnecessary ATM")
    public void callingMergeOnTwoIdsWillShowAGoodLog() {

        Long id1 = 711840L;
        Long id2 = 10867689L;

        String uri = baseURI + "/organisation/" + id1 + "/mergeInto/" + id2;
        ResponseEntity<String> res = getFromVertec(uri, String.class);

        System.out.println(res.getBody());

    }

    @Test
    public void canSetOrgToActiveAndInactive() {

        String uri = baseURI + "/organisation/" + TESTVertecOrganisation1 + "/activate";

        Long id = putToVertec(uri, Long.class).getBody();

        assertEquals("Could not activate organisation before setting it to inactive again!", TESTVertecOrganisation1, id);

        uri = baseURI + "/organisation/" + TESTVertecOrganisation1;

        Organisation org = getFromVertec(uri,Organisation.class).getBody();

        assertTrue("Organisation did not get set to active",org.getActive());

        id = 0L;

        id =  deleteFromVertec(uri, Long.class).getBody();

        assertEquals("Could not deactivate Organisation", TESTVertecOrganisation1, id);

        org = getFromVertec(uri,Organisation.class).getBody();

        assertFalse("Organisation did not get set to inactive",org.getActive());

    }

    @Test
    public void cannotSetRandomIdToActive(){
        Long id = 937645234724623746L;
        String uri = baseURI + "/organisation/" + id + "/activate";

        try{

            id = putToVertec(uri, Long.class).getBody();
            assertTrue("Found organisation with random id",false);
        } catch (HttpStatusCodeException e){
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }

    @Test
    public void cannotSetRandomIdToInactive(){
        Long id = 937645234724623746L;
        String uri = baseURI + "/organisation/" + id;

        try{

            id = deleteFromVertec(uri, Long.class).getBody();
            assertTrue("Found organisation with random id",false);
        } catch (HttpStatusCodeException e){
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }


    /*
    TESTS FOR GET ProjectsForOrganisation
     */

    /**
     * Get projects for known organisation
     * Assert values match known values
     * Assert org details returned match known values
     * tests whether project and its phases are built correctly
     */
    @Test
    public void canGetProjectsForOrganisation() {
        Long orgID = 709814L; //existing vertec Organisation
        String uri =  baseURI + "/organisation/" + orgID + "/projects";

        ProjectsForOrganisation pfo = getFromVertec(uri,ProjectsForOrganisation.class).getBody();
        assertEquals("Got wrong organisation",orgID, pfo.getOrganisationId());
        assertTrue("Deutsche Telekom".equals(pfo.getOrganisationName()));

        assertTrue(2 <= pfo.getProjects().size());
        assertEquals(pfo.getProjects().get(0).getV_id().longValue(), 2073414L);
        assertEquals(pfo.getProjects().get(1).getV_id().longValue(), 16909140L);

        List<JSONPhase> phasesForProj1 = pfo.getProjects().get(0).getPhases();
        List<JSONPhase> phasesForProj2 = pfo.getProjects().get(1).getPhases();

        assertEquals("Wrong phases gotten", phasesForProj1.size() , 2); //project inactve so should not change in the future,
        assertEquals("Wrong phases gotten", phasesForProj2.size() , 3); //but if these assertions fail check on vertec how many phases the project has


        assertEquals(2073433L, phasesForProj1.get(0).getV_id().longValue());
        assertEquals(2073471L, phasesForProj1.get(1).getV_id().longValue());

        assertEquals(16909162L, phasesForProj2.get(0).getV_id().longValue());
        assertEquals(17092562L, phasesForProj2.get(1).getV_id().longValue());
        assertEquals(17093158L, phasesForProj2.get(2).getV_id().longValue());

        assertTrue(pfo.getProjects().get(0).getTitle().equals("T-Mobile, Internet Architect"));
        assertFalse(pfo.getProjects().get(0).getActive());
        assertTrue(pfo.getProjects().get(0).getCode().equals("C11583"));
        assertEquals(pfo.getProjects().get(0).getClientRef().longValue(), 709814L);
        assertEquals(pfo.getProjects().get(0).getCustomerId(), null);
        assertEquals(pfo.getProjects().get(0).getLeader_ref().longValue(), 504354L);
        //type not uses
        //currency not used
        assertTrue(pfo.getProjects().get(0).getCreationDate().equals("2008-08-27T14:22:47"));
        //modifiedDate will change so check is not very useful here

        //phases
        JSONPhase phase = phasesForProj1.get(1);
        assertFalse(phase.getActive());
        assertTrue(phase.getDescription().equals("Proposal"));
        assertTrue(phase.getCode().equals("10_PROPOSAL"));
        assertEquals(3, phase.getStatus());
        assertTrue(phase.getSalesStatus().contains("30"));
        assertTrue(phase.getExternalValue().equals("96,000.00"));
        assertTrue(phase.getStartDate().equals(""));
        assertTrue(phase.getEndDate().equals(""));
        assertTrue(phase.getOfferedDate().equals("2008-08-27"));
        assertTrue(phase.getCompletionDate().equals(""));
        assertTrue(phase.getLostReason().contains("suitable resource"));
        assertTrue(phase.getCreationDate().equals("2008-08-27T14:25:38"));
        assertTrue(phase.getRejectionDate().equals("2008-10-31"));

        assertEquals(504354, phase.getPersonResponsible().longValue());
    }


    /**
     * Given id of org, correctly returns activities for org
     * Assert value match known values
     * also tests construction of JSON activities and their conversion to 'Entity.Activity's
     */
    @Test
    public void canGetActivitiesForOrganisation() {
        Long orgID = 9206250L; //existing Vertec organisation
        String uri =  baseURI + "/organisation/" + orgID + "/activities";

        ActivitiesForOrganisation afo = getFromVertec(uri, ActivitiesForOrganisation.class).getBody();

        assertEquals(orgID, afo.getOrganisationId());
        assertTrue("Quanta Fluid Solutions Ltd".equals(afo.getName()));

        List<VRAPI.Entities.Activity> activities = afo.getActivitiesForOrganisation();

        assertTrue("Not al activities got", activities.size() >= 10);

        assertEquals(9206485L, activities.get(0).getVertecId().longValue());
        assertEquals(27450368L, activities.get(9).getVertecId().longValue());

        Activity activity = activities.get(0);
        assertTrue(activity.getDone());
        assertTrue(activity.getvType().equals("EMail"));
        assertTrue(activity.getText().contains("and Mr Peter Templeton (Quanta)"));
        assertTrue(activity.getDueDate().equals("2011-10-20"));
        assertTrue(activity.getDoneDate().equals("2016-06-17"));
        assertTrue(activity.getCreated().equals("2011-10-20T10:19:12"));

        assertEquals(activity.getVertecDealLink(), null);
        assertEquals(activity.getVertecProjectLink().longValue(), 9206384L);
        assertEquals(activity.getVertecOrganisationLink(), orgID);
        assertEquals(activity.getVertecContactLink(), null);
    }

    /*
    TESTS FOR GET allOrganisations
     */

    /**
     * Tests construction of JSON Organisations, as well as of 'Entities.Organisation's
     */
    @Test
    public void canGetListOfOrganisations(){
        Long orgid1 = 709814L;
        Long orgid2 = 9206250L;
        List<Long> orgids = new ArrayList<>();
        orgids.add(orgid1);
        orgids.add(orgid2);

        String idsAsString = "";
        for(int i = 0; i < orgids.size(); i++) {
            if (i < orgids.size() -1) {
                idsAsString += orgids.get(i) + ",";
            } else {
                idsAsString += orgids.get(i);
            }
        }

        String uri =  baseURI + "/organisations/" + idsAsString;

        OrganisationList organisationList = getFromVertec(uri, OrganisationList.class).getBody();

        assertEquals(orgids.size(), organisationList.getOrganisations().size());

        Organisation firstOrg = organisationList.getOrganisations().get(0);
        Organisation secOrg = organisationList.getOrganisations().get(1);

        assertEquals(orgids.get(0), firstOrg.getVertecId());
        assertEquals(5295L, firstOrg.getOwnerId().longValue());
        assertEquals(null, firstOrg.getParentOrganisation());

        assertTrue("Sales Team".equals(firstOrg.getOwnedOnVertecBy()));
        assertTrue("Deutsche Telekom".equals(firstOrg.getName()));
        assertTrue("".equals(firstOrg.getWebsite()));
        //category not set yet
        //nor is business domain
        assertTrue("".equals(firstOrg.getBuildingName()));
        assertTrue("".equals(firstOrg.getStreet_no()));
        assertTrue("Hatfield Business Park".equals(firstOrg.getStreet()));
        assertTrue("Hatfield".equals(firstOrg.getCity()));
        assertTrue("United Kingdom".equals(firstOrg.getCountry()));
        assertTrue("AL10 9BW".equals(firstOrg.getZip()));
        assertTrue("2002-01-18T15:47:03".equals(firstOrg.getCreated()));
        assertTrue(firstOrg.getActive());

        assertEquals(orgids.get(1), secOrg.getVertecId());
    }
   
    @Test
    public void canGetOrganisationByID() {
        Long orgID= 709814L; //actually exists

        String uri =  baseURI + "/organisation/" + orgID;
        
        Organisation organsiation = getFromVertec(uri, Organisation.class).getBody();
        assertEquals(orgID, organsiation.getVertecId());
        assertEquals(5295L, organsiation.getOwnerId().longValue());
        assertEquals(null, organsiation.getParentOrganisation());

        assertTrue("Sales Team".equals(organsiation.getOwnedOnVertecBy()));
        assertTrue("Deutsche Telekom".equals(organsiation.getName()));
        assertTrue("".equals(organsiation.getWebsite()));
        //category not set yet
        //nor is business domain
        assertTrue("".equals(organsiation.getBuildingName()));
        assertTrue("".equals(organsiation.getStreet_no()));
        assertTrue("Hatfield Business Park".equals(organsiation.getStreet()));
        assertTrue("Hatfield".equals(organsiation.getCity()));
        assertTrue("United Kingdom".equals(organsiation.getCountry()));
        assertTrue("AL10 9BW".equals(organsiation.getZip()));
        assertTrue("2002-01-18T15:47:03".equals(organsiation.getCreated()));
        assertTrue(organsiation.getActive());
    }

    /*
    TESTS FOR HELPER METHODS
     */

    /**
     * Given Document recieved from vertec, can extract obref ids
     */
    @Test
    public void getActivityIdsForOrganisationReturnsCorrectly() throws ParserConfigurationException, IOException, SAXException {
        OrganisationController oc = new OrganisationController();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("src/test/resources/response-activities-for-organisation"));

        List<Long> activityIds = oc.getObjrefsForOrganisationDocument(doc);

        assertEquals("Incorrect list size returned", 2, activityIds.size());
        assertTrue("Activity Id Missing", activityIds.contains(18550676L));
        assertTrue("Activity Id Missing", activityIds.contains(25764191L));

        doc = dBuilder.parse(new File("src/test/resources/response-projects-for-organisation"));

        List<Long> projectIds = oc.getObjrefsForOrganisationDocument(doc);

        assertEquals("Incorrect list size returned", 14, projectIds.size());
        assertTrue("Project Id Missing", projectIds.contains(17927567L));
        assertTrue("Project Id Missing", projectIds.contains(26471389L));
    }

    /**
     * Given Document recieved from vertec, can extract name of org
     */
    @Test
    public void getNameForOrganisationFromDocument() throws ParserConfigurationException, IOException, SAXException {
        OrganisationController oc = new OrganisationController();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("src/test/resources/response-activities-for-organisation"));

        String name = oc.getNameForOrganisationDocument(doc);

        assertEquals("Name incorrectly extracted", "HM Revenue & Customs", name);
    }


    /**
     * Given Organisation o and XMLORganisation vo will set owned_on_vertec_by correctly
     * check each case works
     */
    @Test
    public void canSetOwnedOnVertecBy() {
        Long salesTeamOwnedOrgId = 709814L;
        Long nonZUKOrg = 1359817L; //actually exists
        List<Long> orgids = new ArrayList<>();

        orgids.add(salesTeamOwnedOrgId);
        orgids.add(TESTVertecOrganisation1);
        orgids.add(TESTVertecOrganisation2);
        orgids.add(nonZUKOrg);

        String idsAsString = "";
        for(int i = 0; i < orgids.size(); i++) {
            if (i < orgids.size() -1) {
                idsAsString += orgids.get(i) + ",";
            } else {
                idsAsString += orgids.get(i);
            }
        }

        String uri =  baseURI + "/organisations/" + idsAsString;

        OrganisationList organisationList = getFromVertec(uri, OrganisationList.class).getBody();
        List<Organisation> orgs  = organisationList.getOrganisations();

        System.out.println(orgs.get(0).getOwnedOnVertecBy());
        System.out.println(orgs.get(1).getOwnedOnVertecBy());
        System.out.println(orgs.get(2).getOwnedOnVertecBy());
        System.out.println(orgs.get(3).getOwnedOnVertecBy());
        assertTrue(orgs.get(0).getOwnedOnVertecBy().equals("Sales Team"));
        assertTrue(orgs.get(1).getOwnedOnVertecBy().equals("Not ZUK"));
        assertTrue(orgs.get(2).getOwnedOnVertecBy().equals("ZUK Sub Team"));
        assertTrue(orgs.get(3).getOwnedOnVertecBy().equals("No Owner"));
    }

    /*
    AUTHENTICATION AND INVALID IDS TESTS

        - Should test ifUnauthorisedThrowErrorReponse()
        - For each endpoint, should make sure querying with invalid id returns NOT_FOUND

     */



//





    /*
    OLD TESTS, NEED CONVERTING TO COMMON REPRESENTATION I BELIEVE
     */


//    @Test @Ignore
//    public void canGetAllOrganisationsInCommonRepresentation() {
//
//        String uri = baseURI + "/organisations/all";
//        ResponseEntity<OrganisationList> res = getFromVertec(uri, OrganisationList.class);
//
//        System.out.println(res.getBody().toJSONString());
//
//    }
//
//
//    @Test
//    public void postingOrganisationPostsToVertecAndReturnsPostedObject() {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Authorization", username + ':' + password);
//
//        JSONOrganisation org = new JSONOrganisation();
//        org.setName("Will's company");
//        org.setOwner("justin.cowling@zuhlke.com");
//        org.setCountry("England");
//
//        ResponseEntity<JSONOrganisation> recOrg
//                = rt.exchange(
//                new RequestEntity<Object>(
//                        org,
//                        HttpMethod.POST,
//                        URI.create(baseURI + "/org")), JSONOrganisation.class);
//    }
//
////---------------------------------------------------------------------------------------------------------------------- GET /{ids}

//    @Test
//    public void queryingForOrganisationsListWithValidListReturnsOrganisations() {
//        String idsAsString = "";
//        for(int i = 0; i < organisationIdsPresentInVertec.size(); i++) {
//            if (i < organisationIdsPresentInVertec.size() -1) {
//                idsAsString += organisationIdsPresentInVertec.get(i) + ",";
//            } else {
//                idsAsString += organisationIdsPresentInVertec.get(i);
//            }
//        }
//
//        String uri = baseURI + "/organisation/" + idsAsString;
//        ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);
//
//        assertNotNull(res);
//        assertNotNull(res.getBody());
//        assertNotNull(res.getBody().getOrganisations());
//        res.getBody().getOrganisations().stream()
//                .forEach(org -> {
//                    assertNotNull(org.getName());
//                    assertNotNull(org.getActive());
//                    assertNotNull(org.getCreationTime());
//                    assertNotNull(org.getModified());
//                    assertNotNull(org.getCity());
//                    assertNotNull(org.getStreetAddress());
//                    assertNotNull(org.getAdditionalAdress());
//                    assertNotNull(org.getZip());
//                    assertNotNull(org.getCountry());
//                    assertNotNull(org.getContacts());
//                    org.getContacts().stream()
//                            .forEach(contact -> {
//                                assertNotNull(contact.getObjid());
//                                assertNotNull(contact.getOrganisation());
//                                assertNotNull(contact.getFirstName());
//                                assertNotNull(contact.getSurname());
//                                assertNotNull(contact.getOwner());
//                            });
//                    assertNotNull(org.getOwner());
//                });
//    }
//
//    @Test
//    public void queryingForOrganisationsWithInvalidIdsThrowsNOTFOUND() {
//
//        List<Long> organisationIdsNotPresentInVertec = new ArrayList<>(Arrays.asList(28055041L, 28055044L, 280L, 2805510234L));
//        String idsAsString = "";
//        for(int i = 0; i < organisationIdsNotPresentInVertec.size(); i++) {
//            if (i < organisationIdsNotPresentInVertec.size() -1) {
//                idsAsString += organisationIdsNotPresentInVertec.get(i) + ",";
//            } else {
//                idsAsString += organisationIdsNotPresentInVertec.get(i);
//            }
//        }
//
//        String uri = baseURI + "/org/" + idsAsString;
//        try {
//            ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);
//        } catch (HttpClientErrorException e) {
//            assertEquals("Incorrect status code returned for organisation not found",
//                    HttpStatus.NOT_FOUND,
//                    e.getStatusCode());
//        }
//    }
//
//    @Test
//    public void queryingForOrganisationsWithMixtureIdsThrowsNOTFOUND() {
//
//        List<Long> organisationIdsNotPresentInVertec = new ArrayList<>(Arrays.asList(28055041L, 28055044L, 280L, 2805510234L, 28055047L, 28055033L));
//        String idsAsString = "";
//        for(int i = 0; i < organisationIdsNotPresentInVertec.size(); i++) {
//            if (i < organisationIdsNotPresentInVertec.size() -1) {
//                idsAsString += organisationIdsNotPresentInVertec.get(i) + ",";
//            } else {
//                idsAsString += organisationIdsNotPresentInVertec.get(i);
//            }
//        }
//
//        String uri = baseURI + "/org/" + idsAsString;
//        try {
//            ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);
//        } catch (HttpClientErrorException e) {
//            assertEquals("Incorrect status code returned for organisation not found",
//                    HttpStatus.NOT_FOUND,
//                    e.getStatusCode());
//        }
//    }
//
//
////---------------------------------------------------------------------------------------------------------------------- GET /{id}
//
//    @Test
//    public void queryingForOrganisationByPresentIdReturnsOrganisation() {
//        //Get organisation we know to be present in test database
//        String uri = baseURI + "/organisation/" + TESTVertecOrganisation1;
//        ResponseEntity<JSONOrganisation> res = getFromVertec(uri, JSONOrganisation.class);
//        assertNotNull("Response is null", res);
//        assertNotNull("Response body is null", res.getBody());
//        //check organisation we recieve has same id as requested
//        assertEquals("Incorrect organisation returned", TESTVertecOrganisation1, res.getBody().getObjid());
//        JSONOrganisation org = res.getBody();
//        //check various fields arent null
//        assertNotNull(org.getName());
//        assertNotNull(org.getActive());
//        assertNotNull(org.getCreationTime());
//        assertNotNull(org.getModified());
//        assertNotNull(org.getCity());
//        assertNotNull(org.getStreetAddress());
//        assertNotNull(org.getAdditionalAdress());
//        assertNotNull(org.getZip());
//        assertNotNull(org.getCountry());
//        assertNotNull(org.getContacts());
//        org.getContacts().stream()
//                .forEach(contact -> {
//                    assertNotNull(contact.getObjid());
//                    assertNotNull(contact.getOrganisation());
//                    assertNotNull(contact.getFirstName());
//                    assertNotNull(contact.getSurname());
//                    assertNotNull(contact.getOwner());
//                });
//        assertNotNull(org.getOwner());
//        assertTrue(! org.getContacts().isEmpty());
//        //TODO: find and add fields for category and business domain
//    }
//
//    @Test
//    public void queryingForOrganisationsWithInvalidIdThrowsHTTPNOTFOUND() {
//        String uri = baseURI + "/organisation/" + 2323235L;
//        try {
//            ResponseEntity<JSONOrganisation> res = getFromVertec(uri, JSONOrganisation.class);
//        } catch (HttpClientErrorException e) {
//            assertEquals("Incorrect status code returned for organisation not found",
//                    HttpStatus.NOT_FOUND,
//                    e.getStatusCode());
//        }
//
//    }
//
//
//

//
//    @Test @Ignore
//    public void getActivitiesForOrganisationReturnsCorrectly() {
//        String uri = baseURI + "/organisation/" + 17334035 + "/activities";
//        ResponseEntity<ActivitiesForOrganisation> res = getFromVertec(uri, ActivitiesForOrganisation.class);
//
//        System.out.println(res.getBody().toJSONString());
//
//    }
//
//    @Test @Ignore
//    public void getProjectsForOrganisationReturnsCorrectly() {
//        String uri = baseURI + "/organisation/" + 17927493 + "/projects";
//        ResponseEntity<ProjectsForOrganisation> res = getFromVertec(uri, ProjectsForOrganisation.class);
//
//        System.out.println(res.getBody().toJSONString());
//    }

}
