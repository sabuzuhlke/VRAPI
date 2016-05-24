package VRAPI.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 24/05/2016.
 */
public class Assignee {
    private Long objref;

    public Assignee() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
