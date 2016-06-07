package VRAPI.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Requested resource does not exist")
public class HttpNotFoundException extends RuntimeException {
    public HttpNotFoundException(String message) {
        super(message);
    }

    public HttpNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpNotFoundException(Throwable e) {
        super(e);
    }
}
