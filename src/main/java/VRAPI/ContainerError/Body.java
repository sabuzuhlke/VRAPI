package VRAPI.ContainerError;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 06/05/2016.
 */
public class Body {

    private Fault fault;

    public Body() {
    }

    @XmlElement(name = "Fault")
    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }
}
