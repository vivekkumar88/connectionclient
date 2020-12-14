/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

/**
 * Default implementation of the network status interface. This implementation are just dummy
 * implementations.
 */
public class DefaultNetworkStatus implements INetworkStatus {

    /**
     * @see INetworkStatus#isConnectedToNetwork()
     */
    @Override
    public boolean isConnectedToNetwork() {
        return false;
    }
}
