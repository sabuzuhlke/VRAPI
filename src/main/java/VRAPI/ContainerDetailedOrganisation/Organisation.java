package VRAPI.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 27/04/2016.
 */
public class Organisation {

    private Long objId;
    private PersonResponsible personResponsible;
    private String name;
    private String streetAddress;
    private String country;
    private String city;
    private String zip;
    private String additionalAddressName;
    private String modified;
    private Boolean active;                          //can remove if they ALL come back active (sourced from only active contacts)
    private String creationTime;

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
    public PersonResponsible getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(PersonResponsible personResponsible) {
        this.personResponsible = personResponsible;
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
}
