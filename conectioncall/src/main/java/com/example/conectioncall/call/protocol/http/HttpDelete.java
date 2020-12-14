/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

/**
 * An http delete request.
 */
public class HttpDelete extends HttpBase {

    /**
     * Initialize and set the request to be a DELETE.
     * @param url The url.
     * @throws Exception
     */
    public HttpDelete(String url) throws Exception {
        super(url);
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod("DELETE");
    }
}
