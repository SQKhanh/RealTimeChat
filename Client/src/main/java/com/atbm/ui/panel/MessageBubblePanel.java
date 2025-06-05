/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public class MessageBubblePanel extends JPanel {

    public MessageBubblePanel(String message, boolean isSelf) {
          setLayout(new BorderLayout());
        setOpaque(false);

        // Tạo JTextArea để hiển thị nội dung tin nhắn
        JTextArea messageArea = new JTextArea(message);
        messageArea.setLineWrap(true); // tự động xuống dòng
        messageArea.setWrapStyleWord(true); // không cắt chữ giữa từ
        messageArea.setEditable(false); // không cho sửa
        messageArea.setFocusable(false);
        messageArea.setOpaque(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thiết lập màu nền và căn chỉnh
        if (isSelf) {
            messageArea.setBackground(new Color(173, 216, 230)); // xanh nhạt
            messageArea.setForeground(Color.BLACK);
        } else {
            messageArea.setBackground(new Color(230, 230, 230)); // xám nhạt
            messageArea.setForeground(Color.BLACK);
        }

        // Panel wrapper để căn trái/phải
        JPanel wrapper = new JPanel(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.add(messageArea);

        // Giới hạn kích thước bubble để nó không chiếm hết width
        messageArea.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        wrapper.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        add(wrapper, BorderLayout.CENTER);
    }
}
