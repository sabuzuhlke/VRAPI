package VRAPI.XMLClasses.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 23/05/2016.
 */
public class Type {
    private Long objref;

    public Type() {
    }

    @XmlElement(name  = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
