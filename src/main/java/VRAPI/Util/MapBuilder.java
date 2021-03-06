package VRAPI.Util;

import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerActivityType.ActivityType;
import VRAPI.Exceptions.HttpForbiddenException;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpUnauthorisedException;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;
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
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.w3c.dom.Node.ELEMENT_NODE;

public class MapBuilder {

    private static final String DEFAULT_VERTEC_SERVER_HOST = VertecServerInfo.VERTEC_SERVER_HOST;
    private static final String DEFAULT_VERTEC_SERVER_PORT = VertecServerInfo.VERTEC_SERVER_PORT;

    //in the supervisor map this is what members of the top sales team will point to (including wolfgang) use this to identify you are at the top of the hierarchy
    private final Long SALES_TEAM_IDENTIFIER = -5L;

    private final URI vertecURI;

    //document builder used for parsing xml responses
    private DocumentBuilder documentBuilder;

    //Vertec username and password
    private String username;
    private String password;

    //rest template used to set/recieve http requests
    private RestTemplate rest;

    //map of worker_id to their superior
    public Map<Long, Long> supervisorMap;

    public MapBuilder() {
        //set resttemplate message converters
        this.rest = new RestTemplate();
        vertecURI = URI.create("http://" + DEFAULT_VERTEC_SERVER_HOST + ":" + DEFAULT_VERTEC_SERVER_PORT + "/xml");

        //following lines set up the marshaller/unmarhsaller to accept XML
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

        supervisorMap = new HashMap<>();
    }


    //creates an activity type map as described in StaticMaps.class
    public Map<Long,String> createActivityTypeMap() {
        List<ActivityType> types = getActivityTypes();

        Map<Long, String> activityTypeMap = new HashMap<>();

        types.forEach(type -> activityTypeMap.put(type.getObjid(), type.getTypename()));

        return activityTypeMap;
    }

    //asks vertec for a list of activity types
    private List<ActivityType> getActivityTypes() {

        return callVertec(getActivityTypesQuery(), VRAPI.XMLClasses.ContainerActivityType.Envelope.class)
                .getBody()
                .getQueryResponse()
                .getActivityTypes();

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

        //Adding custom mapping of inactive users
        teamMap.put(5726L, "wolfgang.emmerich@zuhlke.com"); //Vertec id of David Levin
        teamMap.put(18010762L, "sabine.strauss@zuhlke.com"); //Vertec id of allana poleon
        teamMap.put(21741030L, "sabine.strauss@zuhlke.com"); //Vertec id of kathryn fletcher
        teamMap.put(504419L, "sabine.strauss@zuhlke.com"); //Vertec id of maria burley
        teamMap.put(18635504L, "sabine.strauss@zuhlke.com"); //Vertec id of hayley syms
        teamMap.put(10301189L, "justin.cowling@zuhlke.com"); //Vertec id of julia volland
        teamMap.put(1795374L, "justin.cowling@zuhlke.com"); //Vertec id of rod cobain
        teamMap.put(8904906L, "justin.cowling@zuhlke.com"); //Vertec id of afzar haider
        teamMap.put(15948308L, "justin.cowling@zuhlke.com"); //Vertec id of peter mcmanus

        return teamMap;
    }

    //called to build supervisor map as described within StaticMaps
    public void createSupervisorMap() {
        List<Long> ids = getZUKTeamMemberIds();
        String xmlQuery = getXMLQuery_TeamIdsAndEmails(ids);
        final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
        NodeList teamMembers = response.getElementsByTagName("Projektbearbeiter");
        IntStream.range(0, teamMembers.getLength())
                .mapToObj(index -> (Element) teamMembers.item(index))
                .forEach(teamMemberElement -> {
                    final NodeList active = teamMemberElement.getElementsByTagName("aktiv");
                    final NodeList objid = teamMemberElement.getElementsByTagName("objid");
                    Long id = Long.parseLong(objid.item(0).getTextContent());
                   // if (toBoolean(active.item(0).getTextContent())) {
                        supervisorMap.put(id, SALES_TEAM_IDENTIFIER );
                    //}
                    if (id != 5295L) {
                        final NodeList team = teamMemberElement.getElementsByTagName("objref");


                        List<Long> subTeamIds = IntStream.range(0, team.getLength())
                                .mapToObj(index -> Long.parseLong(team.item(index).getTextContent()))
                                .collect(toList());

                        subTeamIds.forEach(subId -> {
                            supervisorMap.put(subId, id);
                        });
                        handleSubTeamMembers(subTeamIds);

                    }

                });


        /*
         * Commented out because these inactive SalesTeam members should have their orgs marked as SALES_TEAM,
         * Will be uploaded to pipedrive with appropriate owner and when sync runs it will update vertec owner
         * to reflect vertec
         */
//        supervisorMap.put(5726L, 5295L); //Vertec id of David Levin
//        supervisorMap.put(18010762L, 23560788L); //Vertec id of allana poleon
//        supervisorMap.put(21741030L, 23560788L); //Vertec id of kathryn fletcher
//        supervisorMap.put(504419L, 23560788L); //Vertec id of maria burley
//        supervisorMap.put(18635504L, 23560788L); //Vertec id of hayley syms
//        supervisorMap.put(10301189L, 8619482L); //Vertec id of julia volland
//        supervisorMap.put(1795374L, 8619482L); //Vertec id of rod cobain
//        supervisorMap.put(8904906L, 8619482L); //Vertec id of afzar haider
//        supervisorMap.put(15948308L, 8619482L); //Vertec id of peter mcmanus


    }

