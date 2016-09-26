package com.braindigit.brain.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brain.test.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by brain on 9/22/16.
 */
public class ShippingActivity extends AppCompatActivity {
    private Button btnOpenMap;
    Toolbar mToolBar;
    EditText etFirstName, etLastName, etEmailAddress, etAddress, etMobile, etPhone, etTownCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        btnOpenMap = (Button) findViewById(R.id.btnOpenMap);
        etFirstName = (EditText) findViewById(R.id.firstName);
        etLastName = (EditText) findViewById(R.id.lastName);
        etEmailAddress = (EditText) findViewById(R.id.emailAddress);
        etAddress = (EditText) findViewById(R.id.address);
        etMobile = (EditText) findViewById(R.id.mobile);
        etPhone = (EditText) findViewById(R.id.phone);
        etTownCity = (EditText) findViewById(R.id.townCity);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle("SHIPPING INFORMATION");
        }

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(ShippingActivity.this, MainActivity.class);
                startActivityForResult(mainActivityIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                etAddress.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String emailAddress = etEmailAddress.getText().toString();
            String address = etAddress.getText().toString();
            String mobile = etMobile.getText().toString();
            String phone = etPhone.getText().toString();
            String townCity = etTownCity.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || emailAddress.isEmpty()
                    || address.isEmpty() || mobile.isEmpty() || phone.isEmpty() || townCity.isEmpty()) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cant_be_empty), Toast.LENGTH_LONG).show();

            } else if (!validateEmail(emailAddress)) {

                etEmailAddress.setError("Insert valid email address");
                etEmailAddress.requestFocus();
                etEmailAddress.setText("");

            }else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
