package com.example;

/**
 * Created by gebo on 28/04/2016.
 */

import VRAPI.Application;
import VRAPI.ContainerActivitiesJSON.ZUKActivitiesResponse;
import VRAPI.ContainerDetailedProjects.Project;
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

import java.net.URI;

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
        this.rc = new ResourceController();
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
        assertTrue("Response status code not OK", res.getStatusCode() == HttpStatus.OK);
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
                .contains("Partial Failure: Username and Password provided " +
                        "do not have sufficient permissions to access all " +
                        "Vertec Data. Some queries may return missing or no " +
                        "information"));
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
                .contains("Ping Failed: Wrong Username or Password received in request header"));

    }

    @Test @Ignore("Takes too long")
    public void canGetZUK(){
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK/";
        ResponseEntity<ZUKOrganisationResponse> res = getFromVertec(url, ZUKOrganisationResponse.class);

        assertNotNull("Response returned as null", res);
        assertTrue("Response status code not OK", res.getStatusCode() == HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
        System.out.println(res.getBody().toPrettyString());
    }

    @Test @Ignore("Takes too long")
    public void canGetZUKProjects() {
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/projects/ZUK/";
        ResponseEntity<ZUKProjectsResponse> res = getFromVertec(url, ZUKProjectsResponse.class);

        System.out.println(res);
        assertNotNull("Response returned as null", res);
        assertTrue("Response status code not OK", res.getStatusCode() == HttpStatus.OK);
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


}
