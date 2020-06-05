package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Could not create book from DTO")
public class UnableToCreateBookFromDTOException extends Exception{
    public UnableToCreateBookFromDTOException(){
        super();
    }

    public UnableToCreateBookFromDTOException(String message)
    {
        super(message);
    }

}
