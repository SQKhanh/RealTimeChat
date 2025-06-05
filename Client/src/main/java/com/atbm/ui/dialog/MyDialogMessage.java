/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.ui.dialog;

import com.atbm.ui.MainFrame;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MyDialogMessage extends JDialog {

    public static final MyDialogMessage Instance = new MyDialogMessage(MainFrame.Instance, "");

    private JLabel label;

    public MyDialogMessage(Frame parent, String message) {
        super(parent, "Thông báo", true); // true = modal

        label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        getContentPane().add(label);

        // 🧱 Không cho resize
        setResizable(false);

        // 🚫 Không cho đóng dialog bằng nút X
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // 🚫 Cản luôn sự kiện tắt cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Không làm gì hết
            }
        });

        // 🧭 Center dialog trên parent
        setSize(300, 150);
        setLocationRelativeTo(parent);
    }

    // Cho phép cập nhật nội dung sau này nếu cần
    public void setMessage(String message) {
        label.setText("<html>" + message + "</html>"); // Cho phép xuống dòng nếu dài
        pack(); // Tự resize dialog theo content mới
        setLocationRelativeTo(getParent()); // Căn lại giữa màn hình
    }

    public void setVisibleTRUE() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public void setVisibleTRUE(int second) {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(second * 1000);
            } catch (Exception e) {
            }
            setVisibleFALSE();
        });
    }

    public void setVisibleFALSE() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
        });
    }
}
