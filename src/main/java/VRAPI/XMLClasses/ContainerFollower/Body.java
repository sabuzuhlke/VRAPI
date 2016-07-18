package VRAPI.XMLClasses.ContainerFollower;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by gebo on 10/05/2016.
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
