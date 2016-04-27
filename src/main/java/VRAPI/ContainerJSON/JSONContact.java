package VRAPI.ContainerJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 27/04/2016.
 */
public class JSONContact {

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("owner")
    private String owner;   // name of owner

    @JsonProperty("orgid")
    private Long orgid;    // vertec id of organisation

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("objid")
    private Long objid;

    public JSONContact() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public Long getOrgid() {
        return orgid;
    }

    public void setOrgid(Long orgid) {
        this.orgid = orgid;
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
}
