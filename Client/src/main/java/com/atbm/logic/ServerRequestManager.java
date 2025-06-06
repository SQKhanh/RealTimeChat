/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic;

import com.atbm.logic.security.AESUtil;
import com.atbm.logic.security.RSAUtil;
import com.atbm.model.MemChat;
import com.atbm.network.CMD;
import com.atbm.network.MessageWriter;
import com.atbm.network.Session;
import com.atbm.ui.MainFrame;
import java.net.Socket;
import java.security.KeyPair;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerRequestManager {

    public static final ServerRequestManager Instance = new ServerRequestManager();

    private Session session = null;

    @Getter
    private String name;
    private SecretKey keyAES;
    private KeyPair keyPairRSA;

    public void sendChatMessage(final MemChat curMemChat, final String text) throws Exception {

        try (var msg = new MessageWriter(CMD.SEND_CHAT_MESSAGE)) {
            final var writer = msg.writer();
            writer.writeUTF(curMemChat.getName());
            writer.writeUTF(AESUtil.encrypt(text, keyAES));
            writer.writeUTF(RSAUtil.encrypt(keyAES, curMemChat.getPubKeyRSA()));
            this.session.putMessage(msg);
        }
    }

    public String decryptMesage(final String cypherText, final String cypherKey) throws Exception {
        final var keyAES = RSAUtil.decryptSecretKey(cypherKey, keyPairRSA.getPrivate());
        return AESUtil.decrypt(cypherText, keyAES);
    }

    public void login() throws Exception {
        try (var msg = new MessageWriter(CMD.LOGIN)) {
            final var writer = msg.writer();
            writer.writeUTF(name);
            writer.writeUTF(RSAUtil.publicKeyToBase64(this.keyPairRSA.getPublic()));
            this.session.putMessage(msg);
        }
    }

    /**
     *
     * @param name
     * @return null nếu kết nối thành công
     */
    public String connect(final String name) {
        if (session != null) {
            return null;
        }

        SecretKey keyAESCheck;

        try {
            keyAESCheck = AESUtil.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khởi tạo khóa AES";
        }

        KeyPair keyPairRSACheck;
        try {
            keyPairRSACheck = RSAUtil.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khởi tạo cặp khóa RSA";
        }

        Session sessionCheck;
        try {
            sessionCheck = new Session(new Socket("localhost", 15555));
        } catch (Exception e) {
            e.printStackTrace();
            return "Không thể kết nối tới máy chủ";
        }

        this.name = name;
        this.keyAES = keyAESCheck;
        this.keyPairRSA = keyPairRSACheck;

        this.session = sessionCheck;
        this.session.startReadMessage();
        this.session.startSendMessage();

        return null;
    }

    public void disconnect() {
        int confirm = JOptionPane.showConfirmDialog(
                MainFrame.Instance,
                "Mất kết nối, vui lòng tắt app bật lại",
                "Thông báo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Thoát chương trình
            System.exit(0);
        }
    }
}
