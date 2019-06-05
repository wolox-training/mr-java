package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Old Password Mismatch")
public class OldPasswordMismatchException extends Exception {
    public OldPasswordMismatchException(){
        super();
    }

    public OldPasswordMismatchException(String message){
        super(message);
    }

    public OldPasswordMismatchException(String message, Exception ex){
        super(message, ex);
    }

}
