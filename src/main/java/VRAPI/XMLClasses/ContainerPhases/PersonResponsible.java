package VRAPI.XMLClasses.ContainerPhases;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 13/05/2016.
 */
public class PersonResponsible {

    private Long objref;

    public PersonResponsible() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
