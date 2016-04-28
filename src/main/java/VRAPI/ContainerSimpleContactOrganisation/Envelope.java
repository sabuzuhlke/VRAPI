package VRAPI.ContainerSimpleContactOrganisation;

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
public class Envelope {

    private Body body;

    public Envelope() {
    }

    @XmlElement(name = "Body")
    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        try {
            JAXBElement<Envelope> jaxbElement = new JAXBElement<>(new QName("Envelope"), Envelope.class, this);
            StringWriter writer = new StringWriter();
            JAXBContext ctx = JAXBContext.newInstance(Envelope.class);
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
