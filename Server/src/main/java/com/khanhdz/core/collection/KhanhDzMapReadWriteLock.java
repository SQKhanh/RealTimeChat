/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.khanhdz.core.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;

/**
 *
 * @author <a href="https://www.facebook.com/khanhdepzai.pro/">KhanhDzai</a>
 * @param <K>
 * @param <V>
 */
public class KhanhDzMapReadWriteLock<K, V>   {

    // Danh sách các đối tượng được bảo vệ bởi ReadWriteLock
    private Map<K, V> objects = new HashMap<>();

    // ReadWriteLock để đồng bộ hóa truy cập
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean isEmpty() {
        try {
            lock.readLock().lock();
            return objects.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    // Lấy kích thước của danh sách
    public int size() {
        try {
            lock.readLock().lock();
            return objects.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public V get(K k) {
        try {
            lock.readLock().lock();
            return objects.get(k);
        } finally {
            lock.readLock().unlock();
        }
    }

    public V getOrDefault(K k, V defaultValue) {
        try {
            lock.readLock().lock();
            return objects.getOrDefault(k, defaultValue);
        } finally {
            lock.readLock().unlock();
        }
    }

    public V put(K k, V v) {
        try {
            debug("put ??? " + k.getClass().getSimpleName() + " - " + v.getClass().getSimpleName());

            lock.writeLock().lock();
            return this.objects.put(k, v);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean remove(K k, V v) {
        try {
            debug("REMOVE ??? " + k.getClass().getSimpleName() + " - " + v.getClass().getSimpleName());

            lock.writeLock().lock();
            return this.objects.remove(k, v);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V remove(K k) {
        try {
            debug("REMOVE ??? " + k.getClass().getSimpleName());
            lock.writeLock().lock();
            return this.objects.remove(k);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<K, V> startRead() {
        lock.readLock().lock();
        return objects;
    }

    public void doneRead() {
        lock.readLock().unlock();
    }

    public void clear() {
        try {
            lock.writeLock().lock();
            this.objects.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void debug(String text) {
//        switch (this) {
//            case MapOffline map:
//                Logger.DebugLogic("MAP: " + text, 2);
//                break;
//            case MapDhvt map:
//                Logger.DebugLogic("MAP: " + text, 2);
//                break;
//            default:
//                break;
//        }

    }

    // =====================
    public void setNewData(Map<K, V> listObject) {
        try {
            lock.writeLock().lock();
            this.objects.clear();
            this.objects = null;
            this.objects = listObject;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V fintObjectByLogic(BiPredicate<K, V> logicFind) {
        try {
            var entrySet = this.startRead().entrySet();
            for (var entry : entrySet) {
                var k = entry.getKey();
                var v = entry.getValue();
                if (logicFind.test(k, v)) {
                    return v;
                }
            }
        } finally {
            this.doneRead();
        }
        return null;
    }

}