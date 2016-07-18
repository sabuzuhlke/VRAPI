package VRAPI.XMLClasses.ContainerSimpleContactOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/04/2016.
 */
public class Organisation {

    private Long objid;

    public Organisation() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
}
