package com.example.loginpage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mysql.fabric.xmlrpc.base.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class SchedulerActivity extends AppCompatActivity {
    UserHistoryData currentUsrData;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);

        final RadioGroup timesSummary = findViewById(R.id.timesSummary);
        final Button submit = findViewById(R.id.schSubmitButton);
        final Button cancel = findViewById(R.id.schHomeButton);
        currentUsrData = (UserHistoryData) getIntent().getSerializableExtra("data");
        timesSummary.check(R.id.radioButton);
        //https://www.javatpoint.com/java-get-current-date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        // Get current time
        LocalDateTime now = LocalDateTime.now();
        Random rand = new Random();

        // Because external systems for calculating delivery time that this app relies
        // on do not exist, this app randomly generates four recommended delivery times
        // for the user to choose from

        // Iterate through radio buttons and populate them with random delivery times
        //https://stackoverflow.com/questions/38207879/android-statement-similar-to-for-each-for-radiogroup
        for(int i = 0; i < timesSummary.getChildCount(); i++){
            RadioButton radioButton =  (RadioButton) timesSummary.getChildAt(i);
            LocalDateTime newTimes = now.plusDays(i).plusHours(rand.nextInt(12) + 1);
            radioButton.setText(dtf.format(newTimes));
        }

        // Handler for submit button; gets selected time, saves it, and goes to checkout screen
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected button
                RadioButton selected = timesSummary.findViewById(timesSummary.getCheckedRadioButtonId());
                // Save the time
                currentUsrData.setTime(selected.getText().toString());
                // Go to checkout
                Intent intent = new Intent(SchedulerActivity.this, CheckoutActivity.class);
                intent.putExtra("data", currentUsrData);
                startActivity(intent);
            }
        });
    }
}