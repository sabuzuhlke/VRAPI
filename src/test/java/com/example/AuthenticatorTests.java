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
public class AuthenticatorTests extends ControllerTests {

    private final String uri = baseURI + "/organisation/" + 28055047;

    @Test
    public void queryWithAuthorizedCredentialsReturnsOK() {
        ResponseEntity<String> res = getFromVertec(uri, String.class);

        assertNotNull("Response is null", res);
        assertEquals("Status code is not OK", res.getStatusCode(), HttpStatus.OK);
        assertNotNull("Response body is null", res.getBody());
    }

    @Test
    public void queryWithLimitedAccessCredetialsReturnsForbidden() {
        MyLimitedCredentials mlc = new MyLimitedCredentials();
        username = mlc.getUserName();
        password = mlc.getPass();
        try {
            ResponseEntity<String> res = getFromVertec(uri, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Wrong status code returned", e.getStatusCode(), HttpStatus.FORBIDDEN);
        }
    }

    @Test
    public void queryWithIncorrectCredetialsReturnsUnauthorized() {

        username = "qwerty";
        password = "Passfsdadsada";
        try{
            ResponseEntity<String> res = getFromVertec(uri, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("INcorrect returned status code", e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }

    }
}
