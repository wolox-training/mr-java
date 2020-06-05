package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.ALREADY_REPORTED, reason = "Book Already Owned")
public class BookAlreadyOwnedException extends Exception {

    public BookAlreadyOwnedException(){
        super();
    }

    public BookAlreadyOwnedException(String message){
        super(message);
    }

    public BookAlreadyOwnedException(String message, Throwable error){
        super(message, error);
    }

}
