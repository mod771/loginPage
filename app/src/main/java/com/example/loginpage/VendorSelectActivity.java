package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VendorSelectActivity extends AppCompatActivity {
    // Keeps track of number of vendors that have been selected
    // Five vendors can be selected maximum
    int vendorsAdded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        // https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_select);
        final Spinner venSelection = findViewById(R.id.filterDropDown);
        final Button search = findViewById(R.id.schSubmitButton);
        final EditText searchField = findViewById(R.id.searchTextField);
        final LinearLayout resultLayout = findViewById(R.id.results);
        final Button add = findViewById(R.id.addButton);
        final TextView selected = findViewById(R.id.vendorsSelected);
        final Button cancel = findViewById(R.id.cancelButton);
        final Button submit = findViewById(R.id.schHomeButton);

        UserHistoryData currentUsrData = (UserHistoryData) getIntent().getSerializableExtra("data");
        // populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vendorSelection, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        venSelection.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ResultSet result = null;
                // clear out results section to make way for new search results
                resultLayout.removeAllViews();
                // If "All Vendors" is selected in the spinner, query the vendor database
                if(venSelection.getSelectedItem().toString().equals("All Vendors")){
                    try {
                        String name = searchField.getText().toString() ;
                        Connection conn = null;
                        Class.forName("com.mysql.jdbc.Driver");

                        conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); //created a god user for this work

                        // Search the vendor database by name with the user input
                        PreparedStatement statement = (PreparedStatement) conn.prepareStatement("SELECT vendorID, name, location FROM vendors WHERE LOWER(name) LIKE LOWER('%" + name + "%')");
                        result = statement.executeQuery();
                        // No results from the database; no vendors matching the search criteria were found
                        if (!result.isBeforeFirst()) {
                            TextView fail = new TextView(VendorSelectActivity.this);
                            fail.setText("Vendors not found");
                            resultLayout.addView(fail);
                        }
                        // The database has sent results/matching vendors
                        else {
                            // Populate search results area with a checkbox for each search result
                            while(result.next()){
                                CheckBox vendor = new CheckBox(VendorSelectActivity.this);
                                String boxString = result.getString("name") + " in " + result.getString("location");
                                // Save vendor ID
                                vendor.setTag(result.getString("vendorID"));
                                vendor.setText(boxString);
                                resultLayout.addView(vendor);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                // Other spinner item selected; search in user history
                else {
                    // Get user history
                    ArrayList<UserHistoryData> historyList = currentUsrData.readHistory(VendorSelectActivity.this);
                    //https://stackoverflow.com/questions/15422428/iterator-over-hashmap-in-java
                    Map<Integer, String> hash = new HashMap<>();
                    // User history is empty; no history to search through
                    if(historyList == null){
                        TextView fail = new TextView(VendorSelectActivity.this);
                        fail.setText("Past vendors not found");
                        resultLayout.addView(fail);
                    }
                    // User history exists
                    else{
                        // Iterate through each delivery in user history
                        // Get its associated vendors and vendor IDs
                        for(int i = 0; i < historyList.size(); i++){
                            UserHistoryData del = historyList.get(i);
                            String[] venarr = del.getVendor();
                            int[] venIDarr = del.getVendorId();
                            // Iterate through associated vendors
                            for(int j = 0; j < venarr.length; j++){
                                if(venarr[j] != null){
                                    // If a vendor matches the search criteria, save it and its ID
                                    if(venarr[j].contains(searchField.getText().toString())){
                                        // Saving in a HashMap eliminates duplicate results
                                        hash.put(venIDarr[j], venarr[j]);
                                    }
                                }
                            }

                        }
                        // Iterate through saved vendors/vendor IDs and populate search results
                        for(Integer key : hash.keySet()){
                            CheckBox vendor = new CheckBox(VendorSelectActivity.this);
                            vendor.setTag(key);
                            vendor.setText(hash.get(key));
                            resultLayout.addView(vendor);
                        }
                    }
                }
            }

        });
        // Add selected vendors to current session data when pressing the "add" button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the number of vendors added has not yet reached the maximum,
                if (vendorsAdded < 5) {
                    try {
                        // iterate through children of search results area/the checkboxes
                        // https://stackoverflow.com/questions/4809834/how-to-iterate-through-a-views-elements
                        for (int i = 0; i < resultLayout.getChildCount(); i++) {
                            CheckBox cb = (CheckBox) resultLayout.getChildAt(i);
                            // If checkbox is checked, extract vendor name and ID and save them
                            if (cb.isChecked()) {
                                // Get vendor name
                                String s = cb.getText().toString();
                                String ss = s.substring(0, s.indexOf(" in "));
                                System.out.println(ss);
                                // Get vendor ID
                                int tag = Integer.parseInt(String.valueOf(cb.getTag()));
                                System.out.println(tag);
                                // Save vendor name and ID
                                currentUsrData.setVendor(ss, tag);
                                // Display updated list of added vendors
                                selected.append(ss + "\n");
                                // Increment number of vendors selected
                                vendorsAdded++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If at least 1 vendor has been selected, go to order number selection screen
                if (vendorsAdded > 0) {
                    Intent intent = new Intent(VendorSelectActivity.this, OrderNoActivity.class);
                    intent.putExtra("data", currentUsrData);
                    startActivity(intent);
                }
            }
        });

        // Go back to home screen if "cancel" button is pressed
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorSelectActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}