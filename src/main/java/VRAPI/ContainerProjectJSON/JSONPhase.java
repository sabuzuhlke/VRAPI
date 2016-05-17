package VRAPI.ContainerProjectJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;

/**
 * Created by sabu on 13/05/2016.
 */
public class JSONPhase {
    //TODO: Get currency

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
    @JsonProperty("internal_value")
    private String internalValue;
    @JsonProperty("person_responsible")
    private String personResponsible;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("offered_date")
    private String offeredDate;
    @JsonProperty("granted_date")
    private String grantedDate;

    public JSONPhase() {
    }

    public JSONPhase (VRAPI.ContainerPhases.ProjectPhase ph){
        this.v_id = ph.getObjid();
        this.active = ph.getActive();
        this.description = ph.getDescription();
        this.code = ph.getCode();
        this.status = ph.getStatus();
        this.externalValue = ph.getExternalValue();
        this.internalValue = ph.getExternalValue();
        this.startDate = ph.getStartDate();
        this.endDate = ph.getEndDate();
        this.offeredDate = ph.getOfferedDate();
        this.salesStatus = ph.getSalesStatus();
        //this.grantedDate = ph
        //set personResponsible outside
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

    public String getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    public String getExternalValue() {

        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    public String getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(String personResponsible) {
        this.personResponsible = personResponsible;
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
}
