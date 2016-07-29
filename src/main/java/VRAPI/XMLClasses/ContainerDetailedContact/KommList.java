package VRAPI.XMLClasses.ContainerDetailedContact;


import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class KommList {
    private Objlist objlist;

    public KommList() {
    }

    @XmlElement(name = "objlist")
    public Objlist getObjlist() {
        return objlist;
    }

    public void setObjlist(Objlist objlist) {
        this.objlist = objlist;
    }
}
