/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

import java.util.HashMap;

/**
 * Provides interface methods that can be implemented that the connectionclient service will call on internal
 * state changes or pertinent events.
 */
public interface IServiceDelegate {

    /**
     * Called when a service event occurs.
     * @param event The event that occured.
     * @param info Any additional information.
     */
    void onEvent(Service.Event event, HashMap<String, Object> info);
}
