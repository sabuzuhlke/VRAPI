package VRAPI;

import VRAPI.Exceptions.HttpBadRequest;
import VRAPI.Exceptions.HttpForbiddenException;
import VRAPI.Exceptions.HttpUnauthorisedException;
import VRAPI.ResourceControllers.Authenticator;
import VRAPI.Util.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

public class VertecServerInfo {
    public static final String VERTEC_SERVER_HOST = "172.18.112.79";
    public static final String VERTEC_SERVER_PORT = "8095";

    public static final Integer BAD_REQUEST  = 3;
    public static final Integer UNAUTHORISED = 2;
    public static final Integer FORBIDDEN    = 1;
    public static final Integer AUTHORISED   = 0;

    public static final Logger log = LoggerFactory.getLogger("TestLogger");

    /**
     * Call this function at the start of every request handler
     * This will make a request for 'ZUK TEAM' from vertec and either setUp the query builder with provided username and pwd
     * or will throw appropriate error
     * @throws ParserConfigurationException
     */
    public static QueryBuilder ifUnauthorisedThrowErrorResponse(HttpServletRequest request) throws ParserConfigurationException {
        Authenticator authenticator = new Authenticator();
        String usernamePassword = request.getHeader("Authorization");
        Integer authLevel = authenticator.requestIsAuthorized(usernamePassword);
        if (authLevel.longValue() == VertecServerInfo.BAD_REQUEST) {
            throw new HttpBadRequest("Username and password not correctly set in header");
        } else if (authLevel.longValue() == VertecServerInfo.UNAUTHORISED) {
            throw new HttpUnauthorisedException("Wrong username or password");
        } else if (authLevel.longValue() == VertecServerInfo.FORBIDDEN) {
            throw new HttpForbiddenException("You have got limited access to the Vertec database, and were not authorised for this query!");
        }
        String[] usrpwd = usernamePassword.split(":");
        return new QueryBuilder(usrpwd[0], usrpwd[1]);
    }

}
