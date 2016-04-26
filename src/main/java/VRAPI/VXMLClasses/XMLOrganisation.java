package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLOrganisation {

    private Long objid;
    private String standardAdresse;
    private String name;
    private String standardOrt;
    private String standardPLZ;
    private XMLObjRef betreuer;
    private XMLObjRef creator;
    private XMLContact contacts;

    public XMLOrganisation() {
    }

    @XmlElement(name = "kontakte")
    public XMLContact getContacts() {
        return contacts;
    }

    public void setContacts(XMLContact contacts) {
        this.contacts = contacts;
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "standardAdresse")
    public String getStandardAdresse() {
        return standardAdresse;
    }

    public void setStandardAdresse(String standardAdresse) {
        this.standardAdresse = standardAdresse;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "betreuer")
    public XMLObjRef getBetreuer() {
        return betreuer;
    }

    public void setBetreuer(XMLObjRef betreuer) {
        this.betreuer = betreuer;
    }

    @XmlElement(name = "standardOrt")
    public String getStandardOrt() {
        return standardOrt;
    }

    public void setStandardOrt(String standardOrt) {
        this.standardOrt = standardOrt;
    }

    @XmlElement(name = "standardPLZ")
    public String getStandardPLZ() {
        return standardPLZ;
    }

    public void setStandardPLZ(String standardPLZ) {
        this.standardPLZ = standardPLZ;
    }

    @XmlElement(name = "creator")
    public XMLObjRef getCreator() {
        return creator;
    }

    public void setCreator(XMLObjRef creator) {
        this.creator = creator;
    }
}