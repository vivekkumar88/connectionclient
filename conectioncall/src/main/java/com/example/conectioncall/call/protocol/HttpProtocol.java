/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol;

import com.example.conectioncall.call.ICall;
import com.example.conectioncall.call.protocol.common.IResponseDelegate;
import com.example.conectioncall.call.protocol.common.Service;
import com.example.conectioncall.call.protocol.common.exception.Error;
import com.example.conectioncall.call.protocol.common.extras.Utility;
import com.example.conectioncall.call.protocol.http.HttpBase;
import com.example.conectioncall.call.protocol.http.HttpCall;
import com.example.conectioncall.call.protocol.http.HttpResponse;
import com.example.conectioncall.call.protocol.http.TrustAllX509TrustManager;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

/**
 * Http implemenetation of the IProtocol interface. This allows HttpCall variants to be sent.
 */
public class HttpProtocol implements IProtocol {

    public static final String ProtocolName = "http";

    public enum Errors {
        SendFailed { public String toString() { return "http_protocol_send_failed"; }},
        ProcessingResponseFailed { public String toString() { return "http_protocol_processing_failed"; }},
        TOKEN_EXPIRED{ public String toString() { return "http_protocol_token_expired"; }}
    }

    public static class RequestFailedException extends Error {
        public RequestFailedException() {
            super("http_protocol_request_failed", "Request failed.");
        }
    }

    public static class ResponseFailedException extends Error {
        public ResponseFailedException() {
            super("http_protocol_response_failed", "Response failed.");
        }
    }

    public static class UnknownException extends Error {
        public UnknownException() {
            super("http_protocol_unknown", "Unknown http error occurred.");
        }
    }

    /** The protocol delegate. */
    private IProtocolDelegate protocolDelegate;

    /**
     * Create an HttpProtocol instance.
     */
    public HttpProtocol() {
        // This is to bypass SSL checks. This is only for development. Production should not enable
        // this as no SSL security checks would be done.
        if (!Service.isSSLEnabled) {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String string, SSLSession ssls) {
                        return true;
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Utility.logException(ex);
            }
        }
    }

    /**
     * Set the protocol delegate.
     * @param protocolDelegate The protocol delegate.
     */
    @Override
    public void setDelegate(IProtocolDelegate protocolDelegate) {
        this.protocolDelegate = protocolDelegate;
    }

    /**
     * @see IProtocol#send(ICall)
     */
    @Override
    public void send(ICall call) throws Exception {
        send(call, null);
    }

    /**
     * @see IProtocol#send(ICall, IResponseDelegate)
     */
    @Override
    public void send(final ICall call, final IResponseDelegate delegate) throws Exception {
        final HttpCall rest = (HttpCall)call;

        // If developer mode and the devModeSend actually captured the call, exit out of the usual
        // send routine.
        if (Service.isDevMode() && devModeSend(rest, delegate)) {
            return;
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpBase httpRequest = rest.getRequest();
                    HttpResponse httpResponse = httpRequest.execute();

                    HashMap<String,Object> response = rest.processResponse(httpResponse);

                    // If the delegate is set, call it with the response object.
                    if (delegate != null) {
                        delegate.handleResponse(response);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Utility.logException(ex);
                    HashMap<String,Object> response = new HashMap<>();
                    response.put(Error.Key, new Error(Errors.SendFailed.toString(), ex.getMessage()));
                    if (delegate != null) {
                        delegate.handleResponse(response);
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * Developer mode send call.
     * @param rest
     * @param responseDelegate
     * @return
     * @throws Exception
     */
    private boolean devModeSend(final HttpCall rest, final IResponseDelegate responseDelegate) throws Exception {
        Utility.log("-- http dev mode --");
        String name = rest.getCallName().toLowerCase();
        String test = Utility.getFile(name);
        if (test != null) {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setResponseCode(200);
            httpResponse.setBody(test);
            final HashMap<String,Object> response = rest.processResponse(httpResponse);
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (responseDelegate != null) {
                        responseDelegate.handleResponse(response);
                    }
                }
            }, 2000, TimeUnit.DAYS.MILLISECONDS);
            return true;
        }
        return false;
    }
}
