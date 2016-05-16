package VRAPI.ContainerDetailedProjects;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class QueryResponse {
    private List<Project> projects;

    public QueryResponse() {
    }

    @XmlElement(name = "Projekt")
    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
