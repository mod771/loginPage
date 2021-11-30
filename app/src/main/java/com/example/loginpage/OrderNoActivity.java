package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderNoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        // https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_no);

        final TextView vendorLabel = findViewById(R.id.vendorLabel);
        final EditText orderNo = findViewById(R.id.orderNoTextField);
        final Button verify = findViewById(R.id.schSubmitButton);
        final Button cancel = findViewById(R.id.schHomeButton);
        final TextView errorLabel = findViewById(R.id.errorLabel);
        //https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
        // Used to get vendor data from VendorSelectActivity
        UserHistoryData currentUsrData = (UserHistoryData) getIntent().getSerializableExtra("data");
        String[] vendorarr;
        int[] vendorIDarr;
        // Get vendors and their IDs
        vendorarr = currentUsrData.getVendor();
        vendorIDarr = currentUsrData.getVendorId();

        // Index keeps track of what vendor user is currently at
        // It needs to be final to be accessed by inner onClick class
        final int[] index = {0};

        // First vendor should appear as soon as order page is opened
        vendorLabel.setText(vendorarr[index[0]]);

        // Handler for "verify" button
        // Compares order number, email, and vendor information to what is stored in the
        // order number database and decides whether the order number is valid accordingly
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultSet result = null;
                try {
                    // Get order number entered by user
                    String orderNum = "'" + orderNo.getText().toString() + "'";
                    // Get user ID/email and hash it
                    String usrID = "'" + Helper.getHashString(currentUsrData.getUserName()) + "'";
                    // Get ID of current vendor
                    int vendorID = vendorIDarr[index[0]];
                    Connection conn = null;
                    Class.forName("com.mysql.jdbc.Driver");

                    conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); // created a god user for this work

                    // Query the order number database for an order number that matches the one entered,
                    // is associated with this user's account, and is associated with the correct vendor
                    PreparedStatement statement = (PreparedStatement) conn.prepareStatement("SELECT orderNo FROM ordernumbers WHERE orderNo = " + orderNum
                            + "AND usrEmail = " + usrID + " AND vendorID = " + vendorID);
                    result = statement.executeQuery();
                    // No results returned; order number entered is invalid
                    if (!result.isBeforeFirst()) {
                        // Inform user the order number is invalid and clear the text field
                        errorLabel.setText("Invalid order number");
                        orderNo.setText("");
                        System.out.println("invalid");
                    }
                    // Valid order number entered; move on to next vendor
                    else {
                        index[0]++;
                        // If the next vendor isn't null/nonexistent and the index is not invalid,
                        // (in other words, if there are still vendors to enter order numbers for)
                        if (vendorarr[index[0]] != null || index[0] < vendorIDarr.length) {
                            // display next vendor to prompt the user for its order number
                            vendorLabel.setText(vendorarr[index[0]]);
                            // and clear text field to make way for next order number
                            orderNo.setText("");
                            errorLabel.setText("Valid order number");
                            System.out.println("valid");
                        }
                        // Otherwise, vendor list has been exhausted; order numbers have been
                        // verified for all vendors
                        else {
                            // Go to scheduler screen since order number verification is done
                            Intent intent = new Intent(OrderNoActivity.this, SchedulerActivity.class);
                            intent.putExtra("data", currentUsrData);
                            startActivity(intent);
                        }
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        // Go back to home screen if user presses "cancel" button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderNoActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}