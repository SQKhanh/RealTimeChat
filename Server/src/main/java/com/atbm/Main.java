/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm;

import com.atbm.network.Session;
import java.net.ServerSocket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    static final int port = 15555;

    static final ServerSocket serverSocket;

    static {
        ServerSocket sv;
        try {
            sv = new ServerSocket(port);
        } catch (Exception e) {
            sv = null;
            e.printStackTrace();
            System.exit(0);
        }
        serverSocket = sv;
    }

    public static void main(String[] args) {
        System.out.println("start server on port " + port);
        while (true) {
            try {
                final var sc = serverSocket.accept();

                System.out.println("Accept an socket connection from: " + sc.getInetAddress().getHostAddress());

                final var session = new Session(sc);

                session.startReadMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
