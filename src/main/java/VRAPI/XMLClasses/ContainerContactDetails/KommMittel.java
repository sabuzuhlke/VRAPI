package VRAPI.XMLClasses.ContainerContactDetails;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class KommMittel {
    @XmlElement(name = "objid")
    private Long objid;

    @XmlElement(name = "eintrag")
    private Objref eintrag;

    @XmlElement(name = "priority")
    private Boolean priority;

    @XmlElement(name = "typ")
    private Objref typ;

    @XmlElement(name = "zieladresse")
    private String value;

    public KommMittel() {
    }

    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    public Objref getEintrag() {
        return eintrag;
    }

    public void setEintrag(Objref eintrag) {
        this.eintrag = eintrag;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Objref getTyp() {
        return typ;
    }

    public void setTyp(Objref typ) {
        this.typ = typ;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
