
package com.empresa.consumo.masivo.gestion.exception;


public class BusinessServiceException extends Exception {

    public BusinessServiceException() {
        super();
    }

    public BusinessServiceException(String message) {
        super(message);
    }

    public BusinessServiceException(String message, Exception e) {
        super(message, e);
    }
}
