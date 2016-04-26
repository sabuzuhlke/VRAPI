package VRAPI;
import java.beans.XMLEncoder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import VRAPI.VXMLClasses.XMLEnvelope;
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
    private final AtomicLong counter = new AtomicLong();
    private String VipAddress;
    private String VportNr;
    private String OwnIpAddress;
    private String OwnPortNr;
    private RestTemplate xRestTemplate; //To be used for querying Vertec --XML Marshaller
    private RestTemplate resRestTemplate;// To be used to respont to requests --JSON Marshaller


    public ResourceController() {
        this.VipAddress = "172.18.10.54";
        this.VportNr = "8095";
        this.OwnIpAddress = "172.18.10.85";
        this.OwnPortNr = "9999";

        this.xRestTemplate = new RestTemplate();
        this.resRestTemplate = new RestTemplate();

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
    public String blah(){
        return new String("blah");

    }

    //===================================================================================================================Organisations
    @RequestMapping(value="/organisations/London/",method=RequestMethod.GET)//          ----------------------London
    public String getLondonOrganisations(){                                 //Return Id, Address, Creator
        String xml;
        RequestEntity<String> req;
        ResponseEntity<XMLEnvelope> res = null;
        MyCredentials creds  = new MyCredentials();

        System.out.println("Received REQ to /organisations/London/");




        xml = new String("<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + creds.getUserName() + "</Name>\n" +
                "      <Password>" + creds.getPass() + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <ocl>firma->select(standardOrt='London')->select(aktiv)</ocl>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Name</member>\n" +                 //Name of Organisation
                "        <member>StandardAdresse</member>\n" +      //Street Address of Organisation
                "        <member>StandardOrt</member>\n" +          //City Organisations is in
                "        <member>StandardPLZ</member>\n" +          //ZIP code
                "        <member>StandardLand</member>\n" +         //Country of Organisation
                "        <member>Betreuer</member>\n" +             //Objref to Zuhlke user who is responsible for the Organisation (Owner)
                "        <member>Creator</member>\n" +              //Objref to Zuhlke user who Created the Organisation Entry
                //"        <member>Kontakte</member>\n" +             // List of Objrefs of contacts from the organisation
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>");
        try{

            req = new RequestEntity<>(xml, HttpMethod.POST,new URI("http://" + VipAddress + ":" + VportNr + "/xml"));
            res = xRestTemplate.exchange(req, XMLEnvelope.class);
            //System.out.println(res.toString());
        }
        catch (Exception e){
            System.out.println("Exception: " + e);
        }
        if(res != null){
            System.out.println("Responding to REQ on /organisations/London/");
            return res.getBody().toString();
        }
        else{

            System.out.println("ERROR: NULL response to REQ on /organisations/London/ ");
            return null;
        }
    }

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
}
