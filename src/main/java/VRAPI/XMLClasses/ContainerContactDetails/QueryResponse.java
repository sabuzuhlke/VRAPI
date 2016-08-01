package VRAPI.XMLClasses.ContainerContactDetails;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 28/07/2016.
 */
public class QueryResponse {
    private List<KommMittel> contactDetails;

    public QueryResponse() {
        this.contactDetails = new ArrayList<>();
    }

    @XmlElement(name = "KommMittel")
    public List<KommMittel> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<KommMittel> contactDetails) {
        this.contactDetails = contactDetails;
    }
}
