package com.example;

import VRAPI.Keys.TestVertecKeys;
import VRAPI.MyAccessCredentials;
import org.junit.Before;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.List;

import static VRAPI.Keys.TestVertecKeys.*;

class ControllerTests {

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
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.PUT, URI.create(uri)),
                responseType);
    }

    <RES> ResponseEntity<RES> deleteFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.DELETE, URI.create(uri)),
                responseType);
    }

    public String idsAsString(List<Long> ids) {
        String idsAsString = "";
        for(int i = 0; i < ids.size(); i++) {
            if (i < ids.size() -1) {
                idsAsString += ids.get(i) + ",";
            } else {
                idsAsString += ids.get(i);
            }
        }
        return idsAsString;
    }
}
