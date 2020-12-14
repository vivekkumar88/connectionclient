/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

// http://stackoverflow.com/questions/19723415/java-overriding-function-to-disable-ssl-certificate-check

/**
 * DO NOT USE IN PRODUCTION!!!!
 *
 * This class will simply trust everything that comes along.
 *
 * @author frank
 *
 */
public class TrustAllX509TrustManager implements X509TrustManager {
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                                   String authType) {
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                   String authType) {
    }

}