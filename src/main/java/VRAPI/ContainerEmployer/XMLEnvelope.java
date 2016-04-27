package VRAPI.ContainerEmployer;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.StringWriter;

/**
 * Created by sabu on 25/04/2016.
 */
@XmlRootElement(name = "Envelope")
public class XMLEnvelope {

    private XMLBody body;

    public XMLEnvelope() {
    }

    @XmlElement(name = "Body")
    public XMLBody getBody() {
        return body;
    }

    public void setBody(XMLBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        try {
            JAXBElement<XMLEnvelope> jaxbElement = new JAXBElement<>(new QName("Envelope"), XMLEnvelope.class, this);
            StringWriter writer = new StringWriter();
            JAXBContext ctx = JAXBContext.newInstance(XMLEnvelope.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(jaxbElement, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "MARSHALLING FAILED BUT THERES SOMETHING HERE";
    }

    public String toJSONString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }

}
