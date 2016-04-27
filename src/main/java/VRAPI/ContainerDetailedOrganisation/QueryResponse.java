package VRAPI.ContainerDetailedOrganisation;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class QueryResponse {

    private List<Organisation> organisationList;

    public QueryResponse() {
    }

    @XmlElement(name = "Firma")
    public List<Organisation> getOrganisationList() {
        return organisationList;
    }

    public void setOrganisationList(List<Organisation> organisationList) {
        this.organisationList = organisationList;
    }
}
