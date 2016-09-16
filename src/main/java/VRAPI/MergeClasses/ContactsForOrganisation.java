package VRAPI.MergeClasses;

import VRAPI.Entities.Contact;

import java.util.List;

/**
 * This class is the POJO for retrieving all the contact linked to a particular organisation, we return this from end point /organisation/{id}/contacts
 */
public class ContactsForOrganisation {

    private String organisationName;
    private Long id;
    private List<Contact> contacts;

    public ContactsForOrganisation(Long id, String organisationName) {
        this.organisationName = organisationName;
        this.id = id;
    }

    public ContactsForOrganisation() {
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
