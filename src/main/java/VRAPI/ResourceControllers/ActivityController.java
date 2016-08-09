package VRAPI.ResourceControllers;

import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.MergeClasses.ActivitiesForAddressEntry;
import VRAPI.Util.QueryBuilder;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerActivity.Activity;
import VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation;
import VRAPI.XMLClasses.ContainerDetailedProjects.Project;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

import static java.util.Collections.singletonList;

@RestController
@Scope("prototype")
public class ActivityController extends Controller {
    public ActivityController(){
        super();
    }

    public ActivityController(QueryBuilder queryBuilder){
        super(queryBuilder);
    }

  //======================================================================================================================//
 // PUT /activity                                                                                                        //
//======================================================================================================================//
  @ApiOperation(value = "Re-Link Activity (id) to an organisation (orgID)", nickname = "setOrgLink")
  @ApiImplicitParams( {
          @ApiImplicitParam(name = "Authorization",
                  value = "username:password",
                  required = true,
                  dataType = "string",
                  paramType = "header")
  })
  @RequestMapping(value = "/activity/{id}/setOrganisationLink/{orgID}", method = RequestMethod.PUT)
  public ResponseEntity<Long> setOrgLinkEndpoint(@PathVariable Long id, @PathVariable Long orgID) throws ParserConfigurationException {
      queryBuilder = AuthenticateThenReturnQueryBuilder();

      return setOrgLink(id, orgID);
  }
    //=======================================METHODS========================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================

    public ResponseEntity<Long> setOrgLink( Long id,Long orgID) {
        VertecServerInfo.log.info("--------------- Setting Organisation Link of Activity ---------------------------->");
        if ( ! isIdOfType(id, "Aktivitaet")) {
            VertecServerInfo.log.info("--------------- Activity with id: " + id + " does not exist ------------------>");
            throw new HttpNotFoundException("Activity with id: " + id + " does not exist");
        }
        if ( ! isIdOfType(orgID, "Firma")) {
            VertecServerInfo.log.info("---------- Organisation with id: " + orgID + " does not exist ----------------->");
            throw new HttpNotFoundException("Organisation with id: " + orgID + " does not exist");
        }
        //Get Activity first
        Activity activity  = callVertec(queryBuilder.getActivities(singletonList(id))
                , VRAPI.XMLClasses.ContainerActivity.Envelope.class)
                .getBody().getQueryResponse().getActivities().get(0); //TODO see whether refactor is possible

        //Then get organisation
        Organisation organisation = callVertec(queryBuilder.getOrganisationDetails(singletonList(orgID))
                , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                .getBody().getQueryResponse().getOrganisationList().get(0);

        VertecServerInfo.log.info("Request seems OK, about to re-point Activity " + activity.getTitle() + "(v_id: " + activity.getObjid() + ")" +
                " to Organisation " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")");

        if(activity.getAddressEntry() != null){
            Long orgref = activity.getAddressEntry().getObjref();
            if(isIdOfType(orgref,"Firma")){
                Organisation org = callVertec(queryBuilder.getOrganisationDetails(singletonList(orgref))
                        , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                        .getBody().getQueryResponse().getOrganisationList().get(0);


                VertecServerInfo.log.info("Activity pointed to Organisation " + org.getName() +
                        "(v_id: " + org.getObjId() + ") this would be overwritten" +
                        " to point to Organisation: " + organisation.getName() +"(v_id: " + organisation.getObjId() + ")!!");
            }
        }

        String putQuery = queryBuilder.setActivityOrgLink(id,orgID);

        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {

            VertecServerInfo.log.info("Activity now points to Organisation: " + orgID);
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            return new ResponseEntity<>(orgID , HttpStatus.OK);

        } else {
            VertecServerInfo.log.info("Failed to Point activity to Organisation, Unknown response from Vertec" );
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));

        }
    }



    public ResponseEntity<Long> setContactLink(Long activityId, Long contactId) {

        if ( ! isIdOfType(contactId, "Kontakt")) {
            VertecServerInfo.log.info("--------------- Activity with id: " + contactId + " does not exist ------------------>");
                throw new HttpNotFoundException("Activity with id: " + contactId + " does not exist");
        }
        String query = queryBuilder.setActivityContactLink(activityId, contactId);

        Document res = responseFor(new RequestEntity<>(query, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {

            VertecServerInfo.log.info("Activity now points to Contact: " + contactId);
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            return new ResponseEntity<>(contactId , HttpStatus.OK);

        } else {
            VertecServerInfo.log.info("Failed to Point activity to Contact, Unknown response from Vertec" );
            VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));

        }
    }
}
