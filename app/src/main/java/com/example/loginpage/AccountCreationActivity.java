// Account creation screen activity
package com.example.loginpage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AccountCreationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        // https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        final EditText firstTextField = findViewById(R.id.firstTextField);
        final EditText lastTextField = findViewById(R.id.lastTextField);
        final EditText pswTextField = findViewById(R.id.pswTextField);
        final EditText ageTextField = findViewById(R.id.ageTextField);
        final EditText idTextField = findViewById(R.id.idTextField);
        final Spinner quesMenu = findViewById(R.id.quesMenu);
        final EditText ansTextField = findViewById(R.id.anTextField);
        final Button submitButton = findViewById(R.id.submitButton);
        final TextView security = findViewById(R.id.security);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.questions_menu, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        quesMenu.setAdapter(adapter);

        // Controls actions taken when "Create Account" button is pressed
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Error dialog for invalid input
                AlertDialog alertDialog = new AlertDialog.Builder(AccountCreationActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                String first = firstTextField.getText().toString();
                String last = lastTextField.getText().toString();
                String password = pswTextField.getText().toString();
                String id = idTextField.getText().toString();
                String answer = ansTextField.getText().toString();
                // Check that all fields have been filled out
                if(first.matches("")
                        || last.matches("")
                        || ageTextField.getText().toString().matches("")
                        || password.matches("")
                        || id.matches("")
                        || answer.matches("")){
                    alertDialog.setMessage("All fields must be filled out.");

                    alertDialog.show();

                }
                // Check that age is at least 18
                else if(Integer.parseInt(ageTextField.getText().toString()) < 18){
                    alertDialog.setMessage("User must be at least 18 years old.");

                    alertDialog.show();

                }
                // Check that an email was entered
                //https://howtodoinjava.com/java/regex/java-regex-validate-email-address/ simple regex email validation
                else if(!idTextField.getText().toString().matches("^[A-Za-z0-9+_.-]+@(.+)$")){
                    alertDialog.setMessage("Please enter a valid email.");

                    alertDialog.show();
                }
                // Check that password is minimum length or more
                else if(pswTextField.getText().toString().length() < 8){
                    alertDialog.setMessage("Password must be at least 8 characters long.");

                    alertDialog.show();
                }
                // If everything is valid, send account information to account database
                else {
                    try {
                        Connection conn = null;
                        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); //created a god user for this work
                        int age = Integer.parseInt(ageTextField.getText().toString());
                        String email = "'" + Helper.getHashString(id) + "'";
                        String pass = "'" + Helper.getHashString(password) + "'";
                        String ques ="'" + quesMenu.getSelectedItem().toString() + "'";
                        PreparedStatement statement = (PreparedStatement) conn.prepareStatement("INSERT INTO accounts (usrEmail, usrPswd, usrQues, usrAns, usrFirst, usrLast, age) VALUES (" + email + ", " + pass + ", " + ques + ", '" + answer + "', '" + first + "', '" + last + "', " + age + ");");
                        int test = statement.executeUpdate();
                        if(test > 0){
                            // Go back to login screen after account creation
                            Intent intent = new Intent(AccountCreationActivity.this, MainActivity.class);
                            startActivity(intent);
                            security.setText("success");
                        }else{
                            security.setText("failure");
                        }


                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}