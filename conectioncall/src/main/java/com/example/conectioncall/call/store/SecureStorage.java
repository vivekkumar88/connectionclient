package com.example.conectioncall.call.store;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.conectioncall.call.protocol.common.extras.Utility;
import com.example.conectioncall.call.protocol.common.store.ISecureStore;
import com.example.conectioncall.call.protocol.common.store.IValueStore;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Securely store strings.
 */
public class SecureStorage implements ISecureStore {

    private static final String CIPHER_TYPE = "AES";
    private static final String KEY = "secure_store_key";
    private static final int KEY_LENGTH = 32;

    /**
     * Generate a valid pass phrase given the key.
     *
     * @param key The key.
     * @return Valid pass phrase.
     */
    private static String getPassphraseSize16(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        char controlChar = '\u0014';
        String key16 = key + controlChar;
        if (key16.length() < 16) {
            while (key16.length() < 16) {
                key16 += key + controlChar;
            }
        }
        if (key16.length() > 16) {
            key16 = key16.substring(key16.length() - 16);
        }
        return key16;
    }

    /**
     * Encode message given key.
     *
     * @param message The message to encrypt.
     * @param key     The key.
     * @return The encrypted message.
     */
    public static String encodeAES(String message, String key) {
        try {
            String passphrase16 = getPassphraseSize16(key);
            SecretKeySpec secretKey = new SecretKeySpec(passphrase16.getBytes(), CIPHER_TYPE);
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encodedText = cipher.doFinal(message.getBytes());
            return Utility.base64encode(encodedText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Decode a message given the key.]
     *
     * @param encodedMessage The message to decode.
     * @param key            The key.
     * @return The decoded message.
     */
    public static String decodeAES(String encodedMessage, final @NonNull String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("'key' is empty");
        }
        try {
            String passphrase16 = getPassphraseSize16(key);
            SecretKeySpec secretKey = new SecretKeySpec(passphrase16.getBytes(), CIPHER_TYPE);
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = cipher.doFinal(Base64.decode(encodedMessage, Base64.DEFAULT));
            final String decryptedText = new String(decodedBytes);
            return decryptedText;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //region old version decryption (don't user this)
    /**
     * Decode a message given the key.]
     *
     * @param encodedMessage The message to decode.
     * @param key            The key.
     * @return The decoded message.
     */
    public static String decodeAESoldVersion(String encodedMessage, final @NonNull String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("'key' is empty");
        }
        try {
            String passphrase16 = getPassphraseSize16(key);
            SecretKeySpec secretKey = new SecretKeySpec(passphrase16.getBytes(), CIPHER_TYPE);
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedText = cipher.doFinal(Base64.decode(encodedMessage, Base64.DEFAULT));
            return new String(decodedText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    //region old version decryption (don't user this)

    private IValueStore mValueStore;
    //private KeyStoreUtility mKeyStore;

    private String mKey;

    /**
     * Instantiate with given options.
     *
     * @param valueStore
     */
    public SecureStorage(IValueStore valueStore/*, KeyStoreUtility keyStore*/) {
        //mKeyStore = keyStore;
        mValueStore = valueStore;

        createAndLoadKey();
    }

    private void createAndLoadKey() {
        boolean hasEverCreatedKey = mValueStore.has(KEY);
        if (hasEverCreatedKey) {
            loadKey();
            return;
        }

        mKey = generateKey();

        mValueStore.store(KEY, mKey);

        /*if (mKeyStore == null) {
            mValueStore.store(KEY, mKey);
        } else {
            String encryptedKey = mKeyStore.encrypt(mKey);
            mValueStore.store(KEY, encryptedKey);
        }*/
    }

    private String generateKey() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;

        for (int i = 0; i < KEY_LENGTH; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void loadKey() {
        mKey = mValueStore.getString(KEY);

        /*if (mKeyStore == null) {
            mKey = mValueStore.getString(KEY);
        } else {
            String cipherKey = mValueStore.getString(KEY);
            mKey = mKeyStore.decrypt(cipherKey);
        }*/
    }

    //region implement ISecureStore
    @Override
    public boolean set(String key, String value) {
        if (key.isEmpty()) {
            return false;
        }

        final String encrypted = encodeAES(value, mKey);
        mValueStore.store(key, encrypted);
        return true;
    }

    @Override
    public String get(String key) {
        if (key.isEmpty()) {
            return "";
        }

        final String encrypted = mValueStore.getString(key, null);
        if (encrypted != null) {
            final String decrypted = decodeAES(encrypted, mKey);
            return decrypted;
        }
        return "";
    }

    @Override
    public void clear(String key) {
        mValueStore.clear(key);
    }

    @Override
    public boolean has(String key) {
        return mValueStore.has(key);
    }
    //endregion implement ISecureStore
}