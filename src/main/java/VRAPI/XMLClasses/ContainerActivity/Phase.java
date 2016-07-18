package VRAPI.XMLClasses.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 23/05/2016.
 */
public class Phase {
    private Long objref;

    public Phase() {
    }

    @XmlElement(name  = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
