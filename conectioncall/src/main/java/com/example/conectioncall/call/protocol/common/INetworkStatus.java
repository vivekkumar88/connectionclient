/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

/**
 * Interface to get network status.
 */
public interface INetworkStatus {

    /**
     * Check if the network is currently connected.
     * @return True if network is connected, false otherwise.
     */
    boolean isConnectedToNetwork();
}
