package VRAPI.ContainerPhases;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import java.io.StringWriter;

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
    private String startDate;
    private String endDate;
    private String completionDate;
    private String rejectionDate;
    private String offeredDate;
    private String salesStatus;
    private String lostReason;
    private String modifiedDate;
    private String creationDate;

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

    @XmlElement(name = "planWertExt")
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

    @XmlElement(name = "xStartDatum")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @XmlElement(name = "endDatum")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @XmlElement(name = "offertDatum")
    public String getOfferedDate() {
        return offeredDate;
    }

    public void setOfferedDate(String offeredDate) {
        this.offeredDate = offeredDate;
    }

    public void setPersonResponsible(PersonResponsible personResponsible) {
        this.personResponsible = personResponsible;
    }

    @XmlElement(name = "verkaufsstatus")
    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    @XmlElement(name = "absagegrundtext")
    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    @XmlElement(name = "modifiedDateTime")
    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @XmlElement(name = "creationDateTime")
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @XmlElement(name = "abschlussDatum")
    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    @XmlElement(name = "abgelehntDatum")
    public String getRejectionDate() {
        return rejectionDate;
    }

    public void setRejectionDate(String rejectionDate) {
        this.rejectionDate = rejectionDate;
    }
}
