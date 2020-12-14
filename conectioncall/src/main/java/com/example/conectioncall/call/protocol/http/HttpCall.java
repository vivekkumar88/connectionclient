/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.http;

import com.example.conectioncall.call.ICall;
import com.example.conectioncall.call.protocol.HttpProtocol;
import com.example.conectioncall.call.protocol.common.Service;
import com.example.conectioncall.call.protocol.common.exception.CallErrorInvalidFormatException;
import com.example.conectioncall.call.protocol.common.exception.CallErrorInvalidResponseException;
import com.example.conectioncall.call.protocol.common.exception.CallErrorMissingPropertyException;
import com.example.conectioncall.call.protocol.common.exception.Error;
import com.example.conectioncall.call.protocol.common.extras.Utility;
import com.example.conectioncall.call.protocol.common.jsonprocessor.ErrorJsonProcessor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Http specific connectionclient service call.
 */
public abstract class HttpCall implements ICall {

    /**
     * Http method to use.
     */
    protected enum Method {
        Post,
        Put,
        Get,
        Delete
    }

    /**
     * Helper methods to create a request.
     *
     * @param method The method for the http request to use.
     * @param params Optional parameters.
     * @param token Optional token to use in authentication.
     * @param payload Optional payload to put in body.
     * @return The HttpBase request.
     * @throws Exception
     */
    protected HttpBase createRequest(Method method, Map<String, String> params, String token, JSONObject payload) throws Exception {
        String url = getUrl();
        if (params != null) {
            for (String p : params.keySet()) {
                String value = params.get(p);
                url = url.replace(p, value);
            }
        }

        Utility.log("http url = " + url);

        HttpBase request = null;

        switch (method) {
            case Delete: request = new HttpDelete(url); break;
            case Post: request = new HttpPost(url); break;
            case Put: request = new HttpPut(url); break;
            case Get: request = new HttpGet(url); break;
        }

        Utility.log("http method = " + request.getClass().getSimpleName());

        if (payload != null) {
            request.setBody(payload.toString());
            Utility.log("http payload = " + payload.toString());
        }

        setSharedHeaders(request, token);

        return request;
    }

    /**
     * @see ICall#getProtocolName()
     */
    @Override
    public String getProtocolName() {
        return "http";
    }

    /**
     * Get the url for this http call. This uses the currently set service environment properties.
     *
     * @return The url string for this call.
     */
    protected String getUrl() throws Exception {

        Boolean secure = true;
        ArrayList<String> keys = new ArrayList<>();
        keys.add("secure");
        HashMap<String, String> httpProtocolProps = Service.sharedInstance().getEnvironmentProperty(this.getProtocolName(), "http_protocol", keys);
        if (httpProtocolProps != null && httpProtocolProps.containsKey("secure")) {
            String secureProp = httpProtocolProps.get("secure");
            if (secureProp.equals("true")) {
                secure = true;
            } else if (secureProp.equals("false")) {
                secure = false;
            }
        }

        HashMap<String, String> property = Service.sharedInstance().getEnvironmentProperty(this.getProtocolName(), this.getCallName().toLowerCase());
        if (property.size() > 0) {
            String host = property.containsKey("host") ? property.get("host") : "";
            String port = property.containsKey("port") ? ":" + property.get("port") : "";
            String path = property.containsKey("path") ? property.get("path") : "";

            // Allow override global secure property on a call by call basis.
            if (property.containsKey("secure")) {
                String secureString = property.get("secure");
                if (secureString.equals("true")) {
                    secure = true;
                } else if (secureString.equals("false")) {
                    secure = false;
                } else {
                    Utility.log("Invalid secure property value.");
                }
            }

            return (secure ? "https://" : "http://") + host + port + path;
        }

        throw new Exception("Check environment properties for call '" + this.getCallName().toLowerCase() + "' has valid values.");
    }

    /**
     * Get the http specific request object to make the service call.
     *
     * @return An HttpBase inherited object that can perform the service call.
     */
    public abstract HttpBase getRequest() throws Exception;

