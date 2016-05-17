package VRAPI.ContainerProjectJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sabu on 13/05/2016.
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
    @JsonProperty("leader_ref")
    private Long leaderRef;
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


    public JSONProject() {
    }

    public JSONProject(VRAPI.ContainerDetailedProjects.Project pr) {
        this.title = pr.getTitle();
        this.v_id = pr.getId();
        this.active = pr.getActive();
        this.code = pr.getCode();
        this.clientRef = pr.getClient().getObjref();
        this.leaderRef = pr.getLeader().getObjref();
        this.customerId = pr.getCustomer().getObjref();
        this.modifiedDate = pr.getModifiedDate();
        this.creationDate = pr.getCreationDate();

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

    public Long getLeaderRef() {
        return leaderRef;
    }

    public void setLeaderRef(Long leaderRef) {
        this.leaderRef = leaderRef;
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
}

