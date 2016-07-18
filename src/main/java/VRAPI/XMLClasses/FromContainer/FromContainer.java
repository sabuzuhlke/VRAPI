package VRAPI.XMLClasses.FromContainer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 11/05/2016.
 */
public class FromContainer {
    private Long objref;

    public FromContainer() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
