package wolox.training.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(){
        super();
    }

    public UserNotFoundException(String message, Throwable cause){
        super(message, cause);
    }


    public UserNotFoundException(String message){
        super(message);
    }
}
