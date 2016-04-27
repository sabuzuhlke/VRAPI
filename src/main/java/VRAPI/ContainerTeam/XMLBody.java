package VRAPI.ContainerTeam;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 25/04/2016.
 */
public class XMLBody {

    private XMLQueryResponse queryResponse;

    public XMLBody() {
    }

    @XmlElement(name = "QueryResponse")
    public XMLQueryResponse getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(XMLQueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }
}
