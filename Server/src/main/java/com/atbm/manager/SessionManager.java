/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.manager;

import com.atbm.logic.ServerNotify;
import com.atbm.network.MessageWriter;
import com.atbm.network.Session;
import com.khanhdz.core.collection.KhanhDzMapReadWriteLock;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionManager {

    /**
     * K: session.name<br>
     * V: session<br>
     */
    private final static KhanhDzMapReadWriteLock<String, Session> sessions = new KhanhDzMapReadWriteLock<>();

    /**
     *
     * @return [[name,pubKeyRSA],..]
     */
    public static List<String[]> getAllNameOnline() {
        try {
            var collection = sessions.startRead().values();
            List<String[]> data = new ArrayList<>();
            for (var session : collection) {
                data.add(new String[]{session.getName(), session.getPubKeyRSA()});
            }

            return data;
        } finally {
            sessions.doneRead();
        }
    }

    public static void sendMessageAllSession(MessageWriter msg) {
        try {
            for (var session : sessions.startRead().values()) {
                session.putMessage(msg);
            }
        } finally {
            sessions.doneRead();
        }
    }

    public static boolean isAlreadyLogin(String name) {
        return getSession(name) instanceof Session;
    }

    public static Session getSession(String name) {
        return sessions.getOrDefault(name, null);
    }

    public static void login(Session session) {
        sessions.put(session.getName(), session);

        Thread.startVirtualThread(() -> {
            ServerNotify.updateMemOnline(session, true);
        });
    }

    public static void disconnect(Session session) {
        if (session.getName() == null) {
            return;
        }
        sessions.remove(session.getName());

        Thread.startVirtualThread(() -> {
            ServerNotify.updateMemOnline(session, false);
        });
    }

}
