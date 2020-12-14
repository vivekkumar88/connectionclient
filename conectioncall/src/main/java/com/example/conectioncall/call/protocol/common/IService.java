/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

import android.content.Context;

import com.example.conectioncall.call.ICall;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ATSP back end service interface.
 */
public interface IService {

    /**
     * Set the environment properties for the service.
     *
     * @param environmentProperties Key/value mapping of environment properties to their
     * values.
     */
    void setEnvironmentProperties(HashMap<String,String> environmentProperties);

    /**
     * Given the raw text of an AEService environment properties file, parse the text and generate
     * the environment properties map.
     *
     * @param text The raw text of an AEService environment properties file.
     */
    void setEnvironmentPropertiesFromText(String text);

    /**
     * Set environment properties from given file. Assumes that the file is in the "raw/raw"
     * directory of the project.
     *
     * @param filename The filename.
     */
    void setEnvironmentPropertiesFromFile(String filename);

    /**
     * Set the current environment.
     *
     * @param environment The environment tag.
     */
    void setEnvironment(String environment);

    /**
     * Add a delegate for handling service events.
     *
     * @param event The event to handle.
     * @param delegate The delegate to call for the event.
     * @return True if delegate was added, false otherwise.
     */
    boolean addDelegate(Service.Event event, IServiceDelegate delegate);

    /**
     * Remove a delegate for a given service event.
     *
     * @param event The event to remove the delegate from.
     * @param delegate The delegate to remove.
     * @return True if delegate was removed, false otherwise.
     */
    boolean removeDelegate(Service.Event event, IServiceDelegate delegate);

    /**
     * Send the given call to the ATSP service. The call does not expect a response. This is a fire
     * and forget type method, even if the call does have a response. If a response does come back,
     * it will be sent back as a notification.
     *
     * @param call The call to send to the ATSP service.
     */
    void send(ICall call);

    /**
     * Send the given call and, if a response occurs, call the given response delegate. Some calls
     * may not have response.
     *
     * @param call The call to send to the ATSP service.
     * @param delegate The response delegate to run if and when a response occurs.
     */
    void send(ICall call, IResponseDelegate delegate);

    /**
     * Get the service environment properties.
     *
     * Note: There should never be any duplicates of environment, protocol or names. This will
     * confuse the property specificity algorithm and you will get invalid results. For example,
     * there should never be a case where the environment name is "http".
     *
     * @param protocolName The protocol name.
     * @param name The name of the specific property to get.
     * @return HashMap<String, String> with host, post, and path set or null.
     */
    HashMap<String, String> getEnvironmentProperty(String protocolName, String name);

    /**
     * Get the service environment properties.
     *
     * Note: There should never be any duplicates of environment, protocol or names. This will
     * confuse the property specificity algorithm and you will get invalid results. For example,
     * there should never be a case where the environment name is "http".
     *
     * @param protocolName The protocol name.
     * @param name The name of the specific property to get.
     * @param keys The property key to get. Defaults are host, port, and path.
     * @return HashMap<String,String> with host, port, and path set, or keys,  or null.
     */
    HashMap<String, String> getEnvironmentProperty(String protocolName, String name, ArrayList<String> keys);

    /**
     * Perform clearing of state for the service.
     */
    void clear();

    /**
     * Set the platform resource delegate interface instance for the library to use.
     * @param platformResourceDelegate
     */
    void setPlatformResourceDelegate(final IPlatformResourceDelegate platformResourceDelegate);

    /**
     * Get the unique client id.
     *
     * @return The unique client id.
     * @todo This should be persisted on app install.
     */
    String getClientId();

    void initializeServiceEnvironment(Context context, int resourceId);
}
