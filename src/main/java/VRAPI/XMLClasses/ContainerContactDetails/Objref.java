package VRAPI.XMLClasses.ContainerContactDetails;

import javax.xml.bind.annotation.XmlElement;

public class Objref {
    private Long objref;

    public Objref() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
