package VRAPI.XMLClasses.FromContainer;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 11/05/2016.
 */
public class QueryResponse {
    private List<GenericLinkContainer> genericLinkContainers;

    public QueryResponse() {
        genericLinkContainers = new ArrayList<>();
    }

    @XmlElement(name = "GenericLinkContainer")
    public List<GenericLinkContainer> getGenericLinkContainers() {
        return genericLinkContainers;
    }

    public void setGenericLinkContainers(List<GenericLinkContainer> genericLinkContainers) {
        this.genericLinkContainers = genericLinkContainers;
    }
}
