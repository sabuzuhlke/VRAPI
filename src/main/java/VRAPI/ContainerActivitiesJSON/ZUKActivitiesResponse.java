package VRAPI.ContainerActivitiesJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ZUKActivitiesResponse {
    private List<JSONActivity> activities;

    public ZUKActivitiesResponse(List<JSONActivity> activities) {
        this.activities = activities;
    }

    @JsonProperty("activities")
    public List<JSONActivity> getActivities() {
        return activities;
    }


    @Override
    public String toString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not build JSON Projects: " + e.toString());
        }
        return retStr;
    }
}
