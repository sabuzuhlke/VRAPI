package VRAPI.XMLClasses.ContainerDetailedOrganisation;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 27/04/2016.
 */
public class Organisation {

    private Long objId;
    private ProjektBearbeiter personResponsible;
    private String name;
    private String streetAddress;
    private String country;
    private String city;
    private String zip;
    private String additionalAddressName;
    private String modified;
    private Boolean active;                          //can remove if they ALL come back active (sourced from only active contacts)
    private String creationTime;
    private ParentFirm parentFirm;
    private DaughterFirms daughterFirm;
    private Contacts contacts;
    private String website;
    private ProjektBearbeiter modifier;

    @XmlElement(name = "mutterfirma")
    public ParentFirm getParentFirm() {
        return parentFirm;
    }

    public void setParentFirm(ParentFirm parentFirm) {
        this.parentFirm = parentFirm;
    }

    @XmlElement(name = "tochterfirmen")
    public DaughterFirms getDaughterFirm() {
        return daughterFirm;
    }

    public void setDaughterFirm(DaughterFirms daughterFirm) {
        this.daughterFirm = daughterFirm;
    }

    @XmlElement(name = "creationDateTime")
    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Organisation() {
    }

    @XmlElement(name = "objid")
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @XmlElement(name = "betreuer")
    public ProjektBearbeiter getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(ProjektBearbeiter projektBearbeiter) {
        this.personResponsible = projektBearbeiter;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "standardAdresse")
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @XmlElement(name = "standardLand")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement(name = "standardOrt")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement(name = "standardPLZ")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @XmlElement(name = "zusatz")
    public String getAdditionalAddressName() {
        return additionalAddressName;
    }

    public void setAdditionalAddressName(String additionalAddressName) {
        this.additionalAddressName = additionalAddressName;
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

    @XmlElement(name = "kontakte")
    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    @XmlElement(name = "standardHomepage")
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @XmlElement(name = "modifier")
    public ProjektBearbeiter getModifier() {
        return modifier;
    }

    public void setModifier(ProjektBearbeiter modifier) {
        this.modifier = modifier;
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
