package VRAPI.XMLClasses.ContainerContactDetails;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class Objref {
    @XmlElement(name = "objref")
    private Long objref;

    public Objref() {
    }

    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
