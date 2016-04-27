package VRAPI.VXMLClasses;

import jdk.internal.dynalink.linker.LinkerServices;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLQueryResponse {
    //TODO: FINISH MANAGEMENT! DOESNT WORK!

    private List<XMLOrganisation> orgs;
    private List<XMLContact> contacts;
    private List<XML_Z_Member> management;

    public XMLQueryResponse() {
        this.orgs = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.management = new ArrayList<>();
    }

    @XmlElement(name = "Kontakt")
    public List<XMLContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<XMLContact> contacts) {
        this.contacts = contacts;
    }

    @XmlElement(name = "Firma")
    public List<XMLOrganisation> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<XMLOrganisation> orgs) {
        this.orgs = orgs;
    }

    @XmlElement(name = "")
    public List<XML_Z_Member> getManagement() {
        return management;
    }

    public void setManagement(List<XML_Z_Member> management) {
        this.management = management;
    }
}
