package VRAPI.ResourceController;

import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.VertecServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class Controller {

    private static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    private static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;

    private RestTemplate rest;

    private DocumentBuilder documentBuilder = null;

    final URI vertecURI;

    QueryBuilder queryBuilder;

    @Autowired
    public HttpServletRequest request;

    Controller() {
        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

        this.rest = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        Jaxb2RootElementHttpMessageConverter jaxbMC = new Jaxb2RootElementHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        jaxbMC.setSupportedMediaTypes(mediaTypes);
        converters.add(jaxbMC);
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        rest.setMessageConverters(converters);

        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException | IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    public List<Long> getObjrefsForOrganisationDocument(Document response) {
        NodeList activityObjrefs =  response.getElementsByTagName("objref");
        return asIdList(activityObjrefs);
    }

    private static List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    private static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    <T> T callVertec(String query, Class<T> responseType) {
        System.out.println("Calling vertec, querying for: " + responseType.getName());
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }

}
