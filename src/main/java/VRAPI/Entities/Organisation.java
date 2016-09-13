package VRAPI.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Organisation {

    private Long vertecId;
    private String ownedOnVertecBy;
    private Boolean active;


    private Long ownerId;
    private Long modifier;

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
    private String fullAddress; //only used when gotten from VPI atm

    private Long parentOrganisation;

    private String modified;
    private String created;

    /**
     * Used to create an organisation class from what we recieve from vertec
     * IMPORTANT!!!! must set owned_on_vertec_by outside constructor
     *
     * @param vo
     */
    public Organisation(VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation vo) {
        vertecId = vo.getObjId();
        active = vo.getActive();
        ownerId = vo.getPersonResponsible().getObjref();

        name = rectifySpecialCharacters(setString(vo.getName()));
        website = setString(vo.getWebsite());
        category =  "";//"CATEGORY PLACEHOLDER";//TODO: replace with actual value
        businessDomain = "";//"BUSINESS DOMAIN PLACEHOLDER";
        //TODO Make synchroniser only compare on full address
//        buildingName = setString(vo.getAdditionalAddressName());
//        String[] addressParts = vo.getStreetAddress().split(",");
//        if (addressParts.length == 2) {
//            street_no = setString(addressParts[0]);
//            street = setString(addressParts[1]);
//            if (street.charAt(0) == ' ') street = street.substring(1, street.length());
//        } else {
//            street_no = "";
//            street = setString(vo.getStreetAddress());
//        }
//        city = setString(vo.getCity());
//        country = setString(vo.getCountry());
//        zip = setString(vo.getZip());

        this.fullAddress = rectifySpecialCharacters(vo.getfullAddress());

        System.out.println(vo.getModifier());
        parentOrganisation = vo.getParentFirm() == null ? null : vo.getParentFirm().getObjref();

        modifier = vo.getModifier() == null ? 0L : (vo.getModifier().getObjref() == null ? 0L : vo.getModifier().getObjref());

        modified = vo.getModified();
        created = vo.getCreationTime(); //TODO: change to common format
    }

    public static String rectifySpecialCharacters(String s) {
        return s.replaceAll("&amp;", "&");
    }

    private String setString(String string) {
        if (string == null) return "";
        else return string;
    }

    public Organisation() {
    }

    public boolean equalsForUpdateAssertion(Organisation org) {
        boolean retval = true;
        if(org == null) return false;
        if(vertecId == null || org.getVertecId() == null) return false;
        else retval = retval && vertecId == org.getVertecId().longValue();

        if(active == null ^ org.getActive() == null) return false;
        else if(active != null && org.getActive() != null) retval = retval && active == org.getActive();

        if(name == null ^ org.getName() == null) return false;
        else if(name != null && org.getName() != null) retval = retval && name.equals(org.getName());

        if(website == null ^ org.getWebsite() == null) return false;
        else if(website != null && org.getWebsite() != null) retval = retval && website.equals(org.getWebsite());

//         if(parentOrganisation == null ^ org.getParentOrganisation() == null) return false;
//        else if(parentOrganisation != null && org.getParentOrganisation() != null) retval = retval && parentOrganisation.equals(org.getParentOrganisation());
//
//         if(buildingName == null ^ org.getBuildingName() == null) return false;
//        else if(buildingName != null && org.getBuildingName() != null) retval = retval && buildingName.equals(org.getBuildingName());
//
//         if(street_no == null ^ org.getStreet_no() == null) return false;
//        else if(street_no != null && org.getStreet_no() != null) retval = retval && (street_no.contains(org.getStreet_no()) || org.getStreet_no().contains(street_no));
//
//         if(street == null ^ org.getStreet() == null) return false;
//        else if(street != null && org.getStreet() != null) retval = retval && (street.contains(org.getStreet()) || org.getStreet().contains(street));
//
//         if(city == null ^ org.getCity() == null) return false;
//        else if(city != null && org.getCity() != null) retval = retval && city.equals(org.getCity());
//
//         if(country == null ^ org.getCountry() == null) return false;
//        else if(country != null && org.getCountry() != null) retval = retval && country.equals(org.getCountry());
//
//         if(zip == null ^ org.getZip() == null) return false;
//        else if(zip != null && org.getZip() != null) retval = retval && zip.equals(org.getZip());

        if(fullAddress == null ^ org.getFullAddress() == null) return false;
        else if(fullAddress != null && org.getFullAddress() != null) retval = retval && fullAddress.replaceAll(" ","").equals(org.getFullAddress().replaceAll(" ",""));

         if(ownerId == null ^ org.getOwnerId() == null) return false;
        else if(ownerId != null && org.getOwnerId() != null) retval = retval && ownerId.equals(org.getOwnerId());

        return retval;


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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public Long getParentOrganisation() {
        return parentOrganisation;
    }

    public void setParentOrganisation(Long parentOrganisation) {
        this.parentOrganisation = parentOrganisation;
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

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public String getFullAddress() {
        if(fullAddress!= null && !fullAddress.isEmpty())
        return fullAddress;
        else {
            String address = "";
            if(this.getBuildingName() != null && !this.getBuildingName().isEmpty()){
                address += this.getBuildingName() + ", ";
            }

            if (this.getStreet_no() != null && !this.getStreet_no().isEmpty()) {
                address += this.getStreet_no() + " ";
            }
            if (this.getStreet() != null && !this.getStreet().isEmpty()) {
                address += this.getStreet() + ", ";
            }

            if (this.getCity() != null && !this.getCity().isEmpty()) {
                address += this.getCity() + ", ";
            }
            if (this.getZip() != null && !this.getZip().isEmpty()) {
                address += this.getZip() + ", ";
            }
            if (this.getCountry() != null && !this.getCountry().isEmpty()) {
                address += this.getCountry();
            }
            return address;
        }
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String toJsonString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try {

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
