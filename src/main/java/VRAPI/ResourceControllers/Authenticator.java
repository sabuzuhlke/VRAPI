package VRAPI.ResourceControllers;

import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Util.QueryBuilder;
import VRAPI.VertecServerInfo;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.w3c.dom.Node.ELEMENT_NODE;

public class Authenticator {
    private URI vertecURI;

    public Authenticator() throws ParserConfigurationException {
        vertecURI = URI.create("http://" + VertecServerInfo.VERTEC_SERVER_HOST + ":" + VertecServerInfo.VERTEC_SERVER_PORT + "/xml");
    }

    /**
     * Call this function to test whether user is authorised before making actual request to vertec
     * @param authorizationHeader represent 'username:password'
     * @return 0 if user is fully authenticated
     *         1 if user is authenticated but lacks permissions
     *         2 if user is not authenticated
     *         3 if bad request
     */
    public Integer requestIsAuthorized(String authorizationHeader) {
        final String[] nameAndPassword = authorizationHeader.split(":");
        if (nameAndPassword.length != 2) {
            return VertecServerInfo.BAD_REQUEST;
        }
        QueryBuilder queryBuilder = new QueryBuilder(nameAndPassword[0], nameAndPassword[1]);
        String xmlQuery = queryBuilder.getLeadersTeam();
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));

        List<Long> list = elementIn(response, "QueryResponse")
                .map(queryResponse -> queryResponse.getElementsByTagName("objref"))
                .map(Authenticator::asIdList)
                .orElse(new ArrayList<>());

        if (list.size() == 0) {
            return failureFrom(response);
        } else {
            return VertecServerInfo.AUTHORISED;
        }
    }

    //given document recieved from vertec will check for error and return integer denoting access level
    private Integer failureFrom(Document document) {
        return elementIn(document, "Fault")
                .map(fault -> fault.getElementsByTagName("detailitem"))
                .map(detailItems -> asFailure(asStream(detailItems).findFirst()))
                .orElseThrow(() -> new HttpInternalServerError("no detailItem"));
    }

    @SuppressWarnings("all")
    //returns authorisation level based on document recieved containing a fault, see VertecServerInfo
    private Integer asFailure(Optional<String> maybeItem) {
        final Integer[] authLevel = new Integer[1];
        maybeItem
                .map(item -> {
                            if (item.contains("read access denied")) {
                                authLevel[0] = VertecServerInfo.FORBIDDEN;
                                return 0;
                            } else if (item.contains("Authentication failure")) {
                                authLevel[0] = VertecServerInfo.UNAUTHORISED;
                                return 0;
                            } else {
                                throw new HttpInternalServerError(item);
                            }
                        }
                ).orElseThrow(() ->  new HttpInternalServerError("missing fault"));
        return authLevel[0];
    }

    private static Optional<Element> elementIn(Document document, String tagname) {
        final NodeList queryResponses = document.getElementsByTagName(tagname);
        return queryResponses.getLength() == 1 && queryResponses.item(0).getNodeType() == ELEMENT_NODE
                ? Optional.of((Element) queryResponses.item(0))
                : Optional.empty();
    }

    //sends request and returns response parsed into document
    Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        RestTemplate rest = new RestTemplate();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            try {
                final ResponseEntity<String> res = rest.exchange(req, String.class);
                return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
            } catch (SAXException | IOException e) {
                throw new HttpInternalServerError(e);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new HttpInternalServerError("OOOOOOOOOH NOOOOH");

        }
    }
    //returns stream of text content found withing nodelist
    static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    //given nodeList containing numbers only returns list of long
    static public List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }
}