    //helper function for building supervisor map
    private void handleSubTeamMembers(List<Long> ids) {

        ids.forEach(id -> {
            String xmlQuery = getXMLQuery_TeamIdsAndSubTeam(id);
            final Document response = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
            NodeList teamMembers = response.getElementsByTagName("Projektbearbeiter");
            IntStream.range(0, teamMembers.getLength())
                    .mapToObj(index -> (Element) teamMembers.item(index))
                    .forEach(teamMemberElement -> {
                        final NodeList team = teamMemberElement.getElementsByTagName("objref");

                        List<Long> subTeamIds = IntStream.range(0, team.getLength())
                                .mapToObj(index -> Long.parseLong(team.item(index).getTextContent()))
                                .collect(toList());

                        subTeamIds.forEach(subId -> {
                            supervisorMap.put(subId, id);
                        });
                        handleSubTeamMembers(subTeamIds);
                    });
        });

    }

    //queires for employee ids team (we dont use query builder here as we have the username and password in this class already)
    public String getXMLQuery_TeamIdsAndSubTeam(Long id) {

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

        bodyStart += "<objref>" + id + "</objref>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "        <member>Team</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }

    //convertec string representing 1 or 0 into boolean
    private Boolean toBoolean(String s) {
        return s.equals("1");
    }

    //queries for team ids emails and subteams
    public String getXMLQuery_TeamIdsAndEmails(List<Long> ids) {

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
                "        <member>Team</member>\n" + //will return Email address of team member
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }


    //asked for ZUK top level team members (users of pipedrive)
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
        return list;
    }

    //if error response returned by server, this checks if there are any details given and then passes those details onto asFailure, else throws exception
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
    //used error detail item to throw appropriate error response
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

    //given a list of nodes parsed by document builder, will extract list of ids (<objref> or <objid>) from it
    private static List<Long> asIdList(NodeList nodeList) {
        return asStream(nodeList).map(Long::parseLong).collect(toList());
    }

    //hack to convert nodelist into stream of nodes
    private static Stream<String> asStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(Node::getTextContent);
    }

    //used to ask document for an element named tagname (if more than one will return first)
    private static Optional<Element> elementIn(Document document, String tagname) {
        final NodeList queryResponses = document.getElementsByTagName(tagname);
        return queryResponses.getLength() == 1 && queryResponses.item(0).getNodeType() == ELEMENT_NODE
                ? Optional.of((Element) queryResponses.item(0))
                : Optional.empty();
    }

    //sends request and  parses response into document
    private Document responseFor(RequestEntity<String> req) throws HttpInternalServerError {
        try {
            final ResponseEntity<String> res = this.rest.exchange(req, String.class);
            return documentBuilder.parse(new ByteArrayInputStream(res.getBody().getBytes(UTF_8)));
        } catch (SAXException| IOException e) {
            throw new HttpInternalServerError(e);
        }
    }

    //builds follower map as described in StaticMaps.class
    public Map<Long, List<String>> createFollowerMap() {
        Map<Long, List<String>> map = new HashMap<>();
        VRAPI.XMLClasses.ContainerFollower.Envelope leader;

        List<Long> teamIds = getZUKTeamMemberIds();

        for (Long id : teamIds) {

            //#2 query for  project leaders aktiv and fromlink
            leader = getGenericLinkContainers(id); //simply gets detailed teamleaders


            if (leader.getBody().getQueryResponse().getProjectWorker().getActive()) {
                VRAPI.XMLClasses.FromContainer.Envelope resFromContainer;

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

    private VRAPI.XMLClasses.FromContainer.Envelope getFromContainer(List<Long> ids) {
        return callVertec(getXMLQuery_FromContainers(ids), VRAPI.XMLClasses.FromContainer.Envelope.class);
    }

    private VRAPI.XMLClasses.ContainerFollower.Envelope getGenericLinkContainers(Long id) {
        return callVertec(getXMLQuery_LeadersFromLinks(id), VRAPI.XMLClasses.ContainerFollower.Envelope.class);
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

    String getActivityTypesQuery() {
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

        bodyStart += "<ocl>aktivitaetsTyp</ocl>\n";

        String bodyEnd = "</Selection>\n" +
                "      <Resultdef>\n" +
                "           <member>bezeichnung</member>\n" +
                "      </Resultdef>\n" +
                "    </Query>\n" +
                "  </Body>\n" +
                "</Envelope>";

        return header + bodyStart + bodyEnd;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
