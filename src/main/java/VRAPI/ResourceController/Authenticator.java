package VRAPI.ResourceController;

import VRAPI.Exceptions.HttpInternalServerError;
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

    public static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    public static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;
    private final URI vertecURI;

    private final Integer BAD_REQUEST = 3;
    private final Integer UNAUTHORISED = 2;
    private final Integer FORBIDDEN = 1;
    private final Integer AUTHORISED = 0;

    private RestTemplate rest;

    private final DocumentBuilder documentBuilder;

    Authenticator() throws ParserConfigurationException {

        this.rest = new RestTemplate();
        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

        this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    /**
     * Call this function to test whether user is authorised before making actual request to vertec
     * @param authorizationHeader represent 'username:password'
     * @return 0 if user is fully authenticated
     *         1 if user is authenticated but lacks permissions
     *         2 if user is not authenticated
     *         3 if bad request
     */
    Integer requestIsAuthorized(String authorizationHeader) {
        final String[] nameAndPassword = authorizationHeader.split(":");
        if (nameAndPassword.length != 2) {
            return BAD_REQUEST;
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
            return AUTHORISED;
        }
    }

    private Integer failureFrom(Document document) {
        return elementIn(document, "Fault")
                .map(fault -> fault.getElementsByTagName("detailitem"))
                .map(detailItems -> asFailure(asStream(detailItems).findFirst()))
                .orElseThrow(() -> new HttpInternalServerError("no detailItem"));
    }

    @SuppressWarnings("all")
    private Integer asFailure(Optional<String> maybeItem) {
        final Integer[] authLevel = new Integer[1];
        maybeItem
                .map(item -> {
                            if (item.contains("read access denied")) {
                                authLevel[0] = FORBIDDEN;
                                return 0;
//                                throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
                            } else if (item.contains("Authentication failure")) {
                                authLevel[0] = UNAUTHORISED;
                                return 0;
//                                throw new HttpUnauthorisedException("Wrong username or password");
                            } else {
                                throw new HttpInternalServerError(item);
                            }
                        }
                ).orElseThrow(() ->  new HttpInternalServerError("missing fault"));
        return authLevel[0];
    }

    private static List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    private static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    private static Optional<Element> elementIn(Document document, String tagname) {
        final NodeList queryResponses = document.getElementsByTagName(tagname);
        return queryResponses.getLength() == 1 && queryResponses.item(0).getNodeType() == ELEMENT_NODE
                ? Optional.of((Element) queryResponses.item(0))
                : Optional.empty();
    }

    private Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException | IOException e) {
            throw new HttpInternalServerError(e);
        }
    }
}
