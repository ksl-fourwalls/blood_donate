package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**

 import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
 */

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupPage();
    }

    public void HomePage() {
    }


    public void LoadLoginPage () {
        setContentView(R.layout.layout_login);

        Button loginbutton = (Button) findViewById(R.id.cirLoginButton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage();
            }
        });

        TextView donthaveaccount = (TextView) findViewById(R.id.notsignedup);
        donthaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupPage();
            }
        });
    }

    public void signupPage() {
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.cirSignupButton);
        View.OnClickListener call_login_page = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadLoginPage();
            }
        };
        button.setOnClickListener(call_login_page);

        // call login page
        TextView loginTextView = (TextView) findViewById(R.id.alreadysignedin);
        loginTextView.setOnClickListener(call_login_page);


    }
}