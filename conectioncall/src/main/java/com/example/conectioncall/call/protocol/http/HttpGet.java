/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

/**
 * An http get request.
 */
public class HttpGet extends HttpBase {

    /**
     * Initialize and set the request to be a GET.
     * @param url The url.
     * @throws Exception
     */
    public HttpGet(String url) throws Exception {
        super(url);
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod("GET");
    }
}
