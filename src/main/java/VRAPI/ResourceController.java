package VRAPI;
import java.net.URI;
import java.util.*;

import VRAPI.ContainerJSON.JSONOrganisation;
import VRAPI.ContainerJSON.OrganisationList;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ResourceController {

    private String VipAddress;
    private String VportNr;
    private String OwnIpAddress;
    private String OwnPortNr; //To be used for querying Vertec --XML Marshaller
    private String username;
    private String password;
    private RestTemplate rest;

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
        MyCredentials creds = new MyCredentials();

        this.username = creds.getUserName();
        this.password = creds.getPass();
    }

    //TODO: make xml access methods private, adjust tests: http://stackoverflow.com/questions/34571/how-to-test-a-class-that-has-private-methods-fields-or-inner-classes
    public List<Long> getZUKTeamMemberIds() {
        RequestEntity<String> req;
        List<Long> ids = new ArrayList<>();
        try {

            String xmlQuery = getXMLQuery_LeadersTeam();
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerTeam.Envelope> res = this.rest.exchange(req, VRAPI.ContainerTeam.Envelope.class);

            ids = res.getBody().getBody().getQueryResponse().getWorkers().get(0).getTeam().getList().getObjects();

        } catch (Exception e) {
            System.out.println("ERROR IN GETTING ZUK TEAM MEMBERS" + e);
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
                if (w.getObjid() == 8619482L) {
                    System.out.println("Justin has " + w.getAddresses().getList().getObjects().size() + " contacts, very very impressive");
                }
                if (w.getObjid() == 5295L) {
                    System.out.println("Wolfgang has " + w.getAddresses().getList().getObjects().size() + " contacts, very very impressive");
                }
                if (w.getActive()) {
                    ids.addAll(w.getAddresses().getList().getObjects());
                }
            }

            System.out.println("LIST: " + ids.size());

            uniqueIds.addAll(ids);

            System.out.println("SET: " + ids.size());

        } catch (Exception e) {
            System.out.println("ERROR IN GETTING Supervised Addresses" + e);
        }

        ids.clear();
        ids.addAll(uniqueIds);

        System.out.println(ids);

        return ids;
    }

    public List<Long> getSimpleContacts(List<Long> contactIds) {
        RequestEntity<String> req;
        //no need for set as well as list as objids queried from set
        List<Long> ids = new ArrayList<>();
        try {

            String xmlQuery = getXMLQuery_GetContacts(contactIds);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerSimpleContact.Envelope> res = this.rest.exchange(req, VRAPI.ContainerSimpleContact.Envelope.class);
            VRAPI.ContainerSimpleContact.Envelope env = res.getBody();

            for(VRAPI.ContainerSimpleContact.Contact c : env.getBody().getQueryResponse().getContacts()) {
                ids.add(c.getObjid());
            }

        } catch (Exception e){
            System.out.println("ERROR IN GETTING SIMPLE CONTACTS: " + e);
        }

        return ids;
    }

    public List<VRAPI.ContainerDetailedContact.Contact> getDetailedContacts(List<Long> ids) {
        RequestEntity<String> req;
        List<VRAPI.ContainerDetailedContact.Contact> contacts = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetContactDetails(ids);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerDetailedContact.Envelope> res = this.rest.exchange(req, VRAPI.ContainerDetailedContact.Envelope.class);

            contacts = res.getBody().getBody().getQueryResponse().getContactList();

        } catch ( Exception e){
            System.out.println("Exception in getDetailedContacts: " + e);
        }
        return contacts;
    }

    public List<VRAPI.ContainerDetailedOrganisation.Organisation> getOrganisations(List<Long> ids) {
        RequestEntity<String> req;
        List<VRAPI.ContainerDetailedOrganisation.Organisation> orgs = new ArrayList<>();
        try {
            String xmlQuery = getXMLQuery_GetOrganisationDetails(ids);
            String uri = "http://" + VipAddress + ":" + VportNr + "/xml";
            req = new RequestEntity<>(xmlQuery, HttpMethod.POST, new URI(uri));
            ResponseEntity<VRAPI.ContainerDetailedOrganisation.Envelope> res = this.rest.exchange(req, VRAPI.ContainerDetailedOrganisation.Envelope.class);

            orgs = res.getBody().getBody().getQueryResponse().getOrganisationList();

        } catch ( Exception e){
            System.out.println("Exception in getDetailed Organisations: " + e);
        }
        return orgs;
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }

    public String getOwnPortNr() {
        return OwnPortNr;
    }

    public String getOwnIpAddress() {
        return OwnIpAddress;
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
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private class ContactComparator implements Comparator<VRAPI.ContainerDetailedContact.Contact> {

        @Override
        public int compare(VRAPI.ContainerDetailedContact.Contact a, VRAPI.ContainerDetailedContact.Contact b) {
            Long aref = a.getOrganisation().getObjref();
            Long bref = b.getOrganisation().getObjref();
            return aref < bref ? -1 : (aref == bref ? 0 : 1);
        }

    }

}
