/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

import com.datatheorem.android.trustkit.TrustKit;
import com.example.conectioncall.call.protocol.common.extras.Utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Encapsulates an NSURLRequest logic for sending and receiving an http request.
 */
public class HttpBase {

    /** The url to send to. */
    protected URL url;

    /** The connection to use for the http request. */
    protected HttpsURLConnection urlConnection;

    /** The connection to use for the http request. */
    protected String serverHostname;

    /** The payload. */
    private String body;

    /**
     * Instantiate the http request.
     * @param url The url.
     * @throws Exception
     */
    protected HttpBase(String url) throws Exception {
        this.url = new URL(url);
        this.serverHostname = this.url.getHost();
        this.urlConnection = (HttpsURLConnection)this.url.openConnection();
        //this.urlConnection.setSSLSocketFactory(TrustKit.getInstance().getSSLSocketFactory(serverHostname));

        // OkHttp 3.3.x and higher
        /*OkHttpClient client =
                new OkHttpClient().newBuilder()
                        .sslSocketFactory(TrustKit.getInstance().getSSLSocketFactory(serverHostname),
                                TrustKit.getInstance().getTrustManager(serverHostname))
                        .build();*/
    }

    /**
     * Set a header value for the http request.
     * @param name The header key.
     * @param value The value.
     */
    public void setHeader(String name, String value) {
        Utility.log("header '" + name + "' = '" + value + "'");
        this.urlConnection.setRequestProperty(name, value);
    }

    /**
     * Set payload.
     * @param body The payload.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Perform the actual Http request.
     * @return An instance of HttpResponse.
     * @throws Exception
     */
    public HttpResponse execute() throws Exception {
        HttpResponse response = new HttpResponse();

        // If the property has been set, initialize the required header values before the
        // the connection is made.
        byte[] bytes = null;
        if (this.body != null && this.body.length() > 0) {
            bytes = body.getBytes("UTF-8");
            urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
        }

        // Make actual connection. This blocks.
        urlConnection.connect();

        // Connection has been made, check if an initialized byte buffer has been initialized and
        // send it upstream.
        if (bytes != null && bytes.length > 0) {
            OutputStream os = urlConnection.getOutputStream();
            os.write(bytes);
            os.flush();
        }

        int responseCode = urlConnection.getResponseCode();
        Utility.log("responseCode = " + responseCode);
        response.setResponseCode(responseCode);

        InputStream is = null;

        try {
            // Check the return code so we know where to get the body.
            if (responseCode >= 200 && responseCode < 300) {
                is = urlConnection.getInputStream();
            } else {
                is = urlConnection.getErrorStream();
            }
        } catch (SSLPeerUnverifiedException ex) {
            Utility.log("SSLPeerUnverifiedException : Certificate not trusted");
            Utility.logException(ex);
            return response;
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) >= 0) {
            baos.write(buf, 0, len);
        }
        String body = new String(baos.toByteArray(), "UTF-8");

        response.setBody(body);

        return response;
    }
}
