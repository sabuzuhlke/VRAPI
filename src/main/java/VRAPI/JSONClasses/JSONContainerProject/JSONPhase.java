package VRAPI.JSONClasses.JSONContainerProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by sabu on 13/05/2016.
 */
public class JSONPhase {

    @JsonProperty("v_id")
    private Long v_id;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("description")
    private String description;
    @JsonProperty("code")
    private String code;
    @JsonProperty("status")
    private int status;
    @JsonProperty("sales_status")
    private String salesStatus;
    @JsonProperty("external_value")
    private String externalValue;
    @JsonProperty("person_responsible_email")
    private String personResponsibleEmail;
    @JsonProperty("person_responsible")
    private Long personResponsible;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("offered_date")
    private String offeredDate;
    @JsonProperty("granted_date")
    private String grantedDate;
    @JsonProperty("lost_reason")
    private String lostReason;
    @JsonProperty("last_modified")
    private String modifiedDate;
    @JsonProperty("created")
    private String creationDate;
    @JsonProperty("completion_date")
    private String completionDate;
    @JsonProperty("rejection_date")
    private String rejectionDate;

    @SuppressWarnings("unused")
    public JSONPhase() {
    }

    public JSONPhase (VRAPI.XMLClasses.ContainerPhases.ProjectPhase ph, String personResponsibleEmail){
        this.v_id = ph.getObjid();
        this.active = ph.getActive();
        this.description = ph.getDescription();
        this.code = ph.getCode();
        this.status = ph.getStatus();
        this.externalValue = ph.getExternalValue();
        this.startDate = setDate(ph.getStartDate());
        this.endDate = setDate(ph.getEndDate());

        this.offeredDate = setDate(ph.getOfferedDate());

        this.salesStatus = ph.getSalesStatus();
        this.lostReason = ph.getLostReason();
        this.modifiedDate = ph.getModifiedDate();
        this.creationDate = ph.getCreationDate();
        //this.grantedDate = ph
        this.personResponsibleEmail = personResponsibleEmail;

        this.completionDate = setDate(ph.getCompletionDate());

        this.rejectionDate = setDate(ph.getRejectionDate());

        this.personResponsible = ph.getPersonResponsible().getObjref();
    }

    private String setDate(String date){
        if(date == null){
            return "";
        } else  {
            return date;
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExternalValue() {

        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    public String getPersonResponsibleEmail() {
        return personResponsibleEmail;
    }

    public void setPersonResponsibleEmail(String personResponsibleEmail) {
        this.personResponsibleEmail = personResponsibleEmail;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOfferedDate() {
        return offeredDate;
    }

    public void setOfferedDate(String offeredDate) {
        this.offeredDate = offeredDate;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    public String getGrantedDate() {
        return grantedDate;
    }

    public void setGrantedDate(String grantedDate) {
        this.grantedDate = grantedDate;
    }

    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
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

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getRejectionDate() {
        return rejectionDate;
    }

    public void setRejectionDate(String rejectionDate) {
        this.rejectionDate = rejectionDate;
    }

    public Long getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(Long personResponsible) {
        this.personResponsible = personResponsible;
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
