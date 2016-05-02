package com.usergo.presentit.helpers;

import android.content.Context;
import android.util.Log;

import com.appspot.yet_another_test_1261.presentIt.model.Course;
import com.appspot.yet_another_test_1261.presentIt.model.CourseCollection;
import com.appspot.yet_another_test_1261.presentIt.model.FileInfo;
import com.appspot.yet_another_test_1261.presentIt.model.HelloClass;
import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.usergo.presentit.Constants;

import java.io.IOException;
import java.util.List;

/**
 * Created by sruth on 4/16/2016.
 */
public class PresentItUtils {

    private final static String TAG = "PresentItUtils";

    private static com.appspot.yet_another_test_1261.presentIt.PresentIt pApiServiceHandler;

    public static String pemail;

    public static void build(Context context, String email) {
        pApiServiceHandler = buildServiceHandler(context, email);
        pemail = email;
    }

    public static com.appspot.yet_another_test_1261.presentIt.PresentIt buildServiceHandler(
            Context context, String email) {
        Log.d(TAG,"Building service handler");
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                context, Constants.AUDIENCE);
        credential.setSelectedAccountName(email);

        com.appspot.yet_another_test_1261.presentIt.PresentIt.Builder builder
                = new com.appspot.yet_another_test_1261.presentIt.PresentIt.Builder(
                Constants.HTTP_TRANSPORT,
                Constants.JSON_FACTORY, credential);
        builder.setApplicationName("yet-another-test-1261");
        return builder.build();
    }

    public static Person getPersonProfile() throws PresentItException, IOException {
        if (null == pApiServiceHandler) {
            Log.e(TAG, "getPersonProfile(): no service handler was built");
            throw new PresentItException();
        }
        Log.d(TAG, pApiServiceHandler.getRootUrl());

        Log.d(TAG, "pemail inside-" + pemail);
        com.appspot.yet_another_test_1261.presentIt.PresentIt.GetPersonProfileAndroid getPersonProfileAndroid =
                pApiServiceHandler.getPersonProfileAndroid(pemail);
        Person person = getPersonProfileAndroid.execute();
        Log.d(TAG, "person profile name-" + person.getFirstName());
        return person;
    }

    public static HelloClass saysHello() throws PresentItException, IOException {
        if (null == pApiServiceHandler) {
            Log.e(TAG, "getPersonProfile(): no service handler was built");
            throw new PresentItException();
        }
        //pApiServiceHandler.
        Log.d(TAG, pApiServiceHandler.getRootUrl());
        Log.d(TAG, "person profile name before-");
        com.appspot.yet_another_test_1261.presentIt.PresentIt.JustHelloWithTwoNames sayingHello =
                pApiServiceHandler.justHelloWithTwoNames("sruthi", "anirudh");
        HelloClass hc = sayingHello.execute();
        Log.d(TAG, "person profile name-" + hc.getMessage());
        return hc;
    }

    public static List<Course> getCoursesToAttend() throws PresentItException, IOException {
        if (null == pApiServiceHandler) {
            Log.e(TAG, "getCoursesToAttend(): no service handler was built");
            throw new PresentItException();
        }
        Log.d(TAG, "pemail inside-"+pemail);
        Log.d(TAG, "executing getCoursesToAttend()");

        com.appspot.yet_another_test_1261.presentIt.PresentIt.GetCoursesCreatedAndroid getCoursesCreatedAndroid =
                pApiServiceHandler.getCoursesCreatedAndroid(pemail);
        List<Course> courses = getCoursesCreatedAndroid.execute().getItems();
        Log.d(TAG, "person profile name-" + courses.size());
        return courses;
    }

    public static List<FileInfo> getFiles(String webSafeKey) throws PresentItException, IOException {
        if (null == pApiServiceHandler) {
            Log.e(TAG, "getCoursesToAttend(): no service handler was built");
            throw new PresentItException();
        }
        Log.d(TAG, "pemail inside-"+pemail);
        Log.d(TAG, "executing getFiles()");

        com.appspot.yet_another_test_1261.presentIt.PresentIt.GetFilesOfCourse getCoursesCreatedAndroid =
                pApiServiceHandler.getFilesOfCourse(webSafeKey);
        List<FileInfo> files = getCoursesCreatedAndroid.execute().getItems();
        Log.d(TAG, "person profile name-" + files.size());
        return files;
    }
}
