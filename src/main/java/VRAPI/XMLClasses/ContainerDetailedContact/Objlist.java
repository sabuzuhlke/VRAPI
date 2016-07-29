package VRAPI.XMLClasses.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class Objlist {
    private Long objref;

    public Objlist() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
