package com.example;
import VRAPI.Application;
import VRAPI.ContainerTeam.XMLEnvelope;
import VRAPI.ResourceController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

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

//    @Test
//    public void apiIsUP(){
//        RestTemplate rt = new RestTemplate();
//        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/ping";
//        RequestEntity<String> req = null;
//        ResponseEntity<String> res;
//        try{
//
//            req = new RequestEntity<String>( HttpMethod.GET,new URI(url));
//        }
//        catch(Exception e){
//            System.out.println("Could not create Request");
//        }
//        assertTrue(req != null);
//
//        res = rt.exchange(req,String.class);
//
//        assertTrue(res != null);
//        assertTrue(res.getStatusCode() == HttpStatus.OK);
//        assertTrue(res.getBody() != null);
//        assertTrue(res.getBody().equals("ping"));
//
//
//    }
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

    @Test public void canParseTeamMembers(){
        VRAPI.ContainerEmployer.XMLEnvelope env = null;

        String xml = "<Envelope>" +
                "   <Body>" +
                "        <QueryResponse>" +
                "           <Projektbearbeiter>" +
                "               <objid>5726</objid>" +
                "               <team>" +
                "                   <objlist>" +
                "                       <objref>5445</objref>" +
                "                       <objref>43345</objref>" +
                "                       <objref>9876</objref>" +
                "                   </objlist>" +
                "               </team>" +
                "           </Projektbearbeiter>" +
                "       </QueryResponse>" +
                "   </Body>" +
                "</Envelope>";

        try{

            JAXBContext jc = JAXBContext.newInstance(VRAPI.ContainerEmployer.XMLEnvelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (VRAPI.ContainerEmployer.XMLEnvelope) u.unmarshal(reader);

        }
        catch(Exception e){
            System.out.println("ERROR in Unmarshall TEAM test: " + e);
        }
        assertTrue(env != null);
        assertTrue(env.getBody() != null);
        assertTrue(env.getBody().getQueryResponse() != null);
        assertTrue(env.getBody().getQueryResponse().getWorkers() != null);
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().isEmpty());
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0) != null);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getObjid() == 5726);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getTeam() != null);
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects().isEmpty());
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects().get(0) == 5445);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects().get(1) == 43345);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects().get(2) == 9876);
    }

    @Test
    public void canParseBetreuteAdressen(){

        VRAPI.ContainerTeam.XMLEnvelope env = null;

        String xml = "<Envelope>" +
                "   <Body>" +
                "        <QueryResponse>" +
                "           <Projektbearbeiter>" +
                "               <objid>5726</objid>" +
                "               <aktiv>1</aktiv>" +
                "               <betreuteAdressen>" +
                "                   <objlist>" +
                "                       <objref>5445</objref>" +
                "                       <objref>43345</objref>" +
                "                       <objref>9876</objref>" +
                "                   </objlist>" +
                "               </betreuteAdressen>" +
                "           </Projektbearbeiter>" +
                "           <Projektbearbeiter>" +
                "               <objid>5728</objid>" +
                "               <aktiv>0</aktiv>" +
                "               <betreuteAdressen>" +
                "                   <objlist>" +
                "                      <objref>54458</objref>" +
                "                       <objref>433458</objref>" +
                "                      <objref>98768</objref>" +
                "                   </objlist>" +
                "               </betreuteAdressen>" +
                "           </Projektbearbeiter>" +
                "       </QueryResponse>" +
                "   </Body>" +
                "</Envelope>";

        try{

            JAXBContext jc = JAXBContext.newInstance(VRAPI.ContainerTeam.XMLEnvelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (XMLEnvelope) u.unmarshal(reader);

        }
        catch(Exception e){
            System.out.println("ERROR in Unmarshall Addresses test: " + e);
        }
        assertTrue(env != null);
        assertTrue(env.getBody() != null);
        assertTrue(env.getBody().getQueryResponse() != null);
        assertTrue(env.getBody().getQueryResponse().getWorkers() != null);
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().isEmpty());
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0) != null);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getObjid() == 5726);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(1).getObjid() == 5728);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getActive());
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().get(1).getActive());
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getAddresses() != null);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(1).getAddresses() != null);
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().get(0).getAddresses().getList().getObjects().isEmpty());
        assertTrue( ! env.getBody().getQueryResponse().getWorkers().get(1).getAddresses().getList().getObjects().isEmpty());
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getAddresses().getList().getObjects().get(0) == 5445);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(1).getAddresses().getList().getObjects().get(0) == 54458);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getAddresses().getList().getObjects().get(1) == 43345);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(1).getAddresses().getList().getObjects().get(1) == 433458);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(0).getAddresses().getList().getObjects().get(2) == 9876);
        assertTrue(env.getBody().getQueryResponse().getWorkers().get(1).getAddresses().getList().getObjects().get(2) == 98768);
    }

    @Test
    public void canGetContactIds() {        //------------------------ Doesn't parse <Person>s
        VRAPI.ContainerSimpleContact.XMLEnvelope env = null;

        String xml = "<Envelope>" +
                "   <Body>" +
                "        <QueryResponse>" +
                "           <Kontakt>" +
                "               <objid>5726</objid>" +
                "               <aktiv>1</aktiv>" +
                "           </Kontakt>" +
                "           <Kontakt>" +
                "               <objid>5728</objid>" +
                "               <aktiv>0</aktiv>" +
                "           </Kontakt>" +
                "           <Person>" +
                "               <objid>6666</objid>" +
                "               <aktiv>0</aktiv>" +
                "           </Person>" +
                "           <Person>" +
                "               <objid>6667</objid>" +
                "               <aktiv>1</aktiv>" +
                "           </Person>" +
                "       </QueryResponse>" +
                "   </Body>" +
                "</Envelope>";

        try {

            JAXBContext jc = JAXBContext.newInstance(VRAPI.ContainerSimpleContact.XMLEnvelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (VRAPI.ContainerSimpleContact.XMLEnvelope) u.unmarshal(reader);
        } catch (Exception e) {
            System.out.println("ERROR in Unmarshall Addresses test: " + e);
        }

        assertTrue(env != null);
        assertTrue(env.getBody() != null);
        assertTrue(env.getBody().getQueryResponse() != null);
        assertTrue( ! env.getBody().getQueryResponse().getContacts().isEmpty());
        assertTrue(env.getBody().getQueryResponse().getContacts().size() == 2);
        assertTrue(env.getBody().getQueryResponse().getContacts().get(0) != null);
        assertTrue(env.getBody().getQueryResponse().getContacts().get(1) != null);
        assertTrue(env.getBody().getQueryResponse().getContacts().get(0).getObjid() == 5726);
        assertTrue(env.getBody().getQueryResponse().getContacts().get(1).getObjid() == 5728);
        assertTrue(env.getBody().getQueryResponse().getContacts().get(0).getAktiv());
        assertTrue( ! env.getBody().getQueryResponse().getContacts().get(1).getAktiv());
    }
}

