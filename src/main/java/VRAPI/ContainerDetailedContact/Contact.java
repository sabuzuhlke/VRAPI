package VRAPI.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 27/04/2016.
 */
public class Contact {

    private Long objId;
    private Organisation organisation;
    private String surnname;
    private String firstName;
    private String email;
    private String phone;
    private String mobile;
    private String modified;
    private PersonResponsible personResponsible;
    private Boolean active;

    public Contact() {
    }

    @XmlElement(name = "betreuer")
    public PersonResponsible getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(PersonResponsible personResponsible) {
        this.personResponsible = personResponsible;
    }

    @XmlElement(name = "objid")
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @XmlElement(name = "firma")
    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @XmlElement(name = "name")
    public String getSurnname() {
        return surnname;
    }

    public void setSurnname(String surnname) {
        this.surnname = surnname;
    }

    @XmlElement(name = "vorname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = "standardEMail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "standardTelefon")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @XmlElement(name = "standardMobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @XmlElement(name = "modifiedDateTime")
    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
