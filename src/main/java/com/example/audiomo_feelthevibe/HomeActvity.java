package com.example.audiomo_feelthevibe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.audiomo_feelthevibe.Model.UploadSong;

public class HomeActvity extends AppCompatActivity {
ImageView PopMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_actvity);

        PopMusic=findViewById(R.id.popmusic);

        PopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActvity.this, Uploaded_songs.class));
            }
        });

    }
}