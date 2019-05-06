package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Book Not Found")
public class BookNotFoundException extends Exception {

    public BookNotFoundException(){
        super();
    }

    public BookNotFoundException(String message, Throwable cause){
        super(message, cause);
    }


    public BookNotFoundException(String message){
        super(message);
    }

}
