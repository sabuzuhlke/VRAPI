package VRAPI.VXMLClasses;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLQueryResponse {

    private List<XMLOrganisation> orgs;

    public XMLQueryResponse() {
        this.orgs = new ArrayList<XMLOrganisation>();
    }

    @XmlElement(name = "Firma")
    public List<XMLOrganisation> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<XMLOrganisation> orgs) {
        this.orgs = orgs;
    }
}
