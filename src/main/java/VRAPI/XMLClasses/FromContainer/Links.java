package VRAPI.XMLClasses.FromContainer;

import VRAPI.XMLClasses.ContainerDetailedContact.Objlist;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 04/08/2016.
 */
public class Links {

    private Objlist objlist;

    public Links() {
    }

    @XmlElement(name = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
