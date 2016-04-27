package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 26/04/2016.
 */
public class XMLContact {

    private Long objid;
    private String cdt;
    private String mdt;

    public XMLContact() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "creationDateTime")
    public String getCdt() {
        return cdt;
    }

    public void setCdt(String cdt) {
        this.cdt = cdt;
    }

    @XmlElement(name = "modifiedDateTime")
    public String getMdt() {
        return mdt;
    }

    public void setMdt(String mdt) {
        this.mdt = mdt;
    }
}
