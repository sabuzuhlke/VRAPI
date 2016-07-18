package VRAPI.XMLClasses.ContainerActivityType;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by gebo on 23/05/2016.
 */
public class QueryResponse {
    private List<ActivityType> activityTypes;

    public QueryResponse() {
    }

    @XmlElement(name = "AktivitaetsTyp")
    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }
}
