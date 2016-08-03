package VRAPI.XMLClasses.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 28/07/2016.
 */
public class Objlist {
    private List<Long> objref;

    public Objlist() {
        this.objref = new ArrayList<>();
    }

    @XmlElement(name = "objref")

    public List<Long> getObjref() {
        return objref;
    }

    public void setObjref(List<Long> objref) {
        this.objref = objref;
    }
}
