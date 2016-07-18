package VRAPI.XMLClasses.ContainerActivity;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

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
    public String toString(){
        StringWriter writer = new StringWriter();

        try {
            JAXBContext jc = JAXBContext.newInstance(this.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, writer);
        } catch (Exception e){
            System.out.println("Could not marshall Detailed Project envelope to XML string");
        }
        return writer.toString();
    }
}
