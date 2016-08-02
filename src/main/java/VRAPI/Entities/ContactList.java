package VRAPI.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by sabu on 01/08/2016.
 */
public class ContactList {

    private List<Contact> contacts;

    public ContactList() {
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String toJSONString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }

}
