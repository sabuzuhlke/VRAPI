package VRAPI;
import java.beans.XMLEncoder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
    private String OwnPortNr;
    private RestTemplate xRestTemplate; //To be used for querying Vertec --XML Marshaller
    private String username;
    private String password;


    public ResourceController() {
        //IpAddress:portNum of VertecServer
        this.VipAddress = "172.18.10.54";
        this.VportNr = "8095";

        this.OwnIpAddress = "172.18.10.85";
        this.OwnPortNr = "9999";

        this.xRestTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);

        converters.add(jaxbMC);


        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        xRestTemplate.setMessageConverters(converters);
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }

    //===================================================================================================================Organisations
//    @RequestMapping(value="/organisations/London/",method=RequestMethod.GET)//          ----------------------London
//    //TODO: refactor so that contacts and betreuers are returned properly as well(Names etc. instead of objrefs)
//    public String getLondonOrganisations() {                                 //Returns Id, Address, Creator
//        String xml;
//        RequestEntity<String> req;
//        ResponseEntity<XMLEnvelope> res = null;
//        MyCredentials creds  = new MyCredentials();
//
//        System.out.println("Received REQ to /organisations/London/");
//
//        xml = "<Envelope>\n" +
//                "  <Header>\n" +
//                "    <BasicAuth>\n" +
//                "      <Name>" + creds.getUserName() + "</Name>\n" +
//                "      <Password>" + creds.getPass() + "</Password>\n" +
//                "      </BasicAuth>\n" +
//                "  </Header>\n" +
//                "\n" +
//                "  <Body>\n" +
//                "    <Query>\n" +
//                "      <Selection>\n" +
//                "        <ocl>firma->select(standardOrt='London')->select(aktiv)</ocl>\n" +
//                "      </Selection>\n" +
//                "      <Resultdef>\n" +
//                "        <member>Name</member>\n" +                 //Name of Organisation
//                "        <member>StandardAdresse</member>\n" +      //Street Address of Organisation
//                "        <member>StandardOrt</member>\n" +          //City Organisations is in
//                "        <member>StandardPLZ</member>\n" +          //ZIP code
//                "        <member>StandardLand</member>\n" +         //Country of Organisation
//                "        <member>Betreuer</member>\n" +             //Objref to Zuhlke user who is responsible for the Organisation (Owner)
//                "        <member>Creator</member>\n" +              //Objref to Zuhlke user who Created the Organisation Entry
//                "        <member>Kontakte</member>\n" +             // List of Objrefs of contacts from the organisation
//                "      </Resultdef>\n" +
//                "    </Query>\n" +
//                "  </Body>\n" +
//                "</Envelope>";
//        try{
//
//            req = new RequestEntity<>(xml, HttpMethod.POST,new URI("http://" + VipAddress + ":" + VportNr + "/xml"));
//            res = xRestTemplate.exchange(req, XMLEnvelope.class);
//            //System.out.println(res.toString());
//            //System.out.println(res.getBody().toJSONString() );
//        }
//        catch (Exception e){
//            System.out.println("Exception: " + e);
//        }
//        if(res != null){
//            System.out.println("Responding to REQ on /organisations/London/");
//            return res.getBody().toString();
//        }
//        else{
//
//            System.out.println("ERROR: NULL response to REQ on /organisations/London/ ");
//            return null;
//        }
//    }

    @RequestMapping(value="/organisations/ZUK/",method=RequestMethod.GET)
    public String getZukOrganisations(){
        String xml;
        RequestEntity<String> req;
        //ResponseEntity<XMLEnvelope> res = null;
        MyCredentials creds  = new MyCredentials();

        System.out.println("Received REQ to /organisations/ZUK/");
        //Get Wolfgang,
        //Get Management team
        //Get organisationids Wolfgang is responsible for
        //Get Organisationids others are responsible for
        //Convert to a set
        //get Organisations from set
        //Save objref to  organisation and kontakt and management mmbr relations
        //return Organisations with kontacts in JSON



        return "Not finished yet";

    }

//    public String getManagement(){
//        //TODO: FINISH MANAGEMENT! DOESNT WORK!
//
//        MyCredentials creds  = new MyCredentials();
//        String xml = getXMLQuery_LeadersTeam();
//
//        RequestEntity<String> req;
//        ResponseEntity<XMLEnvelope> res = null;
//
//        try{
//
//            System.out.println("Getting Management");
//            req = new RequestEntity<>(xml, HttpMethod.POST,new URI("http://" + VipAddress + ":" + VportNr + "/xml"));
//            res = xRestTemplate.exchange(req, XMLEnvelope.class);
//            //System.out.println(res.toString());
//            //System.out.println(res.getBody().toJSONString() );
//
//            System.out.println("Got Management");
//        }
//        catch (Exception e){
//            System.out.println("Exception: " + e);
//        }
//
//        List<XMLContact> cts = res.getBody().getBody().getQueryResponse().getContacts();
//        return "Not Implemented";
//    }
//
//    public List<XMLContact> getContacts(List<Long> Ids) {
//
//        MyCredentials creds  = new MyCredentials();
//        String xml = getXMLQuery_GetContacts(Ids);
//
//        RequestEntity<String> req;
//        ResponseEntity<XMLEnvelope> res = null;
//
//        try{
//
//            System.out.println("Getting contacts");
//            req = new RequestEntity<>(xml, HttpMethod.POST,new URI("http://" + VipAddress + ":" + VportNr + "/xml"));
//            res = xRestTemplate.exchange(req, XMLEnvelope.class);
//            //System.out.println(res.toString());
//            //System.out.println(res.getBody().toJSONString() );
//
//            System.out.println("Got contacts");
//        }
//        catch (Exception e){
//            System.out.println("Exception: " + e);
//        }
//
//        List<XMLContact> cts = res.getBody().getBody().getQueryResponse().getContacts();
//        return cts;
//
//    }

    public String getVipAddress() {
        return this.VipAddress;
    }

    public String getVportNr() {
        return this.VportNr;
    }

    public String getOwnPortNr() {
        return OwnPortNr;
    }

    public void setOwnPortNr(String ownPortNr) {
        OwnPortNr = ownPortNr;
    }

    public String getOwnIpAddress() {
        return OwnIpAddress;
    }

    public void setOwnIpAddress(String ownIpAddress) {
        OwnIpAddress = ownIpAddress;
    }

    public void setVportNr(String vportNr) {
        VportNr = vportNr;
    }

    public void setVipAddress(String vipAddress) {
        VipAddress = vipAddress;
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
                "        <ocl>projektBearbeiter->select(loginName='wje)</ocl>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" + //will return objref for each member of team
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private String getXMLQuery_TeamsResponsibleAddresses(List<Long> memberIds) {
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

        for(Long id : memberIds) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>BetreuteAdressen</member>\n" + //will return list of obj ref for each company
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
                "        <member>Name</member>\n" + //will return list of obj ref for each company
                "        <member>CreationDateTime</member>\n" + //will return list of obj ref for each company
                "        <member>ModifiedDateTime</member>\n" + //will return list of obj ref for each company
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
                "        <member>StandardEMail</member>\n" + //will return list of obj ref for each company
                "        <member>StandardTelefon</member>\n" + //will return list of obj ref for each company
                "        <member>StandardMobile</member>\n" + //will return list of obj ref for each company
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;

    }


}
