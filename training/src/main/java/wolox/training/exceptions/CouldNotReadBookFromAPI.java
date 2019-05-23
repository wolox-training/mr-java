package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Could not read book from API")
public class CouldNotReadBookFromAPI extends Exception {

    public CouldNotReadBookFromAPI(){
        super();
    }

    public CouldNotReadBookFromAPI(String message){
        super(message);
    }

}
