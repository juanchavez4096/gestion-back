package com.empresa.consumo.masivo.gestion.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.empresa.consumo.masivo.gestion.exception.BaseInvalidParamException;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceException;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceNotFoundOkException;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceUnavailableException;
import com.empresa.consumo.masivo.gestion.exception.ExceptionJSONInfo;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseInvalidParamException.class)
    public @ResponseBody
    ExceptionJSONInfo handleBaseException(HttpServletRequest request, Exception ex) {

        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setException(ex.getClass().getName());
        log.error(ex.getMessage());

        return response;
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BusinessServiceNotFoundException.class)
    public @ResponseBody
    ExceptionJSONInfo handleNotFoundException(HttpServletRequest request, Exception ex) {

        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setException(ex.getClass().getName());
        log.error(ex.getMessage());

        return response;
    }
    
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessServiceNotFoundOkException.class)
    public @ResponseBody
    ExceptionJSONInfo handleNotFoundOkException(HttpServletRequest request, Exception ex) {

        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());
        response.setStatus(HttpStatus.OK.value());
        response.setException(ex.getClass().getName());
        log.error(ex.getMessage());

        return response;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(BusinessServiceException.class)
    public @ResponseBody
    ExceptionJSONInfo handleBusinessServiceException(HttpServletRequest request, Exception ex) {

        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setException(ex.getClass().getName());
        log.error(ex.getMessage());

        return response;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(BusinessServiceUnavailableException.class)
    public @ResponseBody
    ExceptionJSONInfo handleBusinessServiceUnavailableException(HttpServletRequest request, Exception ex) {

        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setException(ex.getClass().getName());
        log.error(ex.getMessage());

        return response;
    }
	
}
