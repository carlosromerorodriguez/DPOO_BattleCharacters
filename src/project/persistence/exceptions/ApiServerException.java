package project.persistence.exceptions;

public class ApiServerException extends Exception {
    public ApiServerException() {
        super("""
                Couldn’t connect to the remote server.\s
                Reverting to local data.
                """);
    }
}
