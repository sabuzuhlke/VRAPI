package VRAPI.ContainerFollower;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 10/05/2016.
 */
public class ProjectWorker {

    private Boolean Active;
    private FromLinks fromLinks;
    private String email;

    public ProjectWorker() {
    }

    @XmlElement(name = "aktiv")
    public Boolean getActive() {
        return Active;
    }

    public void setActive(Boolean active) {
        Active = active;
    }

    @XmlElement(name = "fromLinks")
    public FromLinks getFromLinks() {
        return fromLinks;
    }

    public void setFromLinks(FromLinks fromLinks) {
        this.fromLinks = fromLinks;
    }

    @XmlElement(name = "briefEmail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
