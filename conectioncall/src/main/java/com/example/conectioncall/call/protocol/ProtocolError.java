/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol;
import com.example.conectioncall.call.protocol.common.exception.Error;

/**
 * Error protocol.
 */
public class ProtocolError extends Error {

    /**
     * Error protocol.
     * @param label The label.
     * @param description The description.
     */
    public ProtocolError(String label, String description) {
        super(label, description);
    }
}
