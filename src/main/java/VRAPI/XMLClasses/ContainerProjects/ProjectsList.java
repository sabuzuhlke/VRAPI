package VRAPI.XMLClasses.ContainerProjects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 13/05/2016.
 */
public class ProjectsList {

    private ObjList objList;

    public ProjectsList() {
    }

    @XmlElement(name = "objlist")
    public ObjList getObjList() {
        return objList;
    }

    public void setObjList(ObjList objList) {
        this.objList = objList;
    }
}
