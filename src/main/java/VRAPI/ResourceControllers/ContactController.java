package VRAPI.ResourceControllers;


import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
import VRAPI.Util.QueryBuilder;
import VRAPI.VertecServerInfo;
import VRAPI.XMLClasses.ContainerDetailedContact.Contact;
import VRAPI.XMLClasses.ContainerDetailedContact.Envelope;
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
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;


@RestController
@Scope("prototype")
public class ContactController extends Controller {

    public ContactController() {
        super();
    }
    public ContactController(QueryBuilder queryBuilder) {
        super(queryBuilder);
    }
  //======================================================================================================================//
 // PUT /contact                                                                                                         //
//======================================================================================================================//
    /**
     * @return: Id returned is that of the organisation the contact is now linked to
     */
    @ApiOperation(value = "Change the organisation the contact works at", nickname = "Organisation Link")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "Authorization",
                    value = "username:password",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @RequestMapping(value = "/contact/{id}/setOrganisationLink/{orgID}", method = RequestMethod.PUT)
    public ResponseEntity<Long> setOrganisationLinkEndpoint(@PathVariable Long id, @PathVariable Long orgID) throws ParserConfigurationException {
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
    public ResponseEntity<Long> setOrgLink(Long id, Long orgID) {
        VertecServerInfo.log.info("\n\n--------------- Setting Organisation Link of Contact ---------------------------->");

        if ( ! isIdOfType(id, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + id + " does not exist");
        }
        if ( ! isIdOfType(orgID, "Firma")) {
            throw new HttpNotFoundException("Organisation with id" + orgID + " does not exist");
        }

        String xmlQuery = queryBuilder.setContactOrganisationLink(id, orgID);

        //Get contact first
        Contact contact = callVertec(queryBuilder.getContactDetails(singletonList(id))
                , Envelope.class)
                .getBody().getQueryResponse().getContactList().get(0); //TODO see whether refactor is possible

        Organisation organisation = callVertec(queryBuilder.getOrganisationDetails(singletonList(orgID))
                , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                .getBody().getQueryResponse().getOrganisationList().get(0);

        VertecServerInfo.log.info("Request seems OK, about to re-point contact " + contact.getFirstName() + " " + contact.getSurnname() + "(v_id: " + contact.getObjId() + ")" +
                " to Organisation " + organisation.getName() + "(v_id: " + organisation.getObjId() + ")");

        if(contact.getOrganisation()!= null){
            Long clientref = contact.getOrganisation().getObjref();
            if(isIdOfType(clientref,"Firma")){
                Organisation org = callVertec(queryBuilder.getOrganisationDetails(singletonList(clientref))
                        , VRAPI.XMLClasses.ContainerDetailedOrganisation.Envelope.class)
                        .getBody().getQueryResponse().getOrganisationList().get(0);


                VertecServerInfo.log.info("Clientref pointed to Organisation " + org.getName() +
                        "(v_id: " + org.getObjId() + ") this would be overwritten" +
                        " to point to Organisation: " + organisation.getName() +"(v_id: " + organisation.getObjId() + ")!!");
            }
        }

        VertecServerInfo.log.info("Would PUT now, However, PUT is disabled!! (uncomment in code)");
//        Document res = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));
//
//        if (getTextField(res).equals("Updated 1 Objects")) {
//            return new ResponseEntity<>(orgID ,HttpStatus.OK);
//
//        } else {
//            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
//        }
        VertecServerInfo.log.info("-------------------------------------------------------------------------------->\n\n");

        return new ResponseEntity<>(orgID , HttpStatus.OK);
    }
}
