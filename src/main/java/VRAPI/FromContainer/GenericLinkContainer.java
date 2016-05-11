package VRAPI.FromContainer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 11/05/2016.
 */
public class GenericLinkContainer {
    private Long Objid;
    private FromContainer fromContainer;

    public GenericLinkContainer() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return Objid;
    }

    public void setObjid(Long objid) {
        Objid = objid;
    }

    @XmlElement(name = "fromContainer")
    public FromContainer getFromContainer() {
        return fromContainer;
    }

    public void setFromContainer(FromContainer fromContainer) {
        this.fromContainer = fromContainer;
    }
}