    /**
     * Process the response and return a key/value dictionary of the response.
     *
     * @param httpResponse The response to process.
     * @return The processed response normalized into a key/value pair.
     */
    public HashMap<String,Object> processResponse(HttpResponse httpResponse) {
        HashMap<String,Object> response = new HashMap<>();

        try {
            String body = httpResponse.body();
            // Make sure we have a valid body to parse before even trying to serialize it as JSON.
            if (body != null && body.length() > 0) {

                JSONObject jsonObj = new JSONObject();
                try {
                    JSONObject json = new JSONObject(httpResponse.body());
                    jsonObj = json;
                } catch (Exception ex) {
                    JSONArray jsonArray = new JSONArray(httpResponse.body());
                    jsonObj.put("result", jsonArray);
                }

                Utility.log("http json = '" + jsonObj.toString() + "'");

                Object error = new ErrorJsonProcessor().processJson(jsonObj);
                if (error instanceof Error) {
                    ((Error) error).setHttpResponseCode(httpResponse.responseCode());
                    response.put(Error.Key, error);
                } else {
                    response = processJson(jsonObj);
                }
            }

            // If this is an error response code and the json processor did not pull out an error
            // message, add a default error message in the response.
            if (!response.containsKey(Error.Key) && (httpResponse.responseCode() < 200 || httpResponse.responseCode() >= 300)) {
                Error error = new Error("http_response_failure", "response code " + httpResponse.responseCode(), String.valueOf(httpResponse.responseCode()));
                error.setHttpResponseCode(httpResponse.responseCode());
                response.put(Error.Key, error);
            }
        } catch (CallErrorInvalidResponseException ex) {
            ex.printStackTrace();
            Utility.logException(ex);
            Error error = new Error(ex.label(), ex.description());
            error.setHttpResponseCode(httpResponse.responseCode());
            response.put(Error.Key, error);
        } catch (CallErrorMissingPropertyException ex) {
            ex.printStackTrace();
            Utility.logException(ex);
            Error error = new Error("invalid_response", "Missing '" + ex.property() + "'");

            error.setHttpResponseCode(httpResponse.responseCode());
            response.put(Error.Key, error);
        } catch (CallErrorInvalidFormatException ex) {
            ex.printStackTrace();
            Utility.logException(ex);
            Error error = new Error("invalid_response_format", "Invalid response format");
            error.setHttpResponseCode(httpResponse.responseCode());
            response.put(Error.Key, error);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
            String label = HttpProtocol.Errors.ProcessingResponseFailed.toString();
            String desc = "An error occurred during response processing: " + ex.getMessage();
            Error error = new Error(label, desc);
            error.setHttpResponseCode(httpResponse.responseCode());
            response.put(Error.Key, error);
        }

        return response;
    }

    /**
     * Process a JSON.
     *
     * @param json The json object to process.
     * @return The post processed json ready for application consumption.
     * @throws Exception
     */
    public abstract HashMap<String,Object> processJson(JSONObject json) throws Exception;


    /**
     * Set shared header information on given HttpBase instance.
     *
     * @param base The HttpBase to set shared headers for.
     */
    protected void setSharedHeaders(HttpBase base, String token) {
        final String apiKey = Service.yy();
        final String secret = Service.zz();

        base.setHeader("Content-Type", "application/json; charset=UTF-8");
        base.setHeader("User-Agent", "Mobile");
        base.setHeader("X-Client-Id", "mobile");
        base.setHeader("ampApiKey", apiKey);

        if (token != null && token.length() > 0) {
            String authorization = "Bearer " + token;
            Utility.log("authorization = '" + authorization + "'");
            base.setHeader("Authorization", authorization);
        } else {
            String headerAuthValue = Service.sharedInstance().getClientId() + ":" + secret;
            byte[] headerData = headerAuthValue.getBytes();
            String authorization = "Basic " + Utility.base64encode(headerData);
            Utility.log("authorization = '" + authorization + "'");
            base.setHeader("Authorization", authorization);
        }
    }

    /**
     * Default Http validation. Currently no checks. Will always return nil.
     * @return Null. No validation checks done.
     */
    public Error validate() {
        return null;
    }

    /**
     * Get the call name.
     *
     * @return The call name.
     */
    public abstract String getCallName();
}
