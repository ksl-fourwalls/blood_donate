package com.example.project;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.util.EventLog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**

 import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
 */

// connect to sqlite https://www.c-sharpcorner.com/UploadFile/88b6e5/sqlitedatabase-connectivity/


// https://stackoverflow.com/questions/24742230/keep-scrollview-scroll-position-for-dynamic-content
public class MainActivity extends AppCompatActivity  {

    final String url = "jdbc:mysql://192.168.xx.xx:3306/hospital";
    // Create some member variables for the ExecutorService
// and for the Handler that will update the UI from the main thread
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        signupPage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Override onQueryTextSubmit method which is call when submit query is searched
            @Override
            public boolean onQueryTextSubmit(String query) {
                // If the list contains the search query than filter the adapter
                // using the filter method with the query as its argument
                /*
                if (list.contains(query)) {
                    adapter.getFilter().filter(query);
                } else {
                }
*/
                    // Search query not found in List View
                Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_LONG).show();
                return false;
            }

            // This method is overridden to filter the adapter according
            // to a search query when the user is typing search
            @Override
            public boolean onQueryTextChange(String newText) {
               // adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    public void available_bloodgroup(View v) {
        setContentView(R.layout.available_bloodgroup);
        setNavigator2Home();


        ListView listView = (ListView)findViewById(R.id.emergencylist);
        ArrayList<String> names = new ArrayList<>();
        names.add("A+");
        names.add("B+");
        names.add("B-");
        names.add("X");
        names.add("O");



        final ArrayAdapter listadapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_2, android.R.id.text1, names) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(names.get(position));
                text2.setText(String.valueOf((int)(Math.random() * 100)));
                return view;
            }
        };
        listView.setAdapter(listadapter);

    }

    public void searchthings(MenuItem item) {
        if (item.getItemId() == R.id.search)
            setContentView(R.layout.search_hospital);
    }

    public void setNavigator2Home()
    {
        MaterialToolbar materialToolbar = (MaterialToolbar)findViewById(R.id.topbarmenu);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage();
            }
        });
    }

    public void emergencyPage(View v) {
        setContentView(R.layout.emergency_needed);
        setNavigator2Home();

        ListView listView = (ListView)findViewById(R.id.emergencylist);
        ArrayList<String> names = new ArrayList<>();
        names.add("test");
        for (int idx = 0; idx < 100; idx++)
        {
            names.add("fucker");
        }

        final ArrayAdapter listadapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_2, android.R.id.text1, names) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText("fucker");
                text2.setText("A+ A- B+ B-");
                return view;
            }
        };
        listView.setAdapter(listadapter);


    }



    protected void ChangePassword() {
        setContentView(R.layout.reset_password);

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

    public void bookdonorsAppointment(View v) {
        setContentView(R.layout.donors_appointment);
        setNavigator2Home();
    }

    public void recieverAppointment(View v) {
        setContentView(R.layout.receivers_appointment);
        setNavigator2Home();
    }

    public void showMsgDirectMenuXml(MenuItem item) {
        final int itemid = item.getItemId();
        if (itemid ==  R.id.changepassword) {
            ChangePassword();
        }
        else if (itemid == R.id.logout)
        {

        }

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

    public void processInBg(final String url, final boolean finished) {

        final OnProcessedListener listener = new OnProcessedListener() {
            @Override
            public void onProcessed(String result) {
                // Use the handler so we're not trying to update the UI from the bg thread
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI here
                        // ...
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

                        // If we're done with the ExecutorService, shut it down.
                        // (If you want to re-use the ExecutorService,
                        // make sure to shut it down whenever everything's completed
                        // and you don't need it any more.)
                        if (finished) {
                            mExecutor.shutdown();
                        }

                    }
                });
            }
        };
        Runnable backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                // Perform your background operation(s) and set the result(s)

                // Use the interface to pass along the result
                listener.onProcessed("Fucking");
            }
        };

        mExecutor.execute(backgroundRunnable);

    }
        public void signupPage() {
        setContentView(R.layout.activity_main);
        processInBg("thisstring", false);



// Create an interface to respond with the result after processing





        /*
          d  try {

        }
        catch (Exception e) {

        }
         */

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
/*
class Task implements Runnable {

    Context c;
    public Task(Context c) {
        this.c = c;
    }

    @Override
    public void run() {
        try {
            reference.get

            URL url = new URL("https://www.google.com");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String mystr = MainActivity.readStream(in);
            Toast.makeText(MainActivity.getContext(), mystr, Toast.LENGTH_LONG).show();


        }   catch(Exception e) {
            Toast.makeText(this.c, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

 */

interface OnProcessedListener {
    public void onProcessed(String result);
}