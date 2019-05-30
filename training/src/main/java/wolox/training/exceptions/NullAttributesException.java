package wolox.training.exceptions;

import javax.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Received Null Attributes")
public class NullAttributesException extends Exception {

    public NullAttributesException(){ super(); }

}
