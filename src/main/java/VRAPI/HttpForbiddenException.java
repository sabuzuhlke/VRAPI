package VRAPI;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You have limited access to the Vertec database. You were not authorised to carry out this request.")

public class HttpForbiddenException extends RuntimeException {

    public HttpForbiddenException(String message) {super(message);}

    public HttpForbiddenException(String message, Throwable cause) {super(message, cause);}
    public HttpForbiddenException(Throwable cause) {super(cause);}
}
