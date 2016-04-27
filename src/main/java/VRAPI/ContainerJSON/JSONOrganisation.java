package VRAPI.ContainerJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class JSONOrganisation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address; //possibly change to separate address fields

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("objid")
    private Long objid; //unsure if needed

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("contacts")
    private List<JSONContact> contacts;

    public JSONOrganisation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
