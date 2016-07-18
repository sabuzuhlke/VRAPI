package VRAPI.XMLClasses.ContainerFollower;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by gebo on 10/05/2016.
 */
public class Objlist {

    private List<Long> objref;

    public Objlist() {
    }

    @XmlElement(name = "objref")
    public List<Long> getObjref() {
        return objref;
    }

    public void setObjref(List<Long> objref) {
        this.objref = objref;
    }
}
