package VRAPI.XMLClasses.ContainerEmployees;

import javax.xml.bind.annotation.XmlElement;

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
