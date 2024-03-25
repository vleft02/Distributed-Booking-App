package aueb.hestia;

public class RoomUnavailableException extends Exception {
    public RoomUnavailableException() {
        super("The room is not available for the dates given.");
    }

    public RoomUnavailableException(String message) {
        super(message);
    }
}
