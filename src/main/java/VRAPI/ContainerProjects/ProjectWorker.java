package VRAPI.ContainerProjects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 13/05/2016.
 */
public class ProjectWorker {

    private Long objid;
    private Boolean active;
    private ProjectsList projectsList;

    public ProjectWorker() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @XmlElement(name = "bearbProjekte")
    public ProjectsList getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(ProjectsList projectsList) {
        this.projectsList = projectsList;
    }
}
