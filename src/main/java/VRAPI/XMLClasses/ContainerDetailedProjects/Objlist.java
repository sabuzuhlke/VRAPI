package VRAPI.XMLClasses.ContainerDetailedProjects;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class Objlist {
    private List<Long> objrefs;

    public Objlist() {
        this.objrefs = new ArrayList<>();
    }

    @XmlElement(name = "objref")
    public List<Long> getObjrefs() {
        return objrefs;
    }

    public void setObjrefs(List<Long> objrefs) {
        this.objrefs = objrefs;
    }
}
