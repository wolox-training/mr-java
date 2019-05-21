package wolox.training.exceptions;

public class ConnectionFailedException extends Exception {

    public ConnectionFailedException(){
        super();
    }

    public ConnectionFailedException(Integer statusCode, String message){
        super("Connection failed. Status code: "+statusCode+". Message: "+message+".");
    }

}
