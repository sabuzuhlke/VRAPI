package VRAPI.ContainerProjectType;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by gebo on 16/05/2016.
 */
public class QueryResponse {
    private List<ProjectType> projectTypes;

    public QueryResponse() {
    }

    @XmlElement(name = "ProjektTyp")
    public List<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypes(List<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }
}
