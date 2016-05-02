package com.usergo.presentit.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.usergo.presentit.R;

/**
 * Created by sruthi on 4/16/2016.
 */
public class AccountUtils {

    private final static String PREFS_KEY_EMAIL = "email_account";

    public static void saveEmailAccount(Context context, String email) {
        saveStringToPreference(context, PREFS_KEY_EMAIL, email);
    }

    public static String getEmailAccount(Context context) {
        return getStringFromPreference(context, PREFS_KEY_EMAIL);
    }

    public static void saveStringToPreference(Context context, String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (null == value) {
            // we want to remove
            Log.i("AccountUtils", value + "removed" );
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putString(key, value).apply();
        }
    }

    public static String getStringFromPreference(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }

    public static Account[] getGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        return am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public static void displayNetworkErrorMessage(Context context) {
        new AlertDialog.Builder(
                context).setTitle(R.string.api_error_title)
                .setMessage(R.string.api_error_message)
                .setCancelable(false)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.exit(0);
                            }
                        }
                ).create().show();
    }
}
