package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 26/04/2016.
 */
public class XMLContactObjrefList {

    private XMLObjlist list;

    public XMLContactObjrefList() {
    }

    @XmlElement(name = "objlist")
    public XMLObjlist getList() {
        return list;
    }

    public void setList(XMLObjlist list) {
        this.list = list;
    }
}
