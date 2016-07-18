package VRAPI.XMLClasses.ContainerFollower;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 10/05/2016.
 */
public class QueryResponse {

    private ProjectWorker projectWorker;

    public QueryResponse() {
    }

    @XmlElement(name = "Projektbearbeiter")
    public ProjectWorker getProjectWorker() {
        return projectWorker;
    }

    public void setProjectWorker (ProjectWorker projectWorker) {
        this.projectWorker = projectWorker;
    }
}
