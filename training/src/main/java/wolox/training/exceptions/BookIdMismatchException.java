package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Book Id Mismatch")
public class BookIdMismatchException extends Exception {

    public BookIdMismatchException(){ super();  }

    public BookIdMismatchException(String message, Throwable cause){
        super(message, cause);
    }

    public BookIdMismatchException(String message){
        super(message);
    }
}
