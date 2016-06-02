package VRAPI.ContainerActivitiesJSON;

import VRAPI.ContainerActivity.Activity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by gebo on 24/05/2016.
 */
public class JSONActivity {
    private Long id;
    private String title;
    private String text;
    private Boolean done;
    private String assignee; //email
    private Long customer_link;
    private Long phase_link;
    private Long project_link;
    private String date;
    private String type;

    public JSONActivity() {
    }

    public JSONActivity(VRAPI.ContainerActivity.Activity a){
        this.id = a.getObjid();
        this.title = a.getTitle();
        this.text = a.getText();
        this.done = a.getDone();

        //setAssignee oustide
        this.assignee = null;
        if(a.getAddressEntry() != null){

            this.customer_link = a.getAddressEntry().getObjref();
        }
        else{
            this.customer_link = null;
        }

        if(a.getPhase() != null){

            this.phase_link = a.getPhase().getObjref();
        }
        else{
            this.phase_link = null;
        }

        if(a.getProject() != null){

            this.project_link = a.getProject().getObjref();
        }
        else{
            this.project_link = null;
        }

        this.date = a.getDate();

        //set type outside
        this.type = null;
    }

    public JSONActivity(Activity activity, String assignee, String typeName) {
        this(activity);
        this.assignee = assignee;
        this.type = typeName;
        if (customer_link == null && phase_link == null && project_link == null) {
            System.out.println("Found activity not attached to anything: " + toPrettyJSON());
        }
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("done")
    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @JsonProperty("assignee")
    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @JsonProperty("customer_link")
    public Long getCustomer_link() {
        return customer_link;
    }

    public void setCustomer_link(Long customer_link) {
        this.customer_link = customer_link;
    }

    @JsonProperty("project_phase_link")
    public Long getPhase_link() {
        return phase_link;
    }

    public void setPhase_link(Long phase_link) {
        this.phase_link = phase_link;
    }

    @JsonProperty("project_link")
    public Long getProject_link() {
        return project_link;
    }

    public void setProject_link(Long project_link) {
        this.project_link = project_link;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not build JSON activity: " + e.toString());
        }
        return retStr;
    }
}
