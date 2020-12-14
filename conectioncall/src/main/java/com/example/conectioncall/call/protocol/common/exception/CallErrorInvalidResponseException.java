/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.exception;


/**
 * Exception to indicate an invalid response from a call.
 */
public class CallErrorInvalidResponseException extends  Exception {

    /**
     * The error label.
     */
    private String mLabel;

    /**
     * A brief description of the error caused by the call response.
     */
    private String mDescription;

    /**
     * Construct an invalid response exception with the given label and description.
     *
     * @param label The label.
     * @param description The description.
     */
    public CallErrorInvalidResponseException(String label, String description) {
        mLabel = label;
        mDescription = description;
    }

    /**
     * Get the error label.
     * @return The label.
     */
    public String label() { return mLabel; }

    /**
     * Get the error description.
     * @return The description.
     */
    public String description() { return mDescription; }
}
