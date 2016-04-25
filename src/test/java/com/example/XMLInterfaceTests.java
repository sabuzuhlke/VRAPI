package com.example;
import VRAPI.Application;
import VRAPI.ResourceController;
import VRAPI.VXMLClasses.XMLEnvelope;
import VRAPI.VXMLClasses.XMLObjRef;
import VRAPI.VXMLClasses.XMLOrganisation;
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
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
/**
 * Created by gebo on 25/04/2016.
 */
public class XMLInterfaceTests {

    ResourceController rc;

    @Before
    public void setUp(){
        this.rc = new ResourceController();

    }

    @Test
    public void apiIsUP(){
        RestTemplate rt = new RestTemplate();
        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/ping";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;
        try{

            req = new RequestEntity<String>( HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request");
        }
        assertTrue(req != null);

        res = rt.exchange(req,String.class);

        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().equals("blah"));


    }

    @Test
    public void canGetLondonOrgs(){
        RestTemplate rt = new RestTemplate();
        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/London/";
        RequestEntity<String> req = null;
        ResponseEntity<XMLEnvelope> res;
        List<XMLOrganisation> Orgs = new ArrayList<>();
        try{

            req = new RequestEntity<>( HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request");
        }
        assertTrue(req != null);

        res = rt.exchange(req,XMLEnvelope.class);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);

        Orgs = res.getBody().getBody().getQueryResponse().getOrgs();

        for(XMLOrganisation org : Orgs){
            assertTrue(org.getStandardOrt().equals("London"));
        }

        System.out.println("Checked " + Orgs.size() + " organisations");
    }

}
