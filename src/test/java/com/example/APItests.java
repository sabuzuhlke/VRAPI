package com.example;

/**
 * Created by gebo on 28/04/2016.
 */

import VRAPI.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import VRAPI.Application;
import VRAPI.ContainerDetailedContact.Contact;
import VRAPI.ContainerDetailedContact.Organisation;
import VRAPI.ContainerJSON.ZUKResponse;
import VRAPI.ResourceController;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
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
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
/**
 * Created by gebo on 25/04/2016.
 */
public class APItests {
    private ResourceController rc;

    @Before
    public void setUp(){
        this.rc = new ResourceController();

    }

    @Test
    public void apiIsUP(){
        RestTemplate rt = new RestTemplate();
        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/ping";
        RequestEntity<String> req = null;
        ResponseEntity<String> res;
        try{

            req = new RequestEntity<>( HttpMethod.GET,new URI(url));
        }
        catch(Exception e){
            System.out.println("Could not create Request");
        }
        assertTrue(req != null);

        res = rt.exchange(req,String.class);

        assertTrue(res != null);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().equals("ping"));

    }
//
//    @Test
//    public void canGetZUK(){
//
//        RestTemplate rt = new RestTemplate();
//        String url = "http://" + rc.getOwnIpAddress() + ":" + rc.getOwnPortNr() + "/organisations/ZUK/";
//        RequestEntity<String> req = null;
//        ResponseEntity<String> res;
//        try{
//
//            req = new RequestEntity<>( HttpMethod.GET,new URI(url));
//        }
//        catch(Exception e){
//            System.out.println("Could not create Request");
//        }
//    }
}
