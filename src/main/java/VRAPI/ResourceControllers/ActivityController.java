package VRAPI.ResourceControllers;

import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerActivity.Activity;
import VRAPI.XMLClasses.ContainerDetailedOrganisation.Organisation;
import VRAPI.XMLClasses.ContainerDetailedProjects.Project;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;

import static java.util.Collections.singletonList;

@RestController
@Scope("prototype")
public class ActivityController extends Controller {
    public ActivityController(){
        super();
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
  public ResponseEntity<Long> setOrgLink(@PathVariable Long id, @PathVariable Long orgID) throws ParserConfigurationException {
      VertecServerInfo.log.info("--------------- Setting Organisation Link of Activity ---------------------------->");
      queryBuilder = AuthenticateThenReturnQueryBuilder();
      if ( ! isIdOfType(id, "Aktivitaet")) {
          VertecServerInfo.log.info("--------------- Activity with id: " + id + " does not exist ------------------>");
          throw new HttpNotFoundException("Activity with id: " + id + " does not exist");
      }
      if ( ! isIdOfType(orgID, "Firma")) {
          VertecServerInfo.log.info("---------- Organisation with id: " + orgID + " does not exist ----------------->");
          throw new HttpNotFoundException("Organisation with id: " + orgID + " does not exist");
      }
      //Get Activity first -- This and following (org) are needed to provide legible logging of what would happen
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
      //TODO call function to PUT to Vertec here
      VertecServerInfo.log.info("Would PUT now, However, PUT is disabled!! (uncomment in code)");

//        Document res = responseFor(new RequestEntity<>(putQuery, HttpMethod.POST, vertecURI));
//
//        if (getTextField(res).equals("Updated 1 Objects")) {
//            return new ResponseEntity<>(orgID , HttpStatus.OK);
//
//        } else {
//            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
//        }
      VertecServerInfo.log.info("-------------------------------------------------------------------------------->");

      return new ResponseEntity<>(orgID, HttpStatus.OK);
  }
}
