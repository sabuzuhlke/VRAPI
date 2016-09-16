package VRAPI.JSONClasses.JSONContainerProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * POJO for returning details of a particular project, forms part of ZUKProjectsResponse
 */
public class JSONProject {

    @JsonProperty("title")
    private String title;
    @JsonProperty("v_id")
    private Long v_id;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("code")
    private String code;
    @JsonProperty("client_ref")
    private Long clientRef;
    @JsonProperty("leader_email")
    private String leader_email;
    @JsonProperty("leader_ref")
    private Long leader_ref;
    @JsonProperty("customer_ref")
    private Long customerId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("phases")
    private List<JSONPhase> phases;
    @JsonProperty("last_modified")
    private String modifiedDate;
    @JsonProperty("created")
    private String creationDate;
    @JsonProperty("account_manager")
    private String accountManager;


    public JSONProject() {
    }

    //Constructor for creating object from XML POJO recieved from vertec, phases, type and currency must be aquired and set outside of this constructor
    //leaderEmail is the email of the project leader, aManagerEmail is the email of the acccount manager for that project
    public JSONProject(VRAPI.XMLClasses.ContainerDetailedProjects.Project pr, String leaderEmail, String aManagerEmail) {
        this.title = pr.getTitle();
        this.v_id = pr.getId();
        this.active = pr.getActive();
        this.code = pr.getCode();
        this.clientRef = pr.getClient().getObjref();
        this.leader_email = leaderEmail;
        this.customerId = pr.getCustomer().getObjref();
        this.modifiedDate = pr.getModifiedDate();
        this.creationDate = pr.getCreationDate();
        this.accountManager = aManagerEmail;
        this.leader_ref = pr.getLeader().getObjref();

        //set phases outside of constructor
        //set Type outside of constructor
        //set currency outside of constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getClientRef() {
        return clientRef;
    }

    public void setClientRef(Long clientRef) {
        this.clientRef = clientRef;
    }

    public String getLeader_email() {
        return leader_email;
    }

    public void setLeader_email(String leader_email) {
        this.leader_email = leader_email;
    }

    public List<JSONPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<JSONPhase> phases) {
        this.phases = phases;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(String accountManager) {
        this.accountManager = accountManager;
    }

    public Long getLeader_ref() {
        return leader_ref;
    }

    public void setLeader_ref(Long leader_ref) {
        this.leader_ref = leader_ref;
    }

    public String toJSONString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}

