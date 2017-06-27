package ru.aygur.testdbxml;

import java.util.Objects;

/**
 * Created by dmitrii on 19.06.17.
 */
public class CriticalException extends Exception {

    private String detailMessage;
    private Throwable cause = this;

    CriticalException() {
        super();
    }
    CriticalException(String message) {
        this.detailMessage = message;
    }

    CriticalException(String message, Throwable cause) {
        this.detailMessage = message;
        this.cause = cause;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        return detailMessage;
    }
}
