/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.store;

public interface ISecureStore {

    public boolean set(String key, String value);

    public String get(String key);

    public void clear(String key);

    public boolean has(String key);
}
