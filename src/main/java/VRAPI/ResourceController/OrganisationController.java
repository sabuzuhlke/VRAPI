package VRAPI.ResourceController;

import VRAPI.ContainerDetailedOrganisation.Envelope;
import VRAPI.ContainerDetailedOrganisation.Organisation;
import VRAPI.Exceptions.HttpBadRequest;
import VRAPI.Exceptions.HttpForbiddenException;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.Exceptions.HttpUnauthorisedException;
import VRAPI.JSONContainerOrganisation.JSONContact;
import VRAPI.JSONContainerOrganisation.JSONOrganisation;
import VRAPI.JSONContainerOrganisation.JSONOrganisationList;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

@RestController
@Scope("prototype")
public class OrganisationController {

    private static final String DEFAULT_VERTEC_SERVER_HOST = "172.18.10.66";
    private static final String DEFAULT_VERTEC_SERVER_PORT = "8095";

    private RestTemplate rest;

    private final URI vertecURI;

    private final Integer BAD_REQUEST = 3;
    private final Integer UNAUTHORISED = 2;
    private final Integer FORBIDDEN = 1;
    private final Integer AUTHORISED = 0;

    public QueryBuilder queryBuilder;

    private Map<Long, String> teamIdMap;
    private Map<Long, List<String>> contactFollowerMap;

    @Autowired
    private HttpServletRequest request;

    public OrganisationController() {

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

    }

//---------------------------------------------------------------------------------------------------------------------- POST /{id}

    @ApiOperation(value = "Post organisation to vertec", nickname = "post")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public ResponseEntity<JSONOrganisation> postOrganisation(@RequestBody JSONOrganisation orgToPost) {

        System.out.println(orgToPost.toPrettyJSON());

        //TODO: Finish this

        return new ResponseEntity<>(new JSONOrganisation(), HttpStatus.OK);
    }


//---------------------------------------------------------------------------------------------------------------------- GET /{id}


    @ApiOperation(value = "Get organisation by list", nickname = "byList")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/org/{ids}", method = RequestMethod.GET)
    public ResponseEntity<JSONOrganisationList> getOrganisationList(@PathVariable List<Long> ids)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        VRAPI.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(ids),
                             VRAPI.ContainerDetailedOrganisation.Envelope.class);


        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Some or all of the ids requested could not be found as organisations");
        }

        JSONOrganisationList jsonOrgs = new JSONOrganisationList();
        jsonOrgs.setOrganisations(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().stream()
                .map(vertecOrg -> {
                    JSONOrganisation jsonOrganisation = xml2JSON(vertecOrg);
                    jsonOrganisation.setOwner(teamIdMap.get(vertecOrg.getPersonResponsible().getObjref()));
                    return jsonOrganisation;
                })
                .collect(toList()));


        return new ResponseEntity<>(jsonOrgs, HttpStatus.OK);
    }


    @ApiOperation(value = "GET organisation by id", nickname = "byId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/organisation/{id}", method = RequestMethod.GET)
    public ResponseEntity<JSONOrganisation> getOrganisation(@PathVariable Long id)
            throws ParserConfigurationException {
        ifUnauthorisedThrowErrorResponse();

        this.teamIdMap = StaticMaps.INSTANCE.getTeamIDMap();
        List<Long> idAsList = new ArrayList<>();
        idAsList.add(id);
        VRAPI.ContainerDetailedOrganisation.Envelope organisationEnvelope
                = callVertec(queryBuilder.getOrganisationDetails(idAsList),
                             VRAPI.ContainerDetailedOrganisation.Envelope.class);

        if (organisationEnvelope.getBody().getQueryResponse() == null) {
            throw new HttpNotFoundException("Organisation with id: " + id + " could not be found");
        }

        JSONOrganisation jsonOrganisation = xml2JSON(organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0));
        Long orgId = organisationEnvelope.getBody().getQueryResponse().getOrganisationList().get(0).getPersonResponsible().getObjref();
        jsonOrganisation.setOwner(teamIdMap.get(orgId));

        return new ResponseEntity<>(jsonOrganisation, HttpStatus.OK);

    }

    private JSONOrganisation xml2JSON(Organisation vertecOrg) {
        JSONOrganisation org = new JSONOrganisation(vertecOrg);
        org.setContacts(getContactsForOrganisation(vertecOrg.getContacts().getObjlist().getObjref()));
        return org;
    }

    private List<JSONContact> getContactsForOrganisation(List<Long> objref) {
        List<VRAPI.ContainerDetailedContact.Contact> xmlContacts =
                callVertec(queryBuilder.getContactDetails(objref), VRAPI.ContainerDetailedContact.Envelope.class)
                        .getBody()
                        .getQueryResponse()
                        .getContactList();

        this.contactFollowerMap = StaticMaps.INSTANCE.getFollowerMap();

        return xmlContacts.stream()
                .map(xmlContact -> {
                    JSONContact cont = new JSONContact(xmlContact);
                    cont.setOwner(teamIdMap.get(xmlContact.getPersonResponsible().getObjref()));
                    cont.setFollowers(contactFollowerMap.get(xmlContact.getObjId()));
                    return cont;
                })
                .collect(toList());

    }

//---------------------------------------------------------------------------------------------------------------------- UTIL

    /**
     * Call this function at the start of every request handler
     * This will make a request for 'ZUK TEAM' from vertec and either setUp the query builder with provided username and pwd
     * or will throw appropriate error
     * @throws ParserConfigurationException
     */
    private void ifUnauthorisedThrowErrorResponse() throws ParserConfigurationException {
        Authenticator authenticator = new Authenticator();
        String usernamePassword = request.getHeader("Authorization");
        Integer authLevel = authenticator.requestIsAuthorized(usernamePassword);
        if (authLevel.longValue() == BAD_REQUEST) {
            throw new HttpBadRequest("Username and password not correctly set in header");
        } else if (authLevel.longValue() == UNAUTHORISED) {
            throw new HttpUnauthorisedException("Wrong username or password");
        } else if (authLevel.longValue() == FORBIDDEN) {
            throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
        }
        String[] usrpwd = usernamePassword.split(":");
        queryBuilder = new QueryBuilder(usrpwd[0], usrpwd[1]);
    }

    private <T> T callVertec(String query, Class<T> responseType) {
        System.out.println("Calling vertec, querying for: " + responseType.getName());
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }

}
