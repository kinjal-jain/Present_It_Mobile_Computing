package com.usergo.presentit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PresentationActivity extends AppCompatActivity implements Button.OnClickListener{


    Button left;
    Button right;

    TextView txtFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        left = (Button) findViewById(R.id.btnLeftNav);
        left.setOnClickListener(this);
        right = (Button) findViewById(R.id.btnRightNav);
        right.setOnClickListener(this);

        String courseName = getIntent().getExtras().getString("COURSE_NAME");
        String fileName = getIntent().getExtras().getString("FILE_NAME");

        txtFileName = (TextView) findViewById(R.id.txtFileName);
        if(fileName != null) {
            txtFileName.setText(fileName);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLeftNav:
                Toast.makeText(this, "Left button clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnRightNav:
                Toast.makeText(this, "Right button clicked", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
