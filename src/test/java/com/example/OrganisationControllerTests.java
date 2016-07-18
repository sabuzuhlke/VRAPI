package com.example;

import VRAPI.Application;
import VRAPI.Entities.OrganisationList;
import VRAPI.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONContainerOrganisation.JSONOrganisationList;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.MergeClasses.ProjectsForOrganisation;
import VRAPI.MyAccessCredentials;
import VRAPI.ResourceController.OrganisationController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class OrganisationControllerTests {

    private final Long organisationIdPresentInVertec = 28055040L;
    private final List<Long> organisationIdsPresentInVertec = new ArrayList<>(Arrays.asList(28055040L, 28055047L, 28055033L, 28055109L));

    private String username;
    private String password;
    private RestTemplate rt;

    public static final String DEFAULT_OWN_IP = "localhost";
    public static final String DEFAULT_OWN_PORT = "9999";

    public final String baseURI = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT;

    @Before
    public void setUp() {
        MyAccessCredentials mac = new MyAccessCredentials();
        this.username = mac.getUserName();
        this.password = mac.getPass();
        this.rt = new RestTemplate();
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> hostname.equals("localhost"));
    }

    private <RES> ResponseEntity<RES> getFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
    }



    @Test
    public void canGetAllOrganisationsInCommonRepresentation() {

        String uri = baseURI + "/organisations/all";
        ResponseEntity<OrganisationList> res = getFromVertec(uri, OrganisationList.class);

        System.out.println(res.getBody().toJSONString());

    }


    @Test
    public void postingOrganisationPostsToVertecAndReturnsPostedObject() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);

        JSONOrganisation org = new JSONOrganisation();
        org.setName("Will's company");
        org.setOwner("justin.cowling@zuhlke.com");
        org.setCountry("England");

        ResponseEntity<JSONOrganisation> recOrg
                = rt.exchange(
                new RequestEntity<Object>(
                        org,
                        HttpMethod.POST,
                        URI.create(baseURI + "/org")), JSONOrganisation.class);
    }

//---------------------------------------------------------------------------------------------------------------------- GET /{ids}

    @Test
    public void queryingForOrganisationsListWithValidListReturnsOrganisations() {
        String idsAsString = "";
        for(int i = 0; i < organisationIdsPresentInVertec.size(); i++) {
            if (i < organisationIdsPresentInVertec.size() -1) {
                idsAsString += organisationIdsPresentInVertec.get(i) + ",";
            } else {
                idsAsString += organisationIdsPresentInVertec.get(i);
            }
        }

        String uri = baseURI + "/org/" + idsAsString;
        ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);

        assertNotNull(res);
        assertNotNull(res.getBody());
        assertNotNull(res.getBody().getOrganisations());
        res.getBody().getOrganisations().stream()
                .forEach(org -> {
                    assertNotNull(org.getName());
                    assertNotNull(org.getActive());
                    assertNotNull(org.getCreationTime());
                    assertNotNull(org.getModified());
                    assertNotNull(org.getCity());
                    assertNotNull(org.getStreetAddress());
                    assertNotNull(org.getAdditionalAdress());
                    assertNotNull(org.getZip());
                    assertNotNull(org.getCountry());
                    assertNotNull(org.getContacts());
                    org.getContacts().stream()
                            .forEach(contact -> {
                                assertNotNull(contact.getObjid());
                                assertNotNull(contact.getOrganisation());
                                assertNotNull(contact.getFirstName());
                                assertNotNull(contact.getSurname());
                                assertNotNull(contact.getOwner());
                            });
                    assertNotNull(org.getOwner());
                });
    }
    
    @Test
    public void queryingForOrganisationsWithInvalidIdsThrowsNOTFOUND() {

        List<Long> organisationIdsNotPresentInVertec = new ArrayList<>(Arrays.asList(28055041L, 28055044L, 280L, 2805510234L));
        String idsAsString = "";
        for(int i = 0; i < organisationIdsNotPresentInVertec.size(); i++) {
            if (i < organisationIdsNotPresentInVertec.size() -1) {
                idsAsString += organisationIdsNotPresentInVertec.get(i) + ",";
            } else {
                idsAsString += organisationIdsNotPresentInVertec.get(i);
            }
        }

        String uri = baseURI + "/org/" + idsAsString;
        try {
            ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Incorrect status code returned for organisation not found",
                    HttpStatus.NOT_FOUND,
                    e.getStatusCode());
        }
    }

    @Test
    public void queryingForOrganisationsWithMixtureIdsThrowsNOTFOUND() {

        List<Long> organisationIdsNotPresentInVertec = new ArrayList<>(Arrays.asList(28055041L, 28055044L, 280L, 2805510234L, 28055047L, 28055033L));
        String idsAsString = "";
        for(int i = 0; i < organisationIdsNotPresentInVertec.size(); i++) {
            if (i < organisationIdsNotPresentInVertec.size() -1) {
                idsAsString += organisationIdsNotPresentInVertec.get(i) + ",";
            } else {
                idsAsString += organisationIdsNotPresentInVertec.get(i);
            }
        }

        String uri = baseURI + "/org/" + idsAsString;
        try {
            ResponseEntity<JSONOrganisationList> res = getFromVertec(uri, JSONOrganisationList.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Incorrect status code returned for organisation not found",
                    HttpStatus.NOT_FOUND,
                    e.getStatusCode());
        }
    }


