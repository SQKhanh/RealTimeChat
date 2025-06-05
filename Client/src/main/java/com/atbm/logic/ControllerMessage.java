/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.logic;

import com.atbm.network.CMD;
import com.atbm.network.Session;
import com.atbm.network.MessageReader;
import com.atbm.ui.MainFrame;
import com.atbm.ui.dialog.MyDialogMessage;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
            switch (cmd) {
                case CMD.LOGIN -> {
                    final var isOK = msg.readBoolean();
                    if (isOK == false) {
                        final var notify = msg.readUTF();
                        MyDialogMessage.Instance.setVisibleTRUE(3);
                        MyDialogMessage.Instance.setMessage(notify);
                    } else {
                        MyDialogMessage.Instance.setVisibleFALSE();

                        JOptionPane.showConfirmDialog(
                                MainFrame.Instance,
                                "Đăng nhập thành công",
                                "Thông báo",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        final var mems = new String[msg.readInt()];
                        for (int i = 0; i < mems.length; i++) {
                            mems[i] = msg.readUTF();
                        }

                        SwingUtilities.invokeLater(() -> {
                            MainFrame.Instance.getChatPanel1().updateMemOnline(mems);

                            MainFrame.Instance.showPanelChat();
                        });
                    }
                }
                case CMD.UPDATE_MEM_ONLINE -> {
                    final var isOnline = msg.readBoolean();
                    final var name = msg.readUTF();
                    MainFrame.Instance.getChatPanel1().updateMemOnline(name, isOnline);
                    System.out.println("reload mem online");
                }
                case CMD.DIALOG_OK -> {
                    final var notify = msg.readUTF();
                    JOptionPane.showConfirmDialog(
                            MainFrame.Instance,
                            notify,
                            "Thông báo máy chủ",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                case CMD.SEND_CHAT_MESSAGE -> {
                    MainFrame.Instance.getChatPanel1().sendChatFalse();
                }
                case CMD.RECEIVE_CHAT_MESSAGE -> {
                    final var sender = msg.readUTF();
                    final var text = msg.readUTF();

                    System.out.println("nhận tin nhắn từ %s, nội dung: %s".formatted(sender, text));

                    MainFrame.Instance.getChatPanel1().sendMessageToPanel(sender, text);

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
