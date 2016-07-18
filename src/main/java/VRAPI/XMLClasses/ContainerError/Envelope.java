package VRAPI.XMLClasses.ContainerError;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sabu on 06/05/2016.
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
}
