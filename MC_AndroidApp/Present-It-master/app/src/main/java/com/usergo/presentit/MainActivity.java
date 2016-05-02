package com.usergo.presentit;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private String pEmailAccount;
    private AuthorizationCheckTask pAuthTask;
    private static final String TAG = "MainActivity";

    GoogleApiClient pGoogleApiClient;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private ProfileFragment pProfileFragment;
    /**
     * Activity result indicating a return from the Google account selection intent.
     */
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pEmailAccount = AccountUtils.getEmailAccount(this);
        Log.i(TAG, "getemailaccount-" + pEmailAccount);


        if (null != pEmailAccount) {
            Log.d(TAG, "performauthcheck -" + pEmailAccount);
            performAuthCheck(pEmailAccount);
        } else {
            selectAccount();
        }
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        if(mDrawerList==null)
            Log.d("ERROR", null);

        mActivityTitle = "Profile";
        getSupportActionBar().setTitle(mActivityTitle);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, ProfileFragment.newInstance(MainActivity.this));
        tx.commit();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void addDrawerItems() {
        String[] menuArray = { "Profile", "Courses", "Classrooms" };
        mAdapter = new ArrayAdapter<>(this,R.layout.drawer_listview_item , menuArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.bringToFront();
        mDrawerLayout.requestLayout();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void signIn() {
        Log.i(TAG, "pemailaccount-" + null + "entered signIn()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(pGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pAuthTask != null) {
            pAuthTask.cancel(true);
            pAuthTask = null;
        }
    }

    protected void onResume() {
        super.onResume();

        if (null != pEmailAccount) {
            Log.d(TAG, "performauthcheck -" + pEmailAccount);
            performAuthCheck(pEmailAccount);
        } else {
            selectAccount();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_account:
                new AlertDialog.Builder(MainActivity.this).setTitle(null)
                        .setMessage(getString(R.string.clear_account_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                AccountUtils.saveEmailAccount(MainActivity.this, null);
                                pEmailAccount = null;
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                break;
            case R.id.action_reload:

                break;
        }
        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /*
     * Selects an account for talking to Google Play services. If there is more than one account on
     * the device, it allows user to choose one.
     */
    private void selectAccount() {
        Account[] accounts = AccountUtils.getGoogleAccounts(this);
        // String emailAccount = AccountUtils.getEmailAccount(this);
        int numOfAccount = accounts.length;
        Log.i(TAG, "number of google accounts "+numOfAccount);
        switch (numOfAccount) {
            case 0:
                // No accounts registered, nothing to do.
                Toast.makeText(this, R.string.toast_no_google_accounts_registered,
                        Toast.LENGTH_LONG).show();
                Log.i("mainactivity", "pemailaccount-" + null + "entering signIn()");
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                // Build a GoogleApiClient with access to the Google Sign-In API and the
                // options specified by gso.
                pGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                signIn();
                break;
            case 1:
                pEmailAccount = accounts[0].name;
                performAuthCheck(pEmailAccount);
                break;
            default:
                // More than one Google Account is present, a chooser is necessary.
                // Invoke an {@code Intent} to allow the user to select a Google account.
                Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                        getString(R.string.select_account_for_access), null, null, null);
                startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.
            pEmailAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        }
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            finish();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.i(TAG, "Authenticated Username:" + acct.getDisplayName());
                AccountUtils.saveEmailAccount(MainActivity.this, acct.getEmail());
            }
        } else {
            Log.e(TAG, "Failed to authenticate user");
            Toast.makeText(this, "Failed to authenticate user",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Schedule the authorization check.
     */
    private void performAuthCheck(String email) {
        // Cancel previously running tasks.
        if (pAuthTask != null) {
            pAuthTask.cancel(true);
        }

        // Start task to check authorization.
        pAuthTask = new AuthorizationCheckTask();
        pAuthTask.execute(email);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Verifies OAuth2 token access for the application and Google account combination with
     * the {@code AccountManager} and the Play Services installed application. If the appropriate
     * OAuth2 access hasn't been granted (to this application) then the task may fire an
     * {@code Intent} to request that the user approve such access. If the appropriate access does
     * exist then the button that will let the user proceed to the next activity is enabled.
     */
    private class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {

        private final static boolean SUCCESS = true;
        private final static boolean FAILURE = false;
        private Exception mException;

        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i(TAG, "Background task started.");

            if (!AccountUtils.checkGooglePlayServicesAvailable(MainActivity.this)) {
                publishProgress(R.string.gms_not_available);
                return FAILURE;
            }

            String emailAccount = emailAccounts[0];
            Log.i(TAG, emailAccount);
            // Ensure only one task is running at a time.
            pAuthTask = this;

            // Ensure an email was selected.
            if (TextUtils.isEmpty(emailAccount)) {
                publishProgress(R.string.toast_no_google_account_selected);
                return FAILURE;
            }

            pEmailAccount = emailAccount;
            AccountUtils.saveEmailAccount(MainActivity.this, emailAccount);

            return SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... stringIds) {
            // Toast only the most recent.
            Integer stringId = stringIds[0];
            Toast.makeText(MainActivity.this, getString(stringId), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            pAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Authorization check successful, get conferences.
                Log.i(TAG, "Email id - " + pEmailAccount);
                PresentItUtils.build(MainActivity.this, pEmailAccount);
            } else {
                // Authorization check unsuccessful.
                pEmailAccount = null;
                if (mException != null) {
                    AccountUtils.displayNetworkErrorMessage(MainActivity.this);
                }
            }
            pAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            pAuthTask = null;
        }
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            switch(position) {
                case 0:
                    mActivityTitle = "Profile";
                    tx.replace(R.id.main, ProfileFragment.newInstance(MainActivity.this));
                    break;
                case 1:
                    mActivityTitle = "Courses";
                    tx.replace(R.id.main, CoursesFragment.newInstance(MainActivity.this));
                    break;
                default:
                    break;
            }
            tx.commit();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}
