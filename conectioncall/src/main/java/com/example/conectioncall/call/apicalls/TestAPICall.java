/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.apicalls;

import android.util.Log;

import com.example.conectioncall.call.protocol.http.HttpBase;
import com.example.conectioncall.call.protocol.http.HttpCall;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestAPICall extends HttpCall {

    public TestAPICall() { }

    @Override
    public HttpBase getRequest() throws Exception {
        Map<String, String> params = new HashMap<>();
        return createRequest(Method.Get, params, "", null);
    }

    @Override
    public HashMap<String, Object> processJson(JSONObject json) throws Exception {
        HashMap<String, Object> response = new HashMap<>();

        Log.d("TestAPICall", json.toString());
        // @todo - Standardize successful response.
        response.put("status", "SUCCESS");
        response.put("success", true);

        return response;
    }

    @Override
    public String getCallName() {
        return "TestAPICall";
    }
}
