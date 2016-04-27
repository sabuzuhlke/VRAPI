package com.example;

import VRAPI.ContainerAddresses.Envelope;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sabu on 27/04/2016.
 */
public class XMLToPOJOTests{

    @Test public void canParseTeamMembers(){
        VRAPI.ContainerTeam.Envelope env = null;

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

            JAXBContext jc = JAXBContext.newInstance(VRAPI.ContainerTeam.Envelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (VRAPI.ContainerTeam.Envelope) u.unmarshal(reader);

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

        Envelope env = null;

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

            JAXBContext jc = JAXBContext.newInstance(Envelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (Envelope) u.unmarshal(reader);

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
    public void canParseContactIds() {        //------------------------ Doesn't parse <Person>s
        VRAPI.ContainerSimpleContact.Envelope env = null;

        String xml = "<Envelope>" +
                "   <Body>" +
                "        <QueryResponse>" +
                "           <Kontakt>" +
                "               <objid>5726</objid>" +
                "           </Kontakt>" +
                "           <Kontakt>" +
                "               <objid>5728</objid>" +
                "           </Kontakt>" +
                "           <Person>" +
                "               <objid>6666</objid>" +
                "           </Person>" +
                "           <Person>" +
                "               <objid>6667</objid>" +
                "           </Person>" +
                "       </QueryResponse>" +
                "   </Body>" +
                "</Envelope>";

        try {

            JAXBContext jc = JAXBContext.newInstance(VRAPI.ContainerSimpleContact.Envelope.class);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            env = (VRAPI.ContainerSimpleContact.Envelope) u.unmarshal(reader);
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
    }


    @Test
    public void correctlyUnmarshallContactsResponse() {
        VRAPI.ContainerDetailedContact.Envelope env = new VRAPI.ContainerDetailedContact.Envelope();
        try {
            JAXBContext jc = JAXBContext.newInstance(env.getClass());
            Unmarshaller u = jc.createUnmarshaller();
            StringReader xmlReader = new StringReader(getDetailedContactXMLResponse());
            env = (VRAPI.ContainerDetailedContact.Envelope) u.unmarshal(xmlReader);
        } catch (Exception e) {
            System.out.println("ERROR in creating marshaller: " + e);
        }

        assertTrue(env.getBody() != null);
        assertTrue(env.getBody().getQueryResponse() != null);
        assertEquals(env.getBody().getQueryResponse().getContactList().size(), 6);
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getEmail().equals("immo.huneke@zuhlke.com"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getMobile().equals("+44 7941 072 238"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getPhone().equals("+44 870 777 2337"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getFirstName().equals("Immo"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getSurnname().equals("Hueneke"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getModified().equals("2016-02-02T12:38:59"));
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getOrganisation().getObjref() == 37358L);
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getObjId() == 240238L);
        assertTrue(env.getBody().getQueryResponse().getContactList().get(0).getPersonResponsible().getObjref() == 5726L);

    }

    @Test
    public void correctlyUnmarshallOrganisationsResponse() {
        VRAPI.ContainerDetailedOrganisation.Envelope env = new VRAPI.ContainerDetailedOrganisation.Envelope();
        try {
            JAXBContext jc = JAXBContext.newInstance(env.getClass());
            Unmarshaller u = jc.createUnmarshaller();
            StringReader xmlReader = new StringReader(getDetailedOrganisationXMLResponse());
            env = (VRAPI.ContainerDetailedOrganisation.Envelope) u.unmarshal(xmlReader);
        } catch (Exception e) {
            System.out.println("ERROR in creating marshaller: " + e);
        }

        assertTrue(env.getBody() != null);
        assertTrue(env.getBody().getQueryResponse() != null);
        assertEquals(env.getBody().getQueryResponse().getOrganisationList().size(), 3);
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getActive());
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getAdditionalAddressName().equals(""));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getCity().equals("London"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getCountry().equals("United Kingdom"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getModified().equals("2016-03-31T17:39:26"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getName().equals("Zuhlke Engineering Ltd"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getStreetAddress().equals("80 Great Eastern Street"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getZip().equals("EC2A 3JL"));
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getObjId() == 37358L);
        assertTrue(env.getBody().getQueryResponse().getOrganisationList().get(0).getPersonResponsible().getObjref() == 5295L);

    }

    private String getDetailedOrganisationXMLResponse() {
        return "<Envelope>\n" +
                "  <Body>\n" +
                "    <QueryResponse>\n" +
                "      <Firma>\n" +
                "        <objid>37358</objid>\n" +
                "        <aktiv>1</aktiv>\n" +
                "        <betreuer>\n" +
                "          <objref>5295</objref>\n" +
                "        </betreuer>\n" +
                "        <modifiedDateTime>2016-03-31T17:39:26</modifiedDateTime>\n" +
                "        <name>Zuhlke Engineering Ltd</name>\n" +
                "        <standardAdresse>80 Great Eastern Street</standardAdresse>\n" +
                "        <standardLand>United Kingdom</standardLand>\n" +
                "        <standardOrt>London</standardOrt>\n" +
                "        <standardPLZ>EC2A 3JL</standardPLZ>\n" +
                "        <zusatz/>\n" +
                "      </Firma>\n" +
                "      <Firma>\n" +
                "        <objid>710369</objid>\n" +
                "        <aktiv>1</aktiv>\n" +
                "        <betreuer>\n" +
                "          <objref>5295</objref>\n" +
                "        </betreuer>\n" +
                "        <modifiedDateTime>2015-02-24T16:08:43</modifiedDateTime>\n" +
                "        <name>J P Morgan Asset Management UK Ltd.</name>\n" +
                "        <standardAdresse>Finsbury Dials\n" +
                "20 Finsbury Street</standardAdresse>\n" +
                "        <standardLand>United Kingdom</standardLand>\n" +
                "        <standardOrt>London</standardOrt>\n" +
                "        <standardPLZ>EC2Y 9AQ</standardPLZ>\n" +
                "        <zusatz/>\n" +
                "      </Firma>\n" +
                "      <Firma>\n" +
                "        <objid>710627</objid>\n" +
                "        <aktiv>1</aktiv>\n" +
                "        <betreuer>\n" +
                "          <objref>5295</objref>\n" +
                "        </betreuer>\n" +
                "        <modifiedDateTime>2015-02-24T16:12:43</modifiedDateTime>\n" +
                "        <name>Credit Suisse</name>\n" +
                "        <standardAdresse>One Cabot Square</standardAdresse>\n" +
                "        <standardLand>United Kingdom</standardLand>\n" +
                "        <standardOrt>London</standardOrt>\n" +
                "        <standardPLZ>E14 4QJ</standardPLZ>\n" +
                "        <zusatz/>\n" +
                "      </Firma>\n" +
                "    </QueryResponse>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private String getDetailedContactXMLResponse() {
        return "<Envelope>\n" +
                "  <Body>\n" +
                "    <QueryResponse>\n" +
                "      <Kontakt>\n" +
                "        <objid>240238</objid>\n" +
                "        <firma>\n" +
                "          <objref>37358</objref>\n" +
                "        </firma>\n" +
                "        <betreuer>\n" +
                "          <objref>5726</objref>\n" +
                "        </betreuer>\n" +
                "        <modifiedDateTime>2016-02-02T12:38:59</modifiedDateTime>\n" +
                "        <name>Hueneke</name>\n" +
                "        <standardEMail>immo.huneke@zuhlke.com</standardEMail>\n" +
                "        <standardMobile>+44 7941 072 238</standardMobile>\n" +
                "        <standardTelefon>+44 870 777 2337</standardTelefon>\n" +
                "        <vorname>Immo</vorname>\n" +
                "      </Kontakt>\n" +
                "      <Kontakt>\n" +
                "        <objid>723465</objid>\n" +
                "        <firma>\n" +
                "          <objref>710369</objref>\n" +
                "        </firma>\n" +
                "        <modifiedDateTime>2015-07-22T09:12:30</modifiedDateTime>\n" +
                "        <name>Brennan</name>\n" +
                "        <standardEMail>malcolm.brennan@jpmorganfleming.com</standardEMail>\n" +
                "       <standardMobile/>\n" +
                "        <standardTelefon>+44 20 7742 3761</standardTelefon>\n" +
                "        <vorname>Malcolm</vorname>\n" +
                "      </Kontakt>\n" +
                "      <Kontakt>\n" +
                "        <objid>736599</objid>\n" +
                "        <firma>\n" +
                "          <objref>710627</objref>\n" +
                "        </firma>\n" +
                "        <modifiedDateTime>2015-02-24T16:12:43</modifiedDateTime>\n" +
                "        <name>Smith</name>\n" +
                "        <standardEMail>tony.smith@csfb.com</standardEMail>\n" +
                "        <standardMobile/>\n" +
                "        <standardTelefon>+44 20 7888 2212</standardTelefon>\n" +
                "        <vorname>Tony</vorname>\n" +
                "      </Kontakt>\n" +
                "      <Kontakt>\n" +
                "        <objid>742679</objid>\n" +
                "        <firma>\n" +
                "          <objref>710782</objref>\n" +
                "        </firma>\n" +
                "        <modifiedDateTime>2015-02-24T16:14:24</modifiedDateTime>\n" +
                "        <name>Cooper-Bland</name>\n" +
                "        <standardEMail>christina.cooper-bland@bacs.co.uk</standardEMail>\n" +
                "        <standardMobile/>\n" +
                "        <standardTelefon>+44 ext 1317</standardTelefon>\n" +
                "        <vorname>Christina</vorname>\n" +
                "      </Kontakt>\n" +
                "      <Kontakt>\n" +
                "        <objid>746789</objid>\n" +
                "        <firma>\n" +
                "          <objref>709712</objref>\n" +
                "        </firma>\n" +
                "        <modifiedDateTime>2015-02-24T16:02:33</modifiedDateTime>\n" +
                "        <name>Crowley</name>\n" +
                "        <standardEMail>mike.crowley@abbeynational.co.uk</standardEMail>\n" +
                "        <standardMobile/>\n" +
                "        <standardTelefon>+44 870 607 6000</standardTelefon>\n" +
                "        <vorname>Mike</vorname>\n" +
                "      </Kontakt>\n" +
                "      <Kontakt>\n" +
                "        <objid>751315</objid>\n" +
                "        <firma>\n" +
                "          <objref>709991</objref>\n" +
                "        </firma>\n" +
                "        <modifiedDateTime>2015-07-22T09:05:30</modifiedDateTime>\n" +
                "        <name>Spurrier</name>\n" +
                "        <standardEMail>neil.spurrier@uk.zurich.com</standardEMail>\n" +
                "        <standardMobile/>\n" +
                "        <standardTelefon>+44 1793 504 954</standardTelefon>\n" +
                "        <vorname>Neil</vorname>\n" +
                "      </Kontakt>\n" +
                "    </QueryResponse>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }


}