//---------------------------------------------------------------------------------------------------------------------- GET /{id}

    @Test
    public void queryingForOrganisationByPresentIdReturnsOrganisation() {
        //Get organisation we know to be present in test database
        String uri = baseURI + "/organisation/" + organisationIdPresentInVertec;
        ResponseEntity<JSONOrganisation> res = getFromVertec(uri, JSONOrganisation.class);
        assertNotNull("Response is null", res);
        assertNotNull("Response body is null", res.getBody());
        //check organisation we recieve has same id as requested
        assertEquals("Incorrect organisation returned", organisationIdPresentInVertec, res.getBody().getObjid());
        JSONOrganisation org = res.getBody();
        //check various fields arent null
        assertNotNull(org.getName());
        assertNotNull(org.getActive());
        assertNotNull(org.getCreationTime());
        assertNotNull(org.getModified());
        assertNotNull(org.getCity());
        assertNotNull(org.getStreetAddress());
        assertNotNull(org.getAdditionalAdress());
        assertNotNull(org.getZip());
        assertNotNull(org.getCountry());
        assertNotNull(org.getContacts());
        org.getContacts().stream()
                .forEach(contact -> {
                    assertNotNull(contact.getObjid());
                    assertNotNull(contact.getOrganisation());
                    assertNotNull(contact.getFirstName());
                    assertNotNull(contact.getSurname());
                    assertNotNull(contact.getOwner());
                });
        assertNotNull(org.getOwner());
        assertTrue(! org.getContacts().isEmpty());
        //TODO: find and add fields for category and business domain
    }

    @Test
    public void queryingForOrganisationsWithInvalidIdThrowsHTTPNOTFOUND() {
        String uri = baseURI + "/organisation/" + 2323235L;
        try {
            ResponseEntity<JSONOrganisation> res = getFromVertec(uri, JSONOrganisation.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Incorrect status code returned for organisation not found",
                    HttpStatus.NOT_FOUND,
                    e.getStatusCode());
        }

    }

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

    @Test
    public void getNameForOrganisationFromDocument() throws ParserConfigurationException, IOException, SAXException {
        OrganisationController oc = new OrganisationController();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("src/test/resources/response-activities-for-organisation"));

        String name = oc.getNameForOrganisationDocument(doc);

        assertEquals("Name incorrectly extracted", "HM Revenue & Customs", name);
    }

    @Test
    public void getActivitiesForOrganisationReturnsCorrectly() {
        String uri = baseURI + "/organisation/" + 17334035 + "/activities";
        ResponseEntity<ActivitiesForOrganisation> res = getFromVertec(uri, ActivitiesForOrganisation.class);

        System.out.println(res.getBody().toJSONString());

    }

    @Test
    public void getProjectsForOrganisationReturnsCorrectly() {
        String uri = baseURI + "/organisation/" + 17927493 + "/projects";
        ResponseEntity<ProjectsForOrganisation> res = getFromVertec(uri, ProjectsForOrganisation.class);

        System.out.println(res.getBody().toJSONString());
    }

}
