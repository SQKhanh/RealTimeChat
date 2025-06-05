/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic;

import com.atbm.network.CMD;
import com.atbm.network.MessageWriter;
import com.atbm.network.Session;
import com.atbm.ui.MainFrame;
import java.net.Socket;
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
    private String RSApub, RSApri;

    public void sendChatMessage(final String curMemChat, final String text) throws Exception {
        try (var msg = new MessageWriter(CMD.SEND_CHAT_MESSAGE)) {
            final var writer = msg.writer();
            writer.writeUTF(curMemChat);
            writer.writeUTF(text);
            this.session.putMessage(msg);
        }
    }

    public boolean connect(final String name, final String RSApub, final String RSApri) {
        if (session != null) {
            return true;
        }
        Session sessionCheck;
        try {
            sessionCheck = new Session(new Socket("localhost", 15555));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        this.session = sessionCheck;
        this.session.startReadMessage();
        this.session.startSendMessage();

        this.name = name;
        this.RSApub = RSApub;
        this.RSApri = RSApri;
        return true;
    }

    public void login() throws Exception {
        try (var msg = new MessageWriter(CMD.LOGIN)) {
            final var writer = msg.writer();
            writer.writeUTF(name);
            writer.writeUTF(RSApub);
            this.session.putMessage(msg);
        }
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
