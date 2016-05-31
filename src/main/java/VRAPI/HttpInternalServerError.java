package VRAPI;

public class HttpInternalServerError extends RuntimeException {
    public HttpInternalServerError(String message) {
        super(message);
    }

    public HttpInternalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpInternalServerError(Throwable e) {
        super(e);
    }
}
