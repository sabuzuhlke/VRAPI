package VRAPI.XMLClasses.ContainerDetailedContact;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sabu on 27/04/2016.
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

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not build JSON activity: " + e.toString());
        }
        return retStr;
    }

}

