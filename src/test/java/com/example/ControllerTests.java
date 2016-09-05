package com.example;

import VRAPI.Application;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.Keys.TestVertecKeys;
import VRAPI.MyAccessCredentials;
import VRAPI.ResourceControllers.Controller;
import VRAPI.Util.QueryBuilder;
import VRAPI.XMLClasses.FromContainer.GenericLinkContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class ControllerTests {

    String username;
    String password;
    private RestTemplate rt;

    private static final String DEFAULT_OWN_IP = "localhost";
    private static final String DEFAULT_OWN_PORT = "9999";

    final String baseURI = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT;


    Long TESTVertecContact = 28055069L;
    Long TESTVertecOrganisation1 = 28055040L;
    Long TESTVertecOrganisation2 = 28055047L;
    Long TESTRandomID = 9542823859193471L; //this id does not exist on vertec

    @Before
    public void setUp() {
        MyAccessCredentials mac = new MyAccessCredentials();
        this.username = TestVertecKeys.usr;//mac.getUserName();
        this.password = TestVertecKeys.pwd;//mac.getPass();
        this.rt = new RestTemplate();
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> hostname.equals("localhost"));
    }

    <RES> ResponseEntity<RES> getFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
    }

    <RES> ResponseEntity<RES> putToVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        try{

        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.PUT, URI.create(uri)),
                responseType);
        } catch (Exception e){
            System.out.println(e);
            throw e;
        }
    }

    <RES, REQ> ResponseEntity<RES> putToVertec(REQ payload, String uri,  Class<RES> responseType){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(payload, headers, HttpMethod.PUT, URI.create(uri)),
                responseType);
    }

    <RES> ResponseEntity<RES> deleteFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.DELETE, URI.create(uri)),
                responseType);
    }

    <REQ, RES> ResponseEntity<RES> postToVertec(REQ payload, String uri, Class<RES> responseType){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(payload, headers, HttpMethod.POST, URI.create(uri)),
                responseType);
    }

    public String idsAsString(List<Long> ids) {
        String idsAsString = "";
        for (int i = 0; i < ids.size(); i++) {
            if (i < ids.size() - 1) {
                idsAsString += ids.get(i) + ",";
            } else {
                idsAsString += ids.get(i);
            }
        }
        return idsAsString;
    }

    static public HashMap<Long, Long> loadIdMap(String filename) throws IOException {
        String line;

        HashMap<Long, Long> idMap = new HashMap<>();

        File file = new File(filename);

        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader breader = new BufferedReader(reader);

        while ((line = breader.readLine()) != null) {
            String[] ids = line.split(",");
            String key = ids[0];
            String value = ids[1];

            idMap.put(Long.parseLong(key), Long.parseLong(value));

        }
        reader.close();
        return idMap;
    }

    @Test
    public void canGetGenericLinkContainersForValidId() {

        QueryBuilder qb = new QueryBuilder(TestVertecKeys.usr, TestVertecKeys.pwd);
        Controller c = new Controller(qb);

        List<GenericLinkContainer> glcs = c.getGenericLinkContainers(Arrays.asList(8110400L, 20066431L, 958149L));

        assertEquals(8110397L, glcs.get(1).getFromContainer().getObjref().longValue());
        assertEquals(5295L, glcs.get(1).getLinks().getObjlist().getObjref().get(0).longValue());

        assertEquals(3, glcs.size());
    }

    @Test
    public void canNotGetGenericLinkContainersForInvalidIds() {

        QueryBuilder qb = new QueryBuilder(TestVertecKeys.usr, TestVertecKeys.pwd);
        Controller c = new Controller(qb);

        try {
            List<GenericLinkContainer> glcs = c.getGenericLinkContainers(Arrays.asList(811040L, 2001L, 958149L));
            assertTrue(false);
        } catch (HttpNotFoundException e) {
            System.out.println(e.getMessage());
            assertEquals(e.getMessage(), "At least one of the supplied Ids does not belong to a Generic Link Container: [811040, 2001, 958149]");
        }


    }

}
