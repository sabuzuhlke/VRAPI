package VRAPI.XMLClasses.ContainerEmployees;

import javax.xml.bind.annotation.XmlElement;

public class ProjectWorker {

    private Long objid;
    private String email;
    private String name;
    private Boolean active;

    public ProjectWorker() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "briefEmail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
