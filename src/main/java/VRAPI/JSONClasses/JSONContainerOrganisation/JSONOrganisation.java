package VRAPI.JSONClasses.JSONContainerOrganisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO for returning details of a particular organisation, returned as part of ZUKOrganisationResponse
 */
public class JSONOrganisation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("streetAddress")
    private String streetAddress; //possibly change to separate address fields

    @JsonProperty("additonalAdress")
    private String additionalAdress;

    @JsonProperty("zip")
    private String zip;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("objid")
    private Long objid; //unsure if needed

    @JsonProperty("website")
    private String website;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("creationTime")
    private String creationTime;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("parentOrganisationId")
    private Long parentOrganisationId;

    @JsonProperty("childOrganisationList")
    private List<Long> childOrganisationList;

    @JsonProperty("contacts")
    private List<JSONContact> contacts;

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public JSONOrganisation() {
        this.contacts = new ArrayList<>();
    }

    /**
     * Constructor for building object from XML POJO recieved from vertec
     * set contacts outside
     * set owner outside
     * @param o
     */
    public JSONOrganisation(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation o) {
        this.setName(o.getName());
        this.setStreetAddress(o.getStreetAddress());
        this.setAdditionalAdress(o.getAdditionalAddressName());
        this.setCity(o.getCity());
        this.setZip(o.getZip());
        this.setCountry(o.getCountry());
        this.setObjid(o.getObjId());
        this.setWebsite(o.getWebsite() == null ? "" : o.getWebsite());
        this.setModified(o.getModified());
        this.contacts = new ArrayList<>();
        this.creationTime = o.getCreationTime();
        if (o.getDaughterFirm() != null) {
            this.childOrganisationList = o
                    .getDaughterFirm()
                    .getObjlist()
                    .getObjref();
        }
        this.active = o.getActive();
        this.parentOrganisationId = o.getParentFirm().getObjref();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<JSONContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<JSONContact> contacts) {
        this.contacts = contacts;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAdditionalAdress() {
        return additionalAdress;
    }

    public void setAdditionalAdress(String additionalAdress) {
        this.additionalAdress = additionalAdress;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getParentOrganisationId() {
        return parentOrganisationId;
    }

    public void setParentOrganisationId(Long parentOrganisationId) {
        this.parentOrganisationId = parentOrganisationId;
    }

    public List<Long> getChildOrganisationList() {
        return childOrganisationList;
    }

    public void setChildOrganisationList(List<Long> childOrganisationList) {
        this.childOrganisationList = childOrganisationList;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{
            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
