package com.usergo.presentit;


import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.yet_another_test_1261.presentIt.model.Course;
import com.appspot.yet_another_test_1261.presentIt.model.FileInfo;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FileViewerActivity extends ListActivity {

    private static final String TAG = "FileViewerActivity";
    protected String pCourseWebSafeKey;
    protected List<FileInfo> pFiles;
    protected TextView tvCourseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);

        String courseID = getIntent().getExtras().getString("COURSE_ID");
        String courseName = getIntent().getExtras().getString("COURSE_NAME");
        pCourseWebSafeKey = getIntent().getExtras().getString("COURSE_WEB_SAFE_KEY");

        tvCourseTitle = (TextView) findViewById(R.id.mainText);
        tvCourseTitle.setText(courseID + " - " + courseName);

        new FilesLoadAsyncTask().execute();


        Log.d(TAG, "course name - " + courseName + " websafekey - " + pCourseWebSafeKey);
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Log.d(TAG, "on list item click file" + position);
        super.onListItemClick(list, view, position, id);
        FileInfo selectedFile = (FileInfo) getListView().getItemAtPosition(position);
        Log.d(TAG, "on list item click file" + selectedFile.getName());

        Intent intent = new Intent(this, PresentationActivity.class);
        intent.putExtra("FILE_NAME", selectedFile.getName());
        startActivity(intent);
    }

    class FilesLoadAsyncTask extends AsyncTask<Void, List<FileInfo>, List<FileInfo>> {
        private Exception mException;
        ProgressDialog dialog;

        public FilesLoadAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(FileViewerActivity.this, "", "Loading....");
        }

        @Override
        protected List<FileInfo> doInBackground(Void... params) {
            List<FileInfo> files = null;
            try {
                files = PresentItUtils.getFiles(pCourseWebSafeKey);
            } catch (PresentItException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return files;
        }

        @Override
        protected void onPostExecute(List<FileInfo> files) {
            Log.d(TAG, "onpostexecute entered");
            Log.d(TAG, String.valueOf(files.size()));
            setListAdapter(new FileDataAdapter(FileViewerActivity.this, files));
            pFiles = files;
            dialog.dismiss();
        }
    }
}
