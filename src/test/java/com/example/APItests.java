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
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class APItests {
    private ResourceController rc;

    @Before
    public void setUp(){
        this.rc = new ResourceController();
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    @Test
    public void apiIsUP(){

        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/ping";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;

        MyAccessCredentials mac = new MyAccessCredentials();
        String username = mac.getUserName();
        String pwd = mac.getPass();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);
        try{

            req = new RequestEntity<>(headers, HttpMethod.GET, new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create ping Request");
        }
        assertTrue(req != null);

        res = rt.exchange(req,String.class);

        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().equals("Success!"));

    }


    @Test
    public void canNotGetZUKWithLimitedAccess(){
        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;

        MyLimitedCredentials mlc = new MyLimitedCredentials();
        String username = mlc.getUserName();
        String pwd = mlc.getPass();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);
        try{

            req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create ping Request");
        }
        assertTrue(req != null);
        res = rt.exchange(req,String.class);

        System.out.println("res: " + res);

        assertTrue(res != null);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody()
                .contains("Partial Failure: Username and Password provided " +
                        "do not have sufficient permissions to access all " +
                        "Vertec Data. Some queries may return missing or no " +
                        "information"));

    }

    @Test
    public void canNotGetZUKWithNoAccess(){
        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "blah" + ':' + "blah");
        try{

            req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create ping Request");
        }
        assertTrue(req != null);
        res = rt.exchange(req,String.class);
        assertTrue(res != null);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody()
                .contains("Ping Failed: Wrong Username or Password received in request header"));

    }

    @Test
    public void canGetZUK(){

        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK/";
        RequestEntity<String> req = null;
        ResponseEntity<ZUKOrganisationResponse> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        try{


            //add authentication header to headers object
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

            req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request for ZUK");
        }
        assertTrue(req != null);

        res = rt.exchange(req,ZUKOrganisationResponse.class);

        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        System.out.println(res.getBody().toPrettyString());
    }

    @Test
    public void canGetZUKProjects() {

        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/projects/ZUK/";
        RequestEntity<String> req = null;
        ResponseEntity<ZUKProjectsResponse> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        try{


            //add authentication header to headers object
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

            req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request for ZUK Projects");
        }
        assertTrue(req != null);

        res = rt.exchange(req, ZUKProjectsResponse.class);

        System.out.println(res);
        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);

        for(JSONProject p : res.getBody().getProjects()){
            assertTrue(p.getType().contains("SGB_") || p.getType().contains("EMS") || p.getType().contains("DSI") || p.getType().contains("CAP"));
        }
        System.out.println(res.getBody().toString());

    }

    @Test
    public void canGetZUKActivities(){
        RestTemplate rt = new RestTemplate();
        String url = "https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/activities/ZUK/";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;
        MyAccessCredentials creds = new MyAccessCredentials();
        try{


            //add authentication header to headers object
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", creds.getUserName() + ':' + creds.getPass());

            req = new RequestEntity<>(headers, HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request for ZUK Activities");
        }
        assertTrue(req != null);

        res = rt.exchange(req, String.class);

        System.out.println(res);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue( ! res.getBody().contains("26376851"));
        assertTrue( ! res.getBody().contains("28013137"));
        System.out.println(res);
    }

    @Test
    public void singleInstancePerRequest() throws Exception {
        RestTemplate rt = new RestTemplate();

        MyAccessCredentials mac = new MyAccessCredentials();
        String username = mac.getUserName();
        String pwd = mac.getPass();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);
        RequestEntity<String> req = new RequestEntity<>(
                headers,
                HttpMethod.GET,
                URI.create("https://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/singleInstance"));
        assertEquals(rt.exchange(req, String.class), rt.exchange(req, String.class));
    }


}
