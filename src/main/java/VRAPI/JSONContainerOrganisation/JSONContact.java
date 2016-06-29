package VRAPI.JSONContainerOrganisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class JSONContact {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("owner")
    private String owner;   // name of owner

    @JsonProperty("organisation_ref")
    private Long organisation;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("objid")
    private Long objid;

    @JsonProperty("creationTime")
    private String creationTime;

    @JsonProperty("followers")
    private List<String> followers;

    @JsonProperty("active")
    private Boolean active;

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public JSONContact() {
    }

    public JSONContact(VRAPI.ContainerDetailedContact.Contact c){
        this.setObjid(c.getObjId());
        this.setEmail(c.getEmail().toLowerCase());
        this.setMobile(c.getMobile());
        this.setFirstName(c.getFirstName());
        this.setModified(c.getModified());
        //this.setOwner(c.getPersonResponsible().getObjref()); //has to be set in buildZUKOrganisationsResponse
        this.setPhone(c.getPhone());
        this.setSurname(c.getSurnname());
        this.creationTime = c.getCreationTime();

        if (c.getOrganisation() != null) {
            this.organisation = c.getOrganisation().getObjref();
        }
        this.active = c.getActive();
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
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
