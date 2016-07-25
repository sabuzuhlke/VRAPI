package com.example;

import VRAPI.MyAccessCredentials;
import org.junit.Before;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

class ControllerTests {

    String username;
    String password;
    private RestTemplate rt;

    private static final String DEFAULT_OWN_IP = "localhost";
    private static final String DEFAULT_OWN_PORT = "9999";

    final String baseURI = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT;

    @Before
    public void setUp() {
        MyAccessCredentials mac = new MyAccessCredentials();
        this.username = mac.getUserName();
        this.password = mac.getPass();
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


}
