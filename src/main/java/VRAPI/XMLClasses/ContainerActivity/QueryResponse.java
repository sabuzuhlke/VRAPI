package VRAPI.XMLClasses.ContainerActivity;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 23/05/2016.
 */
public class QueryResponse {
    private List<Activity> activities;

    public QueryResponse() {
        activities = new ArrayList<>();
    }


    @XmlElement(name = "Aktivitaet")
    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
