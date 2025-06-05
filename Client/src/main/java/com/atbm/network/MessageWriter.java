/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.atbm.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import lombok.Getter;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 */
public final class MessageWriter implements AutoCloseable {

    @Getter
    private final byte cmd;
    private final DataOutputStream dos;
    private final ByteArrayOutputStream baot;

    public MessageWriter(byte cmd) {
        this.cmd = cmd;
        this.baot = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(baot);
    }

    public DataOutputStream writer() {
        return dos;
    }

    public byte[] getData() {
        return baot.toByteArray();
    }

    @Override
    public void close() {
        try {
            dos.close();
        } catch (Exception e) {
        }
        try {
            baot.close();
        } catch (Exception e) {
        }
    }

}
