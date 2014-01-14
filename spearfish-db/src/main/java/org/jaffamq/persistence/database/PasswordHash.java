package org.jaffamq.persistence.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by urwisy on 13.01.14.
 */
public class PasswordHash {

    private static final String SALT="WhatIsTorpidoMQ?";

    public static String hash(String raw){

        if(raw == null){
            throw new IllegalArgumentException("String to hash should not be null");
        }

        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unexpected NoSuchAlgorithmException", e);
        }

        md.update((raw + SALT).getBytes(StandardCharsets.UTF_8));
        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder(65);

        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
