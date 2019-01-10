package com.empresa.consumo.masivo.gestion.exception;

public class ImageNotFoundException extends BusinessServiceNotFoundOkException {

	public ImageNotFoundException(){
		super();
	}

	public ImageNotFoundException(String message){
		super(message);
	}

	public ImageNotFoundException(String message, Exception e){
		super(message,e);
	}
}
