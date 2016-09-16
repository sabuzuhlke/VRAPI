package VRAPI.Util;

import VRAPI.Entities.Organisation;
import VRAPI.Exceptions.NoIdSuppliedException;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;

import java.util.Collection;
import java.util.List;

/**
 * This class provides acccess to the XML queries we pass to vertec for various requests
 */
public class QueryBuilder {

    private final String header;

    //Constructed using a vertec user name and password (usually provided by incoming request)
    public QueryBuilder(String username, String password) {
        this.header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + username + "</Name>\n" +
                "      <Password>" + password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";
    }

//---------------------------------------------------------------------------------------------------------------------- GET TEAM and SUBTEAM

    //Asks for the list of employees whose supervisor has id {id}
    String getSubTeamOfMember(Long id) {
        return header +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>" + id + "</objref>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>briefEmail</member>\n" + //will return objref for each member of team
                "        <member>Team</member>\n" + //will return objref for each member of team
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    ////asks for list of employee ids whose supervisor had id {id}
    public String getXMLQuery_TeamIdsAndSubTeam(Long id) {

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //---------------------------------------------------------------------------------------------------------------------- POST
    //Builds XML query for posting a new organistion on vertec from organisation POJO
    String postOrganisation(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation organisation) {

        String body = "<Body>\n" +
                "    <Create>\n" +
                "      <Firma> \n";

        body += "<name>" + organisation.getName() + "</name>\n";
        body += "<standardAdresse>" + organisation.getName() + "</standardAdresse>\n";
        body += "<standardLand>" + organisation.getCountry() + "</standardLand>\n";
        body += "<standardOrt>" + organisation.getCity() + "</standardOrt>\n";
        body += "<standardPLZ>" + organisation.getZip() + "</standardPLZ>\n";
        body += "<standardHomepage>" + organisation.getWebsite() + "</standardHomepage>\n";
        body += "<zusatz>" + organisation.getAdditionalAddressName() + "</zusatz>\n";
        body += "<aktiv>" + (organisation.getActive() ? "1" : "0") + "</aktiv>\n";
        if (organisation.getParentFirm().getObjref() != null) {
            body += "<mutterfirma>" + organisation.getParentFirm().getObjref() + "</mutterfirma>\n";
        }
        if (organisation.getDaughterFirm() != null
                && organisation.getDaughterFirm().getObjlist() != null
                && !organisation.getDaughterFirm().getObjlist().getObjref().isEmpty()) {
            body += "<tochterfirma>";
            for (Long objref : organisation.getDaughterFirm().getObjlist().getObjref()) {
                body += "<objref>" + objref + "</objref>\n";
            }
            body += "</tochterfirma>";

        }
        body += "<betreuer> <objref>" + organisation.getPersonResponsible().getObjref() + "</objref> </beteuer>\n";

        body += "</Firma>\n</Create>\n</Body>\n</Envelope>";

        return header + body;
    }


//---------------------------------------------------------------------------------------------------------------------- OLD

    /**
     * Wolfgang's Team
     */
    public String getLeadersTeam() {
        return header +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <ocl>projektBearbeiter->select(loginName='wje')</ocl>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" + //will return objref for each member of team
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    //Asks for the details of each employee id provided in the collection
    public String getTeamDetails(Collection<Long> teamIDs) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for (Long id : teamIDs) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>name</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "        <member>briefEmail</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //Asks for a list of Adresses (Contacts and Organisations) that are owned on vertec by the owner ids supplied, as well as whether that owner is active and their email.
    public String getSupervisedAddresses(Collection<Long> memberIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for (Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>BetreuteAdressen</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "        <member>briefEmail</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //Asks for a list of ids of both contacts and organisations, used to seperate the two into their own lists
    public String getContactAndOrganisationIds(Collection<Long> contactIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : contactIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //ASks for detailed information on the contacts ids provided
    public String getDetailedContact(List<Long> contactIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : contactIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" +
                "        <member>Firma</member>\n" + //will return objref to parent firma
                "        <member>StandardEMail</member>\n" +
                "        <member>StandardTelefon</member>\n" +
                "        <member>StandardMobile</member>\n" +
                "        <member>Vorname</member>\n" +
                "        <member>betreuer</member>\n" +
                "        <member>ModifiedDateTime</member>\n" +
                "        <member>creationDateTime</member>\n" +
                "        <member>aktiv</member>\n" +
                "        <member>stellung</member>\n" +
                "        <member>kommmittel</member>\n" +
                "        <member>fromLinks</member>\n" +
                "        <member>GenericContainers</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //Ask for details of the organisaitons ids provided
    public String getOrganisationDetails(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>betreuer</member>\n" +
                "        <member>StandardAdresse</member>\n" +
                "        <member>StandardLand</member>\n" +
                "        <member>StandardOrt</member>\n" +
                "        <member>StandardPLZ</member>\n" +
                "        <member>zusatz</member>\n" +
                "        <member>aktiv</member>\n" +
                "        <member>ModifiedDateTime</member>\n" +
                "        <member>creationDateTime</member>\n" +
                "        <member>mutterfirma</member>\n" +
                "        <member>tochterfirmen</member>\n" +
                "        <member>kontakte</member>\n" +
                "        <member>standardHomepage</member>\n" +
                "        <member>modifier</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    @SuppressWarnings("all")
    //creates XML to update a organisation with new information (WARNING: empty fields will be set to empty if nothing is provided in field)
    public String updateOrgansiation(Organisation org) throws NoIdSuppliedException {

        if (org.getVertecId() == null)
            throw new NoIdSuppliedException("Cannot update organisation that has not got an ID");

        String bodyStart = "<Body>\n" +
                "   <Update>\n" +
                "       <Firma>\n";
        String body =
                "           <objref>" + org.getVertecId() + "</objref>\n" +
                        "           <name>" + "<![CDATA[" + (org.getName() == null ? "" : org.getName()) + "]]></name>\n" +
                        "           <betreuer>\n<objref>" + (org.getOwnerId() == null ? "" : org.getOwnerId()) + "</objref>\n</betreuer>\n";

        //Full address will be stored in the standardAddresse field as we would need to contact the google api, to break down the address for us otherwise
        body += "           <standardAdresse><![CDATA[" + org.getFullAddress() + "]]></standardAdresse>\n";


        body += "           <standardLand>" + "<![CDATA[" + (org.getCountry() == null ? "" : org.getCountry()) + "]]>" + "</standardLand>\n" +
                "           <standardOrt>" + "<![CDATA[" + (org.getCity() == null ? "" : org.getCity()) + "]]>" +"</standardOrt>\n" +
                "           <standardPLZ>" + (org.getZip() == null ? "" : org.getZip()) + "</standardPLZ>\n" +
                "           <zusatz>" + "<![CDATA[" + (org.getBuildingName() == null ? "" : org.getBuildingName()) + "]]>" + "</zusatz>\n" +
                "           <aktiv>" + (org.getActive() == null ? "0" : (org.getActive() ? "1" : "0")) + "</aktiv>\n" +
                // "           <mutterfirma><objref>" +
                //(org.getParentOrganisation() == null ? 0 : org.getParentOrganisation()) +
                //    "           </objref></mutterfirma>\n" +
                "           <standardHomepage>" + (org.getWebsite() == null ? "" : org.getWebsite()) + "</standardHomepage>\n" +
                //TODO Figure out what to do with category , business domain and org relationships
                "       </Firma>\n" +
                "   </Update>\n" +
                "   </Body>\n" +
                "</Envelope>\n";

        return header + bodyStart + body;

    }

    //Creates and organisation of given name (WARNING: attempting to provide extra details will result in write access denied being retured, but the write will have succeeded)
    //use this function to create an organisation as this successfully return the id of the organisation, then use this id to update the rest of the information
    public String createOrgansiation(Organisation organisation) {
        String bodyStart = "<Body>\n" +
                "   <Create>\n" +
                "       <Firma>\n";
        String bodyEnd =
                "           <name><![CDATA[" + organisation.getName() + "]]></name>\n" +
                        "       </Firma>\n" +
                        "   </Create>\n" +
                        "   </Body>\n" +
                        "</Envelope>\n";

        System.out.println(header+bodyStart+bodyEnd);
        return header + bodyStart + bodyEnd;

    }

    //Asks for details for a project of given id.
    public String getProjectDetails(Collection<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Kunde</member>\n" +
                "        <member>Aktiv</member>\n" +
                "        <member>Phasen</member>\n" +
                "        <member>projektnummer</member>\n" +
                "        <member>hb</member>\n" +
                "        <member>hbstv</member>\n" +
                "        <member>code</member>\n" +
                "        <member>auftraggeber</member>\n" +
                "        <member>typ</member>\n" +
                "        <member>waehrung</member>\n" +
                "        <member>modifieddatetime</member>\n" +
                "        <member>creationdatetime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for the projects that are lead by users with the given ids
    public String getProjectIds(List<Long> memberIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for (Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>bearbProjekte</member>\n" +
                "        <member>Aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for project phase details by id
    public String getProjectPhases(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>aktiv</member>\n" +
                "           <member>PlanWertExt</member>\n" +
                "           <member>Status</member>\n" +
                "           <member>Code</member>\n" +
                "           <member>Beschreibung</member>\n" +
                "           <member>AbschlussDatum</member>\n" +
                "           <member>abgelehntDatum</member>\n" +
                "           <member>Verantwortlicher</member>\n" +
                "           <member>offertdatum</member>\n" +
                "           <member>startDatum</member>\n" +
                "           <member>endDatum</member>\n" +
                "           <member>verkaufsstatus</member>\n" +
                "           <member>absagegrundtext</member>\n" +
                "           <member>creationdatetime</member>\n" +
                "           <member>modifieddatetime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    //asks for the text description of project types by id
    public String getProjectTypes(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    //asks for the text description of currencies by id e.g. 'GBP'
    public String getCurrency(Long id) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";


        return header + bodyStart + bodyEnd;
    }

    //asks for the activites assigned to the members whose ids you provide
    public String getActivityIds(List<Long> memberIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for (Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>pendAktivitaeten</member>\n" + //will return a list of Activities assigned to teamMember
                "        <member>aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for details of activities by id
    public String getActivities(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>text</member>\n" +
                "           <member>datum</member>\n" +
                "           <member>erledigt</member>\n" +
                "           <member>phase</member>\n" +
                "           <member>projekt</member>\n" +
                "           <member>typ</member>\n" +
                "           <member>adresseintrag</member>\n" +
                "           <member>titel</member>\n" +
                "           <member>zustaendig</member>\n" +
                "           <member>erledigtdatum</member>\n" +
                "           <member>creationDateTime</member>\n" +
                "           <member>modifiedDateTime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for the text description of activities
    public String getActivityTypes(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for the email of an employee by id
    public String getUserEmail(Long id) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>briefEmail</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for a list of projects with a given code
    public String getProjectByCode(String code) {
        String body = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "         <ocl>projekt->select(code=\'" + code + "\')</ocl>" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Kunde</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "        <member>Phasen</member>\n" +
                "        <member>projektnummer</member>\n" +
                "        <member>hb</member>\n" +
                "        <member>hBStv</member>\n" +
                "        <member>code</member>\n" +
                "        <member>auftraggeber</member>\n" +
                "        <member>typ</member>\n" +
                "        <member>waehrung</member>\n" +
                "        <member>modifieddatetime</member>\n" +
                "        <member>creationdatetime</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + body;
    }

    //asks for all the activites that have been assigned to a particular addressEntry (organisation/contact)
    public String getActivitiesForAddressEntry(Long id) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>adressAktivitaeten</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;

    }

    //asks for all projects linked to an organisation
    public String getProjectsForOrganisation(Long id) {

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>projekte</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //asks for the contacts that are part of a given organisation
    public String getContactsForOrganisation(Long id) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>kontakte</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //given an organisation id, will set that organisation to active/inactive
    public String setOrganisationActive(Boolean active, Long id) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Firma> \n" +
                "                <objref>" + id + "</objref>\n" +
                "                <aktiv>" + (active ? 1 : 0) + "</aktiv>\n" +
                "      </Firma>\n" +
                "    </Update>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + body;
    }

    //given a contact id, will set that contact to active/inactive
    public String setContactActive(Boolean active, Long id) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Kontakt> \n" +
                "                <objref>" + id + "</objref>\n" +
                "                <aktiv>" + (active ? 1 : 0) + "</aktiv>\n" +
                "      </Kontakt>\n" +
                "    </Update>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + body;
    }

    //given an id of some entitiy will retrieve the type of that entity (WARNING: if recieving response into entity, expected type must be known otherwise unmarshalling will fail)
    public String getTypeOfId(Long id) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //given a contact id and an organisation id, will set that contact to be part of that organisation (used for organisation merging)
    public String setContactOrganisationLink(Long id, Long orgId) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Kontakt> \n" +
                "       <objref>" + id + "</objref>\n" +
                "       <firma>\n" +
                "           <objref>" + orgId + "</objref>\n" +
                "       </firma>\n" +
                "      </Kontakt>\n" +
                "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + body;
    }

    //given a project id and an organisation id, will set that project to be linked to that organisaiton (used for organisation merging)
    public String setProjectOrgLink(Long projId, Long orgId) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Projekt> \n" +
                "       <objref>" + projId + "</objref>\n" +
                "       <kunde>\n" +
                "           <objref>" + orgId + "</objref>\n" +
                "       </kunde>\n" +
                "      </Projekt>\n" +
                "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + body;
    }

    //given activity id and organisation id will set activity to be linked to that organisaiton (used for organisation merging)
    public String setActivityOrgLink(Long activityID, Long orgID) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Aktivitaet> \n" +
                "       <objref>" + activityID + "</objref>\n" +
                "       <adresseintrag>\n" +
                "           <objref>" + orgID + "</objref>\n" +
                "       </adresseintrag>\n" +
                "      </Aktivitaet>\n" +
                "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + body;
    }

    //given an activity id and a contact id will set that activity to be linked to that contact (used for contact merging)
    public String setActivityContactLink(Long activityId, Long contactId) {
        String body = "<Body>\n" +
                "  <Update>\n" +
                "     <Aktivitaet> \n" +
                "       <objref>" + activityId + "</objref>\n" +
                "       <adresseintrag>\n" +
                "           <objref>" + contactId + "</objref>\n" +
                "       </adresseintrag>\n" +
                "      </Aktivitaet>\n" +
                "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + body;

    }

    //given some contact details ids e.g. phone numbers/email addresses will retrieve the details of such
    public String getContactMediumDetails(List<Long> kommMittelIds) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        for (Long id : kommMittelIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>priority</member>\n" +
                "           <member>zieladresse</member>\n" +
                "           <member>typ</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //given ids of generic link containers will return the ids of entitiies which point to it and what entities it points to
    public String getGenericLinkContainers(List<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>links</member>\n" +
                "           <member>fromContainer</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
        return header + bodyStart + bodyEnd;
    }

    //given ids of some generic link containers will set the entity that points to them/ or they point to (use http tester and vertec test instance to figure out which way round this works) to the newId
    public String setFromContainerOfGLC(List<Long> glcids, Long newId) {
        String bodyBegin = "<Body>\n" +
                "  <Update>\n";
        String bodyEnd = "";
        for (Long id : glcids) {
            bodyEnd += "     <GenericLinkContainer> \n" +
                    "       <objref>" + id + "</objref>\n" +
                    "       <fromContainer>\n" +
                    "           <objref>" + newId + "</objref>\n" +
                    "       </fromContainer>\n" +
                    "      </GenericLinkContainer>\n";

        }
        bodyEnd += "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + bodyBegin + bodyEnd;
    }
    //given ids of some generic link containers will set the entity that points to them/ or they point to (use http tester and vertec test instance to figure out which way round this works) to the newId
    public String setLinksListToReplaceMergeIdWithSurvivorId(List<GenericLinkContainer> glcs, Long survivorId, Long mergingId) {

        String bodyBegin = "<Body>\n" +
                "  <Update>\n";
        String bodyEnd = "";

        for (GenericLinkContainer glc : glcs) {

            bodyEnd += "     <GenericLinkContainer> \n" +
                    "       <objref>" + glc.getObjid() + "</objref>\n" +
                    "        <links> <objlist>\n";

            Boolean didFindMergeId = false;
            for (Long link : glc.getLinks().getObjlist().getObjref()) {

                if (link == mergingId.longValue()) {

                    bodyEnd += "<objref>" + survivorId + "</objref>\n";
                    didFindMergeId = true;
                } else {
                    bodyEnd += "<objref>" + link + "</objref>\n";
                }

            }

            if (!didFindMergeId) {
                System.out.print("Could not find mergeId in list for mergeId: " + mergingId + ", survivorId: " + survivorId + ", glcId: " + glc.getObjid());
            }
            bodyEnd += "</objlist>  </links>" +
                    "      </GenericLinkContainer>\n";

        }

        bodyEnd += "    </Update>\n" +
                "  </Body>" +
                "</Envelope>";

        return header + bodyBegin + bodyEnd;

    }


}
