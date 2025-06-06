/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm;

import com.atbm.ui.MainFrame;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    static {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Bắt buộc chạy UI trên thread riêng của Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame.Instance.setVisible(true);
        });
    }
}
