package VRAPI.XMLClasses.ContainerError;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 06/05/2016.
 */
public class Fault {

    private Details details;

    public Fault() {
    }

    @XmlElement(name = "details")
    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
}
