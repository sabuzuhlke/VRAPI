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
    @JsonProperty("external_value")
    private String externalValue;
    @JsonProperty("internal_value")
    private String internalValue;
    @JsonProperty("person_responsible")
    private String personResponsible;

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
}
