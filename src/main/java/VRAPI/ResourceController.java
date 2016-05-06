package VRAPI;
import java.net.URI;
import java.util.*;

import VRAPI.ContainerJSON.JSONContact;
import VRAPI.ContainerJSON.JSONOrganisation;
import VRAPI.ContainerJSON.ZUKResponse;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ResourceController {

    private String VipAddress;
    private String VportNr;
    private String OwnIpAddress;
    private String OwnPortNr; //To be used for querying Vertec --XML Marshaller
    private String username;
    private String password;
    private RestTemplate rest;
    public ContactComparator comparator;

    public ResourceController() {
        //IpAddress:portNum of VertecServer
        this.VipAddress = "172.18.10.54";
        this.VportNr = "8095";

        this.OwnIpAddress = "localhost";
        this.OwnPortNr = "9999";

        //set resttemplate message converters
        this.rest = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);
        converters.add(jaxbMC);
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        rest.setMessageConverters(converters);

        //TODO: replace with proper authenitcation
        MyAccessCredentials creds = new MyAccessCredentials();

        this.username = creds.getUserName();
        this.password = creds.getPass();

        this.comparator = new ContactComparator();
    }
//------------------------------------------------------------------------------------------------------------Paths
    @Autowired
    private HttpServletRequest request;

    //TODO: add appropriate response codes

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {

        RequestEntity<String> req;
        String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
        try {
            authorize();
            String xmlQuery = getXMLQuery_ping();
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerTeam.Envelope> res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);

            checkResHasInfo(res.getBody());

        } catch (Exception e) {
            //hopefully will only happen when response returns Fault from XML Interface, then test to see whether incorret username and pwd, or limited access
            System.out.println("Did not recieve Team, attempting to recieve error message");
            try {

                req = new RequestEntity<>(getXMLQuery_ping(), HttpMethod.POST, new URI(uri));
                ResponseEntity<VRAPI.ContainerError.Envelope> res = this.rest.exchange(req, VRAPI.ContainerError.Envelope.class);

                String errorDetail = res.getBody().getBody().getFault().getDetails().getDetailitem().get(0);
                System.out.println(errorDetail);
                if (errorDetail.contains("Error: Authentication failure. Wrong User Name or Password")) {
                    return "Ping Failed: Wrong Username or Password recieved in request header";
                } else {
                    return "Partial Failure: Username and Password provided do not have sufficient permissions to access all Vertec Data. Some queries may return missing or no information";
                }

            } catch (Exception newe) {
                return "Unhandled Error in server: " + newe;
            }
        }

        return "Success!";
    }

    private void checkResHasInfo(VRAPI.ContainerTeam.Envelope envelope) throws Exception {
        if (envelope.getBody().getQueryResponse() == null) {
            throw new XMLFailureException();
        }
    }

    private class XMLFailureException extends Exception {
        private XMLFailureException() {

        }

        public XMLFailureException(String message) {
            super(message);
        }
    }

    private void authorize() throws Exception {
        try {
            String userpwd = request.getHeader("Authorization");
            String[] both = userpwd.split(":");
            this.username = both[0];
            this.password = both[1];
        } catch (Exception e) {
             throw new Exception("Request failed: Authorization header incorrectly set");
        }
    }

    @RequestMapping(value = "/organisations/ZUK", method = RequestMethod.GET)
    public String getZUKOrganisations() {
        List<Long> teamIds;
        List<Long> addressIds;
        List<List<Long>> contactIdsAndOrgsIds;
        List<Long> contactIds;
        List<Long> orgIds;
        List<VRAPI.ContainerDetailedContact.Contact> contacts;
        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs;
        ZUKResponse zuk;

        try {

            authorize();
            teamIds = getZUKTeamMemberIds();

        } catch (Exception e) {
            //hopefully will only happen when response returns Fault from XML Interface, then test to see whether incorret username and pwd, or limited access
            return e.toString();
        }


        addressIds           = getSupervisedAddresses(teamIds);

        contactIdsAndOrgsIds = getSimpleContactsandOrgs(addressIds);

        contactIds           = contactIdsAndOrgsIds.get(0);

        orgIds               = contactIdsAndOrgsIds.get(1);

        contacts             = getDetailedContacts(contactIds);

        orgs                 = getOrganisations(orgIds);

        zuk                  = buildZUKResponse(contacts, orgs);

        return zuk.toString();
    }



    //------------------------------------------------------------------------------------------------------------Helper Methods
    //TODO: make xml access methods private, adjust tests: http://stackoverflow.com/questions/34571/how-to-test-a-class-that-has-private-methods-fields-or-inner-classes
    public List<Long> getZUKTeamMemberIds() throws Exception {
        RequestEntity<String> req;
        List<Long> ids = new ArrayList<>();
        ResponseEntity<VRAPI.ContainerTeam.Envelope> res;

        String xmlQuery = getXMLQuery_LeadersTeam();
        String uri = "http://" + VipAddress + ":" + VportNr + "/xml";

        req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));

        Exception exception = null;

        try {
            res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);
            checkResHasInfo(res.getBody());
            ids = res.getBody().getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects();

        } catch (XMLFailureException e) {
            System.out.println("XMLException thrown by getZUKTeamMemberIds");
            System.out.println("Did not recieve Team, attempting to recieve error message");
            try {
                ResponseEntity<VRAPI.ContainerError.Envelope> rese = this.rest.exchange(req, VRAPI.ContainerError.Envelope.class);

                String errorDetail = rese.getBody().getBody().getFault().getDetails().getDetailitem().get(0);
                System.out.println(errorDetail);

                if (errorDetail.contains("Error: Authentication failure. Wrong User Name or Password")) {
                    exception = new XMLFailureException("Ping Failed: Wrong Username or Password recieved in request header");
                } else {
                    exception =  new XMLFailureException("Partial Failure: Username and Password provided do not have sufficient permissions to access all Vertec Data. Some queries may return missing or no information");
                }

            } catch (Exception newe) {
                exception = new Exception("Unhandled Error in server: " + newe.toString());
            }
        } catch (Exception e) {
            exception = new Exception("Unhandled Error in server" + e);
        }

        if (exception != null) {
            throw exception;
        }

        return ids;
    }

    public List<Long> getSupervisedAddresses(List<Long> supervisorIds){
        RequestEntity<String> req;
        List<Long> ids = new ArrayList<>();
        Set<Long> uniqueIds = new HashSet<>();
        try {

            String xmlQuery = getXMLQuery_SupervisedAddresses(supervisorIds);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerAddresses.Envelope> res = this.rest.exchange(req, VRAPI.ContainerAddresses.Envelope.class);
            VRAPI.ContainerAddresses.Envelope env = res.getBody();

            for(VRAPI.ContainerAddresses.ProjectWorker w : env.getBody().getQueryResponse().getWorkers()){
                if (w.getActive()) {
                    ids.addAll(w.getAddresses().getList().getObjects());
                }
            }


            uniqueIds.addAll(ids);

        } catch (Exception e) {
            System.out.println("ERROR IN GETTING Supervised Addresses" + e);
        }

        ids.clear();
        ids.addAll(uniqueIds);

        return ids;
    }

    public List<List<Long>> getSimpleContactsandOrgs(List<Long> contactIds) {
        RequestEntity<String> req;
        //no need for set as well as list as objids queried from set
        List<Long> cIds = new ArrayList<>();
        List<Long> oIds = new ArrayList<>();
        List<List<Long>> rIds = new ArrayList<>();
        try {

            String xmlQuery = getXMLQuery_GetContacts(contactIds);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerSimpleContactOrganisation.Envelope> res = this.rest.exchange(req, VRAPI.ContainerSimpleContactOrganisation.Envelope.class);
            VRAPI.ContainerSimpleContactOrganisation.Envelope env = res.getBody();

            for(VRAPI.ContainerSimpleContactOrganisation.Contact c : env.getBody().getQueryResponse().getContacts()) {
                cIds.add(c.getObjid());
            }
            for(VRAPI.ContainerSimpleContactOrganisation.Organisation o : env.getBody().getQueryResponse().getOrgs()) {
                oIds.add(o.getObjid());
            }

        } catch (Exception e){
            System.out.println("ERROR IN GETTING SIMPLE CONTACTS: " + e);
        }

        rIds.add(cIds);
        rIds.add(oIds);

        return rIds;
    }

    public List<VRAPI.ContainerDetailedContact.Contact> getDetailedContacts(List<Long> ids) {
        RequestEntity<String> req;
        List<VRAPI.ContainerDetailedContact.Contact> contacts = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetContactDetails(ids);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerDetailedContact.Envelope> res = this.rest.exchange(req, VRAPI.ContainerDetailedContact.Envelope.class);

            for(VRAPI.ContainerDetailedContact.Contact c : res.getBody().getBody().getQueryResponse().getContactList()){
                if(c.getActive()){
                    contacts.add(c);
                }
            }


        } catch ( Exception e){
            System.out.println("Exception in getDetailedContacts: " + e);
        }

        Collections.sort(contacts, this.comparator);
        return contacts;
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        RequestEntity<String> req;
        ResponseEntity<VRAPI.ContainerDetailedOrganisation.Envelope> res = null;
        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetOrganisationDetails(ids);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            res = this.rest.exchange(req, VRAPI.ContainerDetailedOrganisation.Envelope.class);

            for(VRAPI.ContainerDetailedOrganisation.Organisation o : res.getBody().getBody().getQueryResponse().getOrganisationList()){
                if(o.getActive()){
                    orgs.add(o);
                }
            }


        } catch ( Exception e){
            System.out.println("Exception in getDetailed Organisations: " + e);
            System.out.println("Exception in getDetailed Organisations: " + res);
        }
        return orgs;
    }


    public String getOwnPortNr() {
        return OwnPortNr;
    }

    public String getOwnIpAddress() {
        return OwnIpAddress;
    }

    private String getXMLQuery_ping() {
        return getXMLQuery_LeadersTeam();
    }

    private String getXMLQuery_LeadersTeam() {
        return "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
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


    private String getXMLQuery_SupervisedAddresses(List<Long> memberIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for(Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>BetreuteAdressen</member>\n" + //will return list of obj ref for each company
                "        <member>Aktiv</member>\n" + //will return list of obj ref for each company
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_GetContacts(List<Long> contactIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username+ "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : contactIds) {
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

    private String getXMLQuery_GetContactDetails(List<Long> contactIds){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : contactIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>Firma</member>\n" + //will return objref to parent firma
                "        <member>StandardEMail</member>\n" +
                "        <member>StandardTelefon</member>\n" +
                "        <member>StandardMobile</member>\n" +
                "        <member>Vorname</member>\n" +
                "        <member>betreuer</member>\n" +
                "        <member>ModifiedDateTime</member>\n" +
                "        <member>creationDateTime</member>\n" +
                "        <member>aktiv</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;

    }

    private String getXMLQuery_GetOrganisationDetails(List<Long> ids){
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";

        for(Long id : ids) {
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
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }


    public ZUKResponse buildZUKResponse(List<VRAPI.ContainerDetailedContact.Contact> contacts, List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs){
        ZUKResponse res = new ZUKResponse();
        List<JSONContact> dangle = new ArrayList<>();
        int ccounter = 0;



        List<JSONOrganisation> jsonOrgs = new ArrayList<>();
        for(VRAPI.ContainerDetailedOrganisation.Organisation vo : orgs) {

            JSONOrganisation org = new JSONOrganisation(vo);

            List<JSONContact> orgContacts = new ArrayList<>();

            for(Iterator<VRAPI.ContainerDetailedContact.Contact> vc = contacts.listIterator();vc.hasNext();){
                VRAPI.ContainerDetailedContact.Contact a = vc.next();

                if(a.getOrganisation() == null) continue;
                if(a.getOrganisation().getObjref() == null) continue;

                if(vo.getObjId().longValue() == a.getOrganisation().getObjref().longValue()) {
                    JSONContact c = new JSONContact(a);
                    orgContacts.add(c);
                    ccounter++;
                    vc.remove();
                }
            }

            org.setContacts(orgContacts);

            jsonOrgs.add(org);

        }

        for(Iterator<VRAPI.ContainerDetailedContact.Contact> vc = contacts.listIterator();vc.hasNext();){
            VRAPI.ContainerDetailedContact.Contact a = vc.next();
                JSONContact c = new JSONContact(a);
                dangle.add(c);
        }

        res.setDanglingContacts(dangle);
        res.setOrganisationList(jsonOrgs);

        return res;
    }

    //------------------------------------------------------------------------------------------------------------Comparator
    public class ContactComparator implements Comparator<VRAPI.ContainerDetailedContact.Contact> {

        @Override
        public int compare(VRAPI.ContainerDetailedContact.Contact a, VRAPI.ContainerDetailedContact.Contact b) {
            if((a.getOrganisation() == null || a.getOrganisation().getObjref() == null)
                    && (b.getOrganisation() == null || b.getOrganisation().getObjref() == null)) return 0;
            if((a.getOrganisation() == null) || (a.getOrganisation().getObjref() == null)) return -1;
            if((b.getOrganisation() == null) || (b.getOrganisation().getObjref() == null)) return 1;

            Long aref = a.getOrganisation().getObjref();
            Long bref = b.getOrganisation().getObjref();
            return aref < bref ? -1 : (aref.longValue() == bref.longValue() ? 0 : 1);
        }

    }

}
