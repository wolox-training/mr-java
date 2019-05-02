package wolox.training.exceptions;

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
