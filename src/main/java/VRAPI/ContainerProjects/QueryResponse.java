package VRAPI.ContainerProjects;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class QueryResponse {

    private List<ProjectWorker> projectWorkers;

    public QueryResponse() {
    }

    @XmlElement(name = "Projektbearbeiter")
    public List<ProjectWorker> getProjectWorkers() {
        return projectWorkers;
    }

    public void setProjectWorkers(List<ProjectWorker> projectWorkers) {
        this.projectWorkers = projectWorkers;
    }
}
