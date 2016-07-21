package VRAPI.Util;

/*
This class serves to persist the teamId and follower maps in the API. A new Resource Controller is created on every request, this way
we do not haveto reuild these maps everz single time.
 */
import VRAPI.MyAccessCredentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.runtime.JSType.toBoolean;


public enum StaticMaps {

    INSTANCE;

    MapBuilder mapBuilder = null;

    private Map<Long, String> teamIDMap = new HashMap<>();
    private Map<Long, List<String>> followerMap = new HashMap<>();
    private Map<Long, Long> supervisorMap = new HashMap();
    private Map<Long, String> activityTypeMap = new HashMap<>();
    private Map<Long, String> projectTypeMap = new HashMap<>(); //TODO: populate map
    private Map<Long, String> currencyMap = new HashMap<>(); //TODO: populate map

    public Map<Long, Long> getSupervisorMap() {
        if (supervisorMap.isEmpty()) {
            mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
            MyAccessCredentials mac = new MyAccessCredentials();
            mapBuilder.setPassword(mac.getPass());
            mapBuilder.setUsername(mac.getUserName());
            setUpSupervisorMap();
        }
        return supervisorMap;
    }

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

    public Map<Long, List<String>> getFollowerMap() {
        if(followerMap.isEmpty()){
            mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
            MyAccessCredentials mac = new MyAccessCredentials();
            mapBuilder.setPassword(mac.getPass());
            mapBuilder.setUsername(mac.getUserName());
            setUpFollowerMap();
        }
        return followerMap;
    }

    public Map<Long, String> getActivityTypeMap() {
        if (activityTypeMap.isEmpty()) {
            mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
            MyAccessCredentials mac = new MyAccessCredentials();
            mapBuilder.setPassword(mac.getPass());
            mapBuilder.setUsername(mac.getUserName());
            setUpActivityTypeMap();
        }
        return activityTypeMap;
    }

    private void setUpActivityTypeMap() {
        System.out.println("SETTING UP ACTIVITY TYPE MAP, YOU SHOULD ONLY SEE THIS ONCE");
        this.activityTypeMap = mapBuilder.createActivityTypeMap();
    }

    public String getTeamQuery(List<Long> ids) {
        mapBuilder = mapBuilder == null ? new MapBuilder() : mapBuilder;
        MyAccessCredentials mac = new MyAccessCredentials();
        mapBuilder.setPassword(mac.getPass());
        mapBuilder.setUsername(mac.getUserName());
        return mapBuilder.getXMLQuery_TeamIdsAndEmails(ids);
    }

    public void setUpSupervisorMap() {
        System.out.println("SETTING UP SUPERVISOR MAP, YOU SHOULD ONLY SEE THIS ONCE");
        mapBuilder.createSupervisorMap();
        this.supervisorMap = mapBuilder.supervisorMap;
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
