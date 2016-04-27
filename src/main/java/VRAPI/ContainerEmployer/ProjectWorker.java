package VRAPI.ContainerEmployer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class ProjectWorker {

    private Team team;
    private Long objid;
    public ProjectWorker() {
    }

    @XmlElement(name = "team")
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }
}
