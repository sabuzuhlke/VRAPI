package VRAPI.ContainerTeam;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 27/04/2016.
 */
public class Team {
    private Objlist list;

    public Team() {
    }

    @XmlElement(name = "objlist")

    public Objlist getList() {
        return list;
    }

    public void setList(Objlist list) {
        this.list = list;
    }
}
