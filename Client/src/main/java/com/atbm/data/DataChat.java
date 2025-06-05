/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.data;

import com.atbm.ui.panel.MessageBubblePanel;
import java.util.ArrayList;
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

    private final static Map<String, List<MessageBubblePanel>> dataChat = new HashMap<>();


    public static void saveMessage(String curChat, MessageBubblePanel message) {
        var messages = getData(curChat);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);

        dataChat.put(curChat, messages);
    }

    public static List<MessageBubblePanel> getData(String key) {
        return dataChat.getOrDefault(key, null);
    }

}
