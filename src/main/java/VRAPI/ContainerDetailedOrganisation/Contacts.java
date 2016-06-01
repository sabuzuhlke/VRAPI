package VRAPI.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 01/06/2016.
 */
public class Contacts {
    private Objlist objlist;
    public Contacts() {
    }

    @XmlElement(name = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
