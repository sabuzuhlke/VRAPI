package VRAPI.ContainerDetailedProjects;

import javax.xml.bind.annotation.XmlElement;

public class Currency {
    private Long objref;

    public Currency() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
