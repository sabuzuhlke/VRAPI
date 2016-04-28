package VRAPI.ContainerSimpleContactOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Contact {

    private Long objid;

    public Contact() {
    }
    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
}
