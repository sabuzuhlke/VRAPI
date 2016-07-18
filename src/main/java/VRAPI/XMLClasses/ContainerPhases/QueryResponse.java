package VRAPI.XMLClasses.ContainerPhases;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class QueryResponse {

    private List<ProjectPhase> phases;

    public QueryResponse() {
        this.phases = new ArrayList<>();
    }

    @XmlElement(name = "ProjektPhase")
    public List<ProjectPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<ProjectPhase> phases) {
        this.phases = phases;
    }
}
