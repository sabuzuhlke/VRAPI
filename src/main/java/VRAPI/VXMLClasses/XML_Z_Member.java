package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 26/04/2016.
 */
public class XML_Z_Member {
    private Long objid;

    public XML_Z_Member() {
    }
    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
}
