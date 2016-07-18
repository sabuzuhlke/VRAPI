package VRAPI.XMLClasses.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 23/05/2016.
 */
public class Project {
    private Long objref;
    public Project() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
