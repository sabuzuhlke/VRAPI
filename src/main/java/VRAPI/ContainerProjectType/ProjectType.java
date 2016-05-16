package VRAPI.ContainerProjectType;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 16/05/2016.
 */
public class ProjectType {
    private String descripton; //contains Office designation

    public ProjectType() {
    }

    @XmlElement(name = "bezeichnung")
    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }
}
