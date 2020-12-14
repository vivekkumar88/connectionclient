/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

/**
 * Encapsulates the information for an http response.
 */
public class HttpResponse {

    /** The http response code. */
    private int mResponseCode;

    /** The payload mBody. */
    private String mBody;

    /**
     * Initialize the response.
     */
    public HttpResponse() {
    }

    /**
     * Set the response code for this response.
     * @param code The response code.
     */
    public void setResponseCode(int code) {
        this.mResponseCode = code;
    }

    /**
     * Set the response body.
     * @param body The response body.
     */
    public void setBody(String body) {
        this.mBody = body;
    }

    /**
     * Get the response body.
     * @return the respnse body.
     */
    public String body() {
        return this.mBody;
    }

    /**
     * Get the response code.
     * @return The response code.
     */
    public int responseCode() { return mResponseCode; }
}
