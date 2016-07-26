package VRAPI.ResourceControllers;


import VRAPI.Entities.Contact;
import VRAPI.Exceptions.HttpInternalServerError;
import VRAPI.Exceptions.HttpNotFoundException;
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


@RestController
@Scope("prototype")
public class ContactController extends Controller {

    public ContactController() {
        super();
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
    @RequestMapping(value = "/contact/{id}/setOrganisationLink/{orgId}", method = RequestMethod.PUT)
    public ResponseEntity<Long> updateOrganisationLink(@PathVariable Long id, @PathVariable Long orgId) throws ParserConfigurationException {
        queryBuilder = AuthenticateThenReturnQueryBuilder();
        if ( ! isIdOfType(id, "Kontakt")) {
            throw new HttpNotFoundException("Contact with id: " + id + " does not exist");
        }
        if ( ! isIdOfType(orgId, "Firma")) {
            throw new HttpNotFoundException("Organisation with id" + orgId + " does not exist");
        }

        String xmlQuery = queryBuilder.setContactOrganisationLink(id, orgId);
        Document res = responseFor(new RequestEntity<>(xmlQuery, HttpMethod.POST, vertecURI));

        if (getTextField(res).equals("Updated 1 Objects")) {
            return new ResponseEntity<>(orgId ,HttpStatus.OK);

        } else {
            throw new HttpInternalServerError("Unknown response from vertec: " + getTextField(res));
        }
    }
}
