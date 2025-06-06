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
public final class ServerNotify {

    public static void updateMemOnline(Session session, boolean isOnline) {
        try (var msg = new MessageWriter(CMD.UPDATE_MEM_ONLINE)) {
            final var writer = msg.writer();
            writer.writeBoolean(isOnline);
            writer.writeUTF(session.getName());
            writer.writeUTF(session.getPubKeyRSA());
            SessionManager.sendMessageAllSession(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
