package VRAPI.ContainerProjectType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import java.io.StringWriter;

/**
 * Created by gebo on 16/05/2016.
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
    @Override
    public String toString(){
        StringWriter writer = new StringWriter();

        try {
            JAXBContext jc = JAXBContext.newInstance(this.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, writer);
        } catch (Exception e){
            System.out.println("Could not marshall Detailed Project envelope to XML string");
        }
        return writer.toString();
    }
}
