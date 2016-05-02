package com.usergo.presentit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.yet_another_test_1261.presentIt.model.Course;

import java.util.List;

/**
 * Created by sruth on 4/18/2016.
 */
public class CourseDataAdapter extends ArrayAdapter<Course> {

    private static final String TAG = "CourseDataAdapter";
    private final Context pContext;

    public CourseDataAdapter(Context context, List<Course> courses) {
        super(context, R.layout.listview_item, courses);
        this.pContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) pContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        Course courseObj = getItem(position);
        Log.d(TAG, "entered");

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.tvCourseID = (TextView) convertView.findViewById(R.id.tvCourseID);
            holder.tvCourseName = (TextView) convertView.findViewById(R.id.tvCourseName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCourseID.setText(courseObj.getCourseId());
        holder.tvCourseName.setText(courseObj.getCourseName());
        return convertView;
    }

    private class ViewHolder {
        TextView tvCourseID;
        TextView tvCourseName;
    }

    public void setData(List<Course> data) {
        clear();
        if (data != null) {
            for (Course item : data) {
                add(item);
            }
        }

    }
}
