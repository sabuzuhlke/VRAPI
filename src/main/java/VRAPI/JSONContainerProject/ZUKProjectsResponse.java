package VRAPI.JSONContainerProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class ZUKProjectsResponse {

    @JsonProperty("projects")
    private List<JSONProject> projects;

    public ZUKProjectsResponse() {
    }

    public List<JSONProject> getProjects() {
        return projects;
    }

    public void setProjects(List<JSONProject> projects) {
        this.projects = projects;
    }

    public String toPrettyJSON() {
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
