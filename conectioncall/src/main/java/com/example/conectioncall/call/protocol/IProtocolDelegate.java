/*
 Created by V K on 12/13/20.
 */


package com.example.conectioncall.call.protocol;

/**
 * Types of status a protocol can become.
 */
public interface IProtocolDelegate {

    /**
     * Type of status a protocol can become.
     */
    enum Status {
        /** Protocol is in a state of disconnected. */
        Disconnected,

        /** Protocol requesting logout. */
        Logout,
    }

    /**
     * Send a status update.
     * @param status The new status.
     * @param info Any information regarding the update.
     */
    void statusUpdate(Status status, String info);
}
