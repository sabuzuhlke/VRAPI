package VRAPI.XMLClasses.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 12/05/2016.
 */
public class ParentFirm {

    private Long objref;

    public ParentFirm() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
