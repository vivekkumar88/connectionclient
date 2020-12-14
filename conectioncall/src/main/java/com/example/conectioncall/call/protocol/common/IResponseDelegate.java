/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

import java.util.HashMap;

/**
 * A response delegate.
 */
public interface IResponseDelegate {

    /**
     * Provides a mechanism for a response handler to be created for connectionclient service calls.
     * @param response A key, object much containing response data.
     */
    void handleResponse(final HashMap<String,Object> response);
}
