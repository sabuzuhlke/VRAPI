package VRAPI.XMLClasses.ContainerContactDetails;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class KommMittel {
    private Long objid;
    private Boolean priority;
    private Objref typ;
    private String value;

    public KommMittel() {
    }

    @XmlElement(name = "objid")
    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    @XmlElement(name = "priority")
    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    @XmlElement(name = "typ")
    public Objref getTyp() {
        return typ;
    }

    public void setTyp(Objref typ) {
        this.typ = typ;
    }

    @XmlElement(name = "zieladresse")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
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
