package VRAPI;
import VRAPI.ContainerActivitiesJSON.JSONActivity;
import VRAPI.ContainerActivitiesJSON.ZUKActivitiesResponse;
import VRAPI.ContainerActivity.Activity;
import VRAPI.ContainerActivity.Type;
import VRAPI.ContainerActivityType.ActivityType;
import VRAPI.ContainerDetailedProjects.Project;
import VRAPI.ContainerOrganisationJSON.JSONContact;
import VRAPI.ContainerOrganisationJSON.JSONOrganisation;
import VRAPI.ContainerOrganisationJSON.ZUKOrganisationResponse;
import VRAPI.ContainerProjectJSON.JSONPhase;
import VRAPI.ContainerProjectJSON.JSONProject;
import VRAPI.ContainerProjectJSON.ZUKProjectsResponse;
import VRAPI.ContainerProjectType.ProjectType;
import VRAPI.ContainerProjects.ProjectWorker;
import VRAPI.ContainerSimpleContactOrganisation.Contact;
import VRAPI.ContainerSimpleContactOrganisation.Organisation;
import VRAPI.Exceptions.HttpBadRequest;
import VRAPI.Exceptions.HttpForbiddenException;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpUnauthorisedException;
import VRAPI.FromContainer.GenericLinkContainer;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.w3c.dom.Node.ELEMENT_NODE;

public class MapBuilder {
    public static final String DEFAULT_VERTEC_SERVER_HOST = "172.18.10.54";
    public static final String DEFAULT_VERTEC_SERVER_PORT = "8095";

    private final URI vertecURI;

    private DocumentBuilder documentBuilder = null;

    private String username;
    private String password;

    private RestTemplate rest;

    private Map<Long, String> teamMap;
    private Map<Long, List<String>> followerMap;

