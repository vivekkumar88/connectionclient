/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

import com.example.conectioncall.call.protocol.common.store.ISecureStore;
import com.example.conectioncall.call.protocol.common.store.IValueStore;

/**
 * Platform resource delegate interface. This provides the library with platform specific resources
 * that it requires to perform some operations.
 */
public interface IPlatformResourceDelegate {

    /**
     * Get a platform specific implementation for the value store interface.
     * @return An instance of the IValueStore interface.
     */
    IValueStore getValueStore();

    /**
     * Get a platform specific implementation for the secure store interface.
     * @return An instance of the ISecureStore interface
     */
    ISecureStore getSecureStore();

    /**
     * Get a platform specific implementation for the network status interface.
     * @return An instance of the INetworkStatus interface.
     */
    INetworkStatus getNetworkStatus();
}
