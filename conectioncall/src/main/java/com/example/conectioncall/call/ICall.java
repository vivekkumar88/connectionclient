/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call;

import com.example.conectioncall.call.protocol.common.exception.Error;

/**
 * Interface for all service calls.
 */
public interface ICall {

    /**
     * Get the protocol that this response will use.
     * @return The string identifier for the protocol.
     */
    String getProtocolName();

    /**
     * Call specific validation to be performed.
     * @return A valid error instance if an error occurred, or nil otherwise.
     */
    Error validate();
}