    public MapBuilder() {
        //set resttemplate message converters
        this.rest = new RestTemplate();
        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

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


    /**
     * called to build teamMap on each request
     */
    public Map<Long, String> createTeamIdMap()  {
        Map<Long, String> teamMap = new HashMap<>();
        List<Long> ids = getZUKTeamMemberIds();
        String xmlQuery = getXMLQuery_TeamIdsAndEmails(ids);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        NodeList teamMembers = response.getElementsByTagName("Projektbearbeiter");
        IntStream.range(0, teamMembers.getLength())
                .mapToObj(index -> (Element) teamMembers.item(index))
                .forEach(teamMemberElement -> {
                    final NodeList active = teamMemberElement.getElementsByTagName("aktiv");
                    if (toBoolean(active.item(0).getTextContent())) {
                        final NodeList briefEmail = teamMemberElement.getElementsByTagName("briefEmail");
                        String email = briefEmail.item(0).getTextContent();
                        if(briefEmail.getLength(
                        ) != 1) {
                            throw new RuntimeException("XML Document parse for briefEmail went wrong"); //for debugging
                        }
                        final NodeList objid = teamMemberElement.getElementsByTagName("objid");
                        Long id = Long.parseLong(objid.item(0).getTextContent());
                        teamMap.put(id, email.toLowerCase());
                    }
                });


        return teamMap;
    }

    private Boolean toBoolean(String s) {
        return s.equals("1");
    }

    private String getXMLQuery_TeamIdsAndEmails(List<Long> ids) {

        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>5295</objref>\n";

        for (Long id : ids) {
            bodyStart += "<objref>" + id + "</objref>\n";
        }

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Aktiv</member>\n" +
                "        <member>briefEmail</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }


    public List<Long> getZUKTeamMemberIds() {

        String xmlQuery = getXMLQuery_LeadersTeam();

        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        List<Long> list = elementIn(response, "QueryResponse")
                .map(queryResponse -> queryResponse.getElementsByTagName("objref"))
                .map(MapBuilder::asIdList)
                .orElse(new ArrayList<>());
        if (list.size() == 0) {
            failureFrom(response);
        }

        //TODO: check this is what we actually want to do

        return list;
    }

    private void failureFrom(Document document) {
        elementIn(document, "Fault")
                .map(fault -> fault.getElementsByTagName("detailitem"))
                .map(detailItems -> {
                    asFailure(asStream(detailItems).findFirst());
                    return 0;
                })
                .orElseThrow(() -> new HttpInternalServerError("no detailItem"));
    }

    @SuppressWarnings("all")
    private void asFailure(Optional<String> maybeItem) {
        maybeItem
                .map(item -> {
                            if (item.contains("read access denied")) {
                                throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
                            } else if (item.contains("Authentication failure")) {
                                throw new HttpUnauthorisedException("Wrong username or password");
                            } else {
                                throw new HttpInternalServerError(item);
                            }
                        }
                ).orElseThrow(() ->  new HttpInternalServerError("missing fault"));
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
        } catch (SAXException| IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    public Map<Long, List<String>> createFollowerMap() {
        Map<Long, List<String>> map = new HashMap<>();
        VRAPI.ContainerFollower.Envelope leader;

        List<Long> teamIds = getZUKTeamMemberIds();

        for (Long id : teamIds) {

            //#2 query for  project leaders aktiv and fromlink
            leader = getGenericLinkContainers(id);


            if (leader.getBody().getQueryResponse().getProjectWorker().getActive()) {
                VRAPI.FromContainer.Envelope resFromContainer;

                //#3 query for generic Link Containers
                resFromContainer = getFromContainer(leader.getBody().getQueryResponse().getProjectWorker().getFromLinks().getObjlist().getObjref());

                List<GenericLinkContainer> genericLinkContainers = resFromContainer.getBody().getQueryResponse().getGenericLinkContainers();

                for (GenericLinkContainer glc : genericLinkContainers) {
                    Long objref = glc.getFromContainer().getObjref();

                    if(map.containsKey(objref)){
                        List<String> idsFollowing = map.get(objref);
                        idsFollowing.add(leader.getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.replace(objref, idsFollowing);
                    } else {

                        List<String> idsFollowing = new ArrayList<>();
                        idsFollowing.add(leader.getBody().getQueryResponse().getProjectWorker().getEmail().toLowerCase());
                        map.put(objref, idsFollowing);
                    }
                }
            }
        }

        return map;

    }

    private VRAPI.FromContainer.Envelope getFromContainer(List<Long> ids) {
        return callVertec(getXMLQuery_FromContainers(ids), VRAPI.FromContainer.Envelope.class);
    }

    private VRAPI.ContainerFollower.Envelope getGenericLinkContainers(Long id) {
        return callVertec(getXMLQuery_LeadersFromLinks(id), VRAPI.ContainerFollower.Envelope.class);
    }

    private String getXMLQuery_FromContainers(List<Long> containerIds) {
        String header = "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n";

        String bodyStart = "<Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n";
        if (containerIds != null) {
            for (Long id : containerIds) {
                bodyStart += "<objref>" + id + "</objref>\n";
            }
        }
        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>fromContainer</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    private String getXMLQuery_LeadersFromLinks(Long id) {
        return "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <objref>" + id + "</objref>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>fromlinks</member>\n" +
                "        <member>aktiv</member>\n" +
                "        <member>briefEmail</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private String getXMLQuery_LeadersTeam() {
        return "<Envelope>\n" +
                "  <Header>\n" +
                "    <BasicAuth>\n" +
                "      <Name>" + this.username + "</Name>\n" +
                "      <Password>" + this.password + "</Password>\n" +
                "      </BasicAuth>\n" +
                "  </Header>\n" +
                "\n" +
                "  <Body>\n" +
                "    <Query>\n" +
                "      <Selection>\n" +
                "        <ocl>projektBearbeiter->select(loginName='wje')</ocl>\n" +
                "      </Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" + //will return objref for each member of team
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";
    }

    private <T> T callVertec(String query, Class<T> responseType) {
        return rest.exchange(
                new RequestEntity<>(query, HttpMethod.POST, vertecURI),
                responseType).getBody();
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
