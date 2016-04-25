package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;

public class XMLResultDef {

    private String[] members;

    public XMLResultDef() {
    }

    @XmlElement(name="member")
    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }
}
