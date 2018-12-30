package com.empresa.consumo.masivo.gestion.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class NaiveEncryptService implements EncryptService {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @Override
    public String encrypt(String password) {
    	return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean check(String checkPassword, String realPassword) {
        return checkPassword.equals(realPassword);
    }
}
