package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button newButton = findViewById(R.id.schSubmitButton);
        final Button histButton = findViewById(R.id.schHomeButton);
        // Recent history TextViews
        final TextView recent1 = findViewById(R.id.recent1);
        final TextView recent2 = findViewById(R.id.recent2);
        final TextView recent3 = findViewById(R.id.recent3);

        // Create new user history object to hold data for current session
        UserHistoryData currentUsrData = new UserHistoryData();
        // Retrieve email from login activity and save it
        Intent extra = getIntent();

        System.out.println("email set");
        String email = extra.getExtras().getString("email");
        currentUsrData.setUserName(email);
        System.out.println(currentUsrData.getUserName());

        // Test data; uncomment to check if user history is working
        // Beware that leaving these uncommented will cause these to be
        // written to the history file each the home page is visited
        /*UserHistoryData.setUserName("mymail@email.com");
        UserHistoryData obj1 = new UserHistoryData();
        obj1.setVendor("Mart", 23);
        obj1.setOrderNo("abc123");
        obj1.setTime("10/10/21 8:00 PM");
        UserHistoryData obj2 = new UserHistoryData();
        obj2.setVendor("Retail store", 12);
        obj2.setOrderNo("123abc");
        obj2.setTime("11/11/21 5:00 PM");
        UserHistoryData obj3 = new UserHistoryData();
        obj3.setTime("12/12/21 10:00 AM");
        obj3.setVendor("Cool mart", 24);
        obj3.setVendor("Other mart", 11);
        obj3.setOrderNo("321cba");
        obj3.setOrderNo("cba321");

        obj1.checkout(this);
        obj2.checkout(this);
        System.out.println(obj3.checkout(this));
*/

        // Retrieve data from user history file
        ArrayList<UserHistoryData> history = currentUsrData.readHistory(this);
        // If user history file does not exist, there is no recent history to show
        if (history == null)
            recent1.setText("You have no recent deliveries.");

        // User history file exists and thus must contain at least one entry
        // Populate recent history TextViews
        else if (history.size() > 0) {
            // First TextView can be safely populated, other two need to be checked
            recent1.setText(history.get(history.size() - 1).displayTimeAndVendor());
            if (history.size() >= 2) {
                recent2.setText(history.get(history.size() - 2).displayTimeAndVendor());
                if (history.size() >= 3)
                    recent3.setText(history.get(history.size() - 3).displayTimeAndVendor());
            }
        }


        // When "New Delivery" button is pressed, go to vendor select screen
        // to start new delivery creation process
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, VendorSelectActivity.class);
                intent.putExtra("data", currentUsrData);
                startActivity(intent);
            }
        });

        // Go to full user history screen when pressing "View All" button
        ArrayList<UserHistoryData> finalHistory = history;
        histButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                // Pass user history data to user history screen if history exists
                if (history != null)
                    intent.putExtra("history", finalHistory);
                startActivity(intent);
            }
        });


    }
}