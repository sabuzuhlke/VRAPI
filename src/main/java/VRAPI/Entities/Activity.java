package VRAPI.Entities;

public class Activity {
    private Long vertecId;
    private Boolean done;

    private String vType;

    private String subject; //Titel on vertec, there is aconversion function called createActivitySubject
    private String text; // note on pd is text prepended with #vid

    private  String dueDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String doneDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String created; //add_time on pd
    private String modified; //update_time on pd

    private Long vertecDealLink;
    private Long vertecProjectLink;
    private Long vertecOrganisationLink;
    private Long vertecContactLink;

    private Long vertecAssignee;


    public Activity() {
    }


    /**
     * Used to create an Activity from the xml POJO we recieve from vertec
     * Set activity type outside
     * Set organisation link outside
     * Set contact link outside
     * @param a
     */
    public Activity(VRAPI.XMLClasses.ContainerActivity.Activity a) {
        this.vertecId = a.getObjid();
        this.done = a.getDone();
        this.subject = a.getTitle();
        this.text = a.getText();

        this.dueDate = a.getDate();
        this.doneDate = a.getDoneDate();

        this.created = a.getCreationDateTime();
        this.modified = a.getModifiedDateTime();

        this.vertecDealLink = a.getPhase() != null ? a.getPhase().getObjref() : null;
        this.vertecProjectLink = a.getProject() != null ? a.getProject().getObjref() : null;

        this.vertecAssignee = a.getAssignee() != null ? a.getAssignee().getObjref() : null;
    }

    public Long getVertecProjectLink() {
        return vertecProjectLink;
    }

    public void setVertecProjectLink(Long vertecProjectLink) {
        this.vertecProjectLink = vertecProjectLink;
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Long getVertecDealLink() {
        return vertecDealLink;
    }

    public void setVertecDealLink(Long vertecDealLink) {
        this.vertecDealLink = vertecDealLink;
    }

    public Long getVertecOrganisationLink() {
        return vertecOrganisationLink;
    }

    public void setVertecOrganisationLink(Long vertecOrganisationLink) {
        this.vertecOrganisationLink = vertecOrganisationLink;
    }

    public Long getVertecContactLink() {
        return vertecContactLink;
    }

    public void setVertecContactLink(Long vertecContactLink) {
        this.vertecContactLink = vertecContactLink;
    }

    public Long getVertecAssignee() {
        return vertecAssignee;
    }

    public void setVertecAssignee(Long vertecAssignee) {
        this.vertecAssignee = vertecAssignee;
    }
}
