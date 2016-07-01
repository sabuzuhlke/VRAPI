package com.example;

/**
 * Created by gebo on 28/04/2016.
 */

import VRAPI.*;
import VRAPI.JSONContainerActivities.JSONActivitiesResponse;
import VRAPI.JSONContainerActivities.JSONActivity;
import VRAPI.JSONContainerOrganisation.JSONContact;
import VRAPI.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONContainerOrganisation.ZUKOrganisationResponse;
import VRAPI.JSONContainerProject.JSONPhase;
import VRAPI.JSONContainerProject.JSONProject;
import VRAPI.JSONContainerProject.ZUKProjectsResponse;
import VRAPI.ResourceController.ResourceController;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class APItests {
    private ResourceController rc;
    private String username;
    private String password;
    private RestTemplate rt;
    
    public static final String DEFAULT_OWN_IP = "localhost";
    public static final String DEFAULT_OWN_PORT = "9999";

    @Before
    public void setUp(){
        try {
            this.rc = new ResourceController();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
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
    public void apiIsUP(){
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/ping";
        ResponseEntity<String> res = getFromVertec(url, String.class);

        assertNotNull("Response returned as null", res);
        assertEquals("Response status code not OK",
                res.getStatusCode(),
                HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
        assertEquals("Response body doesnt not equal 'Success!'", res.getBody(), "Success!");
    }


    @Test
    public void canNotGetZUKWithLimitedAccess(){
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/organisations/ZUK";
        MyLimitedCredentials mlc = new MyLimitedCredentials();
        this.username = mlc.getUserName();
        this.password = mlc.getPass();
        ResponseEntity<String> res;

        try {
            res = getFromVertec(url, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong Status code returned", e.getStatusCode(), HttpStatus.FORBIDDEN);
            return;
        }
        assertTrue("No Exception caught", false);


    }

    @Test
    public void canNotGetZUKWithNoAccess(){
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/organisations/ZUK";
        this.username = "blah";
        this.password = "blah";
        ResponseEntity<String> res = null;

        try {
            res = getFromVertec(url, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong StatusCode Returned", e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            return;
        }
        assertTrue("No Exception caught", false);
    }

    @Test //@Ignore("Takes too long")
    public void canGetZUK(){
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/organisations/ZUK/";
        ResponseEntity<ZUKOrganisationResponse> res = getFromVertec(url, ZUKOrganisationResponse.class);

        assertNotNull("Response returned as null", res);
        assertEquals("Response status code not OK",
                res.getStatusCode(),
                HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
        System.out.println(res.getBody().toPrettyString());
    }

    @Test //@Ignore("Takes too long")
    public void canGetZUKProjects() {
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/projects/ZUK/";
        ResponseEntity<ZUKProjectsResponse> res = getFromVertec(url, ZUKProjectsResponse.class);

        System.out.println(res);
        assertNotNull("Response returned as null", res);
        assertEquals("Response status code not OK",
                res.getStatusCode(),
                HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());

        for(JSONProject p : res.getBody().getProjects()){
            assertTrue("Project recieved has type not from UK",
                    p.getType().contains("SGB_")
                            || p.getType().contains("EMS")
                            || p.getType().contains("DSI")
                            || p.getType().contains("CAP"));
        }
        System.out.println(res.getBody().toPrettyJSON());

    }

    @Test //@Ignore("Takes too long")
    public void canGetZUKActivities(){
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/activities/ZUK/";
        ResponseEntity<JSONActivitiesResponse> res = getFromVertec(url, JSONActivitiesResponse.class);

        assertTrue("Response status code not OK", res.getStatusCode() == HttpStatus.OK);
//        assertTrue("Activities were filtered out incorrectly", ! res.getBody().contains("26376851"));
        System.out.println(res.getBody().toPrettyJSON());
    }

    @Test
    public void canGetOrganisationById() throws URISyntaxException {
        Long id = 709814L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/organisations/" + id ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONOrganisation> res;
        MyAccessCredentials creds = new MyAccessCredentials();
            //add authentication header to headers object
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONOrganisation.class);

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody().getName().equals("Deutsche Telekom"));
        assertTrue(res.getBody().getCountry().equals("United Kingdom"));
        assertTrue(res.getBody().getContacts() != null);
        assertTrue(res.getBody().getContacts().size() == 16);

        assertTrue(res.getBody().getContacts().get(0).getFirstName().equals("Anthony"));
        assertTrue(res.getBody().getContacts().get(0).getOwner().equals("Wolfgang.Emmerich@zuhlke.com"));
        assertTrue(res.getBody().getOwner().equals("Wolfgang.Emmerich@zuhlke.com"));

    }

    @Test
    public void canGetContactById() throws URISyntaxException {
        Long id = 240238L; //Immo

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/contacts/" + id ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONContact> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONContact.class);

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody().getFirstName().equals("Immo"));
        assertTrue(res.getBody().getSurname().equals("Hueneke"));
        assertTrue(res.getBody().getPhone().equals("+44 870 777 2337"));
        assertTrue(res.getBody().getOwner().equals("David.Levin@zuhlke.com"));
        assertTrue(res.getBody().getCreationTime().equals("1900-01-01"));

    }


    @Test
    public void canGetProjectByCode() throws URISyntaxException {
        String code = "c15823";

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/projects/" + code ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONProject> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONProject.class);

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody().getPhases().size(),3);
        assertEquals(res.getBody().getTitle(), "HSBC HSS off-line demo");
        assertEquals(res.getBody().getClientRef().longValue(), 710229);
        assertEquals(res.getBody().getCurrency(),"GBP");
        assertEquals(res.getBody().getType(),"BU CAP");
        assertEquals("Account manager not set properly",res.getBody().getAccountManager(),"justin.cowling@zuhlke.com" );

        JSONPhase phase = res.getBody().getPhases().get(0);

        assertTrue( ! phase.getActive());
        assertEquals(phase.getCode(), "10_INITIAL_BUILD");
        assertEquals(phase.getPersonResponsible(), "keith.braithwaite@zuhlke.com");

        System.out.println(res.getBody().toJSONString());
    }
    @Test
    public void cannotGetNonExistingProjectCode() throws URISyntaxException {
        String code  = "sbdapidf";

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/projects/" + code ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONProject> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        try{

            res = rt.exchange(req, JSONProject.class);
        } catch (HttpStatusCodeException e){
            assertTrue(e.getStatusCode() == HttpStatus.NOT_FOUND);
            return;
        }

        assertTrue(false);
    }

    @Test
    public void canGetProjectById() throws URISyntaxException {
        Long id = 12065530L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/projects/" + id ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONProject> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONProject.class);

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody().getPhases().size(),3);
        assertEquals(res.getBody().getTitle(), "HSBC HSS off-line demo");
        assertEquals(res.getBody().getClientRef().longValue(), 710229);
        assertEquals(res.getBody().getCurrency(),"GBP");
        assertEquals(res.getBody().getType(),"BU CAP");

        JSONPhase phase = res.getBody().getPhases().get(0);

        assertTrue( ! phase.getActive());
        assertEquals(phase.getCode(), "10_INITIAL_BUILD");
        assertEquals(phase.getPersonResponsible(), "keith.braithwaite@zuhlke.com");

        System.out.println(res.getBody().toJSONString());
    }

    @Test
    public void cannotGetNonExistingProjectId() throws URISyntaxException {
        Long code  = 201L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/projects/" + code ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONProject> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        try{

            res = rt.exchange(req, JSONProject.class);
        } catch (HttpStatusCodeException e){
            assertTrue(e.getStatusCode() == HttpStatus.NOT_FOUND);
            return;
        }

        assertTrue(false);
    }
    @Test
    public void canGetOrgAsAddressEntry() throws URISyntaxException {
        Long id = 709814L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/addressEntry/" + id ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONOrganisation> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONOrganisation.class);

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody().getName().equals("Deutsche Telekom"));
        assertTrue(res.getBody().getCountry().equals("United Kingdom"));
        assertTrue(res.getBody().getContacts() != null);
        assertTrue(res.getBody().getContacts().size() == 16);

        assertTrue(res.getBody().getContacts().get(0).getFirstName().equals("Anthony"));
        assertTrue(res.getBody().getContacts().get(0).getOwner().equals("Wolfgang.Emmerich@zuhlke.com"));
        assertTrue(res.getBody().getOwner().equals("Wolfgang.Emmerich@zuhlke.com"));
    }

    @Test
    public void canGetGontactAsAddressentry() throws URISyntaxException {
        Long id = 240238L; //Immo

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/addressEntry/" + id ;
        RequestEntity<String> req = null;
        ResponseEntity<JSONContact> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONContact.class);

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody().getFirstName().equals("Immo"));
        assertTrue(res.getBody().getSurname().equals("Hueneke"));
        assertTrue(res.getBody().getPhone().equals("+44 870 777 2337"));
        assertTrue(res.getBody().getOwner().equals("David.Levin@zuhlke.com"));
        assertTrue(res.getBody().getCreationTime().equals("1900-01-01"));
    }

    @Test
    public void cannotGetPersonAsAddressEntry() {
        Long code  = 234L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/addressEntry/" + code ;
        RequestEntity<String> req = null;
        ResponseEntity<String> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));

        try{

            res = rt.exchange(req, String.class);
        } catch (HttpStatusCodeException e){
            assertTrue(e.getStatusCode() == HttpStatus.NOT_FOUND);
            return;
        }

        assertTrue(false);
    }

    @Test
    public void canGetActivitById() throws URISyntaxException {
        Long id = 10003025L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/activities/" + id ;
        RequestEntity<String> req;
        ResponseEntity<JSONActivitiesResponse> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));

        res = rt.exchange(req, JSONActivitiesResponse.class);

        assertEquals("wrong statuscode received", res.getStatusCode(), HttpStatus.OK);
        assertEquals("more than 1 item received", 1, res.getBody().getActivities().size());
        JSONActivity act = res.getBody().getActivities().get(0);
        assertEquals("field 'title' didnt get through", act.getTitle(), "Discuss Vodafone Group opportunities");
        assertEquals("User not returned correctly", act.getAssignee(), "justin.cowling@zuhlke.com");

    }

    @Test @Ignore("takes too long")
    public void cannotGetNonExistingActivity(){
        Long code  = 234L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/activities/" + code ;
        RequestEntity<String> req = null;
        ResponseEntity<String> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

        req = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));

        try{

            res = rt.exchange(req, String.class);
        } catch (HttpStatusCodeException e){
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
            return;
        }

        assertTrue(false);
    }

}
