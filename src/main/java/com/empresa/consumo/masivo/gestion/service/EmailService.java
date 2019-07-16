package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

@Component
public class EmailService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TemplateEngine templateEngine;

	public void sendSimpleMessage(String[] to, Usuario usuario, String codigoVerificacion) {
		new Thread(() -> {

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("smtp.gmail.com");
			mailSender.setPort(587);

			mailSender.setUsername("jjchavez@urbe.edu.ve");
			mailSender.setPassword("1956784");

			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.debug", "true");

			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			String titulo = "Recuperacion de contraseña";
			message.setSubject(titulo);
			String content = build(usuario, codigoVerificacion, titulo);

			message.setText(content);

			mailSender.send(message);
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
