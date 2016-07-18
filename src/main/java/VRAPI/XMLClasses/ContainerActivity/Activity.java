package VRAPI.XMLClasses.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 23/05/2016.
 */
public class Activity {
    private Long objid;
    private String text;
    private Boolean done;
    private AddressEntry addressEntry;
    private Phase phase;
    private Project project;
    private String date;
    private Type type;
    private String title;
    private Assignee assignee;
    private String doneDate;
    private String creationDateTime;

    public Activity() {

    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlElement(name = "erledigt")
    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @XmlElement(name = "adresseintrag")
    public AddressEntry getAddressEntry() {
        return addressEntry;
    }

    public void setAddressEntry(AddressEntry addressEntry) {
        this.addressEntry = addressEntry;
    }

    @XmlElement(name = "phase")
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @XmlElement(name = "projekt")
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @XmlElement(name = "typ")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @XmlElement(name = "titel")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "datum")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @XmlElement(name  = "zustaendig")
    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    @XmlElement(name = "erledigtDatum")
    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    @XmlElement(name = "creationDateTime")
    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
