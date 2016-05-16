package VRAPI.ContainerDetailedProjects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 13/05/2016.
 */
public class Project {

    private Long id;
    private Boolean active;
    private String code;
    private Client client;
    private Phases phases;
    private Leader leader;
    private String title;

    public Project() {
    }

    @XmlElement(name = "objid")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @XmlElement(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(name = "kunde")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @XmlElement(name = "phasen")
    public Phases getPhases() {
        return phases;
    }

    public void setPhases(Phases phases) {
        this.phases = phases;
    }

    @XmlElement(name = "projektleiter")
    public Leader getLeader() {
        return leader;
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    @XmlElement(name = "projektnummer")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}