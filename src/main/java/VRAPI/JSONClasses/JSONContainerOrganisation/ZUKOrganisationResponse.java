package VRAPI.JSONClasses.JSONContainerOrganisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO for returning all the organisations and contacts owned by ZUK team members
 * Each organisation has its own nested contacts
 * Many contacts are not part of an organisation and are returned as dangling contacts
 */
public class ZUKOrganisationResponse {

    @JsonProperty("organisations")
    private List<JSONOrganisation> organisationList;

    @JsonProperty("danglingContacts")
    private List<JSONContact> danglingContacts;

    public ZUKOrganisationResponse() {
        this.organisationList = new ArrayList<>();
        this.danglingContacts = new ArrayList<>();
    }

    public List<JSONOrganisation> getOrganisationList() {
        return organisationList;
    }

    public void setOrganisationList(List<JSONOrganisation> organisationList) {
        this.organisationList = organisationList;
    }

    public List<JSONContact> getDanglingContacts() {
        return danglingContacts;
    }

    public void setDanglingContacts(List<JSONContact> danglingContacts) {
        this.danglingContacts = danglingContacts;
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

    public String toPrettyString() {
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
