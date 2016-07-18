package VRAPI.MergeClasses;

import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ProjectsForOrganisation {

    private Long organisationId;
    private String organisationName;
    private List<JSONProject> projects;

    public ProjectsForOrganisation() {
    }

    public ProjectsForOrganisation(Long organisationId, String organisationName) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<JSONProject> getProjects() {
        return projects;
    }

    public void setProjects(List<JSONProject> projects) {
        this.projects = projects;
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
