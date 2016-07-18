package VRAPI.XMLClasses.ContainerActivityType;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 23/05/2016.
 */
public class ActivityType {
    private Long objid;
    private String typename;
    public ActivityType() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "bezeichnung")
    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ActivityType && this.objid == ((ActivityType) o).getObjid().longValue();
    }

    @Override
    public int hashCode(){
        return (int) this.objid.longValue();
    }
}
