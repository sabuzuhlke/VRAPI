package VRAPI;

/**
 * This class is used in tests to show requests will fail if invalid vertec username and password is provided in request header
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MyLimitedCredentials {

    private String pass = "";
    private String userName = "";

    public MyLimitedCredentials() {
        String line;
        try{

            File file = new File("limitedCreds.txt");

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