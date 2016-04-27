package VRAPI.ContainerEmployer;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 26/04/2016.
 */
public class XMLObjlist {

    private List<Long> objects;

    public XMLObjlist() {
        this.objects = new ArrayList<>();
    }

    @XmlElement(name = "objref")
    public List<Long> getObjects() {
        return objects;
    }

    public void setObjects(List<Long> objects) {
        this.objects = objects;
    }
}
