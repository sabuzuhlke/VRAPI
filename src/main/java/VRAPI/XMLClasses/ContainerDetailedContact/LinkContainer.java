package VRAPI.XMLClasses.ContainerDetailedContact;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 03/08/2016.
 */
public class LinkContainer {
    public LinkContainer() {
    }

    private Objlist objlist;

    @XmlElement(name = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
