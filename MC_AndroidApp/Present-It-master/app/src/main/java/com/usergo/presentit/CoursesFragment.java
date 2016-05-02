package com.usergo.presentit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.appspot.yet_another_test_1261.presentIt.model.Course;
import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sruth on 4/17/2016.
 */
public class CoursesFragment extends ListFragment {

    private static final String TAG = "ConferenceListFragment";

    private CourseDataAdapter pAdapter;
    protected List<Course> pCourses;

    public static CoursesFragment newInstance(Context context) {
        CoursesFragment f = new CoursesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        new CoursesLoadAsyncTask().execute();
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        Log.d(TAG, "ITEM AT"+position+"clicked");
        if(pCourses != null) {
            Course item = pCourses.get(position);
            Toast.makeText(getActivity(), item.getCourseName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), FileViewerActivity.class);
            intent.putExtra("COURSE_ID", item.getCourseId());
            intent.putExtra("COURSE_NAME", item.getCourseName());
            intent.putExtra("COURSE_WEB_SAFE_KEY", item.getWebsafeKey());
            startActivity(intent);
        }
    }

    class CoursesLoadAsyncTask extends AsyncTask<Void, List<Course>, List<Course>> {
        private Exception mException;
        ProgressDialog dialog;

        public CoursesLoadAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "", "Loading....");
        }

        @Override
        protected List<Course> doInBackground(Void... params) {
            try {
                if (AccountUtils.getEmailAccount(getActivity())!=null) {
                    Log.d("email", AccountUtils.getEmailAccount(getActivity()));
                    PresentItUtils.build(getActivity(), AccountUtils.getEmailAccount(getActivity()));
                    List<Course> courses = PresentItUtils.getCoursesToAttend();
                    return courses;
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
        protected void onPostExecute(List<Course> courses) {
            Log.d(TAG, "onpostexecute entered");
            setListAdapter(new CourseDataAdapter(getActivity(), courses));
            pCourses = courses;
            dialog.dismiss();
        }
    }
}
