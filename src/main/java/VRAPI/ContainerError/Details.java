package VRAPI.ContainerError;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by sabu on 06/05/2016.
 */
public class Details {

    private List<String> detailitem;

    public Details() {
    }

    @XmlElement(name = "detailitem")
    public List<String> getDetailitem() {
        return detailitem;
    }

    public void setDetailitem(List<String> detailitem) {
        this.detailitem = detailitem;
    }
}
