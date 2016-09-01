package VRAPI.Util;

import VRAPI.Entities.Organisation;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;

import java.util.Collection;
import java.util.List;

/**
 * Created by gebo on 02/06/2016.
 */
public class QueryBuilder {

    private final String header;

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
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    @SuppressWarnings("all")
    public String updateOrgansiation(Organisation org) throws NoIdSuppliedException {

        if (org.getVertecId() == null)
            throw new NoIdSuppliedException("Cannot update organisation that has not got an ID");

        String bodyStart = "<Body>\n" +
                "   <Update>\n" +
                "       <Firma>\n";
        String body =
                "           <objref>" + org.getVertecId() + "</objref>\n" +
                        "           <name>" + (org.getName() == null ? "" : org.getName()) + "</name>\n" +
                        "           <betreuer>\n<objref>" + (org.getOwnerId() == null ? "" : org.getOwnerId()) + "</objref>\n</betreuer>\n" +
                        "           <standardAdresse>" + ((org.getStreet_no() + ", " + org.getStreet()) == ", " ? "" : (org.getStreet_no() + ", " + org.getStreet())) + "</standardAdresse>\n" +
                        "           <standardLand>" + (org.getCountry() == null ? "" : org.getCountry()) + "</standardLand>\n" +
                        "           <standardOrt>" + (org.getCity() == null ? "" : org.getCity()) + "</standardOrt>\n" +
                        "           <standardPLZ>" + (org.getZip() == null ? "" : org.getZip()) + "</standardPLZ>\n" +
                        "           <zusatz>" + (org.getBuildingName() == null ? "" : org.getBuildingName()) + "</zusatz>\n" +
                        "           <aktiv>" + (org.getActive() == null ? "0" : (org.getActive() ? "1" : "0")) + "</aktiv>\n" +
                        "           <mutterfirma><objref>" +
                                        (org.getParentOrganisation() == null ? 0 : org.getParentOrganisation()) +
                        "           </objref></mutterfirma>\n" +
//                "           <tochterfirmen>" + (org.getActive() == null ? 0 : org.getActive()) + "<tochterfirmen>\n" +
                        "           <standardHomepage>" + (org.getWebsite() == null ? "" : org.getWebsite()) + "</standardHomepage>\n" +
                        //TODO Figure out what to do with category , business domain and org relationships
                        "       </Firma>\n" +
                        "   </Update>\n" +
                        "   </Body>\n" +
                        "</Envelope>\n";

        return header + bodyStart + body;

    }

    public String createOrgansiation(Organisation organisation) {
        String bodyStart = "<Body>\n" +
                "   <Create>\n" +
                "       <Firma>\n";
        String bodyEnd =
                        "           <name>" + organisation.getName() + "</name>\n" +
                        "       </Firma>\n" +
                        "   </Create>\n" +
                        "   </Body>\n" +
                        "</Envelope>\n";

        return header + bodyStart + bodyEnd;

    }

    public String getProjectDetails(Collection<Long> ids) {
        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Kunde</member>\n" + //will return list of obj ref for each company
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
                "        <member>bearbProjekte</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

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
