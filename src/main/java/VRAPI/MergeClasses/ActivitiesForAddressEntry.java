package VRAPI.MergeClasses;

import VRAPI.Entities.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This class is the POJO for retrieving all the activities linked to a particular organisation, we return this from end point /organisation/{id}/activities
 */
public class ActivitiesForAddressEntry {

    private Long organisationId;
    private String name;
    private List<Activity> activities;

    public ActivitiesForAddressEntry() {
    }

    public ActivitiesForAddressEntry(Long organisationId, String name) {
        this.organisationId = organisationId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public String toJSONString(){
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
