
package com.empresa.consumo.masivo.gestion.exception;

public abstract class BaseInvalidParamException extends Exception {

    public BaseInvalidParamException() {
        super();
    }

    public BaseInvalidParamException(String message) {
        super(message);
    }

    public BaseInvalidParamException(String message, Exception e) {

        super(message, e);
    }
}
