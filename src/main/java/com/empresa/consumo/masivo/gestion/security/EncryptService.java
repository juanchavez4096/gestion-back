package com.empresa.consumo.masivo.gestion.security;

public interface EncryptService {
    String encrypt(String password);
    boolean check(String checkPassword, String realPassword);
}
