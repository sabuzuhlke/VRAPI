package VRAPI.VXMLClasses;

import VRAPI.MyCredentials;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class XMLService {

    private RestTemplate restTemplate;
    private String ipAddress;
    private String portNo;
    private String username;
    private String pwd;

    public XMLService(String ipAddress, String portNo) {
        this.restTemplate = new RestTemplate();


        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);

        converters.add(jaxbMC);


        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(converters);
        this.ipAddress = ipAddress;
        this.portNo = portNo;
        MyCredentials creds = new MyCredentials();
        this.username = creds.getUserName();
        this.pwd = creds.getPass();
    }
//
//    public ResponseEntity<XMLEnvelope> getLondonOrganisations() {
//        RequestEntity<String> req;
//        ResponseEntity<XMLEnvelope> res = null;
//
//        String uri = ipAddress + ":" + portNo +  "/organisations/London/";
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//
//            res = restTemplate.exchange(req, XMLEnvelope.class);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
////
//    public void testGet() {
//        RequestEntity<String> req;
//        ResponseEntity<String> res;
//        try {
//            String s = getXMLString();
//            req = new RequestEntity<>(s, HttpMethod.POST, new URI(ipAddress + ":" + portNo + "/xml"));
//
//            res = restTemplate.exchange(req, String.class);
//
//            System.out.println(res.toString());
//
//
//        } catch (Exception e) {
//            System.out.println("Exception: ");
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public void xmlToObject() {
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(XMLEnvelope.class);
//            Unmarshaller um = jaxbContext.createUnmarshaller();
//
//            StringReader reader = new StringReader(getXMLString());
//            XMLEnvelope envelope = (XMLEnvelope) um.unmarshal(reader);
//            System.out.println(envelope.toString());
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//    }


}
