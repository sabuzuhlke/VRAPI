package VRAPI.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 27/04/2016.
 */
public class Organisation {

    private Long objref;

    public Organisation() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
