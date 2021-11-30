package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {
    UserHistoryData currentUsrData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // email
        final TextView transacSummary1 = findViewById(R.id.recent1);
        // order number(s)
        final TextView transacSummary2 = findViewById(R.id.recent2);
        // vendor(s)
        final TextView transacSummary3 = findViewById(R.id.recent3);
        // delivery time
        final TextView transacSummary4 = findViewById(R.id.textView5);
        // confirm button
        final Button confirmButton = findViewById(R.id.schSubmitButton);
        // cancel button
        final Button cancelButton = findViewById(R.id.schHomeButton);

        // Dialog that shows up in response to button presses or errors
        AlertDialog alertDialog = new AlertDialog.Builder(CheckoutActivity.this).create();
        // Return to home screen when button is pressed
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            currentUsrData = (UserHistoryData) extra.getSerializable("data");
            String[] vendorArr = currentUsrData.getVendor();
            String[] orderArr = currentUsrData.getOrderNo();

            // display email
            transacSummary1.setText("User Name: " + currentUsrData.getUserName());

            // get order number string
            String orders = getString(orderArr);

            // display order numbers
            transacSummary2.setSingleLine(false);
            transacSummary2.setText("Order Number(s): " + orders);

            // get vendor string
            String vendors = getString(vendorArr);

            // display vendors
            transacSummary3.setSingleLine(false);
            transacSummary3.setText("Vendor(s): " + vendors);

            // display time
            transacSummary4.setText(currentUsrData.getTime());

            // do the checkout if confirm button is pressed
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // do the checkout and get the results
                    String output = currentUsrData.checkout(CheckoutActivity.this);
                    alertDialog.setTitle("Checkout");
                    // show results of checkout
                    alertDialog.setMessage(output);
                    alertDialog.show();
                }
            });

        }
        // Something went wrong; current user data was unavailable or lost
        else {
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Session data has been lost. Press OK to return to the home screen.");
            alertDialog.show();
        }
    }

    // Helper method for iterating through vendors and order numbers
    private static String getString(String[] array) {
        StringBuilder strB = new StringBuilder();
        for (String str: array) {
            if (str != null) {
                strB.append(str + " ");
            }
        }
        return strB.toString();
    }
}
