/*
 Created by V K on 12/13/20.
 */


package com.example.conectioncall.call.protocol.common.jsonprocessor;

import org.json.JSONObject;

/**
 * Interface for processing portion of a json object.
 */
public interface IJsonProcessor {

    /**
     * Process a json to an application level object.
     *
     * @param json The json to parse.
     * @return The application level object.
     */
    Object processJson(JSONObject json) throws Exception;
}
