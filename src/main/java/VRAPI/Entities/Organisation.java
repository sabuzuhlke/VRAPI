package VRAPI.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Organisation {

    private Long vertecId;
    private String ownedOnVertecBy;
    private Boolean active;

    private String supervisingEmail;

    private String name;
    private String website;
    private String category;
    private String businessDomain;
    private String buildingName;
    private String street_no;
    private String street;
    private String city;
    private String country;
    private String zip;

    private String modified;
    private String created;

    /**
     * Used to create an organisation class from what we recieve from vertec
     * IMPORTANT!!!! must set owned_on_vertec_by and supervisor email outside constructor
     * @param vo
     */
    public Organisation(VRAPI.ContainerDetailedOrganisation.Organisation vo) {
        vertecId = vo .getObjId();
        active = vo.getActive();
        name = vo.getName();
        website = vo.getWebsite();
        category = "CATEGORY PLACEHOLDER";//TODO: replace with actual value
        businessDomain = "BUSINESS DOMAIN PLACEHOLDER";
        buildingName = vo.getAdditionalAddressName();
        String[] addressParts = vo.getStreetAddress().split(" ");
        if (addressParts.length == 2) {
            street_no = addressParts[0];
            street = addressParts[1];
        } else {
            street = vo.getStreetAddress();
        }
        city = vo.getCity();
        country = vo.getCountry();
        zip = vo.getZip();

        modified = vo.getModified();
        created = vo.getCreationTime(); //TODO: change to common format
    }

    public Organisation() {
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public String getOwnedOnVertecBy() {
        return ownedOnVertecBy;
    }

    public void setOwnedOnVertecBy(String ownedOnVertecBy) {
        this.ownedOnVertecBy = ownedOnVertecBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSupervisingEmail() {
        return supervisingEmail;
    }

    public void setSupervisingEmail(String supervisingEmail) {
        this.supervisingEmail = supervisingEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreet_no() {
        return street_no;
    }

    public void setStreet_no(String street_no) {
        this.street_no = street_no;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
