package com.usergo.presentit;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.appspot.yet_another_test_1261.presentIt.model.HelloClass;
import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by sruth on 4/16/2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    public Person mPersonProfile;
    protected TextView txtUserName;
    protected TextView txtEmailAddress;
    protected TextView txtUfid;


    public static ProfileFragment newInstance(Context context) {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile_fragment, null);
        txtUserName = (TextView) root.findViewById(R.id.user_display_name);
        txtEmailAddress = (TextView) root.findViewById(R.id.user_email);
        txtUfid = (TextView) root.findViewById(R.id.user_ufid);

        new ProfileLoadAsyncTask().execute();
        return root;
    }

    class ProfileLoadAsyncTask extends AsyncTask<Void, Person, Person> {
        private Exception mException;
        ProgressDialog dialog;

        public ProfileLoadAsyncTask() {
            mPersonProfile = null;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "", "Loading....");
        }

        @Override
        protected Person doInBackground(Void... params) {
            try {
                if (AccountUtils.getEmailAccount(getActivity())!=null) {
                    Log.d("email" , AccountUtils.getEmailAccount(getActivity()));
                    PresentItUtils.build(getActivity(), AccountUtils.getEmailAccount(getActivity()));
                    Person person = PresentItUtils.getPersonProfile();
                    return person;
                } else {
                    return null;
                }
            } catch (IOException e) {
                mException = e;
            } catch (PresentItException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person person) {
            dialog.dismiss();
            Log.d(TAG, "onpostexecute entered");
            if(person!=null) {
                Log.d(TAG, "onpostexecute entered not null");
                txtUserName.setText(person.getFirstName() + " " + person.getLastName());
                txtEmailAddress.setText(person.getEmail());
                txtUfid.setText(person.getUfid());
            }
        }
    }
}
