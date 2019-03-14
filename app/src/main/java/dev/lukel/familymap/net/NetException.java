package dev.lukel.familymap.net;

import java.net.MalformedURLException;

public class NetException extends Exception {
    public NetException() {
        super();
    }
    public NetException(String message) {
        super(message);
    }
    public NetException(Exception e) {
        super(e);
    }
    public NetException(String message, Throwable cause) {
        super(message, cause);
    }
}
