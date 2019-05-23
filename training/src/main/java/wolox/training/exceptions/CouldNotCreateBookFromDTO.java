package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Could not create book from DTO")
public class CouldNotCreateBookFromDTO extends Exception{
    public CouldNotCreateBookFromDTO(){
        super();
    }

    public CouldNotCreateBookFromDTO(String message)
    {
        super(message);
    }

}
