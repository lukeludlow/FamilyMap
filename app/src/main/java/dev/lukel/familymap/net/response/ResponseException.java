package dev.lukel.familymap.net.response;

/**
 * Error Response Body:
 * {
 * “message”: “Description of the error”
 * }
 */
public class ResponseException extends Exception {
    public ResponseException() {
        super("description of the error");
    }
    public ResponseException(String s) {
        super(s);
    }
    public ResponseException(Exception e) {
        super(e.getMessage());
    }
}

