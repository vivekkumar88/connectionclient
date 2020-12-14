/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common;

import android.content.Context;

import com.example.conectioncall.call.Constants;
import com.example.conectioncall.call.ICall;
import com.example.conectioncall.call.protocol.HttpProtocol;
import com.example.conectioncall.call.protocol.IProtocol;
import com.example.conectioncall.call.protocol.IProtocolDelegate;
import com.example.conectioncall.call.protocol.common.exception.Error;
import com.example.conectioncall.call.protocol.common.extras.Utility;
import com.example.conectioncall.call.protocol.common.store.DefaultSecureStore;
import com.example.conectioncall.call.protocol.common.store.DefaultValueStore;
import com.example.conectioncall.call.protocol.common.store.ISecureStore;
import com.example.conectioncall.call.protocol.common.store.IValueStore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

public class Service implements IService, IProtocolDelegate {

    /** Service errors. */
    public enum Errors {

        SEND_FAILED { public String toString() { return "service_send_failed"; }},
        NOT_CONNECTED { public String toString() { return "service_not_connected_to_network"; }},
        UNKNOWN { public String toString() { return "service_error_unknown"; }}
    }

    /** Default value store interface implementation that performs dummy operations. */
    private static final IValueStore DEFAULT_VALUE_STORE = new DefaultValueStore();

    /** Default secure store interface implementation that performs dummy operations. */
    private static final ISecureStore DEFAULT_SECURE_STORE = new DefaultSecureStore();

    /** Default network status interface implementation that performs dummy operations. */
    private static final INetworkStatus DEFAULT_NETWORK_STATUS = new DefaultNetworkStatus();

    /** Singleton instance. */
    private static IService instance;

    /** Flag indicating we are in test mode. */
    private static boolean isTestMode;

    /** Flag indicating we are in development mode. */
    private static boolean mIsDevMode;

    /** Flag indicating whether we should use the more secure payload. */
    public static boolean SECURE_PAYLOAD = true;

    /** Current running service environment. */
    private String environment = "prod";

    /** The parsed environment properties. */
    private HashMap<String, String> environmentProperties = new HashMap<>();

    /** Initialized protocol map. */
    private Map<String, IProtocol> mProtocols = new HashMap<>();

    /** Maps a list of delegates for a given service event. */
    private HashMap<Event, ArrayList<IServiceDelegate>> delegateMap = new HashMap<>();

    /** Unique client id. */
    private String mClientId;

    /** Device token used for push notifications. This must be set before login. */
    private String mDeviceToken;

    /**
     * Only used for synchronization lock.
     */
    private final Timer mTimerLock = new Timer();

    /**
     * Timer for when the access token will expire.
     */
    private Timer mAccessTokenExpTimer;

    /**
     * Timer for when the challenge key will expire.
     */
    private Timer mCKeyExpTimer;

    /**
     * Get current development state.
     * @return True if development mode is enabled, false otherwise.
     */
    public static boolean isDevMode() { return mIsDevMode; }

    /**
     * Flag indicating that SSL is enabled.
     */
    public static boolean isSSLEnabled = true;

    /**
     * Platform resource delegate interface.
     */
    private WeakReference<IPlatformResourceDelegate> mPlatformResourceDelegate;

    /** Logging tag. */
    private static final String TAG = "Service";


