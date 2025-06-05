/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.network;

import com.atbm.logic.ControllerMessage;
import com.atbm.manager.SessionManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public final class Session {

    @Getter
    private String name, RSApub = null;

    private final Socket socket;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    private final LinkedBlockingQueue<MessageWriter> messages;

    @Getter
    private final long lastTimeCreat = System.currentTimeMillis();

    private Thread threadSendMessage = null;

    public Session(Socket sc) throws Exception {
        socket = sc;
        socket.setTcpNoDelay(true);
        dos = new DataOutputStream(new BufferedOutputStream(sc.getOutputStream()));
        dis = new DataInputStream(new BufferedInputStream(sc.getInputStream()));
        messages = new LinkedBlockingQueue<>();
    }

    public void login(String name, String RSApub) {
        this.name = name;
        this.RSApub = RSApub;
        SessionManager.login(this);
    }

    public void putMessage(MessageWriter msg) {
        if (isDispose || socket.isClosed()) {
            return;
        }

        if (msg == null) {
            return;
        }

        try {
            messages.put(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReadMessage() {
        Thread.startVirtualThread(() -> {
            while (isDispose == false) {
                try (var msg = this.readMessage();) {
                    if (name == null) {
                        ControllerMessage.onMessage(msg, this);
                        if (name != null) {
                            this.startSendMessage();
                        }
                    } else {
                        ControllerMessage.onMessage(msg, this);
                    }
                } catch (SocketException e) {
                    // client bị crash
                    System.out.println("CLIENT CRASH");
                    dispose();
                } catch (IOException e) {
                    // client out game
                    System.out.println("CLIENT OUT");
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    dispose();
                }
            }
            System.out.println("SOCKET END READ MESSAGE");
        });

    }

    private void startSendMessage() {
        threadSendMessage = Thread.startVirtualThread(() -> {
            while (isDispose == false) {
                try (var message = Session.this.messages.take();) {
                    sendMessage(message);
                } catch (InterruptedException e) {
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    dispose();
                }
            }
            System.out.println("SOCKET END SEND MESSAGE");
        });
    }

    MessageReader readMessage() throws Exception {

        final var cmd = dis.readByte();
        var size = dis.readInt();

        final byte[] data = new byte[size];

        int i = 0;
        while (size-- > 0) {
            data[i++] = dis.readByte();
        }

        return new MessageReader(cmd, data);
    }

    private void sendMessage(MessageWriter msg) throws Exception {

        final var datas = msg.getData();

        dos.writeByte(msg.getCmd());
        dos.writeInt(datas.length);

        for (byte data : datas) {
            dos.writeByte(data);
        }

        dos.flush();
    }

    @Getter
    private boolean isDispose = false;

    private synchronized void dispose() {
        if (isDispose) {
            return;
        }
        isDispose = true;
        SessionManager.disconnect(this);

        if (threadSendMessage != null) {
            threadSendMessage.interrupt();
        }
        threadSendMessage = null;

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ex) {
                // Đúng logic là đéo có lỗi cặc gì cả =))
            }
        }

        if (dis != null) {
            try {
                dis.close();
            } catch (IOException ex) {
            }
        }

        if (dos != null) {
            try {
                dos.close();
            } catch (IOException ex) {
            }
        }

        if (messages != null) {
            messages.clear();
        }
    }

}
