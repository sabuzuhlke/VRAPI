package VRAPI.XMLClasses.ContainerEmployees;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class QueryResponse {

    private List<ProjectWorker> workers;

    public QueryResponse() {
        this.workers = new ArrayList<>();
    }

    @XmlElement(name = "Projektbearbeiter")
    public List<ProjectWorker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<ProjectWorker> workers) {
        this.workers = workers;
    }
}
