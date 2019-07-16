package com.empresa.consumo.masivo.gestion.controller;

import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.service.PdfService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("api/pdf")
public class PdfController {
	
	Logger log = LogManager.getLogger();

	@Autowired
	private PdfService pdfService;

	@RequestMapping(path = "downloadPdf", method = RequestMethod.GET)
	public Void downloadPdf(@RequestParam("desde") Date desde, @RequestParam("hasta") Date hasta, @RequestParam("type") String type, @AuthenticationPrincipal UsuarioDTO usuarioDTO,
										   HttpServletRequest request, HttpServletResponse response) throws IllegalStateException{
		
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return pdfService.generatePdf(desde, hasta, usuarioDTO, type, request, response);
	}
	
	
}
