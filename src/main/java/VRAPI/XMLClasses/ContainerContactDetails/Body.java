package VRAPI.XMLClasses.ContainerContactDetails;


import VRAPI.XMLClasses.ContainerContactDetails.QueryResponse;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
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
