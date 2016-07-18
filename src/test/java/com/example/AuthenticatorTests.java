package com.example;

import VRAPI.Application;
import VRAPI.MyAccessCredentials;
import VRAPI.MyLimitedCredentials;
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

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class AuthenticatorTests {

    private String username;
    private String password;
    private RestTemplate rt;

    public static final String DEFAULT_OWN_IP = "localhost";
    public static final String DEFAULT_OWN_PORT = "9999";

    public final String uri = "https://" + DEFAULT_OWN_IP + ":" + DEFAULT_OWN_PORT + "/organisation/" + 1;

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

    private <RES> ResponseEntity<RES> getFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + password);
        return rt.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
    }

    @Test
    public void queryWithAuthorizedCredentialsReturnsOK() {
        ResponseEntity<String> res = getFromVertec(uri, String.class);

        assertNotNull("Response is null", res);
        assertEquals("Status code is not OK", res.getStatusCode(), HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
        assertEquals("Incorrect response", res.getBody(), "Requested Organisation From Vertec, ID: 1");
    }

    @Test
    public void queryWithLimitedAccessCredetialsReturnsForbidden() {
        MyLimitedCredentials mlc = new MyLimitedCredentials();
        this.username = mlc.getUserName();
        this.password = mlc.getPass();
        try {
            ResponseEntity<String> res = getFromVertec(uri, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong status code returned", e.getStatusCode(), HttpStatus.FORBIDDEN);
        }
    }

    @Test
    public void queryWithIncorrectCredetialsReturnsUnauthorized() {

        this.username = "qwerty";
        this.password = "Passfsdadsada";
        try{
            ResponseEntity<String> res = getFromVertec(uri, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("INcorrect returned status code", e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }

    }
}