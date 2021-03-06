package VRAPI.JSONClasses.JSONContainerActivities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Class used to return list of all activities related to ZUK
 */
public class JSONActivitiesResponse {
    private List<JSONActivity> activities;

    public JSONActivitiesResponse() {}

    public JSONActivitiesResponse(List<JSONActivity> activities) {
        this.activities = activities;
    }

    @JsonProperty("activities")
    public List<JSONActivity> getActivities() {
        return activities;
    }


    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not build JSON Activities: " + e.toString());
        }
        return retStr;
    }
}
