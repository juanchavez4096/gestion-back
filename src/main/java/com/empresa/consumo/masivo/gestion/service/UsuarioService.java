package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.EmpresaRepository;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.UsuarioException;
import com.empresa.consumo.masivo.gestion.security.EncryptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EncryptService encryptService;


	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Boolean generatePasswordCodeBase64(String userEmail) throws UsuarioException, IOException {

		Usuario user;
		Boolean success = false;
		String encodedPassword = null;


		Optional<Usuario> userObj = usuarioRepository.findByEmail(userEmail);

		if (userObj.isPresent()) {



			user = userObj.get();
			String password = randomString();
			encodedPassword = passwordEncoder.encode(password);
			user.setCodigoRecuperacion(encodedPassword);
			usuarioRepository.save(user);
			String[] emailsArray = new String[1];
			emailsArray[0] = user.getEmail();
			emailService.sendSimpleMessage(emailsArray, user, password);
			success = true;
		} else {
			throw new UsuarioException("El correo utilizado no se encuentra registrado, utilice otro");
		}

		return success;
	}

	String randomString() {
		int len = 8;
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		// static SecureRandom rnd = new SecureRandom();

		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		SecureRandom rnd = new SecureRandom();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();

	}

	public Boolean changeForgottenPassword(String email, String password, String codigoVerificacion) throws UsuarioException {
		Boolean success = false;

		Optional<Usuario> userObj = usuarioRepository.findByEmail(email);
		if (userObj.isPresent()) {
			if (userObj.get().getCodigoRecuperacion() == null){
				throw new UsuarioException("Debe recuperar contraseña con este correo para enviarle un código de recuperacion");
			}
			if (encryptService.check(codigoVerificacion, userObj.get().getCodigoRecuperacion())){
				userObj.get().setPassword(encryptService.encrypt(password));
				userObj.get().setCodigoRecuperacion(null);
				usuarioRepository.save(userObj.get());
				success = true;
			}else{
				throw new UsuarioException("El codigo de recuperación utilizado es incorrecto, utilice otro");
			}
		} else {
			throw new UsuarioException("El correo utilizado no se encuentra registrado, utilice otro");
		}

		return success;
	}

}
