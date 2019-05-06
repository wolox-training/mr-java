package wolox.training.exceptions;

import javax.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Received Null Attributes")
public class NullAttributesException extends Exception {

    public NullAttributesException(){ super(); }

}
