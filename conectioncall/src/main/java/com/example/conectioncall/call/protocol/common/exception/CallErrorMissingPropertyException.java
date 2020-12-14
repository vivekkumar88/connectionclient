/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.exception;

/**
 * Exception to indicate that a call response is missing a property.
 */
public class CallErrorMissingPropertyException extends Exception {

    /**
     * The property that was missing.
     */
    private String mProperty;

    /**
     * Construct a missing property exception with the given property name that is missing.
     * @param property The property that was missing.
     */
    public CallErrorMissingPropertyException(String property) {
        mProperty = property;
    }

    /**
     * Get the property that was missing.
     * @return The missing property.
     */
    public String property() { return mProperty; }
}
