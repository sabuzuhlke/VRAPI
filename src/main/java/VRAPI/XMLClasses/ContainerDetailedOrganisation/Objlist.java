package VRAPI.XMLClasses.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 12/05/2016.
 */
public class Objlist {
    private List<Long> objrefs;

    public Objlist() {
        this.objrefs = new ArrayList<>();
    }

    @XmlElement(name = "objref")
    public List<Long> getObjref() {
        return objrefs;
    }

    public void setObjref(List<Long> objref) {
        this.objrefs = objref;
    }
}
