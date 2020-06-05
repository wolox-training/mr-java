package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "User Id Mismatch")
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
