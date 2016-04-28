package com.example;
import VRAPI.Application;
import VRAPI.ContainerDetailedContact.Contact;
import VRAPI.ContainerDetailedContact.Organisation;
import VRAPI.ContainerJSON.ZUKResponse;
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
import java.util.Collections;
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

    @Test
    public void canCompareContacts(){
        VRAPI.ContainerDetailedContact.Contact a = new VRAPI.ContainerDetailedContact.Contact();
        VRAPI.ContainerDetailedContact.Contact b = new VRAPI.ContainerDetailedContact.Contact();
        VRAPI.ContainerDetailedContact.Contact c = new VRAPI.ContainerDetailedContact.Contact();
        int r;


        a.setFirstName("Ronald McDonald");
        b.setFirstName("The King");
        c.setFirstName("The Colonel");
        a.setOrganisation(new VRAPI.ContainerDetailedContact.Organisation(1L));
        b.setOrganisation(new VRAPI.ContainerDetailedContact.Organisation(null));
        c.setOrganisation(new VRAPI.ContainerDetailedContact.Organisation(3L));

        r = rc.comparator.compare(a,a);
        assertTrue(r == 0);

        r = rc.comparator.compare(a,b);
        assertTrue(r == 1);

        r = rc.comparator.compare(a,c);
        assertTrue(r == -1);

        r = rc.comparator.compare(b,a);
        assertTrue(r == -1);

        r = rc.comparator.compare(b,b);
        assertTrue(r == 0);

        r = rc.comparator.compare(c,a);
        assertTrue(r == 1);

    }

    @Test
    public void canSortContacts(){
        Contact a = new Contact();
        Contact b = new Contact();
        Contact c = new Contact();
        Contact d = new Contact();
        Contact e = new Contact();
        Contact f = new Contact();
        Contact g = new Contact();
        Contact h = new Contact();

        List<Contact> contacts = new ArrayList<>();

        a.setFirstName("Ronald McDonald");
        b.setFirstName("The King");
        c.setFirstName("The Colonel");
        d.setFirstName("Ms Wendy");
        e.setFirstName("ferNando");
        f.setFirstName("Mr Byron");
        g.setFirstName("Mama Waga");
        h.setFirstName("Sir Sub");
        a.setOrganisation(new Organisation(1L));
        b.setOrganisation(new Organisation(null));
        g.setOrganisation(new Organisation(null));
        c.setOrganisation(new Organisation(1L));
        d.setOrganisation(new Organisation(2L));
        h.setOrganisation(new Organisation(2L));
        e.setOrganisation(null);
        f.setOrganisation(null);

        contacts.add(a);
        contacts.add(b);
        contacts.add(c);
        contacts.add(d);
        contacts.add(e);
        contacts.add(f);
        contacts.add(g);
        contacts.add(h);

        Collections.sort(contacts, rc.comparator);

        assertTrue(contacts.get(4).getOrganisation().getObjref() == 1L);
        assertTrue(contacts.get(5).getOrganisation().getObjref() == 1L);
        assertTrue(contacts.get(6).getOrganisation().getObjref() == 2L);
        assertTrue(contacts.get(7).getOrganisation().getObjref() == 2L);
    }


    @Test
    public void canCreateJsonContainer(){

        ZUKResponse res = rc.buildZUKResponse(getsomeContacts(),getsomeOrgs());

        assertTrue(res.getDanglingContacts().size() == 2);
        assertTrue(res.getDanglingContacts().get(0).getFirstName().equals("The King"));
        assertTrue(res.getDanglingContacts().get(0).getSurname().equals("Burger"));
        assertTrue(res.getDanglingContacts().get(0).getPhone().equals("999"));
        assertTrue(res.getDanglingContacts().get(0).getMobile().equals("07999"));
        assertTrue(res.getDanglingContacts().get(0).getEmail().equals("whopper@star.com"));
        assertTrue(res.getDanglingContacts().get(0).getModified().equals("12:12:2012"));
        assertTrue(res.getDanglingContacts().get(0).getObjid() == 3L);
        assertTrue(res.getDanglingContacts().get(1).getSurname().equals("Waga"));
        assertTrue(res.getDanglingContacts().get(1).getFirstName().equals("Mama"));

        assertTrue( ! res.getOrganisationList().isEmpty());
        assertTrue(res.getOrganisationList().size() == 2);
        assertTrue(res.getOrganisationList().get(0).getObjid() == 1L);
        assertTrue(res.getOrganisationList().get(0).getContacts().size() == 2);
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getFirstName().equals("Ronald"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getSurname().equals("McDonald"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getPhone().equals("999"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getMobile().equals("07999"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getEmail().equals("childrenwelcome@me.com"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(0).getObjid() == 1L);

        assertTrue(res.getOrganisationList().get(0).getContacts().get(1).getFirstName().equals("The Colonel"));
        assertTrue(res.getOrganisationList().get(0).getContacts().get(1).getObjid() == 2L);

        assertTrue(res.getOrganisationList().get(1).getObjid() == 2L);
        assertTrue(res.getOrganisationList().get(1).getContacts().isEmpty());


        System.out.println(res.toPrettyString());


    }

    public List<Contact> getsomeContacts(){
        //TODO: do responsible ppl
        Contact a = new Contact();
        Contact b = new Contact();
        Contact c = new Contact();
        Contact d = new Contact();

        List<Contact> contacts = new ArrayList<>();

        a.setFirstName("Ronald");
        b.setFirstName("The Colonel");
        c.setFirstName("The King");
        d.setFirstName("Mama");
        a.setOrganisation(new Organisation(1L));
        b.setOrganisation(new Organisation(1L));
        c.setOrganisation(null);
        d.setOrganisation(new Organisation(null));

        a.setSurnname("McDonald");
        b.setSurnname("Sanders");
        c.setSurnname("Burger");
        d.setSurnname("Waga");

        a.setPhone("999");
        b.setPhone("999");
        c.setPhone("999");
        d.setPhone("999");

        a.setMobile("07999");
        b.setMobile("07999");
        c.setMobile("07999");
        d.setMobile("07999");

        a.setEmail("childrenwelcome@me.com");
        b.setEmail("chicken@chicken.com");
        c.setEmail("whopper@star.com");
        d.setEmail("bad@service.com");

        a.setObjId(1L);
        b.setObjId(2L);
        c.setObjId(3L);
        d.setObjId(4L);

        a.setModified("12:12:2012");
        b.setModified("12:12:2012");
        c.setModified("12:12:2012");
        d.setModified("12:12:2012");

        contacts.add(a);
        contacts.add(b);
        contacts.add(c);
        contacts.add(d);

        Collections.sort(contacts, rc.comparator);

        return contacts;
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getsomeOrgs(){
        //TODO: do responsible ppl
        VRAPI.ContainerDetailedOrganisation.Organisation o1 = new VRAPI.ContainerDetailedOrganisation.Organisation();
        VRAPI.ContainerDetailedOrganisation.Organisation o2 = new VRAPI.ContainerDetailedOrganisation.Organisation();
        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = new ArrayList<>();

        o1.setObjId(1L);
        o2.setObjId(2L);

        o1.setModified("23:23:1876");
        o2.setModified("23:23:1876");

        o1.setAdditionalAddressName(" no!");
        o2.setAdditionalAddressName(" no!");

        o1.setCity("Sin City");
        o2.setCity("Ouahog");

        o1.setCountry("Murica!");
        o2.setCountry("Murica!");

        o1.setName("Association of good Fast Food Chains");
        o2.setName("The healthy options");

        o1.setStreetAddress("666 Highway To Hell");
        o2.setStreetAddress("667 Stairway To Heaven");

        o1.setZip("666");
        o2.setZip("777");

        orgs.add(o1);
        orgs.add(o2);

        return orgs;
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

