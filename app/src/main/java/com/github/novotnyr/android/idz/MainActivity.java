package com.github.novotnyr.android.idz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView directionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if ("SHOW_ACTIVITY".equals(intent.getAction())) {
            Intent stopNavigationService = new Intent(this, NavigationService.class);
            stopService(stopNavigationService);
            return;
        }

        directionTextView = findViewById(R.id.directionTextView);
        directionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NavigationService.class);
                ContextCompat.startForegroundService(MainActivity.this, intent);
            }
        });
    }
}
