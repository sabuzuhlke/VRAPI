package VRAPI.XMLClasses.ContainerContactDetails;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by gebo on 28/07/2016.
 */
@XmlRootElement(name = "Envelope")
public class Envelope {
    @XmlElement(name = "Body")
    private Body body;

    public Envelope() {
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
