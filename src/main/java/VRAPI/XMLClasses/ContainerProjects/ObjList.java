package VRAPI.XMLClasses.ContainerProjects;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 13/05/2016.
 */
public class ObjList {

    private List<Long> objrefs;

    public ObjList() {
    }

    @XmlElement(name = "objref")
    public List<Long> getObjrefs() {
        return objrefs;
    }

    public void setObjrefs(List<Long> objrefs) {
        this.objrefs = objrefs;
    }
}
