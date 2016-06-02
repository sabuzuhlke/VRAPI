package VRAPI.ResourceController;

/*
This class serves to persist the teamId and follower maps in the API. A new Resource Controller is created on every request, this way
we do not haveto reuild these maps everz single time.
 */
import VRAPI.MapBuilder;
import VRAPI.MyAccessCredentials;
import VRAPI.ResourceController.ResourceController;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.runtime.JSType.toBoolean;


public enum StaticMaps {

    INSTANCE;

    MapBuilder mapBuilder = null;

    private Map<Long, String> teamIDMap = new HashMap<>();
    private Map<Long, List<String>> followerMap = new HashMap<>();

    public Map<Long, String> getTeamIDMap() {
        if(teamIDMap.isEmpty()){
            mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
            MyAccessCredentials mac = new MyAccessCredentials();
            mapBuilder.setPassword(mac.getPass());
            mapBuilder.setUsername(mac.getUserName());
            setUpTeamMap();
        }
        return teamIDMap;
    }

    public Map<Long, List<String>> getFollowerMap(){
        if(followerMap.isEmpty()){
            mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
            MyAccessCredentials mac = new MyAccessCredentials();
            mapBuilder.setPassword(mac.getPass());
            mapBuilder.setUsername(mac.getUserName());
            setUpFollowerMap();
        }
        return followerMap;
    }


    public void setUpTeamMap() {
        System.out.println("SETTING UP TEAM MAP, YOU SHOULD ONLY SEE THIS ONCE");
        this.teamIDMap = mapBuilder.createTeamIdMap();
    }

    public void setUpFollowerMap() {
        System.out.println("SETTING UP FOLLOWER MAP, YOU SHOULD ONLY SEE THIS ONCE");
        this.followerMap = mapBuilder.createFollowerMap();
    }
}
