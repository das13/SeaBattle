package seaBattle.model;

public class BSException extends Exception {

    /**
     * empty constructor
     */
    public BSException() {
        super();
    }

    /**
     * constructor with message
     * @param message message for user
     */
    public BSException(String message) {
        super(message);
    }

    /**
     * constructor with message and exception
     * @param message message for user
     * @param cause exception that will lead to this
     */
    public BSException(String message, Throwable cause) {
        super(message, cause);
    }
}
