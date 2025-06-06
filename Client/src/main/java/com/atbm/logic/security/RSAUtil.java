/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic.security;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public final class RSAUtil {

    // Tạo cặp key RSA 2048-bit
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    // Mã hóa với public key, trả về Base64
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Chế độ chuẩn
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Mã hóa key AES với public key, trả về Base64
    public static String encrypt(SecretKey secretKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Chế độ chuẩn
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(secretKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Giải mã với private key
    public static String decrypt(String encryptedBase64, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
        return new String(decryptedBytes);
    }

    public static SecretKey decryptSecretKey(String encryptedBase64Key, PrivateKey rsaPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);

        byte[] decryptedKeyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64Key));
        return new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");
    }

    public static String publicKeyToBase64(PublicKey publicKey) {
        byte[] encoded = publicKey.getEncoded(); // Lấy encoding chuẩn X.509
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static PublicKey base64ToPublicKey(String base64PublicKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    static {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String original = "Hello RSA, bảo mật chuẩn!";

        // Tạo cặp key
        KeyPair keyPair = RSAUtil.generateKeyPair();

        // Mã hóa
        String encrypted = RSAUtil.encrypt(original, keyPair.getPublic());
        System.out.println("Đã mã hóa: " + encrypted);

        // Giải mã
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivate());
        System.out.println("Đã giải mã: " + decrypted);

        var pubKey = keyPair.getPublic();

        var pubStr = publicKeyToBase64(pubKey);

        var pubKey2 = base64ToPublicKey(pubStr);

        encrypted = RSAUtil.encrypt(original, pubKey2);
        System.out.println("Đã mã hóa: " + encrypted);

        decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivate());
        System.out.println("Đã giải mã: " + decrypted);
    }
}
