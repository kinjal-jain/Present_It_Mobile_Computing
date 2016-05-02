package com.usergo.presentit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.appspot.yet_another_test_1261.presentIt.model.FileInfo;

import java.util.List;

/**
 * Created by sruth on 4/20/2016.
 */
public class FileDataAdapter extends ArrayAdapter<FileInfo>{

    private static final String TAG = "CourseDataAdapter";
    private final Context pContext;

    public FileDataAdapter(Context context, List<FileInfo> files) {
        super(context, R.layout.file_row_layout, files);
        pContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) pContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        FileInfo file = getItem(position);
        Log.d(TAG, "entered");

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.file_row_layout, null);
            holder = new ViewHolder();
            holder.tvFileName = (TextView) convertView.findViewById(R.id.listText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvFileName.setText(file.getName());
        return convertView;
    }

    private class ViewHolder {
        TextView tvFileName;
    }
}
