package VRAPI.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 *POJO for returning list of organisations in Entities.Organisation format
 */
public class OrganisationList {

    private List<Organisation> organisations;

    public OrganisationList() {
    }

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }
 @Override
    public String toString(){
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
