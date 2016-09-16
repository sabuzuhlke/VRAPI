package VRAPI.Exceptions;



public class NoIdSuppliedException extends Exception {
        public NoIdSuppliedException(String message) {
            super(message);
        }

        public NoIdSuppliedException(String message, Throwable cause) {
            super(message, cause);
        }

        public NoIdSuppliedException(Throwable e) {
            super(e);
        }
}
