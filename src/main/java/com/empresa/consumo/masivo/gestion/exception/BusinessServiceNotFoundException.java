package com.empresa.consumo.masivo.gestion.exception;

public class BusinessServiceNotFoundException extends Exception {

    public BusinessServiceNotFoundException() {
        super();
    }

    public BusinessServiceNotFoundException(String message) {
        super(message);
    }

    public BusinessServiceNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
