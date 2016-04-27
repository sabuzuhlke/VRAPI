package VRAPI.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class QueryResponse {

    private List<Contact> contactList;

    public QueryResponse() {
    }

    @XmlElement(name = "Kontakt")
    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