    /**
     * Get an instance of the singleton. Perform initialization if one has not yet occurred.
     * @return Singleton instance.
     */
    public static IService sharedInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    /**
     * Initialize or get the proper protocol instance given the protocol name passed in.
     * @param protocolName The name of the protocol to initialize.
     * @return The protocol instance or null if no valid protocol was found.
     */
    private IProtocol getProtocol(String protocolName) {
        IProtocol protocol = mProtocols.get(protocolName);
        try {
            if (protocol == null) {
                if (protocolName.equals("http")) {
                    protocol = new HttpProtocol();
                    protocol.setDelegate(this);
                } else if (protocolName.equals("mqtt")) {

                }

                mProtocols.put(protocolName, protocol);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return protocol;
    }

    /**
     * Get network status interface.
     *
     * @return The network status interface.
     */
    public INetworkStatus networkStatus() {
        if (mPlatformResourceDelegate == null || mPlatformResourceDelegate.get() == null) {
            Utility.loge("No platform resource delegate set");
            return DEFAULT_NETWORK_STATUS;
        }
        final INetworkStatus networkStatus = mPlatformResourceDelegate.get().getNetworkStatus();
        if (networkStatus == null) {
            Utility.loge("No valid network status interface");
            return DEFAULT_NETWORK_STATUS;
        }
        return networkStatus;
    }

    /**
     * Get the value store instance.
     *
     * @return The value store instance.
     */
    public IValueStore valueStore() {
        // Check if we have a valid platform resource delegate interface, and if not, use the
        // default value store which performs dummy operations. Should output an error.
        if (mPlatformResourceDelegate == null || mPlatformResourceDelegate.get() == null) {
            Utility.loge("No platform resource delegate set");
            return DEFAULT_VALUE_STORE;
        }

        // Get a value store interface instance from the platform.
        final IValueStore valueStore = mPlatformResourceDelegate.get().getValueStore();

        // Validate to make sure this value store instance exists.
        if (valueStore == null) {
            Utility.loge("No valid value store interface");
            return DEFAULT_VALUE_STORE;
        }

        return valueStore;
    }

    /**
     * Make sure to initialize the connectionclient environment variables before performing any calls. This
     * should be among the first items initialized when the app starts.
     */
    public void initializeServiceEnvironment(Context context, int resourceId) {
        // Read in and set environment file.
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer text = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                text.append(line + "\n");
            }

            Service.sharedInstance().setEnvironmentPropertiesFromText(text.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // This is to set development mode.
    /*//
    Service.enableDevMode(true);
    //*/

        // This is how you can set test mode.
    /*//
    Service.enableTestMode(true);
    //*/

        // Manually set SRI payload.
    /*//
    Service.SECURE_PAYLOAD = false;
    //*/
    }

    /**
     * @see IService#setEnvironmentPropertiesFromFile(String)
     */
    public void setEnvironmentPropertiesFromFile(String filename) {
        String text = Utility.getFile(filename);
        setEnvironmentPropertiesFromText(text);
    }

    /**
     * @see IService#setEnvironmentPropertiesFromText(String)
     */
    public void setEnvironmentPropertiesFromText(String text) {
        HashMap<String, String> environmentProperties = new HashMap<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.startsWith("#")) { continue; }

            // Get the first occurrence of the equal sign. Everything to the left is the key, and
            // everything to the right is the value.
            int ix = line.indexOf('=');

            // No equal sign means this is an invalid property.
            if (ix < 0) { continue; }

            String k = line.substring(0, ix);
            String v = line.substring(ix + 1, line.length());

            // Take out any white spaces.
            String key = k.trim();
            String value = v.trim();

            // No key means no property.
            if (key.length() <= 0) { continue; }

            environmentProperties.put(key, value);
        }
        setEnvironmentProperties(environmentProperties);
    }

