package VRAPI.XMLClasses.ContainerContactDetails;


import VRAPI.XMLClasses.ContainerActivity.QueryResponse;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 28/07/2016.
 */
public class Body {
    @XmlElement(name = "QueryResponse")
    private QueryResponse queryResponse;

    public Body() {
    }

    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }

}
