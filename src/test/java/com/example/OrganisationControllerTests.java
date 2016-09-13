package com.example;

import VRAPI.Entities.Activity;
import VRAPI.Entities.Organisation;
import VRAPI.Entities.OrganisationList;
import VRAPI.JSONClasses.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONClasses.JSONContainerProject.JSONPhase;
import VRAPI.MergeClasses.ActivitiesForAddressEntry;
import VRAPI.MergeClasses.ProjectsForAddressEntry;
import VRAPI.ResourceControllers.OrganisationController;
import VRAPI.Util.NoIdSuppliedException;
import VRAPI.Util.QueryBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrganisationControllerTests extends ControllerTests {

    @Test
    public void canUpdateOrganisation() {

        Organisation org = new Organisation();
        org.setVertecId(TESTVertecOrganisation1);
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");
        org.setParentOrganisation(TESTVertecOrganisation2);

        String uri = baseURI + "/organisation/" + org.getVertecId();
        ResponseEntity<String> res = putToVertec(org, uri, String.class);

        assertTrue(res.getBody().contains("Success"));

        Organisation org2 = new Organisation();
        org2.setVertecId(TESTVertecOrganisation1);
        org2.setActive(true);
        org2.setBuildingName("building name2");
        org2.setBusinessDomain("business domain2");
        org2.setCategory("category2");
        org2.setCity("city2");
        org2.setCountry("country2");
        org2.setName("GEBO test org2");
        org2.setOwnedOnVertecBy("Sales Team");
        org2.setOwnerId(5295L);
        org2.setStreet("street2");
        org2.setStreet_no("street_no2");
        org2.setWebsite("website2");
        org2.setZip("zip2");

        String uri2 = baseURI + "/organisation/" + org2.getVertecId();
        ResponseEntity<String> res2 = putToVertec(org2, uri2, String.class);

        assertTrue(res2.getBody().contains("Success"));
    }

    @Test
    public void canNotUpdateOrganisationWithoutId() {

        Organisation org = new Organisation();
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");

        String uri = baseURI + "/organisation/" + 3;
        try {

            ResponseEntity<String> res = putToVertec(org, uri, String.class);
            assertTrue("No exception thrown", false);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong error message returned", HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        }
        assertTrue(true);
    }

    @Test
    public void canNotUpdateNonExistingOrganisation() {

        Organisation org = new Organisation();
        org.setVertecId(TESTRandomID);
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");

        String uri = baseURI + "/organisation/" + org.getVertecId();
        try {

            ResponseEntity<String> res = putToVertec(org, uri, String.class);
            assertTrue("No exception thrown", false);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong error message returned", HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        assertTrue(true);
    }

    @Test
    public void canCreateUpdateQuery() throws NoIdSuppliedException {
        QueryBuilder qb = new QueryBuilder("hab", "babbalab");

        Organisation org = new Organisation();
        org.setVertecId(TESTVertecOrganisation1);
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");

        System.out.println(qb.updateOrgansiation(org));
    }

    @Test
    public void canNotCreateUpdateQueryWithoutID() {
        QueryBuilder qb = new QueryBuilder("hab", "babbalab");

        Organisation org = new Organisation();
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");

        try {

            System.out.println(qb.updateOrgansiation(org));
            assertTrue("No exception thrown", false);
        } catch (NoIdSuppliedException nie) {
            assertTrue(true);
        }
    }

    /**
     * CAREFUL!! do not run on real vertec instance
     */
    @Test
    public void canCreateOrganisation() {
        Organisation org = new Organisation();
        org.setActive(true);
        org.setBuildingName("building name");
        org.setBusinessDomain("business domain");
        org.setCategory("category");
        org.setCity("city");
        org.setCountry("country");
        org.setName("GEBO test org");
        org.setOwnedOnVertecBy("Sales Team");
        org.setOwnerId(5295L);
        org.setStreet("street");
        org.setStreet_no("street_no");
        org.setWebsite("website");
        org.setZip("zip");
        org.setParentOrganisation(TESTVertecOrganisation2); //TODO figure out what to do here

        String uri = baseURI + "/organisation";
        ResponseEntity<String> res = postToVertec(org, uri, String.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());

    }

    /**
     * Below test showed that there is only one organisation that has a parent organisation, and one that has a child
     * Hence It is believed that the best solution is to solve the parentOrganisations by hand.
     * <p>
     * However as it turns out both of these have been merged with their respective child/parent resulting in interesting organisational relationships
     */
    @Test
    @Ignore("Not necessary anymore")
    public void doWeNeedToRepointParentOrganisationsAfterMerge() {
        String line;
        Long mergingID;
        Long survivingID;
        List<Long> mergedIds = new ArrayList<>();
        int counter = 0;
        try {

            File file = new File("OrganisationsThatHaveBeenMergedOnVertec.txt");

            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader breader = new BufferedReader(reader);
            while ((line = breader.readLine()) != null) {
                String[] parts = line.split(",");
                mergingID = Long.parseLong(parts[0]);
                survivingID = Long.parseLong(parts[1]);
                mergedIds.add(mergingID);

            }
            for (Long id : mergedIds) {
                String uri = baseURI + "/org/" + id;
                JSONOrganisation org = getFromVertec(uri, JSONOrganisation.class).getBody();
                if (!org.getChildOrganisationList().isEmpty() || org.getParentOrganisationId() != null) {
                    System.out.println("parent: " + org.getParentOrganisationId());
                    System.out.println("child: " + org.getChildOrganisationList());
                    System.out.println("Org: " + org.getName() + "(v_id: " + org.getObjid() + ")");

                    counter++;
                }
            }
            System.out.println("Total nr of merged away orgs with relatives: " + counter);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Ignore("Already ran, organisations merged")
    public void mergeOrganisations() {
        List<List<Long>> idsList = new ArrayList<>();
        List<Long> mergingIds = new ArrayList<>(); //<mergingId, survivingId>
        List<Long> survivingIds = new ArrayList<>(); //<mergingId, survivingId>

        //ECS
        mergingIds.add(20066194L);
        survivingIds.add(17913930L);

        //Waveguide -->2
        mergingIds.add(19440239L);
        survivingIds.add(20067398L);

        //Goldman Sachs -->3
        mergingIds.add(1807900L);
        survivingIds.add(710854L);

        //Elekta -->5
        mergingIds.add(16374120L);
        survivingIds.add(9050265L);
        //Bank of England -->12
        mergingIds.add(1878532L);
        survivingIds.add(710744L);
        //Nordea -->14
        mergingIds.add(692179L);
        survivingIds.add(13109201L);
        //HSBC Bank PLC -- 28
        mergingIds.add(15315614L);
        survivingIds.add(710229L);

        //Lein applied diagnostics -->16
        mergingIds.add(20066543L);
        survivingIds.add(20015906L);

        //Mitsubishi -->17
        mergingIds.add(709719L);
        survivingIds.add(710917L);
        //Travelex -->18
        mergingIds.add(1808837L);
        survivingIds.add(3613025L);
        //zedsen -->19
        mergingIds.add(25361568L);
        survivingIds.add(24436501L);
        //RaymondJames ->20
        mergingIds.add(20019540L);
        survivingIds.add(20311840L);
        //Elektron -->22
        mergingIds.add(19440192L);
        survivingIds.add(12776071L);
        //Glaxo Smith Kline -->23
        mergingIds.add(25881446L);
        survivingIds.add(17430668L);
        //UCL -->24
        mergingIds.add(3604761L);
        survivingIds.add(711204L);
        //Safeguard -->25
        mergingIds.add(710501L);
        survivingIds.add(2721808L);
        //Francis Crick Institute-->26
        mergingIds.add(17488058L);
        survivingIds.add(24075580L);
        //Elekta 2 -->27
        mergingIds.add(20746251L);
        survivingIds.add(9050265L);
        //Scentrics -->29
        mergingIds.add(3648896L);
        survivingIds.add(4819987L);
        //Imperial --> 30
        mergingIds.add(1808803L);
        survivingIds.add(3585109L);
        //M&G -->33
        mergingIds.add(1369007L);
        survivingIds.add(17765068L);
        //World programming Company --> 34
        mergingIds.add(6570392L);
        survivingIds.add(6231419L);
        //British Airways --> 35
        mergingIds.add(710006L);
        survivingIds.add(710401L);
        //Deloitte--> 36
        mergingIds.add(3604552L);
        survivingIds.add(3586027L);
        //Morgan Stanley--> 37
        mergingIds.add(711029L);
        survivingIds.add(709877L);

        assertEquals(mergingIds.size(), survivingIds.size());

        for (int i = 0; i < mergingIds.size(); i++) {
            String uri = baseURI + "/organisation/" + mergingIds.get(i) + "/mergeInto/" + survivingIds.get(i);
            System.out.print(uri);
            String res = getFromVertec(uri, String.class).getBody();
            System.out.println("============= " + res + " ===================");
        }

    }

    @Test
    @Ignore("Already ran, all mentioned ids deleted")
    public void deleteOrganisationsFromVertecBasedOnMerger() {
        List<Long> idsToDel = new ArrayList<>();
        idsToDel.add(9469332L);
        idsToDel.add(26062339L);
        idsToDel.add(20810990L);
        idsToDel.add(710236L);
        idsToDel.add(20729447L);
        idsToDel.add(12185089L);
        idsToDel.add(710253L);
        idsToDel.add(22639820L);

        for (Long id : idsToDel) {
            String uri = baseURI + "/organisation/" + id;

            Long recId = deleteFromVertec(uri, Long.class).getBody();

            assertEquals("Could not deactivate Organisation", id, recId);
        }


    }

    @Test
    public void canSetOrgToActiveAndInactive() {

        String uri = baseURI + "/organisation/" + TESTVertecOrganisation1 + "/activate";

        Long id = putToVertec(uri, Long.class).getBody();

        assertEquals("Could not activate organisation before setting it to inactive again!", TESTVertecOrganisation1, id);

        uri = baseURI + "/organisation/" + TESTVertecOrganisation1;

        Organisation org = getFromVertec(uri, Organisation.class).getBody();

        assertTrue("Organisation did not get set to active", org.getActive());

        id = 0L;

        id = deleteFromVertec(uri, Long.class).getBody();

        assertEquals("Could not deactivate Organisation", TESTVertecOrganisation1, id);

        org = getFromVertec(uri, Organisation.class).getBody();

        assertFalse("Organisation did not get set to inactive", org.getActive());

    }

    @Test
    public void cannotSetRandomIdToActive() {
        Long id = TESTRandomID;
        String uri = baseURI + "/organisation/" + id + "/activate";

        try {

            id = putToVertec(uri, Long.class).getBody();
            assertTrue("Found organisation with random id", false);
        } catch (HttpStatusCodeException e) {
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }

    }

    @Test
    public void cannotSetRandomIdToInactive() {
        Long id = TESTRandomID;
        String uri = baseURI + "/organisation/" + id;

        try {

            id = deleteFromVertec(uri, Long.class).getBody();
            assertTrue("Found organisation with random id", false);
        } catch (HttpStatusCodeException e) {
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }


    }


    /*
    TESTS FOR GET ProjectsForAddressEntry
     */

    /**
     * Get projects for known organisation
     * Assert values match known values
     * Assert org details returned match known values
     * tests whether project and its phases are built correctly
     */
    @Test
    public void canGetProjectsForOrganisation() {
        Long orgID = 709814L; //existing vertec Organisation
        String uri = baseURI + "/organisation/" + orgID + "/projects";

        ProjectsForAddressEntry pfo = getFromVertec(uri, ProjectsForAddressEntry.class).getBody();
        assertEquals("Got wrong organisation", orgID, pfo.getOrganisationId());
        assertTrue("Deutsche Telekom".equals(pfo.getOrganisationName()));

        assertTrue(2 <= pfo.getProjects().size());
        assertEquals(pfo.getProjects().get(0).getV_id().longValue(), 2073414L);
        assertEquals(pfo.getProjects().get(1).getV_id().longValue(), 16909140L);

        List<JSONPhase> phasesForProj1 = pfo.getProjects().get(0).getPhases();
        List<JSONPhase> phasesForProj2 = pfo.getProjects().get(1).getPhases();

        assertEquals("Wrong phases gotten", phasesForProj1.size(), 2); //project inactve so should not change in the future,
        assertEquals("Wrong phases gotten", phasesForProj2.size(), 3); //but if these assertions fail check on vertec how many phases the project has


        assertEquals(2073433L, phasesForProj1.get(0).getV_id().longValue());
        assertEquals(2073471L, phasesForProj1.get(1).getV_id().longValue());

        assertEquals(16909162L, phasesForProj2.get(0).getV_id().longValue());
        assertEquals(17092562L, phasesForProj2.get(1).getV_id().longValue());
        assertEquals(17093158L, phasesForProj2.get(2).getV_id().longValue());

        assertTrue(pfo.getProjects().get(0).getTitle().equals("T-Mobile, Internet Architect"));
        assertFalse(pfo.getProjects().get(0).getActive());
        assertTrue(pfo.getProjects().get(0).getCode().equals("C11583"));
        assertEquals(pfo.getProjects().get(0).getClientRef().longValue(), 709814L);
        assertEquals(pfo.getProjects().get(0).getCustomerId(), null);
        assertEquals(pfo.getProjects().get(0).getLeader_ref().longValue(), 504354L);
        //type not uses
        //currency not used
        assertTrue(pfo.getProjects().get(0).getCreationDate().equals("2008-08-27T14:22:47"));
        //modifiedDate will change so check is not very useful here

        //phases
        JSONPhase phase = phasesForProj1.get(1);
        assertFalse(phase.getActive());
        assertTrue(phase.getDescription().equals("Proposal"));
        assertTrue(phase.getCode().equals("10_PROPOSAL"));
        assertEquals(3, phase.getStatus());
        assertTrue(phase.getSalesStatus().contains("30"));
        assertTrue(phase.getExternalValue().equals("96,000.00"));
        assertTrue(phase.getStartDate().equals(""));
        assertTrue(phase.getEndDate().equals(""));
        assertTrue(phase.getOfferedDate().equals("2008-08-27"));
        assertTrue(phase.getCompletionDate().equals(""));
        assertTrue(phase.getLostReason().contains("suitable resource"));
        assertTrue(phase.getCreationDate().equals("2008-08-27T14:25:38"));
        assertTrue(phase.getRejectionDate().equals("2008-10-31"));

        assertEquals(504354, phase.getPersonResponsible().longValue());
    }


    /**
     * Given id of org, correctly returns activities for org
     * Assert value match known values
     * also tests construction of JSON activities and their conversion to 'Entity.Activity's
     */
    @Test
    public void canGetActivitiesForOrganisation() {
        Long orgID = 9206250L; //existing Vertec organisation
        String uri = baseURI + "/organisation/" + orgID + "/activities";

        ActivitiesForAddressEntry afo = getFromVertec(uri, ActivitiesForAddressEntry.class).getBody();

        assertEquals(orgID, afo.getOrganisationId());
        assertTrue("Quanta Fluid Solutions Ltd".equals(afo.getName()));

        List<VRAPI.Entities.Activity> activities = afo.getActivities();

        assertTrue("Not al activities got", activities.size() >= 10);

        assertEquals(9206485L, activities.get(0).getVertecId().longValue());
        assertEquals(27450368L, activities.get(9).getVertecId().longValue());

        Activity activity = activities.get(0);
        assertTrue(activity.getDone());
        assertTrue(activity.getvType().equals("EMail"));
        assertTrue(activity.getText().contains("and Mr Peter Templeton (Quanta)"));
        assertTrue(activity.getDueDate().equals("2011-10-20"));
        assertTrue(activity.getDoneDate().equals("2016-06-17"));
        assertTrue(activity.getCreated().equals("2011-10-20T10:19:12"));

        assertEquals(activity.getVertecDealLink(), null);
        assertEquals(activity.getVertecProjectLink().longValue(), 9206384L);
        assertEquals(activity.getVertecOrganisationLink(), orgID);
        assertEquals(activity.getVertecContactLink(), null);
    }

    /*
    TESTS FOR GET allOrganisations
     */

    /**
     * Tests construction of JSON Organisations, as well as of 'Entities.Organisation's
     */
    @Test
    public void canGetListOfOrganisations() {
        Long orgid1 = 709814L;
        Long orgid2 = 9206250L;
        List<Long> orgids = new ArrayList<>();
        orgids.add(orgid1);
        orgids.add(orgid2);

        String uri = baseURI + "/organisations/" + idsAsString(orgids);

        OrganisationList organisationList = getFromVertec(uri, OrganisationList.class).getBody();

        assertEquals(orgids.size(), organisationList.getOrganisations().size());

        Organisation firstOrg = organisationList.getOrganisations().get(0);
        Organisation secOrg = organisationList.getOrganisations().get(1);

        assertEquals(orgids.get(0), firstOrg.getVertecId());
        assertEquals(5295L, firstOrg.getOwnerId().longValue());
        assertEquals(null, firstOrg.getParentOrganisation());

        assertTrue("Sales Team".equals(firstOrg.getOwnedOnVertecBy()));
        assertTrue("Deutsche Telekom".equals(firstOrg.getName()));
        assertTrue("".equals(firstOrg.getWebsite()));
        //category not set yet
        //nor is business domain
        assertTrue("".equals(firstOrg.getBuildingName()));
        assertTrue("".equals(firstOrg.getStreet_no()));
        assertTrue("Hatfield Business Park".equals(firstOrg.getStreet()));
        assertTrue("Hatfield".equals(firstOrg.getCity()));
        assertTrue("United Kingdom".equals(firstOrg.getCountry()));
        assertTrue("AL10 9BW".equals(firstOrg.getZip()));
        assertTrue("2002-01-18T15:47:03".equals(firstOrg.getCreated()));
        assertTrue(firstOrg.getActive());

        assertEquals(orgids.get(1), secOrg.getVertecId());
    }

    @Test
    public void canGetOrganisationByID() {
        Long orgID = 709814L; //actually exists

        String uri = baseURI + "/organisation/" + orgID;

        Organisation organsiation = getFromVertec(uri, Organisation.class).getBody();
        assertEquals(orgID, organsiation.getVertecId());
        assertEquals(5295L, organsiation.getOwnerId().longValue());
        assertEquals(null, organsiation.getParentOrganisation());

        assertTrue("Sales Team".equals(organsiation.getOwnedOnVertecBy()));
        assertTrue("Deutsche Telekom".equals(organsiation.getName()));
        assertTrue("".equals(organsiation.getWebsite()));
        //category not set yet
        //nor is business domain
        assertTrue("".equals(organsiation.getBuildingName()));
        assertTrue("".equals(organsiation.getStreet_no()));
        assertTrue("Hatfield Business Park".equals(organsiation.getStreet()));
        assertTrue("Hatfield".equals(organsiation.getCity()));
        assertTrue("United Kingdom".equals(organsiation.getCountry()));
        assertTrue("AL10 9BW".equals(organsiation.getZip()));
        assertTrue("2002-01-18T15:47:03".equals(organsiation.getCreated()));
        assertTrue(organsiation.getActive());
        assertEquals(13497634L, organsiation.getModifier().longValue());
    }

    /*
    TESTS FOR HELPER METHODS
     */

    /**
     * Given Document recieved from vertec, can extract obref ids
     */
    @Test
    public void getActivityIdsForOrganisationReturnsCorrectly() throws ParserConfigurationException, IOException, SAXException {
        OrganisationController oc = new OrganisationController();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("src/test/resources/response-activities-for-organisation"));

        List<Long> activityIds = oc.getObjrefsForOrganisationDocument(doc);

        assertEquals("Incorrect list size returned", 2, activityIds.size());
        assertTrue("Activity Id Missing", activityIds.contains(18550676L));
        assertTrue("Activity Id Missing", activityIds.contains(25764191L));

        doc = dBuilder.parse(new File("src/test/resources/response-projects-for-organisation"));

        List<Long> projectIds = oc.getObjrefsForOrganisationDocument(doc);

        assertEquals("Incorrect list size returned", 14, projectIds.size());
        assertTrue("Project Id Missing", projectIds.contains(17927567L));
        assertTrue("Project Id Missing", projectIds.contains(26471389L));
    }

    /**
     * Given Document recieved from vertec, can extract name of org
     */
    @Test
    public void getNameForOrganisationFromDocument() throws ParserConfigurationException, IOException, SAXException {
        OrganisationController oc = new OrganisationController();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("src/test/resources/response-activities-for-organisation"));

        String name = oc.getNameForOrganisationDocument(doc);

        assertEquals("Name incorrectly extracted", "HM Revenue & Customs", name);
    }


    /**
     * Given Organisation o and XMLORganisation vo will set owned_on_vertec_by correctly
     * check each case works
     */
    @Test
    public void canSetOwnedOnVertecBy() {
        Long salesTeamOwnedOrgId = 709814L;
        Long nonZUKOrg = 1359817L; //actually exists
        Long subTeamOrg = 15158065L; //actually exists
        List<Long> orgids = new ArrayList<>();

        orgids.add(salesTeamOwnedOrgId);
        orgids.add(subTeamOrg);
        orgids.add(TESTVertecOrganisation2);
        orgids.add(nonZUKOrg);

        String idsAsString = "";
        for (int i = 0; i < orgids.size(); i++) {
            if (i < orgids.size() - 1) {
                idsAsString += orgids.get(i) + ",";
            } else {
                idsAsString += orgids.get(i);
            }
        }

        String uri = baseURI + "/organisations/" + idsAsString;

        OrganisationList organisationList = getFromVertec(uri, OrganisationList.class).getBody();
        List<Organisation> orgs = organisationList.getOrganisations();

        System.out.println(orgs.get(0).getOwnedOnVertecBy());
        System.out.println(orgs.get(1).getOwnedOnVertecBy());
        System.out.println(orgs.get(2).getOwnedOnVertecBy());
        System.out.println(orgs.get(3).getOwnedOnVertecBy());
        assertTrue(orgs.get(0).getOwnedOnVertecBy().equals("Sales Team"));
        assertTrue(orgs.get(1).getOwnedOnVertecBy().equals("Not ZUK"));
        assertTrue(orgs.get(2).getOwnedOnVertecBy().equals("ZUK Sub Team"));
        assertTrue(orgs.get(3).getOwnedOnVertecBy().equals("No Owner"));
    }


    @Test
    @Ignore
    public void canGetAllOrganisationsInCommonRepresentation() throws IOException {

        String uri = baseURI + "/organisations/all";
        ResponseEntity<OrganisationList> res = getFromVertec(uri, OrganisationList.class);
        System.out.println(res.getBody());
        new File("/Users/gebo/IdeaProjects/VRAPI/src/test/resources/ZUKOrganisation").createNewFile();
        FileWriter file = new FileWriter("/Users/gebo/IdeaProjects/VRAPI/src/test/resources/ZUKOrganisation");
        file.write(res.getBody().toString());
        file.close();

    }

    @Test
    public void readsAddressCorrectly() {
        VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation o = new VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation();
        o.setName("Hubba");
        o.setAdditionalAddressName("");
        o.setStreetAddress("16, Street, City, ZIP, Country");
        o.setZip("ZIP");

        assertEquals(o.getStreetAddress(), o.getfullAddress());

        o = new VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation();
        o.setName("org");
        o.setAdditionalAddressName("Building");
        o.setStreetAddress("16 Street, City, ZIP, Country");
        o.setZip("ZIP");

        assertEquals("Building, " + o.getStreetAddress(), o.getfullAddress());

        o = new VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation();
        o.setName("Bubba");
        o.setAdditionalAddressName("Building");
        o.setStreetAddress("Street_no Street");
        o.setCity("City");
        o.setZip("ZIP");
        o.setCountry("Country");

        assertEquals("Building, Street_no Street, City, ZIP, Country", o.getfullAddress());
    }

    @Test
    public void organisationEqualsWorks() {
        Organisation organisation = new Organisation();
        organisation.setName("Name");
        organisation.setVertecId(1L);
        organisation.setFullAddress("Building, Street_no Street, City, ZIP, Country");
        organisation.setActive(true);
        organisation.setWebsite("website.net");
        organisation.setOwnerId(2L);


        Organisation org2 = new Organisation();
        org2.setName("Name");
        org2.setVertecId(1L);
        org2.setBuildingName("Building");
        org2.setStreet_no("Street_no");
        org2.setStreet("Street");
        org2.setCity("City");
        org2.setZip("ZIP");
        org2.setCountry("Country");
        org2.setActive(true);
        org2.setWebsite("website.net");
        org2.setOwnerId(2L);

        System.out.println(organisation.toJsonString());
        System.out.println(org2.toJsonString());

        assertTrue(organisation.equalsForUpdateAssertion(org2));
    }

    @Test
    public void canReplaceSpecialCharacter() {

        String s = "blah&amp;blah&amp;blah&amp;&amp;";
        String r = Organisation.rectifySpecialCharacters(s);
        assertEquals("blah&blah&blah&&", r);

    }



}
