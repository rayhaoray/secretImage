package com.zlei.secretimage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

/**
 * SessionMTransaction provides an example of how to contact SessionM with an
 * install transaction.
 * 
 * There is also a listener interface provided so the app can receive updates
 * about transaction results. An app key (obtained from SessionM) is required to
 * use this class. This class uses the ANDROID_ID to identify the transaction.
 * 
 * Here is how to use it:
 * 
 * SessionMTransaction txn = new SessionMTransaction(this,
 * "<YOUR-APP-KEY-HERE"); txn.start();
 * 
 * This should generally be run when your app first starts up.
 * 
 */
public class SessionMTransaction {

    /**
     * Use this interface to find out when/if transaction are successfully sent
     * to SessionM servers
     * 
     */
    public interface SessionMTransactionListener {

        /**
         * Called when the transaction is successfully sent to SessionM servers
         * 
         * @param txn
         */
        public void onSuccess(SessionMTransaction txn);

        /**
         * Called when an error occurs while sending an install transaction.
         * 
         * @param txn
         * @param statusCode
         * @param error
         */
        public void onError(SessionMTransaction txn, int statusCode, Throwable error);
    }

    private static final String host = "api.sessionm.com";
    private static final String sessionMPrefs = "com.sessionm.cpi.prefsfile";
    private static final String installSentKey = "com.sessionm.cpi.install.sent";
    private static final String GET_ADVERTISING_ID_INFO = "getAdvertisingIdInfo";
    private static final String ADVERTISING_ID_CLIENT_CLASS = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
    private static String advertiser_id = "";

    private String appKey;
    private static Context appContext;
    private SessionMTransactionListener listener;

    /**
     * Initialize a SessionMTransaction using your Application Context and your
     * appKey (obtained from SessionM).
     * 
     * @param appContext
     * @param appKey
     */
    public SessionMTransaction(Context appContext, String appKey) {
        if (appContext == null) {
            throw new NullPointerException("Could not initialize transaction. appContext was null");
        }
        if (appKey == null) {
            throw new NullPointerException("Could not initialize transaction. appKey was null");
        }
        this.appContext = appContext;
        this.appKey = appKey;
    }

    /**
     * Call this to initiate sending an install back to SessionM.
     */
    public void start() {
        if (installWasSent()) {
            return;
        }
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                SessionMTransaction.this.sendInstall();
            }
        });
    }

    /**
     * Use this to set your SessionMTransaction listener
     * 
     * @param listener
     */
    public void setListener(SessionMTransactionListener listener) {
        this.listener = listener;
    }

    private void sendInstall() {
        Throwable error = null;
        String response = null;
        int statusCode = -1;
        HttpURLConnection conn = null;
        String url = String.format("https://%s/transactions", host);
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            ((HttpsURLConnection) conn).setHostnameVerifier(new AllowAllHostnameVerifier());
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(getPayload().getBytes());
            os.flush();
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = readStream(in);
            in.close();
            statusCode = conn.getResponseCode();
        } catch (Throwable e) {
            if (e instanceof FileNotFoundException) {
                try {
                    statusCode = conn.getResponseCode();
                } catch (IOException e1) {
                    // Attempt to get a status code in case of server error.
                }
            }
            error = e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        if (error == null && response != null && response.equals("ok") && statusCode == 201) {
            SharedPreferences prefs = appContext.getSharedPreferences(sessionMPrefs, Context.MODE_PRIVATE);
            if (prefs != null) {
                Editor editor = prefs.edit();
                editor.putBoolean(installSentKey, true);
                editor.commit();
                if (listener != null) {
                    listener.onSuccess(this);
                }
            }
        } else {
            if (listener != null) {
                listener.onError(this, statusCode, error);
            }
        }
    }

    private boolean installWasSent() {
        SharedPreferences prefs = appContext.getSharedPreferences(sessionMPrefs, Context.MODE_PRIVATE);
        return prefs.getBoolean(installSentKey, false);
    }

    private String getPayload() {
        advertiser_id = retrieveDeviceId();
        String payload = String.format("id2s[]=%s:%s", this.appKey, advertiser_id);
        return payload;
    }

    private String readStream(InputStream in) throws IOException {
        final int BUFFER_SIZE = 1024 * 10;

        byte[] buf = new byte[BUFFER_SIZE];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        while ((count = in.read(buf)) != -1) {
            out.write(buf, 0, count);
        }
        return out.toString();
    }

    private static String retrieveDeviceId() {
        String identifier = "";
        identifier = getAdvertiserId(appContext);
        if (identifier.isEmpty())
            identifier = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return identifier;
    }

    private static String getAdvertiserId(Context appContext) {
        String identifier = "";
        try {
            Class<?> adIdClient = Class.forName(ADVERTISING_ID_CLIENT_CLASS);
            Method getAdIdInfoMethod = adIdClient.getMethod(GET_ADVERTISING_ID_INFO, Context.class);
            Object adInfo = getAdIdInfoMethod.invoke(adIdClient.getClass(), appContext);
            Method getIdMethod = adInfo.getClass().getMethod("getId");
            identifier = (String) getIdMethod.invoke(adInfo);
        } catch (Exception e) {
            Log.i("Fail: ", "Error retreiving advertiser id: ", e);
        }
        return identifier;
    }
}
