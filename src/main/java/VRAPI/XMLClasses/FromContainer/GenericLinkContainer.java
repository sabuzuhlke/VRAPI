package VRAPI.XMLClasses.FromContainer;

import VRAPI.XMLClasses.ContainerContactDetails.Objref;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 11/05/2016.
 */
public class GenericLinkContainer {
    private Long Objid;
    private FromContainer fromContainer;
    private Objref objref;

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

    @XmlElement(name = "rolle")
    public Objref getObjref() {
        return objref;
    }

    public void setObjref(Objref objref) {
        this.objref = objref;
    }
}
