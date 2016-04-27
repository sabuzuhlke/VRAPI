package VRAPI.ContainerTeam;


import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLQueryResponse {

    private List<ProjectWorker> workers;


    public XMLQueryResponse() {
        this.workers = new ArrayList<>();
        ;
    }

    @XmlElement(name = "Projektbearbeiter")
    public List<ProjectWorker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<ProjectWorker> workers) {
        this.workers = workers;
    }
}


