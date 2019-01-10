package com.empresa.consumo.masivo.gestion.exception;


public class BusinessServiceNotFoundOkException extends Exception {

    public BusinessServiceNotFoundOkException() {
        super();
    }

    public BusinessServiceNotFoundOkException(String message) {
        super(message);
    }

    public BusinessServiceNotFoundOkException(String message, Exception e) {
        super(message, e);
    }
}
