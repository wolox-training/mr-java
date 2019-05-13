package wolox.training.exceptions;

public class BookIdMismatchException extends Exception {

    public BookIdMismatchException(){
        super();
    }

    public BookIdMismatchException(String message, Throwable cause){
        super(message, cause);
    }

    public BookIdMismatchException(String message){
        super(message);
    }
}
