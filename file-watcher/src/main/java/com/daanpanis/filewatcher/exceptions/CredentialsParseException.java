package com.daanpanis.filewatcher.exceptions;

public class CredentialsParseException extends Exception {

    public CredentialsParseException() {
    }

    public CredentialsParseException(String message) {
        super(message);
    }

    public CredentialsParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsParseException(Throwable cause) {
        super(cause);
    }

    public CredentialsParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
