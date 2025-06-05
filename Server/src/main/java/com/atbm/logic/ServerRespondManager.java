/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic;

import com.atbm.manager.SessionManager;
import com.atbm.network.CMD;
import com.atbm.network.MessageWriter;
import com.atbm.network.Session;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerRespondManager {

    public static final ServerRespondManager Instance = new ServerRespondManager();

    public void respondDialogOK(Session session, String notify) throws Exception {
        try (var msg = new MessageWriter(CMD.DIALOG_OK)) {
            final var writer = msg.writer();
            writer.writeUTF(notify);
            session.putMessage(msg);
        }
    }

    public void respondReceiveChatMessage(String sender, Session receiver, String text) throws Exception {
        try (var msg = new MessageWriter(CMD.RECEIVE_CHAT_MESSAGE)) {
            final var writer = msg.writer();
            writer.writeUTF(sender);
            writer.writeUTF(text);
            receiver.putMessage(msg);
        }
    }
    
    public void respondSendChatMessageFALSE(Session session) throws Exception {
        try (var msg = new MessageWriter(CMD.SEND_CHAT_MESSAGE)) {
            session.putMessage(msg);
        }
    }

    public void respondLoginFalse(Session session, String notify) throws Exception {
        try (var msg = new MessageWriter(CMD.LOGIN)) {
            final var writer = msg.writer();
            writer.writeBoolean(false);
            writer.writeUTF(notify);
            session.putMessage(msg);
        }
    }

    public void respondLoginOK(Session session) throws Exception {
        try (var msg = new MessageWriter(CMD.LOGIN)) {
            final var writer = msg.writer();
            writer.writeBoolean(true);

            var mems = SessionManager.getAllNameOnline();

            writer.writeInt(mems.length);
            for (var mem : mems) {
                writer.writeUTF(mem);
            }

            session.putMessage(msg);
        }
    }
}
