/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic.security;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AESUtil {

    // Tạo key AES ngẫu nhiên 128-bit
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // key size
        return keyGen.generateKey();
    }

    // Mã hóa chuỗi đầu vào thành base64
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // chế độ đơn giản nhất
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // mã hóa ra base64 cho dễ in/log
    }

    // Giải mã từ base64 về lại plaintext
    public static String decrypt(String encryptedBase64, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
        return new String(decryptedBytes);
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
        // Chuẩn bị dữ liệu
        String original = "Hello AES, siêu bảo mật!";

        // Tạo key AES ngẫu nhiên
        SecretKey key = AESUtil.generateKey();

        // Mã hóa
        String encrypted = AESUtil.encrypt(original, key);
        System.out.println("Đã mã hóa: " + encrypted);

        // Giải mã
        String decrypted = AESUtil.decrypt(encrypted, key);
        System.out.println("Đã giải mã: " + decrypted);

    }
}
