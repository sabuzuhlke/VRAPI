package VRAPI.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 12/05/2016.
 */
public class DaughterFirms {

    private Objlist objlist;

    public DaughterFirms() {
    }

    @XmlElement(name  = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
