/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import lombok.Getter;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public final class MessageReader extends DataInputStream implements AutoCloseable {

  
    @Getter
    private final byte cmd;

    public MessageReader(byte cmd, byte[] data) {
        super(new ByteArrayInputStream(data));
        this.cmd = cmd;
    }

}
