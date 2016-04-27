package VRAPI.ContainerTeam;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Addresses {
    private XMLObjlist list;

    public Addresses() {
    }

    @XmlElement(name = "objlist")
    public XMLObjlist getList() {
        return list;
    }

    public void setList(XMLObjlist list) {
        this.list = list;
    }
}
