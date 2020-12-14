/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

/**
 * An http put request.
 */
public class HttpPut extends HttpBase {

    /**
     * Initialize and set the request to be a PUT.
     * @param url The url.
     * @throws Exception
     */
    public HttpPut(String url) throws Exception {
        super(url);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
    }
}
