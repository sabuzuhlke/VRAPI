package VRAPI.ContainerJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class OrganisationList {

    @JsonProperty("organisations")
    private List<JSONOrganisation> organisationList;

    public OrganisationList() {
    }

    public List<JSONOrganisation> getOrganisationList() {
        return organisationList;
    }

    public void setOrganisationList(List<JSONOrganisation> organisationList) {
        this.organisationList = organisationList;
    }
}
