/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic;

import com.atbm.manager.SessionManager;
import com.atbm.network.CMD;
import com.atbm.network.Session;
import com.atbm.network.MessageReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerMessage {

    public static void onMessage(MessageReader msg, Session session) {
        try {
            final var cmd = msg.getCmd();
            System.out.println("receive cmd> > " + cmd);
            switch (cmd) {
                case CMD.LOGIN -> {

                    final var name = msg.readUTF();
                    final var pubKeyRSA = msg.readUTF();

                    if (SessionManager.isAlreadyLogin(name)) {
                        ServerRespondManager.Instance.respondLoginFalse(session, "Đã có người sử dụng tên này rồi");
                        return;
                    }

                    session.login(name, pubKeyRSA);
                    ServerRespondManager.Instance.respondLoginOK(session);

                    System.out.println("done handle request login");
                }
                case CMD.SEND_CHAT_MESSAGE -> {

                    final var sendTo = msg.readUTF();
                    final var text = msg.readUTF();
                    final var keyAES = msg.readUTF();

                    if (SessionManager.getSession(sendTo) instanceof Session receiver) {
                        ServerRespondManager.Instance.respondReceiveChatMessage(session.getName(), receiver, text,keyAES);
                    } else {
                        ServerRespondManager.Instance.respondSendChatMessageFALSE(session);
                    }
                }
                default -> {
                    System.out.println("unknow cmd: " + cmd);
                }
            }
        } catch (Exception e) {
            System.out.println("error handle message");
            e.printStackTrace();
        }
    }
}
