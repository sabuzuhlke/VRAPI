package VRAPI.ContainerPhases;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class QueryResponse {

    private List<ProjectPhase> phases = emptyList();

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
