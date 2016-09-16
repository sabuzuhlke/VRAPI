package VRAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * This class provides a way of accessing confidential credentials used for building Static Maps, and for testing.
 */
public class MyAccessCredentials {

    private String pass = "";
    private String userName = "";

    public MyAccessCredentials() {
        String line;
        try{

            File file = new File("creds.txt");

            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader breader = new BufferedReader(reader);
            if((line = breader.readLine()) != null){
                userName = line;
                if((line = breader.readLine()) != null){
                    pass = line;
                } else System.out.println("Couldnt read password.");
            } else System.out.println("Couldnt read username nor Password.");
        }
        catch(Exception e){
            System.out.println("Could not open file: " +e.toString());
        }

    }

    public String getPass() {
        return pass;
    }

    public String getUserName() {
        return userName;
    }
}
