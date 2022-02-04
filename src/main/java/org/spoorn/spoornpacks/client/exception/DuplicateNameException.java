package org.spoorn.spoornpacks.client.exception;

public class DuplicateNameException extends Exception {

    public DuplicateNameException(String message) {
        super(message);
    }

    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
