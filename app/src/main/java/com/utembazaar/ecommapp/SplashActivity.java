package com.utembazaar.ecommapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Set the splash screen layout

        // Find the ImageView in the splash screen layout
        logoImageView = findViewById(R.id.logo);

        // Load the image locally using Glide
        loadSplashImage();

        // Simulate a delay (e.g., 2 seconds)
        new Handler().postDelayed(() -> {
            // Navigate to the main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the splash activity
        }, 2000); // 2000 milliseconds = 2 seconds
    }

    private void loadSplashImage() {
        Glide.with(this)
                .load(R.drawable.splashlogo)
                .apply(new RequestOptions()
                        .override(800, 800) // Resize the image to a smaller resolution
                        .fitCenter() // Scale it to fit nicely within the screen
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(logoImageView);
    }
}
