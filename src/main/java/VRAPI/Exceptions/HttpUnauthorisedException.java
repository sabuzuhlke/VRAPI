package VRAPI.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Wrong username or password")
public class HttpUnauthorisedException extends RuntimeException{
    public HttpUnauthorisedException(String message) {super(message);}

    public HttpUnauthorisedException(String message, Throwable cause) {super(message, cause);}
    public HttpUnauthorisedException(Throwable cause) {super(cause);}
}
