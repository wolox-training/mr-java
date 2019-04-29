package wolox.training.exceptions;

import javax.persistence.RollbackException;

public class NullArgumentsException extends Exception {

    public NullArgumentsException(String errorMessage){
        super(errorMessage);
    }

}
