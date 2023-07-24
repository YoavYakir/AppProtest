package com.example.approtest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.approtest.R;
import com.google.android.material.textfield.TextInputEditText;

public class NewEventActivity extends AppCompatActivity {

    TextInputEditText editTextName, editTextLocation, editTextDate;
    Button buttonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        editTextName = (TextInputEditText) findViewById(R.id.event_name);
        editTextLocation = (TextInputEditText) findViewById(R.id.event_location);
        editTextDate = (TextInputEditText) findViewById(R.id.event_date);
        buttonRegister = (Button) findViewById(R.id.create_event_button);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String name = String.valueOf(editTextName.getText()).trim();
                 String location = String.valueOf(editTextLocation.getText()).trim();
                 String date = String.valueOf(editTextDate.getText()).trim();

                 if (name.isEmpty() || location.isEmpty() || date.isEmpty()) {
                     Toast.makeText(NewEventActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);

                     if (TextUtils.isEmpty(name)) {
                         editTextName.setError("Name is required");
                         editTextName.requestFocus();
                     }

                     if (TextUtils.isEmpty(location)) {
                         editTextLocation.setError("Location is required");
                         editTextLocation.requestFocus();
                     }

                     if (TextUtils.isEmpty(date)) {
                         editTextDate.setError("Date is required");
                         editTextDate.requestFocus();
                     }
                 }
                 else {
                     Toast.makeText(NewEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
                    finish();
                }
             }
        }
        );

    }

}