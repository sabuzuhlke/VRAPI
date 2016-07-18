package VRAPI.XMLClasses.ContainerFollower;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 11/05/2016.
 */
public class FromLinks {

    private Objlist objlist;

    public FromLinks() {
    }


    @XmlElement(name = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
