/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

/**
 * An http post request.
 */
public class HttpPost extends HttpBase {

    /**
     * Initialize and set the request to be a POST.
     * @param url The url.
     * @throws Exception
     */
    public HttpPost(String url) throws Exception {
        super(url);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
    }
}
