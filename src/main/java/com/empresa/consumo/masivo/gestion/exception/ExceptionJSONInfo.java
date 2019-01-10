package com.empresa.consumo.masivo.gestion.exception;

public class ExceptionJSONInfo {

    private String path;
    private String message;
    private Integer status;
    private String exception;

    public String getPath() {
        return path;
    }

    public void setPath(String url) {
        this.path = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}

