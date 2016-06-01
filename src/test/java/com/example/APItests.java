package com.example;

/**
 * Created by gebo on 28/04/2016.
 */

import VRAPI.Application;
import VRAPI.ContainerActivitiesJSON.ZUKActivitiesResponse;
import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.ContainerOrganisationJSON.JSONContact;
import VRAPI.ContainerOrganisationJSON.JSONOrganisation;
import VRAPI.ContainerOrganisationJSON.ZUKOrganisationResponse;
import VRAPI.ContainerProjectJSON.JSONProject;
import VRAPI.ContainerProjectJSON.ZUKProjectsResponse;
import VRAPI.MyAccessCredentials;
import VRAPI.MyLimitedCredentials;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import VRAPI.ResourceController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/ping";
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
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK";
        MyLimitedCredentials mlc = new MyLimitedCredentials();
        this.username = mlc.getUserName();
        this.password = mlc.getPass();
        ResponseEntity<String> res = getFromVertec(url, String.class);

        System.out.println("res: " + res);

        assertNotNull("Response returned as null", res);
        assertNotNull("Response body is null", res.getBody());
        assertTrue(res.getBody()
                .contains("Forbidden"));

    }

    @Test
    public void canNotGetZUKWithNoAccess(){
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK";
        this.username = "blah";
        this.password = "blah";

        ResponseEntity<String> res = getFromVertec(url, String.class);

        assertNotNull("Response returned as null", res);
        assertNotNull("Response body is null", res.getBody());
        assertTrue(res.getBody()
                .contains("Unauthorized"));

    }

    @Test @Ignore("Takes too long")
    public void canGetZUK(){
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK/";
        ResponseEntity<ZUKOrganisationResponse> res = getFromVertec(url, ZUKOrganisationResponse.class);

        assertNotNull("Response returned as null", res);
        assertEquals("Response status code not OK",
                res.getStatusCode(),
                HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
        System.out.println(res.getBody().toPrettyString());
    }

    @Test @Ignore("Takes too long")
    public void canGetZUKProjects() {
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/projects/ZUK/";
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
        System.out.println(res.getBody().toString());

    }

    @Test @Ignore("Takes too long")
    public void canGetZUKActivities(){
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/activities/ZUK/";
        ResponseEntity<String> res = getFromVertec(url, String.class);

        assertTrue("Response status code not OK", res.getStatusCode() == HttpStatus.OK);
        assertTrue("Activities were filtered out incorrectly", ! res.getBody().contains("26376851"));
        assertTrue("Activities were filtered out incorrectly", ! res.getBody().contains("28013137"));
        System.out.println(res);
    }

    @Test
    public void singleInstancePerRequest() throws Exception {
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/singleInstance";
        assertEquals("Two requests made in succession do not access different instances of ResourceController class",
                getFromVertec(url, String.class),
                getFromVertec(url, String.class));
    }

    @Test
    public void canGetOrganisationById() throws URISyntaxException {
        Long id = 709814L;

        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/" + id ;
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
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/contacts/" + id ;
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


}
