package VRAPI.ContainerCurrency;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 16/05/2016.
 */
public class Currency {
    private Long objid;
    private String name;

    public Currency() {
    }

    @XmlElement(name = "bezeichnung")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
}
