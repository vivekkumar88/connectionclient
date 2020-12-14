/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.extras;

import android.util.Base64;
import android.util.Log;

import com.example.conectioncall.BuildConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for methods that are useful anywhere but don't belong within another class.
 */
public class Utility {

    /**
     * Flag indicating whether the log output is to console or logging framework.
     */
    public static boolean isConsoleOut = false;

    /**
     * Get text contents of a file in the res/raw android folder.
     * @param name The name of the file.
     * @return The text contents of the file.
     */
    public static String getFile(String name) {
        String text = null;
        try {
            String filename = "res/raw/" + name;
            InputStream is = Utility.class.getClassLoader().getResourceAsStream(filename);
            if (is != null) {
                final int bufSize = 1024;
                final char[] buf = new char[bufSize];
                final StringBuilder out = new StringBuilder();
                Reader in = new InputStreamReader(is, "UTF-8");
                for (; ; ) {
                    int rsz = in.read(buf, 0, buf.length);
                    if (rsz < 0) {
                        break;
                    }
                    out.append(buf, 0, rsz);
                }
                text = out.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return text;
    }

    /**
     * Convert a java Date class into a unix timestamp string.
     * @param date The Date instance to convert.
     * @return The unix timestamp representation.
     */
    public static String dateToUnixFormat(Date date) {
        String timestamp = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            timestamp = df.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return timestamp;
    }

    /**
     *
     * @param unixTimestamp
     * @return
     */
    public static Date dateFromUnixTimestampString(String unixTimestamp) {
        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            date = df.parse(unixTimestamp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        Utility.log("'" + unixTimestamp + "' -> date = " + date.toString());
        return date;
    }

    /**
     * Parse a long to hex string.
     * @param hex The hex string to parse.
     * @return The long equivalent.
     */
    public static Long hexToLong(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * Generate a unique hex id using the current timestamp.
     * @return Current time in millis since Jan 1, 1970 as hex string.
     */
    public static String generateTimestampIdAsHex() {
        return Long.toHexString(new Date().getTime());
    }

    /**
     * Helper method to join a list of strings.
     * @param delimiter The delimiter to use.
     * @param list The list of strings.
     * @return The single joined string.
     */
    public static String join(String delimiter, List<String> list) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String s : list) {
            if (first) {
                sb.append(s);
            } else {
                sb.append(delimiter + s);
            }
            first = false;
        }
        return sb.toString();
    }

    /**
     * Single interface point for logging. Only need to change here if we change logging frameworks.
     * @param s The text log.
     */
    public static void log(String s) {
        if (BuildConfig.DEBUG) {
            if (!isConsoleOut) {
                Log.d("httpLib", s);
            } else {
                System.out.println(s);
            }
        }
    }

    /**
     * Log an exception
     * @param e
     */
    public static void logException(final Exception e) {
        if (e == null) {
            return;
        }
    }

    /**
     * Log an error.
     *
     * @param s The error string to log.
     */
    public static void loge(final String s) {
        if (s == null) {
            return;
        }
        if (!isConsoleOut) {
            Log.e("httpLib", s);
        } else {
            System.err.println(s);
        }
    }

    /**
     * Wrapper for base 64 encoding. If we change implementation we will only have to change here.
     * Trying to consolidate all android specific calls. This way we can use the library outside
     * of android.
     *
     * @param bytes The bytes to encode.
     * @return The base64 string encoding of the bytes.
     */
    public static String base64encode(byte[] bytes, int type) {
        String base64 = new String(Base64.encode(bytes, type));
        return base64;
    }

    /**
     * Default NO_WRAP base64 encoding.
     *
     * @param bytes The bytes to encode.
     * @return The base64 string encoding of the bytes.
     */
    public static String base64encode(byte[] bytes) {
        return base64encode(bytes, Base64.NO_WRAP);
    }

    /**
     * Base64 encode given string.
     *
     * @param base64encoded The string to encode.
     * @return Base64 encoding of string.
     */
    public static byte[] base64decode(String base64encoded) {
        byte[] bytes = Base64.decode(base64encoded, Base64.DEFAULT);
        return bytes;
    }

    /**
     * HMAC SHA256 of data with key.
     * @param key The key.
     * @param data The data.
     * @return The signature.
     */
    public static byte[] hmacSha256(String key, String data) {
        Mac hmacSHA256;
        byte[] signature;
        try {
            hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            hmacSHA256.init(secret_key);
            signature = hmacSHA256.doFinal(data.getBytes());
            return signature;
        } catch (Exception ex)  {
            ex.printStackTrace();
            Utility.logException(ex);
        }
        return null;
    }

    /**
     * Used for converting bytes to hex.
     */
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Convert bye array to hex string.
     * @param bytes The byte array.
     * @return The string representation in hex.
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
