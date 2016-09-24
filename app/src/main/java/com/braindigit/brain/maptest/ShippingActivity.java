package com.braindigit.brain.maptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.brain.test.R;

/**
 * Created by brain on 9/22/16.
 */
public class ShippingActivity extends AppCompatActivity {
    private Button btnOpenMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        btnOpenMap = (Button) findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(ShippingActivity.this,MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
    }
}
