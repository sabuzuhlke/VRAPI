package VRAPI.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 12/05/2016.
 */
public class DaughterFirms {

    private Objlist objlistt;

    public DaughterFirms() {
    }

    @XmlElement(name  = "objlist")
    public Objlist getObjlistt() {
        return objlistt;
    }

    public void setObjlistt(Objlist objlistt) {
        this.objlistt = objlistt;
    }
}
