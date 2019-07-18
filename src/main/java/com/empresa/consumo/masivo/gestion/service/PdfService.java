package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.Pdf;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.controller.MaterialController;
import com.empresa.consumo.masivo.gestion.controller.ProductoController;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoRepository;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.openhtmltopdf.DOMBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private MaterialController materialController;
	@Autowired
	private ProductoController productoController;
	@Autowired
	private UsuarioRepository usuarioRepository;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Void generatePdf(Date desde, Date hasta, UsuarioDTO usuarioDTO, String type, HttpServletRequest request, HttpServletResponse response) {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addDialect(new Java8TimeDialect());
		templateEngine.setTemplateResolver(templateResolver);

		Context context = new Context();

		Pdf pdf = null;
		if (type.equals("Materiales")){
			List<MaterialDTO> listMaterials =  materialRepository.findByEmpresa_EmpresaIdAndActivoAndFechaCreacionBetweenOrderByNombre(usuarioDTO.getEmpresaId(), Boolean.TRUE, LocalDateTime.ofInstant(desde.toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(hasta.toInstant(), ZoneId.systemDefault()))
					.stream()
					.map(ProductoMapper.INSTANCE::materialToMaterialDTO)
					.collect(Collectors.toList());


			pdf = new Pdf(materialController.doLogic(listMaterials, usuarioDTO));
		}else if (type.equals("Productos")){
			pdf = new Pdf(productoController.getAllProductsByMaterialIdWithUpdate(null, usuarioDTO.getEmpresaId(), false, desde, hasta));
		}else if (type.equals("Administración")){
			pdf = new Pdf(usuarioRepository.findByEmpresa_EmpresaIdAndFechaCreacionBetweenIgnoreCaseOrderByNombre(usuarioDTO.getEmpresaId(), LocalDateTime.ofInstant(desde.toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(hasta.toInstant(), ZoneId.systemDefault())));
		}

		context.setVariable("titulo", type);
		context.setVariable("pdf", pdf);
		context.setVariable("usuarioDTO", usuarioDTO);

		context.setVariable("desde", desde);
		context.setVariable("hasta", hasta);
		context.setVariable("hoy", LocalDateTime.now(ZoneId.systemDefault()));


		String html = templateEngine.process("home", context);

		response.setContentType("application/pdf");

		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", type+ ".pdf");
		response.setHeader(headerKey, headerValue);




		try{
			OutputStream outputStream = response.getOutputStream();
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(DOMBuilder.jsoup2DOM(Jsoup.parse(html)),"");
			renderer.layout();
			renderer.createPDF(outputStream);
			outputStream.close();
		} catch (Exception e) {

		}



		return null;

	}
}
