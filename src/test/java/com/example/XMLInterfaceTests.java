package com.example;
import VRAPI.Application;
import VRAPI.ContainerTeam.XMLEnvelope;
import VRAPI.ResourceController;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.net.URI;

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

            req = new RequestEntity<>( HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request");
        }
        assertTrue(req != null);

        res = rt.exchange(req,String.class);

        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().equals("ping"));

    }
//
//    @Test
//    public void canGetLondonOrgs(){
//        RestTemplate rt = new RestTemplate();
//        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/London/";
//        RequestEntity<String> req = null;
//        ResponseEntity<XMLEnvelope> res;
//        List<XMLOrganisation> Orgs = new ArrayList<>();
//        try{
//
//            req = new RequestEntity<>( HttpMethod.GET,new URI(url));
//        }
//        catch(Exception e){
//            System.out.println("Could not create Request");
//        }
//        assertTrue(req != null);
//
//        res = rt.exchange(req,XMLEnvelope.class);
//        assertTrue(res.getStatusCode() == HttpStatus.OK);
//        assertTrue(res.getBody() != null);
//
//        Orgs = res.getBody().getBody().getQueryResponse().getOrgs();
//
//        for(XMLOrganisation org : Orgs){
//            assertTrue(org.getStandardOrt().equals("London"));
//        }
//
//        System.out.println("Checked " + Orgs.size() + " organisations");
//    }
//
//    @Test
//    public void gettingContactsDoesntReceivePersonsAmongContacts() {
//        List<Long> Ids = new ArrayList<Long>();
//        Ids.add(240238L);
//        Ids.add(723465L);
//        Ids.add(751315L);
//        Ids.add(771935L);
//        Ids.add(2394423L);
//        Ids.add(2394981L);
//        List<XMLContact> cts = rc.getContacts(Ids);
//
//        assertEquals(cts.size(), 4);
//    }

    }

