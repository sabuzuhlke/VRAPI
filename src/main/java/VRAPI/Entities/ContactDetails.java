package VRAPI.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a collection of Contact details
 */
public class ContactDetails {
    private List<ContactDetail> emails;
    private List<ContactDetail> phones;

    public ContactDetails(){
        this.emails = new ArrayList<>();
        this.phones = new ArrayList<>();
    }

    public List<ContactDetail> getEmails() {
        return emails;
    }

    public void setEmails(List<ContactDetail> emails) {
        this.emails = emails;
    }

    public List<ContactDetail> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactDetail> phones) {
        this.phones = phones;
    }

}
