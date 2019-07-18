package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String springMailUsername;

	public void sendSimpleMessage(String[] to, Usuario usuario, String codigoVerificacion) {
		new Thread(() -> {

			MimeMessage messageToSend = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;

			try {
				helper = new MimeMessageHelper(messageToSend, true);
				String strDomain = "recovery@gestioncostosoperativos";
				helper.setFrom(new InternetAddress(springMailUsername, strDomain));
				helper.setTo(to);
				String title = "Recuperacion de contraseña";
				helper.setSubject(title);
				String content = build(usuario, codigoVerificacion,  title);
				helper.setText(content, true);
			} catch (MessagingException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			javaMailSender.send(messageToSend);
			//mailSender.send(message);

			log.info("An Email Notification was sent succesfully");
		}).start();
	}
	
	public String build(Usuario usuario, String codigoVerificacion, String titulo) {
    	Context context = new Context();
        context.setVariable("usuario",usuario);
        context.setVariable("codigoVerificacion", codigoVerificacion);
        context.setVariable("titulo", titulo);

        return templateEngine.process("resetPassword", context);
    }   

}
