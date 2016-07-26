package VRAPI.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Entity supplied cannot be brought into agreement with vertec")
public class HttpUnprocessableEntityException extends RuntimeException{
    public HttpUnprocessableEntityException(String message) {super(message);}

    public HttpUnprocessableEntityException(String message, Throwable cause) {super(message, cause);}
    public HttpUnprocessableEntityException(Throwable cause) {super(cause);}
}


