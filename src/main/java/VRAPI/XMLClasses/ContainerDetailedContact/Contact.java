package VRAPI.XMLClasses.ContainerDetailedContact;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private String position;
    private KommList kommMittel;
    private LinkContainer fromLinks;
    private LinkContainer genericContainers;


    private String creationTime;

    @XmlElement(name = "creationDateTime")
    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Contact() {
        this.organisation = null;
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

    @XmlElement(name = "stellung")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @XmlElement(name = "kommMittel")
    public KommList getKommMittel() {
        return kommMittel;
    }

    public void setKommMittel(KommList kommMittel) {
        this.kommMittel = kommMittel;
    }

    @XmlElement(name = "fromLinks")
    public LinkContainer getFromLinks() {
        return fromLinks;
    }

    public void setFromLinks(LinkContainer fromLinks) {
        this.fromLinks = fromLinks;
    }

    @XmlElement(name ="genericContainers")
    public LinkContainer getGenericContainers() {
        return genericContainers;
    }

    public void setGenericContainers(LinkContainer genericContainers) {
        this.genericContainers = genericContainers;
    }

    @Override
    public String toString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