    /**
     * @see IService#setEnvironment(String)
     */
    public void setEnvironmentProperties(HashMap<String,String> environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    /**
     * @see IService#setEnvironment(String)
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @see IService#addDelegate(Event, IServiceDelegate)
     */
    public boolean addDelegate(Event event, IServiceDelegate delegate) {
        if (delegate == null) {
            return false;
        }
        ArrayList<IServiceDelegate> delegates = delegateMap.get(event);
        if (delegates == null) {
            delegates = new ArrayList<>();
            delegateMap.put(event, delegates);
        }
        return delegates.add(delegate);
    }

    /**
     * @see IService#removeDelegate(Event, IServiceDelegate)
     */
    public boolean removeDelegate(Event event, IServiceDelegate delegate) {
        if (delegate == null) {
            return false;
        }
        ArrayList<IServiceDelegate> delegates = delegateMap.get(event);
        if (delegates == null) {
            return false;
        }
        boolean result = delegates.remove(delegate);
        return result;
    }

    /**
     * Invoke the given delegates for the event.
     * @param event The even that occurred.
     * @param info Any information to pass down to delegates.
     */
    private void invokeDelegates(final Event event, HashMap<String, Object> info) {
        if (event == null) {
            throw new IllegalArgumentException("'event' is null");
        }

        Utility.log("invokeDelegates event = " + event);

        final ArrayList<IServiceDelegate> delegates = delegateMap.get(event);
        if (delegates == null || delegates.size() <= 0) {
            return;
        }

        for (final IServiceDelegate d : delegates) {
            if (d == null) {
                continue;
            }
            d.onEvent(event, info);
        }
    }

    /**
     * @see IService#send(ICall)
     */
    public void send(ICall call) {
        send(call, null);
    }

    /**
     * @see IService#send(ICall, IResponseDelegate)
     */
    public void send(ICall call, IResponseDelegate delegate) {
        try {

            // First check if network is connected otherwise return.
            if (!networkStatus().isConnectedToNetwork()) {
                HashMap<String,Object> response = new HashMap<>();
                response.put(Error.Key, new Error(Errors.NOT_CONNECTED.name(), Errors.NOT_CONNECTED.toString(), Errors.NOT_CONNECTED.name()));
                if (delegate != null) {
                    delegate.handleResponse(response);
                }
                return;
            }

            // Perform any validation the call requires.
            if (call != null) {
                Error error = call.validate();
                if (error != null) {
                    HashMap<String, Object> response = new HashMap<>();
                    response.put(Error.Key, call.validate());
                    if (delegate != null) {
                        delegate.handleResponse(response);
                    }
                    return;
                }
            }

            IProtocol protocol = getProtocol(call.getProtocolName());

            // Check to see if we have got a valid channel
            if (protocol != null) {
                protocol.send(call, delegate);
            } else {
                // Inform delegates of send failure, if available.
                if (delegate != null) {
                    final HashMap<String, Object> response = new HashMap<>();
                    final Error error = new Error(Errors.SEND_FAILED.toString(), "Could not send.");
                    response.put(Error.Key, error);
                    delegate.handleResponse(response);
                }
            }
        } catch (Error err) {
            err.printStackTrace();
            Utility.logException(err);
            if (delegate != null) {
                HashMap<String, Object> response = new HashMap<>();
                String label = err.label();
                String desc = err.getMessage();
                String code = err.errorCode();
                response.put(Error.Key, new Error(label, desc, code));
                delegate.handleResponse(response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
            if (delegate != null) {
                HashMap<String,Object> response = new HashMap<>();
                String label = Errors.SEND_FAILED.toString();
                String desc = ex.getLocalizedMessage();
                response.put(Error.Key, new Error(label, desc));
                delegate.handleResponse(response);
            }
        }
    }

    /**
     * @see IService#getEnvironmentProperty(String, String)
     */
    public HashMap<String, String> getEnvironmentProperty(String protocolName, String name) {
        return getEnvironmentProperty(protocolName, name, null);
    }

    /**
     * @see IService#getEnvironmentProperty(String, String, ArrayList)
     */
    public HashMap<String, String> getEnvironmentProperty(String protocolName, String name, ArrayList<String> keys) {
        HashMap<String, String> result = new HashMap<>();
        if (environmentProperties != null) {
            int e = (1 << 2);
            int p = (1 << 1);
            int n = (1 << 0);
            int max = e | p | n;
            String env = environment;

            ArrayList<String> keysList;
            if (keys == null) {
                keysList = new ArrayList<>();
                keysList.add("host");
                keysList.add("port");
                keysList.add("path");
                keysList.add("secure");
            } else {
                keysList = keys;
            }
            for (String key : keysList) {
                for (int i = max; i >= 0; i--) {
                    ArrayList<String> propertyChain = new ArrayList<>();
                    if ((e & i) != 0) propertyChain.add(env);
                    if ((p & i) != 0) propertyChain.add(protocolName);
                    if ((n & i) != 0) propertyChain.add(name);
                    String property = Utility.join(".", propertyChain);
                    String propertyKey = property + (i != 0 ? "." : "") + key;
                    if (environmentProperties.containsKey(propertyKey)) {
                        result.put(key, environmentProperties.get(propertyKey));
                        break;
                    }
                }
            }
        }
        if (result.size() <= 0) {
            return null;
        }
        return result;
    }

    /**
     * @see IService#setPlatformResourceDelegate(IPlatformResourceDelegate)
     */
    @Override
    public void setPlatformResourceDelegate(final IPlatformResourceDelegate platformResourceDelegate) {
        if (platformResourceDelegate == null) {
            Utility.loge("'platformResourceDelegate' is null");
            return;
        }

        mPlatformResourceDelegate = new WeakReference<>(platformResourceDelegate);
    }

    /**
     * @see IService#clear()
     */
    @Override
    public void clear() {
        clearProtocols();
    }

    /**
     * Method to properly clear currently cached protocols.
     */
    private void clearProtocols() {
        if (mProtocols == null) {
            return;
        }

        if (mProtocols.size() <= 0) {
            return;
        }

        synchronized (mProtocols) {
            Iterator<Map.Entry<String, IProtocol>> itr = mProtocols.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, IProtocol> entry = itr.next();
                /*IProtocol protocol = entry.getValue();
                if (protocol instanceof MqttProtocol) {
                    MqttProtocol mqttProtocol = (MqttProtocol) protocol;
                    mqttProtocol.disconnect();
                }*/
            }

            mProtocols.clear();
        }
    }

    /**
     * @see IService#getClientId()
     */
    public String getClientId() {
        if (mClientId == null) {
            initializeClientId();
        }
        return mClientId;
    }

    /**
     * Properly generate or retrieve the unique client id.
     */
    private void initializeClientId() {
        // Initializing the client id. Check if the service does not have it set and try to get it
        // from the value store. If no value is found, this means no client id has ever been
        // generated, create the device id and store it, otherwise, just set the value found.
        if (mClientId == null || mClientId.isEmpty()) {
            final String clientId = valueStore().getString(Constants.STORE_KEY_CLIENT_ID);
            if (clientId == null || clientId.trim().isEmpty()) {
                Utility.log("generating client id...");
                mClientId = UUID.randomUUID().toString();
                valueStore().store(Constants.STORE_KEY_CLIENT_ID, mClientId);
            } else {
                Utility.log("retrieved client id...");
                mClientId = clientId;
            }
            Utility.log("client id = " + mClientId);
        }
    }
    /** Service events. */
    public enum Event {

    }

    /**
     * API Key
     */
    private static final byte[] KEY = { 0x66, 0x64, 0x34, 0x35, 0x39, 0x62, 0x37, 0x33, 0x65, 0x66, 0x35, 0x31, 0x34, 0x32, 0x35, 0x34, 0x38, 0x38, 0x64, 0x31, 0x32, 0x33, 0x36, 0x38, 0x39, 0x66, 0x62, 0x38, 0x61, 0x31, 0x39, 0x38 };

    /**
     * Secret
     */
    private static final byte[] SECRET = { 0x61, 0x6d, 0x70, 0x43, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x54, 0x72, 0x75, 0x73, 0x74, 0x65, 0x64, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74 };

    /**
     * Get key.
     * @return Key
     */
    public static String yy() {
        try {
            return new String(KEY, "UTF-8");
        } catch(Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return "";
    }

    /**
     * Get secret.
     * @return Secret
     */
    public static String zz() {
        try {
            return new String(SECRET, "UTF-8");
        } catch(Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return "";
    }

    @Override
    public void statusUpdate(Status status, String info) {

    }
}
