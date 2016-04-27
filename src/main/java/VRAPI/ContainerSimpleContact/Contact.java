package VRAPI.ContainerSimpleContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Contact {

    private Long objid;
    private Boolean aktiv;

    public Contact() {
    }
    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
    @XmlElement(name = "aktiv")
    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }
}
