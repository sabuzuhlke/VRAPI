package VRAPI.ContainerSimpleContactOrganisation;



import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 25/04/2016.
 */
public class QueryResponse {

    private List<Contact> contacts;
    private List<Organisation> orgs;


    public QueryResponse() {
        this.contacts = new ArrayList<>();
        this.orgs = new ArrayList<>();
    }
    @XmlElement(name = "Kontakt")
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @XmlElement(name = "Firma")
    public List<Organisation> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<Organisation> orgs) {
        this.orgs = orgs;
    }
}


