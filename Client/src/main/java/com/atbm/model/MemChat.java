/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.model;

import com.atbm.logic.security.RSAUtil;
import com.atbm.ui.panel.MessageBubblePanel;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
@Getter
public final class MemChat {

    private final String name;
    private PublicKey pubKeyRSA;
    private final List<MessageBubblePanel> messages = new ArrayList<>();

    public MemChat(String name, PublicKey pubKeyRSA) {
        this.name = name;
        this.changepubKeyRSA(pubKeyRSA);
    }

    public void changepubKeyRSA(PublicKey pubKeyRSA) {
        this.pubKeyRSA = pubKeyRSA;
        this.messages.clear();
    }
}
