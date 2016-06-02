package VRAPI;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Missing username or password")
public class HttpBadRequest extends RuntimeException {
    public HttpBadRequest(String message) {
        super(message);
    }
}
