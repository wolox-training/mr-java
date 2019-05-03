package wolox.training.exceptions;

public class UserIdMismatchException extends Exception {

    public UserIdMismatchException(){
        super();
    }

    public UserIdMismatchException(String message){
        super(message);
    }

    public UserIdMismatchException(String message, Throwable cause){
        super(message, cause);
    }
}
