package VRAPI.ContainerEmployer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Team {
    private XMLObjlist list;

    public Team() {
    }

    @XmlElement(name = "objlist")

    public XMLObjlist getList() {
        return list;
    }

    public void setList(XMLObjlist list) {
        this.list = list;
    }
}
