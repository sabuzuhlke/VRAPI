package com.example;

import VRAPI.Application;
import VRAPI.ResourceControllers.OrganisationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class OrganisationControllerTests extends ControllerTests {

    private final Long organisationIdPresentInVertec = 28055040L;
    private final List<Long> organisationIdsPresentInVertec = new ArrayList<>(Arrays.asList(28055040L, 28055047L, 28055033L, 28055109L));


    @Test
    public void callingMergeOnTwoIdsWillShowAGoodLog() {

        Long id1 = 711840L;
        Long id2 = 10867689L;

        String uri = baseURI + "/organisation/" + id1 + "/mergeInto/" + id2;
        ResponseEntity<String> res = getFromVertec(uri, String.class);

        System.out.println(res.getBody());

    }


    /*
    TESTS FOR GET ProjectsForOrganisation
     */

    /**
     * Get projects for known organisation
     * Assert values match known values
     * Assert org details returned match known values
     */
    @Test
    public void canGetProjectsForOrganisation() {
        assertTrue(false);
    }

    /**
     * Given list of known project ids, returns list of JSON project
     * Assert values match known values
     */
    @Test
    public void canGetDetailedProjectsFromListOfIds() {
        assertTrue(false);
    }

    /**
     * Given xml project recieved from vertec, can correctly convert it to a JSONProject
     */
    @Test
    public void canCreateJsonProjectFromXmlProject() {
        assertTrue(false);
    }

    /**
     * Given xml Project recieved from vertec, will return List of JsonPhase
     * Phases include internal phases
     * Assert value match with known values
     */
    @Test
    public void canGetPhasesForProject() {
        assertTrue(false);
    }

    /**
     * Given List of phase Ids will return list of xml Phases from vertec
     * Assert values match with known values
     */
    @Test
    public void canGetXMLPhasesFromListOfPhaseIds() {
        assertTrue(false);
    }

    /**
     * Given collection of project IDs will return List of xml projects
     * Assert values match known values
     */
    @Test
    public void canGetXMLProjectsFromListOfProjectIds() {
        assertTrue(false);
    }

    /*
    TESTS FOR GET ActivitiesForOrganisation
     */

    /**
     * Given id of org, correctly returns activities for org
     * Assert value match known values
     */
    @Test
    public void canGetActivitiesForOrganisation() {
        assertTrue(false);
    }

    /**
     * Given list of activity IDs will return list of activities
     * Assert values match known values
     */
    @Test
    public void canGetActivitiesFromListOfActivityIDs() {
        assertTrue(false);
    }

    /**
     * Given xml activity from vertec, correctly converts to json Activity
     * assert values match
     */
    @Test
    public void canCreateActivityFromXMLActivity() {
        assertTrue(false);
    }

    /**
     * Given list of activityIds will return list of xml activities
     * Assert values match known values
     */
    @Test
    public void canGetXMLActivitiesFromListOfIds() {
        assertTrue(false);
    }

    /*
    TESTS FOR GET allOrganisations
     */

    /**
     * Will return all organisations owned by ZUK
     * Not sure how best to test this
     */
    @Test
    public void canGetAllOrganisations() {
        assertTrue(false);
    }

    /**
     * Given list of organisationIDs will get xml organisations
     * Assert values match known values
     */
    @Test
    public void canGetXMlOrganisationsGivenIDList() {
        assertTrue(false);
    }

    /**
     * Given list of org and contact ids will return list of lists
     * with first list containing contact ids and second list containing org ids
     * Assert each list contains correct ids
     */
    @Test
    public void givenListOfAssortedOrganisationAndContactIdsCanSeperateThem() {
        assertTrue(false);
    }

    /**
     * Given employee ids then will return set of addresses they own
     * Assert set contains correct values
     */
    @Test
    public void givenEmployeeIdListReturnsIdsOfAllAdressesTheySupervise() {
        assertTrue(false);
    }

    /*
    TESTS FOR GET organisationList
     */

    /**
     * Given List Of organisation Ids will return ORganisations
     * Assert values match known values
     */
    @Test
    public void canGetOrganisationListByIdsList() {
        assertTrue(false);
    }

    /**
     * Given organisation id will return organisation
     * Assert values match expected
     */
    @Test
    public void canGetOrganisationByID() {
        assertTrue(false);
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
     * Given NodeList of objref nodes, will return list of ids contained within
     */
    @Test
    public void canGetIdsListFromNodeList() {
        assertTrue(false);
    }

    /**
     * Given NodeList wiill return Stream of String where string is textContent of each node
     */
    @Test
    public void canGetStreamOfStringsFromNodeList() {
        assertTrue(false);
    }

    /**
     * Given requestEntity will query vertec and return Document of reponse
     */
    @Test
    public void canGetVertecResponseForQueryInDocumentForm() {
        assertTrue(false);
    }

    /**
     * Given List of xml organisations will return list of json organisations
     */
    @Test
    public void canGetListOfOrganisationFromListOfXMLOrganisation() {
        assertTrue(false);
    }

    /**
     * Given XML ORganisation will return JsonOrganisation
     * method: xml2JSon
     */
    @Test
    public void canCreateOrganisationFromXMLOrganisation() {
        assertTrue(false);
    }

    /**
     * Given Organisation o and XMLORganisation vo will set owned_on_vertec_by correctly
     * check each case works
     */
    @Test
    public void canSetOwnedOnVertecBy() {
        assertTrue(false);
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
//
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
//        String uri = baseURI + "/org/" + idsAsString;
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
//        String uri = baseURI + "/organisation/" + organisationIdPresentInVertec;
//        ResponseEntity<JSONOrganisation> res = getFromVertec(uri, JSONOrganisation.class);
//        assertNotNull("Response is null", res);
//        assertNotNull("Response body is null", res.getBody());
//        //check organisation we recieve has same id as requested
//        assertEquals("Incorrect organisation returned", organisationIdPresentInVertec, res.getBody().getObjid());
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
