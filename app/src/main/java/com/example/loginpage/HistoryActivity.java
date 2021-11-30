package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        final Button homeButton = findViewById(R.id.homeButton);
        final TextView usrIDLabel = findViewById(R.id.usrIDLabel);


        populateTable(usrIDLabel);

        // Go back to home page when pressing home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
                intent.putExtra("email", usrIDLabel.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void populateTable(TextView label) {
        TableLayout userHistoryArea = findViewById(R.id.userHistoryArea);
        int columns = 4;
        // Populates top row of table (which has column names)
        TableRow heading = new TableRow(HistoryActivity.this);
        TextView text1 = new TextView(HistoryActivity.this);
        text1.setText(" Vendor ");
        TextView text2 = new TextView(HistoryActivity.this);
        text2.setText(" Order # ");
        TextView text3 = new TextView(HistoryActivity.this);
        text3.setText(" Transaction # ");
        TextView text4 = new TextView(HistoryActivity.this);
        text4.setText(" Delivery Date/Time ");
        heading.addView(text1);
        heading.addView(text2);
        heading.addView(text3);
        heading.addView(text4);
        userHistoryArea.addView(heading);
        // If history exists, populate subsequent rows with it
        if (getIntent().getSerializableExtra("history") != null) {
            ArrayList<UserHistoryData> history = (ArrayList<UserHistoryData>) getIntent().getSerializableExtra("history");
            StringBuilder sb = new StringBuilder();
            label.setText(history.get(0).getUserName());
            for (int delivery = 0; delivery < history.size(); delivery++) {
                UserHistoryData pastDelivery = history.get(delivery);
                TableRow row = new TableRow(HistoryActivity.this);

                // Get vendors
                String[] vendorList = pastDelivery.getVendor();
                for (String vendor: vendorList) {
                    if (vendor != null)
                        sb.append(vendor + "\n");
                }
                TextView vendorView = new TextView(HistoryActivity.this);
                vendorView.setSingleLine(false);
                vendorView.setText(sb.toString());
                row.addView(vendorView);
                sb.setLength(0);

                // Get order numbers
                String[] orderList = pastDelivery.getOrderNo();
                for (String orderNo: orderList) {
                    if (orderNo != null)
                        sb.append(orderNo + "\n");
                }
                TextView orderView = new TextView(HistoryActivity.this);
                orderView.setSingleLine(false);
                orderView.setText(sb.toString());
                row.addView(orderView);
                sb.setLength(0);

                // Get transaction ID
                TextView transacView = new TextView(HistoryActivity.this);
                String tr = pastDelivery.getTransac() + "";
                transacView.setText(tr);
                row.addView(transacView);

                // Get date/time
                TextView timeView = new TextView(HistoryActivity.this);
                timeView.setText(pastDelivery.getTime());
                row.addView(timeView);

                // Add row to table
                userHistoryArea.addView(row);

            }

        }
    }
}