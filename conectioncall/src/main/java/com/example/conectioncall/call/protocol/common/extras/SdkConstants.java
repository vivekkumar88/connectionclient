/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.extras;

public class SdkConstants {

    /** Event info value for disconnection. */
    public static final String EVENT_INFO_DISCONNECTED = "disconnected";

    /** Event info value for a success. */
    public static final String EVENT_INFO_SUCCESS = "success";

    /** Event info value for a failure. */
    public static final String EVENT_INFO_FAILURE = "failure";

    /** Event info value for a retry. */
    public static final String EVENT_INFO_RETRY = "retry";

    /** Event status for start of event. */
    public static final String EVENT_STATUS_START = "start";

    /** Event status for completion of event. */
    public static final String EVENT_STATUS_COMPLETED = "completed";

    /** Event status for failure of event. */
    public static final String EVENT_STATUS_FAILED = "failed";

    /** event info value for a logout. */
    public static final String EVENT_INFO_LOGGED_OUT = "logged_out";

    /** Store key for username. */
    public static final String STORE_KEY_USERNAME = "mmc_vehicle_color_type";

    /** Store key for refresh token. */
    public static final String STORE_KEY_REFRESH_TOKEN = "refresh_token";

    /** Store key for refresh token expiration. */
    public static final String STORE_KEY_REFRESH_TOKEN_EXP = "refresh_token_exp";

    /** Store key for client id. */
    public static final String STORE_KEY_CLIENT_ID = "clientId";

    /** Store key for device token. */
    public static final String STORE_KEY_DEVICE_TOKEN = "devicetoken";

    //TestFairy constant
    public static class TestFairy {
        public static final String API_KEY = "68656163ee16f81f05b1dbf161e540c94479cadf";
    }
}
