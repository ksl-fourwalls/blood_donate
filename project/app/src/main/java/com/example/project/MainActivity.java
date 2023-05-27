package com.example.project;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import org.w3c.dom.Text;

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

   //  https://guides.codepath.com/android/Working-with-the-EditText
    public void HomePage() {
        setContentView(R.layout.side_bar);
        MaterialToolbar topbarmenu = (MaterialToolbar) findViewById(R.id.topbarmenu);
        topbarmenu.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                dLayout.openDrawer(GravityCompat.START);
            }
        });


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