package VRAPI.ContainerPhases;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 13/05/2016.
 */
public class ProjectPhase {

    private Long objid;
    private Boolean active;
    private String description;
    private String code;
    private int status;
    private String externalValue;
    private String internalValue;
    private PersonResponsible personResponsible;

    public ProjectPhase() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @XmlElement(name = "beschreibung")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @XmlElement(name = "sumWertExt")
    public String getExternalValue() {
        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    @XmlElement(name = "sumWertInt")
    public String getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    @XmlElement(name = "verantwortlicher")
    public PersonResponsible getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(PersonResponsible personResponsible) {
        this.personResponsible = personResponsible;
    }
}
