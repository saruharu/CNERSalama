package com.example.cnersalama;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    TextView markerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        markerTxt = findViewById(R.id.marker);
        String title = getIntent().getStringExtra("title");
        String snippet = getIntent().getStringExtra("snippet");
        markerTxt.setText(snippet);


    }
}


