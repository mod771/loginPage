// Login screen activity
package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity {

    boolean validated = false;
    String answer = "";
    String validEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Uncomment the following to skip past login and go straight to the home page
        // activity for testing purposes
        //Intent intent = new Intent(MainActivity.this, SchedulerActivity.class);
        // Pass validated email/usrID to home page so it can be used there
        //intent.putExtra("email", "mymail@email.com");
       // startActivity(intent);

        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        // https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText emailTextField = findViewById(R.id.emailTextField);
        final EditText pswdTextField = findViewById(R.id.pswdTextField);
        final Button submitButton = findViewById(R.id.submitButton);
        final Button newAccountButton = findViewById(R.id.newAccountButton);
        final TextView quesLabel = findViewById(R.id.quesLabel);
        final EditText ansTextField = findViewById(R.id.ansTextField);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultSet result = null;

                try {
                    // Valid email/password have not yet been entered
                    if(!validated) {
                        String email = "'" + Helper.getHashString(emailTextField.getText().toString()) + "'";
                        String pass = "'" + Helper.getHashString(pswdTextField.getText().toString()) + "'";


                        Connection conn = null;
                        Class.forName("com.mysql.jdbc.Driver");

                        conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); //created a god user for this work

                        quesLabel.setText("top");
                        // Query the account database to see if the email and password entered match
                        PreparedStatement statement = (PreparedStatement) conn.prepareStatement("SELECT usrEmail, usrPswd, usrQues, usrAns FROM accounts WHERE usrEmail = " + email + " AND usrPswd = " + pass);
                        result = statement.executeQuery();
                        // No results returned; email/password entered do not exist
                        // in database, so email/password entered are invalid
                        //https://stackoverflow.com/questions/867194/java-resultset-how-to-check-if-there-are-any-results
                        if (!result.isBeforeFirst()) {
                            quesLabel.setText("Invalid credentials");
                        }
                        // Valid email/password entered
                        // Present user with security question
                        else {
                            validated = true;
                            validEmail = emailTextField.getText().toString();
                            result.next();
                            String question = result.getString("usrQues");
                            quesLabel.setText(question);
                            answer = result.getString("usrAns");
                        }
                    }
                    // Valid email/password have been entered
                    // Check if correct answer to security question has been entered
                    else {
                        if(answer.equalsIgnoreCase(ansTextField.getText().toString())) {
                            // quesLabel.setText("Success");
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            // Pass validated email/usrID to home page so it can be used there
                            System.out.println(validEmail);
                            intent.putExtra("email", validEmail);
                            startActivity(intent);
                        }
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    quesLabel.setText(e.toString());
                }
            }
        });
        // New account button pressed; go to account creation screen
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, AccountCreationActivity.class);
                startActivity(intent);
            }
        });
    }

}