package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLObjRef {

    private Long objref;

    public XMLObjRef() {
    }

    @XmlElement(name = "objref")
    public Long getObjref() {
        return objref;
    }

    public void setObjref(Long objref) {
        this.objref = objref;
    }
}
