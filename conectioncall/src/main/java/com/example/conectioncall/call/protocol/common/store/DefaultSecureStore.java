/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.store;

public class DefaultSecureStore implements ISecureStore {

    @Override
    public boolean set(String key, String value) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void clear(String key) {

    }

    @Override
    public boolean has(String key) {
        return false;
    }
}
