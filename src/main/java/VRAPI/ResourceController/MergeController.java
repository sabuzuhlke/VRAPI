package VRAPI.ResourceController;

import VRAPI.Exceptions.HttpBadRequest;
import VRAPI.Exceptions.HttpForbiddenException;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpUnauthorisedException;
import VRAPI.JSONClasses.JSONContainerProject.JSONProject;
import VRAPI.MergeClasses.ActivitiesForOrganisation;
import VRAPI.MergeClasses.ContactsForOrganisation;
import VRAPI.MergeClasses.ProjectsForOrganisation;
import VRAPI.MyAccessCredentials;
import VRAPI.VertecServerInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
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

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@Scope("prototype")
public class MergeController {


    private static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    private static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;

    private RestTemplate rest;

    private DocumentBuilder documentBuilder = null;

    private final URI vertecURI;

    private QueryBuilder queryBuilder;

    private String username;
    private String password;
    private RestTemplate rt;


    public static final String DEFAULT_OWN_IP = "localhost";
    public static final String DEFAULT_OWN_PORT = "9999";

    public final String baseURI = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT;


    @Autowired
    private HttpServletRequest request;

    public MergeController() {
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

        MyAccessCredentials mac = new MyAccessCredentials();
        this.username = mac.getUserName();
        this.password = mac.getPass();
        this.rt = new RestTemplate();

    }


    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{mergingId}/mergeInto/{survivingId}", method = RequestMethod.GET) //TODO: write test for this function
    public ResponseEntity<String> getProjectsForOrganisation(@PathVariable Long mergingId, @PathVariable Long survivingId)
            throws ParserConfigurationException {

        queryBuilder = VertecServerInfo.ifUnauthorisedThrowErrorResponse(request);

        System.out.println();
        VertecServerInfo.log.info("=============================== START MERGE FOR ID: " + mergingId + " INTO ID: " + survivingId + "===============================");
        //Get Names of organisations from vertec for logging
        String mergingQuery = queryBuilder.getProjectsForOrganisation(mergingId);
        String survivingQuery = queryBuilder.getProjectsForOrganisation(survivingId);

        final Document mergeResponse = responseFor(new RequestEntity<>(mergingQuery, HttpMethod.POST, vertecURI));
        final Document suviveResponse = responseFor(new RequestEntity<>(survivingQuery, HttpMethod.POST, vertecURI));

        String orgToMerge = getNameForOrganisationDocument(mergeResponse);
        String orgToSurvive = getNameForOrganisationDocument(suviveResponse);

        VertecServerInfo.log.info("Merging organisation: '" + orgToMerge + "' into '" + orgToSurvive + "'");
        //for the mergingOrg, get all projects, get all activities, get all contacts
        String activitiesUri = baseURI + "/organisation/" + mergingId + "/activities";
        String projectsUri = baseURI + "/organisation/" + mergingId + "/projects";
        String contactsUri = baseURI + "/organisation/" + mergingId + "/contacts";

        ResponseEntity<ActivitiesForOrganisation> activityRes = getFromVertec(activitiesUri, ActivitiesForOrganisation.class);
        ResponseEntity<ProjectsForOrganisation> projectRes = getFromVertec(projectsUri, ProjectsForOrganisation.class);
        ResponseEntity<ContactsForOrganisation> contactRes = getFromVertec(contactsUri, ContactsForOrganisation.class);

        //log that we plan on updating each of these to point to surviving org

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING PROJECTS =========================");

        projectRes.getBody().getProjects().forEach(project -> {
            VertecServerInfo.log.info("Updating Project name: " + project.getTitle() + ", Code: " + project.getCode() + " to be linked to Organisation ID: " + mergingId);
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING ACTIVITIES =========================");

        activityRes.getBody().getActivitiesForOrganisation().forEach(activity -> {
            VertecServerInfo.log.info("Updating Activity name: " + activity.getSubject() + ", Type: " + activity.getvType() + " , id" + activity.getVertecId()+ activity.getDoneDate() + activity.getDueDate() + " to be linked to Organisation ID: " + mergingId);
        });

        VertecServerInfo.log.info("======================== UPDATING THE FOLLOWING CONTACTS =========================");

        contactRes.getBody().getContacts().forEach(contact -> {
            VertecServerInfo.log.info("Updating Contact name: " + contact.getFirstName() + " " + contact.getSurname() + " Email: " + (contact.getEmails().size() > 0 ? contact.getEmails().get(0).getValue() : "null") + " to be linked to Organisation ID: " + mergingId);
        });

        return new ResponseEntity<>("Recieved call to merge organisation with id: " + mergingId + " into organisation with id: " + survivingId, HttpStatus.OK);
    }


    public String getNameForOrganisationDocument(Document response) {
        return response.getElementsByTagName("name").item(0).getTextContent();
    }


    private Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException | IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    private <RES> ResponseEntity<RES> getFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
    }


    private <T> T callVertec(String query, Class<T> responseType) {
        System.out.println("Calling vertec, querying for: " + responseType.getName());
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }

}
