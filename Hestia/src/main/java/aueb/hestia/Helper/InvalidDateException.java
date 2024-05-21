package aueb.hestia.Helper;

public class InvalidDateException extends Exception {
    public InvalidDateException() {
        super("The date given is Invalid");
    }

    public InvalidDateException(String message) {
        super(message);
    }
}
