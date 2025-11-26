package com.example.a3_simulacaobancaria_com_interface;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CriptografiaUtil {

    private static final String CHAVE = "1234567890123456"; // 16 bytes

    public static String criptografar(String texto) throws Exception {
        SecretKeySpec chave = new SecretKeySpec(CHAVE.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, chave);

        byte[] criptografado = cipher.doFinal(texto.getBytes("UTF-8"));

        return Base64.getEncoder().withoutPadding().encodeToString(criptografado);
    }

    public static String descriptografar(String textoCriptografado) throws Exception {

        textoCriptografado = textoCriptografado
                .replace("\n", "")
                .replace("\r", "")
                .replace(" ", "")
                .trim();

        SecretKeySpec chave = new SecretKeySpec(CHAVE.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, chave);

        byte[] decodificado = Base64.getDecoder().decode(textoCriptografado);
        byte[] descriptografado = cipher.doFinal(decodificado);

        return new String(descriptografado, "UTF-8");
    }
}
