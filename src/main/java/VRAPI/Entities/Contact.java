package VRAPI.Entities;


import java.util.ArrayList;
import java.util.List;

public class Contact {

    private Long vertecId;

    private Long vertecOrgLink;

    private Long ownerId;

    private String firstName;
    private String surname;
    private Boolean active;

    private List<ContactDetail> emails;
    private List<ContactDetail> phones; // TODO figure out how phone vs mobile differentiation applies

    private String creationTime;
    private String modifiedTime;

    private String ownedOnVertecBy;

    private String position;

    private List<String> followers;

    private List<Long> fromLinks;
    private List<Long> genericContainers;

    public Contact() {
        this.emails = new ArrayList<>();
        this.phones = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.fromLinks = new ArrayList<>();
        this.genericContainers = new ArrayList<>();
    }

    /**
     * Used to construct a contact from an xml contact recieved from vertec
     * must set ownedOnVertecBy outside constructor
     * must set followers outside constructor
     *
     * @param c
     */
    public Contact(VRAPI.XMLClasses.ContainerDetailedContact.Contact c) {

        vertecId = c.getObjId();
        vertecOrgLink = c.getOrganisation().getObjref();
        ownerId = c.getPersonResponsible().getObjref();

        firstName = c.getFirstName();
        surname = c.getSurnname();
        active = c.getActive();

        creationTime = c.getCreationTime();
        modifiedTime = c.getModified();

        position = c.getPosition();

        followers = new ArrayList<>();

        ContactDetail e = new ContactDetail(c.getEmail(), true);
        ContactDetail p = new ContactDetail(c.getPhone(), true);
        ContactDetail m = new ContactDetail(c.getMobile(), false);

        List<ContactDetail> email = new ArrayList<>();
        email.add(e);
        emails = email;

        List<ContactDetail> phone = new ArrayList<>();
        phone.add(p);
        phone.add(m);
        phones = phone;

        this.fromLinks = new ArrayList<>();
        if (c.getFromLinks() != null) {
            this.fromLinks = c.getFromLinks().getObjlist().getObjref();
        }
        this.genericContainers = new ArrayList<>();
        if (c.getGenericContainers() != null) {
            this.genericContainers = c.getGenericContainers().getObjlist().getObjref();
        }
    }


    public String getFullName() {
        String Name = "";
        if (firstName != null && !firstName.isEmpty()) Name = firstName;
        if ((firstName != null && !firstName.isEmpty()) && (surname != null && !surname.isEmpty())) Name += " ";
        if (surname != null && !surname.isEmpty()) Name += surname;
        return Name;
    }


    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public Long getVertecOrgLink() {
        return vertecOrgLink;
    }

    public void setVertecOrgLink(Long vertecOrgLink) {
        this.vertecOrgLink = vertecOrgLink;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getOwnedOnVertecBy() {
        return ownedOnVertecBy;
    }

    public void setOwnedOnVertecBy(String ownedOnVertecBy) {
        this.ownedOnVertecBy = ownedOnVertecBy;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<Long> getFromLinks() {
        return fromLinks;
    }

    public void setFromLinks(List<Long> fromLinks) {
        this.fromLinks = fromLinks;
    }

    public List<Long> getGenericContainers() {
        return genericContainers;
    }

    public void setGenericContainers(List<Long> genericContainers) {
        this.genericContainers = genericContainers;
    }
}
