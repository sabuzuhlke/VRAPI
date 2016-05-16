package VRAPI.ContainerDetailedProjects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 16/05/2016.
 */
public class Customer {
    private Long objref;

    public Customer() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
