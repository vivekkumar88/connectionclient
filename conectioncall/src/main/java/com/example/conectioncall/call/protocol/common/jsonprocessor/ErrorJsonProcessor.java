/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.jsonprocessor;

import com.example.conectioncall.call.protocol.HttpProtocol;
import com.example.conectioncall.call.protocol.common.Service;
import com.example.conectioncall.call.protocol.common.exception.Error;

import org.json.JSONObject;

/**
 *  Default error processor. This will try to parse the json object for any indicatation of an error
 * and, upon finding it, create a proper AEError instance and return it. Currently it looks for
 * any property in the root of the JSON object with the name:
 *
 * - "error"
 * - "errorCode"
 * - "errorDescription"
 *
 * If any of these are present, it will assume an error has occured.
 */
public class ErrorJsonProcessor implements IJsonProcessor {

    /**
     * @see IJsonProcessor#processJson(JSONObject)
     */
    @Override
    public Object processJson(JSONObject json) throws Exception {

        if (json.has("error") ||
            json.has("errorCode") ||
            json.has("errorDescription")) {

            String label = json.optString("error", Service.Errors.UNKNOWN.toString());
            String code = json.optString("errorCode", Service.Errors.UNKNOWN.toString());
            String description = json.optString("errorDescription", "Unknown error");

            Error error = new Error(label, description, code);
            error.setData(json.opt("records"));

            return error;
        }

        return new Boolean(false);
    }
}
