package VRAPI.XMLClasses.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 27/04/2016.
 */
public class Organisation {

    private Long objref;

    public Organisation() {
    }

    public Organisation(Long objref) { //ATM purely for testing purposes
        this.objref = objref;
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
