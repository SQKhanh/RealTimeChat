/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.data;

import com.atbm.logic.security.RSAUtil;
import com.atbm.model.MemChat;
import com.atbm.ui.panel.MessageBubblePanel;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataChat {

    private final static Map<String, MemChat> dataChat = new HashMap<>();

    public static MemChat getMem(String name) {
        return dataChat.getOrDefault(name, null);
    }

    public static void updateMemOnline(String name, String keyBase64, boolean online) {
        if (online) {
            PublicKey pubKey;
            try {
                pubKey = RSAUtil.base64ToPublicKey(keyBase64);
            } catch (Exception e) {
                pubKey = null;
            }
            if (pubKey == null) {
                dataChat.remove(name);
                return;
            }

            if (dataChat.getOrDefault(name, null) instanceof MemChat mem) {
                mem.changepubKeyRSA(pubKey);
            } else {
                dataChat.put(name, new MemChat(name, pubKey));
            }
        } else {
            dataChat.remove(name);
        }
    }

    public static void saveMessage(MemChat mem, MessageBubblePanel message) {
        mem.getMessages().add(message);
    }

    public static void load(List<MemChat> memChats) {
        dataChat.clear();
        for (MemChat memChat : memChats) {
            dataChat.put(memChat.getName(), memChat);
        }
    }

}
