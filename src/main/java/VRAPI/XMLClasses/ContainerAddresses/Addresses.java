package VRAPI.XMLClasses.ContainerAddresses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Addresses {
    private Objlist list;

    public Addresses() {
    }

    @XmlElement(name = "objlist")
    public Objlist getList() {
        return list;
    }

    public void setList(Objlist list) {
        this.list = list;
    }
}
