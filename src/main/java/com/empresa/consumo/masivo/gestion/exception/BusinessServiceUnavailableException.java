package com.empresa.consumo.masivo.gestion.exception;

public class BusinessServiceUnavailableException extends Exception {

    public BusinessServiceUnavailableException() {
        super();
    }

    public BusinessServiceUnavailableException(String message) {
        super(message);
    }

    public BusinessServiceUnavailableException(String message, Exception e) {
        super(message, e);
    }
}
