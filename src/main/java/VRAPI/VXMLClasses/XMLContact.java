package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 26/04/2016.
 */
public class XMLContact {

    private XMLObjlist list;

    public XMLContact() {
    }

    @XmlElement(name = "objlist")
    public XMLObjlist getList() {
        return list;
    }

    public void setList(XMLObjlist list) {
        this.list = list;
    }
}
