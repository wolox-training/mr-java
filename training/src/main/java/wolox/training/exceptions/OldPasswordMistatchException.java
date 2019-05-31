package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Old Password Mismatch")
public class OldPasswordMistatchException extends Exception {
    public OldPasswordMistatchException(){
        super();
    }

    public OldPasswordMistatchException(String message){
        super(message);
    }

    public OldPasswordMistatchException(String message, Exception ex){
        super(message, ex);
    }

}
