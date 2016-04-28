package com.example;
import VRAPI.Application;
import VRAPI.ResourceController;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
/**
 * Created by gebo on 25/04/2016.
 */
public class XMLInterfaceTests {

    private ResourceController rc;

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

    @Test
    public void canGetZUKTeamMembers() {
        List<Long> res = rc.getZUKTeamMemberIds();

        System.out.println(res.toString());

        assertTrue( ! res.isEmpty());
        assertTrue( res.contains(5726L) ); //first item returned (from httptest.exe)
        assertTrue( res.size() >= 10 );

    }

    @Test
    public void canGetTeamMembersResponsibleAddresses() {

        Long[] array = getSomeTeamMemberIds();
        List<Long> teamMemberIds = new ArrayList<>(Arrays.asList(array));

        List<Long> res = rc.getSupervisedAddresses(teamMemberIds);


        assertTrue( ! res.isEmpty() );
        assertTrue( ! res.contains(1307942L) ); //some item returned by inactive contact
        assertTrue( res.contains(711471L) ); //some item returned
        assertTrue( res.contains(23137004L) ); //some other item returned
        assertTrue( res.size() >= 100 );

    }

    public Long[] getSomeTeamMemberIds() {
        Long[] a = {504419L, 504749L, 1795374L, 6574798L, 8619482L, 8904906L, 10301189L, 12456812L};
        return a;
    }

    @Test
    public void canGetSimpleContacts() {
        Long[] array = getSomeAddressIds();
        List<Long> contactIdsFromActiveTeamMembers = new ArrayList<>(Arrays.asList(array));

        List<List<Long>> res = rc.getSimpleContactsandOrgs(contactIdsFromActiveTeamMembers);

        assertTrue( ! res.isEmpty());
        assertTrue( ! res.get(0).contains(504419L));
        assertTrue( ! res.get(0).contains(504419L));
        assertTrue( ! res.get(1).contains(504419L));
        assertTrue( ! res.get(1).contains(745314L));
        assertTrue(res.get(0).contains(17533851L));
        assertTrue(res.get(0).contains(17534224L));
        assertTrue(res.get(1).contains(692179L));
        assertTrue(res.get(1).contains(695902L));
        assertTrue(res.size() == 2);
        assertTrue(res.get(0).size() == 2);
        assertTrue(res.get(1).size() == 2);


    }

    public Long[] getSomeAddressIds() {
        Long[] a = {504419L, 745314L, 17533851L, 17534224L, 692179L, 695902L};
        return a;
    }

    @Test
    public void canGetDetailedContacts() {
        Long[] array = getSomeContactIds();
        List<Long> ids = new ArrayList<>(Arrays.asList(array));

        List<VRAPI.ContainerDetailedContact.Contact> contacts = rc.getDetailedContacts(ids);

        assertTrue( ! contacts.isEmpty());
        assertTrue(contacts.size() == 4);
        assertTrue(contacts.get(0).getFirstName().equals("Immo"));
        assertTrue(contacts.get(0).getSurnname().equals("Hueneke"));
        assertTrue(contacts.get(0).getEmail().equals("immo.huneke@zuhlke.com"));
        assertTrue(contacts.get(0).getMobile().equals("+44 7941 072 238"));
        assertTrue(contacts.get(0).getPhone().equals("+44 870 777 2337"));
        assertTrue(contacts.get(0).getOrganisation().getObjref() == 37358L);
        assertTrue(contacts.get(0).getObjId() == 240238L);
        assertTrue(contacts.get(0).getPersonResponsible().getObjref() == 5726L);
        assertTrue(contacts.get(1).getFirstName().equals("Jason"));
        assertTrue(contacts.get(2).getFirstName().equals("Ygor"));
        assertTrue(contacts.get(3).getFirstName().equals("Mirco"));
    }

    public Long[] getSomeContactIds(){
        Long[] a = { 17534224L, 22481505L, 22481489L, 240238L};
        return a;
    }

    @Test
    public void canGetDetailedOrganisations() {
        Long[] array = getSomeOrgIds();
        List<Long> ids = new ArrayList<>(Arrays.asList(array));

        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = rc.getOrganisations(ids);

        assertTrue( ! orgs.isEmpty());
        assertTrue(orgs.size() == 3);
        assertTrue(orgs.get(0).getActive());
        assertTrue(orgs.get(0).getAdditionalAddressName().equals(""));
        assertTrue(orgs.get(0).getCity().equals("London"));
        assertTrue(orgs.get(0).getCountry().equals("United Kingdom"));
        assertTrue(orgs.get(0).getModified().equals("2016-03-31T17:39:26"));
        assertTrue(orgs.get(0).getName().equals("Zuhlke Engineering Ltd"));
        assertTrue(orgs.get(0).getStreetAddress().equals("80 Great Eastern Street"));
        assertTrue(orgs.get(0).getZip().equals("EC2A 3JL"));
        assertTrue(orgs.get(0).getObjId() == 37358L);
        assertTrue(orgs.get(0).getPersonResponsible().getObjref() == 5295L);
        assertTrue(orgs.get(1).getActive());
        assertTrue(orgs.get(2).getActive());
        assertTrue(orgs.get(1).getZip().equals("EC2Y 9AQ"));
        assertTrue(orgs.get(2).getZip().equals("E14 4QJ"));

    }

    public Long[] getSomeOrgIds(){
        Long[] a = {37358L, 710369L, 710627L};
        return a;
    }

//    @Test
//    public void testthiasandthat(){
//        List<Long> teamids = rc.getZUKTeamMemberIds();
//        List<Long> addrids = rc.getSupervisedAddresses(teamids);
//        List<List<Long>> CO = rc.getSimpleContactsandOrgs(addrids);
//        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = rc.getOrganisations(CO.get(1));
//
//
//        System.out.println("Nr contacts: " + CO.get(0).size());
//        System.out.println("Nr orgs: " + CO.get(1).size());
//        System.out.println("Active out of these: " + orgs.size());
//
//    }

}

