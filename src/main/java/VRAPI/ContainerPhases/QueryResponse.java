package VRAPI.ContainerPhases;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class QueryResponse {

    private List<ProjectPhase> phases;

    public QueryResponse() {
    }

    @XmlElement(name = "ProjektPhase")
    public List<ProjectPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<ProjectPhase> phases) {
        this.phases = phases;
    }
}
