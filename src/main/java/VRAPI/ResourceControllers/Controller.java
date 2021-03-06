package VRAPI.ResourceControllers;

import VRAPI.Entities.Activity;
import VRAPI.Entities.Contact;
import VRAPI.Exceptions.*;
import VRAPI.MergeClasses.ActivitiesForAddressEntry;
import VRAPI.Util.QueryBuilder;
import VRAPI.Util.StaticMaps;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
import java.util.Map;
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

    Map<Long, Long> supervisorIdMap;
    private Map<Long, String> activityTypeMap;

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

    //Constructot used in testing to avoid having to make a request to the server each time with credentials in header
    public Controller(QueryBuilder queryBuilder) {
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
        this.queryBuilder = queryBuilder;
    }

    //parses response from vertec into document
    Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            System.out.println(res);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException | IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    /**
     * Extracts all objrefs from an xml Response
     *
     * @param response
     * @return
     */
    public List<Long> getObjrefsForOrganisationDocument(Document response) {
        NodeList activityObjrefs = response.getElementsByTagName("objref");
        return activityObjrefs == null ? new ArrayList<>() : asIdList(activityObjrefs);
    }

    //given nodelist containing ids, returns them as list
    static public List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    //returns stream of text content of nodes in nodelist
    static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    //generic method for passing a query to vertec and recieving response in POJO form
    <T> T callVertec(String query, Class<T> responseType) {
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }


    /**
     * Call this function at the start of every request handler
     * This will make a request for 'ZUK TEAM' from vertec and either setUp the query builder with provided username and pwd
     * or will throw appropriate error
     *
     * @throws ParserConfigurationException
     */
    public QueryBuilder AuthenticateThenReturnQueryBuilder() throws ParserConfigurationException {
        Authenticator authenticator = new Authenticator();
        String usernamePassword = request.getHeader("Authorization");
        Integer authLevel = authenticator.requestIsAuthorized(usernamePassword);
        if (authLevel.longValue() == VertecServerInfo.BAD_REQUEST) {
            throw new HttpBadRequest("Username and password not correctly set in header");
        } else if (authLevel.longValue() == VertecServerInfo.UNAUTHORISED) {
            throw new HttpUnauthorisedException("Wrong username or password");
        } else if (authLevel.longValue() == VertecServerInfo.FORBIDDEN) {
            throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
        }
        String[] usrpwd = usernamePassword.split(":");
        return new QueryBuilder(usrpwd[0], usrpwd[1]);
    }

    /**
     * Can only be called by a fucntion that is an endpoint due to querybuilder having to be built
     *
     * @param id
     * @return
     */
    public Boolean isIdOfType(Long id, String vertecType) {
        String xmlQuery = queryBuilder.getTypeOfId(id);
        Document res = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        return (res.getElementsByTagName(vertecType).getLength() > 0);
    }

    /**
     * Call to extract text field of xml response, used for seeing if an item has been updated
     *
     * @param response
     * @return
     */
    public String getTextField(Document response) {
        Node res = response.getElementsByTagName("text").item(0);
        if (res != null) {
            return res.getTextContent();
        } else {
            return "";
        }
    }

    //given list of contact ids returns list of Entities.Contacts
    List<Contact> getDetailedContacts(List<Long> contactIdsForOrg) {
        if (contactIdsForOrg.isEmpty()) return new ArrayList<>();
        return getContacts(contactIdsForOrg).stream()
                .map(this::asContact)
                .collect(toList());
    }

    //converts XML contact in Entities.Contact
    Contact asContact(VRAPI.XMLClasses.ContainerDetailedContact.Contact contact) {
        Contact c = new Contact(contact);
        setOwnedOnVertecByForContact(c, contact);
        //TODO: set followers for contact here?
        //TODO: get complete list of contact details here
        return c;
    }

    //given list of contact ids returns list of XML Contacts
    public List<VRAPI.XMLClasses.ContainerDetailedContact.Contact> getContacts(List<Long> contactIdsForOrg) {
        return callVertec(
                queryBuilder.getDetailedContact(contactIdsForOrg),
                VRAPI.XMLClasses.ContainerDetailedContact.Envelope.class).getBody().getQueryResponse().getContactList();
    }

    //sets owned on vertec by for contact
    private void setOwnedOnVertecByForContact(Contact c, VRAPI.XMLClasses.ContainerDetailedContact.Contact vc) {
        Long responsibleId = vc.getPersonResponsible().getObjref();
        c.setOwnedOnVertecBy(getOwnedOnVertecByStringForOwnerId(responsibleId));
    }

    //for a given ownder id, will check supervisor map for that id, if not present then its owned by somebody outside ZUK
    String getOwnedOnVertecByStringForOwnerId(Long ownerId) {
        Long supervisorId = supervisorIdMap.get(ownerId);
        Long SALES_TEAM_IDENTIFIER = -5L; //members of the top sales team, including wolfgang have their 'supervisorId' set to -5 within the map;
        if (ownerId == null) return "No Owner";

        if (supervisorId == null || supervisorId == 0L) return "Not ZUK"; // might be wrong

        if (supervisorId.longValue() == SALES_TEAM_IDENTIFIER) {
            return "Sales Team";
        } else {
            return "ZUK Sub Team";
        }
    }

    //gets all activites for a given addressEntry id
    public ResponseEntity<ActivitiesForAddressEntry> getActivitiesForAddressEntry(Long id) {
        String xmlQuery = queryBuilder.getActivitiesForAddressEntry(id);

        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        //Query vertec with organisation id for list of activities associated with it.
        List<Long> activityIdsForOrg = getObjrefsForOrganisationDocument(response);
        String organisationName = getNameForOrganisationDocument(response);

        List<Activity> activities = getActivityDetails(activityIdsForOrg);
        //Query vertec for details of each activity build response object
        ActivitiesForAddressEntry res = new ActivitiesForAddressEntry(id, organisationName);
        //set Activity list
        res.setActivities(activities);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Returns the first 'name' field returned by vertec as String
     *
     * @param response
     * @return
     */
    public String getNameForOrganisationDocument(Document response) {
        Node node = response.getElementsByTagName("name").item(0);
        return node == null ? "" : node.getTextContent();
    }

    //given list of activity ids, returns details
    private List<Activity> getActivityDetails(List<Long> activityIdsForOrg) {
        if (activityIdsForOrg.isEmpty()) return new ArrayList<>();
        activityTypeMap = StaticMaps.INSTANCE.getActivityTypeMap();
        return getActivities(activityIdsForOrg).stream()
                .map(this::getJsonActivity).collect(toList());

    }

    //converts XML activity to Entities.Activity
    private Activity getJsonActivity(VRAPI.XMLClasses.ContainerActivity.Activity activity) {
        Activity a = new Activity(activity);
        a.setvType(activityTypeMap.get(activity.getType() != null ? activity.getType().getObjref() : null));
        //Set Organisation link
        a.setVertecOrganisationLink(activity.getAddressEntry() != null ? activity.getAddressEntry().getObjref() : null);
        return a;
    }

    //gets XML activites for given ids
    private List<VRAPI.XMLClasses.ContainerActivity.Activity> getActivities(List<Long> ids) {
        try {
            return callVertec(queryBuilder.getActivities(ids), VRAPI.XMLClasses.ContainerActivity.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getActivities();
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("At least one of the supplied Ids does not belong to an activity: " + ids);
        }
    }

    //gets list of generic link containers for given ids
    public List<GenericLinkContainer> getGenericLinkContainers(List<Long> ids) {
        try {
            return callVertec(queryBuilder.getGenericLinkContainers(ids), VRAPI.XMLClasses.FromContainer.Envelope.class)
                    .getBody()
                    .getQueryResponse()
                    .getGenericLinkContainers();
        } catch (NullPointerException npe) {
            throw new HttpNotFoundException("At least one of the supplied Ids does not belong to a Generic Link Container: " + ids);
        }
    }

    //sets link container to point to a new owner
    public ResponseEntity<Long> setFromContainerOfGLC(Long survivorId, List<Long> glcids) {
        Document res = responseFor(new RequestEntity<>(queryBuilder.setFromContainerOfGLC(glcids
                , survivorId), HttpMethod.POST, vertecURI));

        return returnResponseEntityOrThrowError(res, survivorId, glcids);
    }

    //replaces generic links of merging id to point to surviving id
    public ResponseEntity<Long> replaceLinks(Long survivorId, Long mergingId, List<GenericLinkContainer> glcs) {
        Document res = responseFor(new RequestEntity<>(
                queryBuilder.setLinksListToReplaceMergeIdWithSurvivorId(glcs, survivorId, mergingId),
                HttpMethod.POST,
                vertecURI)
        );

        return returnResponseEntityOrThrowError(res, survivorId, glcs);

    }

    //reads response from vertec and throws error if something went wrong or returns response entity if all went fine
    public ResponseEntity<Long> returnResponseEntityOrThrowError(Document res, Long survivorId, List<?> glcs) {
        if (getTextField(res).contains("Updated " + glcs.size())) {

            VertecServerInfo.log.info("Generic Link containers" + glcs + " now linked to object: " + survivorId);
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            return new ResponseEntity<>(survivorId, HttpStatus.OK);

        } else {
            VertecServerInfo.log.info("Could not re-link glc-s " + glcs + ", Unknown response from Vertec");
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }

    }


}
