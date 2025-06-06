/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic.security;

import java.security.KeyPair;
import javax.crypto.SecretKey;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public class TEST {

    static class MEM {

        SecretKey keyAES;
        KeyPair keyPairRSA;

        public MEM() throws Exception {
            this.keyAES = AESUtil.generateKey();
            this.keyPairRSA = RSAUtil.generateKeyPair();
        }

    }

    public static void main(String[] args) {
        try {
            var sender = new MEM();
            var getter = new MEM();

            var text = "TEST MÃ HÓA NÀO";

            var cypherText = AESUtil.encrypt(text, sender.keyAES);
            var cypherKeyAES = RSAUtil.encrypt(sender.keyAES, getter.keyPairRSA.getPublic());
            
            
            var decodeCypherKeyAES =  RSAUtil.decryptSecretKey(cypherKeyAES, getter.keyPairRSA.getPrivate());
            var decodeCypherText = AESUtil.decrypt(cypherText, decodeCypherKeyAES);
            
            
            System.out.println("decodeCypherText:  "  + decodeCypherText) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
