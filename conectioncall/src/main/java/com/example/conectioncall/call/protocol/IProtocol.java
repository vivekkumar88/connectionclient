/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol;

import com.example.conectioncall.call.ICall;
import com.example.conectioncall.call.protocol.common.IResponseDelegate;

/**
 * A protocol channel interface. Implemented protocols must be able to handle ICall instances to
 * send through.
 */
public interface IProtocol {

    /**
     * Set the protocol delegate. This will be called by the protocol when some events occur within
     * it and the delegate wishes to know about these events.
     * @param protocolDelegate The protocol delegate.
     */
    void setDelegate(IProtocolDelegate protocolDelegate);

    /**
     * Send the service call to the appropriate channel.
     * @param call The call to send through.
     */
    void send(final ICall call) throws Exception;

    /**
     * Send the service call to the appropriate channel and call the response delegate when the call
     * is done. Some calls may not have a response.
     * @param call The service call to perform.
     * @param delegate The delegate to call on response if applicable. Can be null.
     */
    void send(final ICall call, final IResponseDelegate delegate) throws Exception;
}
