package com.example;
import VRAPI.Application;
import VRAPI.JSONContainerActivities.JSONActivitiesResponse;
import VRAPI.ContainerActivity.Activity;
import VRAPI.ContainerActivityType.ActivityType;
import VRAPI.ContainerDetailedContact.Contact;
import VRAPI.ContainerDetailedContact.Organisation;
import VRAPI.ContainerDetailedContact.PersonResponsible;
import VRAPI.ContainerDetailedOrganisation.DaughterFirms;
import VRAPI.ContainerDetailedOrganisation.Objlist;
import VRAPI.ContainerDetailedOrganisation.ParentFirm;
import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.JSONContainerOrganisation.ZUKOrganisationResponse;
import VRAPI.MyAccessCredentials;
import VRAPI.ResourceController.QueryBuilder;
import VRAPI.ResourceController.ResourceController;
import VRAPI.ResourceController.StaticMaps;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class XMLInterfaceTests {

    public static final List<Long> KNOWN_SUPERVISOR_IDS = asList(504419L, 504749L, 1795374L, 6574798L, 8619482L, 8904906L, 10301189L, 12456812L);
    public static final long ADDRESS_BELONGING_TO_INACTIVE_TEAM_MEMBER = 1307942L;
    private static ResourceController rc = null;

    static {
        try {
            rc = new ResourceController();
            MyAccessCredentials mac = new MyAccessCredentials();
            rc.setPassword(mac.getPass());
            rc.setUsername(mac.getUserName());
            rc.queryBuilder = new QueryBuilder(mac.getUserName(), mac.getPass());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static StaticMaps staticMaps;

    public XMLInterfaceTests() throws ParserConfigurationException {
    }

    @BeforeClass
    public static void before() {
        staticMaps = StaticMaps.INSTANCE;
    }

    @Test
    public void canGetZUKTeamMembers() {
        List<Long> res = new ArrayList<>();
        try{

            res = rc.getZUKTeamMemberIds();
        } catch (Exception e){
            System.out.println("Exception in canGetZUKTeamMembers: " + e);
        }

        System.out.println(res.toString());

        assertTrue( ! res.isEmpty());
        assertTrue( res.contains(5726L) ); //first item returned (from httptest.exe)
        assertTrue( res.size() >= 10 );

    }

    @Test
    public void canGetTeamMembersResponsibleAddresses() {
        List<Long> addressIds = rc.getAddressIdsSupervisedBy(KNOWN_SUPERVISOR_IDS);
        assertThat(addressIds, hasItems(711471L, 23137004L));
        assertThat(addressIds, not(hasItem(ADDRESS_BELONGING_TO_INACTIVE_TEAM_MEMBER)));
        assertThat(addressIds, hasSize(greaterThan(100)));
    }

    public Long[] getSomeProjectIds() {
        return new Long[]{23207688L, 24276335L, 26437983L, 26661642L};
    }

//==============================================================================================================PROJECTS
    @Test
    public void canGetDetailedProjects() {
        Set<Long> projectsIds = new HashSet<>(asList(getSomeProjectIds()));

        List<VRAPI.ContainerDetailedProjects.Project> projects = rc.getDetailedProjects(projectsIds);

        assertTrue( ! projectsIds.isEmpty());
        assertTrue(projects.size() == 4);

        System.out.println(projects.get(0).toJSONString());

        assertTrue(projects.get(0).getActive());
        assertTrue(projects.get(0).getCode().equals("C19066"));
        assertTrue(projects.get(0).getId() == 23207688L);
        assertTrue(projects.get(0).getClient().getObjref() == 1882164);
        assertTrue(projects.get(0).getLeader().getObjref() == 5295L);
        assertTrue( ! projects.get(0).getPhases().getObjlist().getObjrefs().isEmpty());
        assertTrue(projects.get(0).getCustomer() != null);
        assertTrue(projects.get(0).getType().getObjref() == 26540859L);
        assertTrue(projects.get(0).getCurrency() != null);
        assertTrue(projects.get(0).getCurrency().getObjref() == 346658L);
        assertEquals("Account manager got incorrectly", 16887415L, projects
                .get(0)
                .getAccountManager().getObjref().longValue());

        for(int i = 0; i < projects.size(); i++ ){

            System.out.println(projects.get(i).toJSONString());
        }
    }

    @Test
    public void canGetListofPhases(){
        List<Long> phaseIds = new ArrayList<>(asList(getSomePhases()));

        List<VRAPI.ContainerPhases.ProjectPhase> phases = rc.getPhasesForProject(phaseIds);

        assertTrue(phases.size() == 3);
        assertTrue( ! phases.get(1).getActive());
        assertTrue(phases.get(1).getCode().equals("10_ALPHA"));
        assertTrue(phases.get(1).getDescription().equals("CA Alpha"));
        assertTrue(phases.get(1).getStatus() == 1);
        assertTrue(phases.get(1).getPersonResponsible().getObjref() == 504354);
        assertTrue(phases.get(1).getSalesStatus().equals("21 - Verkauft (schriftlich) / Sold (written PO)"));


    }
    public Long[] getSomePhases(){
        Long[] a = {23207714L, 23207775L, 23238394L};
        return a;
    }



    @Test
    public void canGetTeamsProjectIds() {

        Set<Long> teamMemberIds = new HashSet<>(asList(new Long[]{504419L, 504749L, 1795374L, 6574798L, 8619482L, 8904906L, 10301189L, 12456812L}));

        Set<Long> projectIds = rc.getProjectsTeamAreWorkingOn(teamMemberIds);

        assertTrue( ! projectIds.isEmpty());
        assertTrue(projectIds.size() > 10);
    }

    @Test
    public void canGetProjectType(){
        assertThat(rc.getProjectType(26540859L).getDescripton(),
                   containsString("SGB_"));

    }

    public Long[] getSomeTypeids(){
        return new Long[]{26540859L, 592903L, 26540856L};
    }

    @Test
    public void canGetProjectCurrency(){

        Set<Long> projectIds = new HashSet<>(asList(getSomeProjectIds()));
        List<Project> projects = rc.getDetailedProjects(projectIds);
        Long currencyId = projects.get(0).getCurrency().getObjref(); //GBP

        VRAPI.ContainerCurrency.Currency currency = rc.getCurrency(currencyId);

        assertEquals("GBP", currency.getName());

    }

//==============================================================================================================CONTACTS
    @Test
    public void canGetSimpleContacts() {
        Long[] array = getSomeAddressIds();
        List<Long> contactIdsFromActiveTeamMembers = new ArrayList<>(asList(array));

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
        List<Long> ids = new ArrayList<>(asList(array));

        List<VRAPI.ContainerDetailedContact.Contact> contacts = rc.getActiveDetailedContacts(ids);

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

        r = rc.contactComparator.compare(a,a);
        assertTrue(r == 0);

        r = rc.contactComparator.compare(a,b);
        assertTrue(r == 1);

        r = rc.contactComparator.compare(a,c);
        assertTrue(r == -1);

        r = rc.contactComparator.compare(b,a);
        assertTrue(r == -1);

        r = rc.contactComparator.compare(b,b);
        assertTrue(r == 0);

        r = rc.contactComparator.compare(c,a);
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
        a.setOrganisation(new Organisation(2L));
        b.setOrganisation(new Organisation(null));
        g.setOrganisation(new Organisation(null));
        c.setOrganisation(new Organisation(1L));
        d.setOrganisation(new Organisation(2L));
        h.setOrganisation(new Organisation(1L));
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

        Collections.sort(contacts, rc.contactComparator);

        assertTrue(contacts.get(4).getOrganisation().getObjref() == 1L);
        assertTrue(contacts.get(5).getOrganisation().getObjref() == 1L);
        assertTrue(contacts.get(6).getOrganisation().getObjref() == 2L);
        assertTrue(contacts.get(7).getOrganisation().getObjref() == 2L);
    }


    @Test
    public void canCreateJsonContainer(){

        Map<Long, String> teamMap = new HashMap<>();

        teamMap.put(1L, "a@eat.com");
        teamMap.put(2L, "b@eat.com");
        teamMap.put(3L, "c@eat.com");

        rc.setTeamMap(teamMap);

        Map<Long, List<String>> followers = new HashMap<>();

        List<String> followerlist = new ArrayList<>();
        followerlist.add("b@eat.com");

        followers.put(2L, followerlist);

        rc.setFollowerMap(followers);

        ZUKOrganisationResponse res = rc.buildZUKOrganisationsResponse(getsomeContacts(),getsomeOrgs());

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

        System.out.println(res.toPrettyString());
        assertTrue( ! res.getOrganisationList().isEmpty());
        assertTrue(res.getOrganisationList().size() == 2);
        assertTrue(res.getOrganisationList().get(1).getObjid() == 2L);
        assertTrue(res.getOrganisationList().get(1).getContacts().size() == 2);
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getFirstName().equals("Ronald"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getSurname().equals("McDonald"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getPhone().equals("999"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getFollowers().isEmpty());
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getMobile().equals("07999"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getEmail().equals("childrenwelcome@me.com"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getObjid() == 1L);
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getOwner().equals("a@eat.com"));


        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getFirstName().equals("The Colonel"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getObjid() == 2L);
        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getOwner().equals("b@eat.com"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getFollowers().get(0).equals("b@eat.com"));

        assertTrue(res.getOrganisationList().get(0).getObjid() == 1L);
        assertTrue(res.getOrganisationList().get(0).getContacts().isEmpty());
        assertTrue(res.getOrganisationList().get(0).getOwner().equals("a@eat.com"));




    }

    public List<Contact> getsomeContacts(){
        Contact a = new Contact();
        Contact b = new Contact();
        Contact c = new Contact();
        Contact d = new Contact();

        List<Contact> contacts = new ArrayList<>();

        a.setFirstName("Ronald");
        b.setFirstName("The Colonel");
        c.setFirstName("The King");
        d.setFirstName("Mama");
        a.setOrganisation(new Organisation(2L));
        b.setOrganisation(new Organisation(2L));
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

        a.setPersonResponsible(new PersonResponsible());
        b.setPersonResponsible(new PersonResponsible());
        c.setPersonResponsible(new PersonResponsible());
        d.setPersonResponsible(new PersonResponsible());

        a.getPersonResponsible().setObjref(1L);
        b.getPersonResponsible().setObjref(2L);
        c.getPersonResponsible().setObjref(2L);
        d.getPersonResponsible().setObjref(3L);


        contacts.add(a);
        contacts.add(b);
        contacts.add(c);
        contacts.add(d);

        Collections.sort(contacts, rc.contactComparator);

        return contacts;
    }
//=========================================================================================================ORGANISATIONS

    @Test
    public void canGetDetailedOrganisations() {
        Long[] array = getSomeOrgIds();
        List<Long> ids = new ArrayList<>(asList(array));

        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = rc.getOrganisations(ids);

        assertTrue( ! orgs.isEmpty());
        assertTrue(orgs.size() == 3);
        assertTrue(orgs.get(0).getActive());
        assertTrue(orgs.get(0).getAdditionalAddressName().equals(""));
        assertTrue(orgs.get(0).getCity().equals("London"));
        assertTrue(orgs.get(0).getCountry().equals("United Kingdom"));
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

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getsomeOrgs(){
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

        o1.setPersonResponsible(new VRAPI.ContainerDetailedOrganisation.PersonResponsible());
        o2.setPersonResponsible(new VRAPI.ContainerDetailedOrganisation.PersonResponsible());

        o1.getPersonResponsible().setObjref(1L);
        o2.getPersonResponsible().setObjref(1L);

        o1.setDaughterFirm(new DaughterFirms());
        o1.getDaughterFirm().setObjlist(new Objlist());
        o1.getDaughterFirm().getObjlist().setObjref(new ArrayList<>());

        o1.setParentFirm(new ParentFirm());
        o1.getParentFirm().setObjref(1234L);

        o2.setDaughterFirm(new DaughterFirms());
        o2.getDaughterFirm().setObjlist(new Objlist());
        o2.getDaughterFirm().getObjlist().setObjref(new ArrayList<>());

        o2.setParentFirm(new ParentFirm());
        o2.getParentFirm().setObjref(1234L);

        orgs.add(o1);
        orgs.add(o2);


        return orgs;
    }

//    @Test
//    public void testthiasandthat(){
//        List<Long> teamids = rc.getZUKTeamMemberIds();
//        List<Long> addrids = rc.getAddressIdsSupervisedBy(teamids);
//        List<List<Long>> CO = rc.getSimpleContactsandOrgs(addrids);
//        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = rc.getOrganisations(CO.get(1));
//
//
//        System.out.println("Nr contacts: " + CO.get(0).size());
//        System.out.println("Nr orgs: " + CO.get(1).size());
//        System.out.println("Active out of these: " + orgs.size());
//
//    }

//=============================================================================================================FOLLOWERS
    @Test @Ignore
    public void GivenTeamListCanBuildMapOfFollowedObjects() throws Exception {
        List<Long> teamIds = rc.getZUKTeamMemberIds();

        assertTrue(!teamIds.isEmpty());

        MyAccessCredentials mac = new MyAccessCredentials();
        rc.setUsername(mac.getUserName());
        rc.setPassword(mac.getPass());
        Map<Long, List<String>> map = staticMaps.getFollowerMap();

        assertTrue(map.get(13030752L).contains("justin.cowling@zuhlke.com"));
        assertTrue(map.get(22285081L).contains("justin.cowling@zuhlke.com"));
        assertTrue(map.get(22285152L).contains("justin.cowling@zuhlke.com"));
        assertTrue(map.get(22286793L).contains("justin.cowling@zuhlke.com"));

    }

//============================================================================================================ACTIVITIES
    @Test
    public void canGetActivitiesOfUsers(){
        List<Long> teamMembers = new ArrayList<>();
        teamMembers.add(5295L);
        teamMembers.add(8619482L);
        List<Long> activityRefs = rc.getActivityIds(teamMembers);

        List<VRAPI.ContainerActivity.Activity> actList = rc.getActivities(activityRefs);

        assertTrue(actList.size() >= 10);
        System.out.println(actList.get(1));

    }

    @Test
    public void canGetActivityType(){
        List<Long> activityIds = new ArrayList<>();
        activityIds.add(24541514L);
        activityIds.add(26395314L);


        List<Activity> activities = rc.getActivities(activityIds);

        List<ActivityType> activityTypes = rc.getActivityTypes(activities);

        assertTrue(activityTypes.size() == 2);
        assertTrue(activityTypes.get(1).getObjid() == 573113L);
        assertTrue(activityTypes.get(1).getTypename().contains("Order Confirmation"));
    }

    @Test @Ignore
    public void displayAllActivityTypes(){
        List<Long> teamIds = new ArrayList<>();

        try{
            teamIds = rc.getZUKTeamMemberIds();
        } catch (Exception e){
            System.out.println("Exception in getting all activity types: " + e + "}");
        }

        List<Long> activityIds = rc.getActivityIds(teamIds);
        List<Activity> activities = rc.getActivities(activityIds);
        Set<ActivityType> types =  new HashSet<>(rc.getActivityTypes(activities));

        for(ActivityType t : types){
            System.out.println("{" + t.getObjid() + " : " + t.getTypename());
        }
    }


    @Test
    public void canBuildActivitiesResponse(){
        List<Long> teamIds = new ArrayList<>();
        teamIds.add(504749L);
        teamIds.add(16887415L);

        List<Long> aIds = rc.getActivityIds(teamIds);
        List<Activity> activities = rc.getActivities(aIds);
        List<ActivityType> aTypes = rc.getActivityTypes(activities);

        JSONActivitiesResponse aRes = rc.buildJSONActivitiesResponse(activities, aTypes);

        System.out.println(aRes);

        //TODO: make assertions

    }

    @Test
    public void canGetUserEmail(){
        Long id = 5295L;

        String eMail = rc.getUserEmail(id);

        assertTrue(eMail.equals("Wolfgang.Emmerich@zuhlke.com"));
    }

    @Test
    public void canCreateTeamIdMap() throws ParserConfigurationException {
        MyAccessCredentials mac = new MyAccessCredentials();
        //rc.setPassword(mac.getPass());
        //rc.setUsername(mac.getUserName());

        Map<Long, String> map = staticMaps.getTeamIDMap();

        assertEquals(map.get(5295L), "wolfgang.emmerich@zuhlke.com");
    }

    @Test
    public void canGetProjectByCode(){
        String code = "c15823";

        VRAPI.ContainerDetailedProjects.Project project =  rc.callVertec(rc.queryBuilder.getProjectByCode(code),
                                                                        VRAPI.ContainerDetailedProjects.Envelope.class)
                                                                        .getBody().getQueryResponse().getProjects().get(0);

        assertEquals(project.getId().longValue(), 12065530);
        assertEquals(project.getLeader().getObjref().longValue(), 504354);
        assertEquals(project.getPhases().getObjlist().getObjrefs().size(), 4);
        assertEquals(project.getType().getObjref().longValue(), 505895);
    }

}


























