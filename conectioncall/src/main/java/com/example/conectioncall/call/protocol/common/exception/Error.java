/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.exception;

/**
 * Standard error return for service delegates response.
 */
public class Error extends Exception {

    /**
     * The key used to place the error in the response map returned.
     */
    public static final String Key = "atsp_service_error";

    /**
     * The unique error code.
     */
    private String errorCode;
    /**
     * The unique error label.
     */
    private String label;

    /**
     * Description of error.
     */
    private String description;

    /**
     * Hold http response code return from backend
     */
    private int httpResponseCode = 0;

    private Object data;

    /**
     * Create a service response delegate error.
     *
     * @param label       The error label.
     * @param description The error description.
     */
    public Error(String label, String description) {
        super(description);
        this.label = label;
        this.description = description;
        this.errorCode = "";
    }

    /**
     * Create a service response delegate error.
     *
     * @param label       The error label.
     * @param description The error description.
     * @param errorCode   The Error code.
     */
    public Error(String label, String description, String errorCode) {
        super(description);
        this.label = label;
        this.description = description;
        this.errorCode = errorCode;
    }

    /**
     * Get the error code.
     *
     * @return
     */
    public String errorCode() {
        return errorCode;
    }

    /**
     * Get the error label.
     *
     * @return The error label.
     */
    public String label() {
        return label;
    }

    /**
     * Get the error description.
     *
     * @return The error description.
     */
    public String description() {
        return description;
    }

    /**
     * Get HTTP Response code
     *
     * @return
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * Set HTTP Response code
     *
     * @param httpResponseCode
     */
    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    /**
     * Set data object, if api return error with some data information
     * @return
     */
    public Object getData() {
        return data;
    }

    /**
     * Get data object, if api return error with some data information
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }
}
