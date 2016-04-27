package VRAPI.ContainerAddresses;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by sabu on 25/04/2016.
 */
public class Body {

    private QueryResponse queryResponse;

    public Body() {
    }

    @XmlElement(name = "QueryResponse")
    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }
}
