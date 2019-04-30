package wolox.training.exceptions;

public class BookNotFoundException extends Exception {

    public BookNotFoundException(){

    }

    public BookNotFoundException(String message, Throwable cause){
        super(message, cause);
    }


    public BookNotFoundException(String message){
        super(message);
    }

}
