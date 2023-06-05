package com.example.project;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// connect to sqlite https://www.c-sharpcorner.com/UploadFile/88b6e5/sqlitedatabase-connectivity/
// https://stackoverflow.com/questions/24742230/keep-scrollview-scroll-position-for-dynamic-content
public class MainActivity extends AppCompatActivity  {

    // Create some member variables for the ExecutorService
    // and for the Handler that will update the UI from the main thread
    final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    final Handler mHandler = new Handler(Looper.getMainLooper());

    String useremail = null, userpassword = null, username = null, userphoneno = null;
    final String ip = "192.168.1.3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupPage(null);
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


    // run process in background
    public void process_bg(final userurl, OnProcessedListener listener)
    {
        Runnable backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                try {
			// Perform your background operation(s) and set the result(s)
		    URL url = new URL(targeturl);
		    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		    urlConnection.setRequestMethod("GET");
                    String result = readStream(new BufferedInputStream(urlConnection.getInputStream()));;
                    // Use the interface to pass along the result
                    listener.onProcessed(result);
                }
                catch (Exception e)
                {
                    listener.onProcessed(e.toString());
                }
            }
        };
        mExecutor.execute(backgroundRunnable);
    }


    public void available_bloodgroup(View v) {
        setContentView(R.layout.available_bloodgroup);
        setNavigator2Home();
	process_bg(String.format("http://%s:8000/app.php?hospital&email=%s&password=%s", 
				ip, useremail, userpassword),
			new OnProcessedListener() {
				@Override
				public void onProcessed(String result) {
					// Use the handler so we're not trying to update the UI from the bg thread
					mHandler.post(new Runnable() {
						@Override
						public void run() {

							if (result.substring(0, 4).equals("true")) {
								try {
									class BloodGroupData {
										public final String BloodGroup, count;

										public BloodGroupData(String BloodGroup, int count)
										{
											this.BloodGroup = BloodGroup;
											this.count = String.valueOf(count);
										}
									}

									ListView listView = (ListView)findViewById(R.id.emergencylist);
									JSONObject jObject = new JSONObject(result.substring(5));
									Iterator<String> keys = jObject.keys();
									ArrayList<BloodGroupData> bloodGroupData = new ArrayList<>();

									while (keys.hasNext()) {
										String key = keys.next();
										bloodGroupData.add(new BloodGroupData(key, Integer.parseInt(jObject.getString(key))));
									}

									final ArrayAdapter listadapter = new ArrayAdapter(MainActivity.this,
											android.R.layout.simple_list_item_2, android.R.id.text1, bloodGroupData) {
										@Override
										public View getView (int position, View convertView, ViewGroup parent) {
											View view = super.getView(position, convertView, parent);
											TextView text1 = (TextView) view.findViewById(android.R.id.text1);
											TextView text2 = (TextView) view.findViewById(android.R.id.text2);

											text1.setText(bloodGroupData.get(position).BloodGroup);
											text2.setText(bloodGroupData.get(position).count);

											return view;
										}
									};
									listView.setAdapter(listadapter);

								}catch (JSONException ignored) {}
							}
						}
					});
				}
	});
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
                HomePage(v);
            }
        });
    }



    // emergency page
    public void emergencyPage(View v) {
        setContentView(R.layout.emergency_needed);
        setNavigator2Home();

	process_bg(String.format("http://%s:8000/app.php?emergency&email=%s&password=%s", 
				useremail, userpassword), new OnProcessedListener() {
            @Override
            public void onProcessed(String result) {
                // Use the handler so we're not trying to update the UI from the bg thread
		    mHandler.post(new Runnable() {
			    @Override
			    public void run() {
				    class EmergencyNeeded {
					    final String hospitalname, bloodgroup;
					    public EmergencyNeeded(String hospitalname, String bloodgroup)
					    {
						    this.hospitalname = hospitalname;
						    this.bloodgroup = bloodgroup;
					    }
				    }

				    if (result.substring(0, 4).equals("true")) 
				    {
				        try {
						JSONObject jObject = new JSONObject(result.substring(5));
						Iterator<String> keys = jObject.keys();
						ArrayList<EmergencyNeeded> emergency_needed = new ArrayList<>();

						while (keys.hasNext()) {
							String key = keys.next();
							emergency_needed.add(new EmergencyNeeded(key, Integer.parseInt(jObject.getString(key))));
						}

						// parse two things first hospital name and blood group
						ListView listView = (ListView)findViewById(R.id.emergencylist);
						final ArrayAdapter listadapter = new ArrayAdapter(MainActivity.this,
								android.R.layout.simple_list_item_2, android.R.id.text1, emergency_needed) {
							@Override
							public View getView (int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								TextView text1 = (TextView) view.findViewById(android.R.id.text1);
								TextView text2 = (TextView) view.findViewById(android.R.id.text2);

								// set hospital name and bloodgroup as listfield
								text1.setText(bloodgroupdata.get(position).hospitalname);
								text2.setText(bloodgroupdata.get(position).bloodgroup);
								return view;
							}
						}
						listView.setAdapter(listadapter);
					}
					catch (Exception e) { }
				    }
			    } 
		    });
	    }
	});
    }



    protected void ChangePassword() {
        setContentView(R.layout.reset_password);

    }

   //  https://guides.codepath.com/android/Working-with-the-EditText
    public void HomePage(View v) {
        setContentView(R.layout.side_bar);

        // update text view values
        TextView usernameTextV = (TextView) findViewById(R.id.username);
        usernameTextV.setText(username);
        TextView userphonenoTextV = (TextView) findViewById(R.id.userphoneno) ;
        userphonenoTextV.setText(userphoneno);
        TextView useremailTextV = (TextView) findViewById(R.id.useremail);
        useremailTextV.setText(useremail);



        MaterialToolbar topbarmenu = (MaterialToolbar) findViewById(R.id.topbarmenu);
        topbarmenu.setTitle(username);
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

	process_bg(String.format("http://%s:8000/app.php?donor&email=%s&password=%s", 
				ip, useremail, userpassword), new OnProcessedListener() {

		@Override
		public void onProcessed(String result) {
			mHandler.post(new Runnable() {
				// String Date = ((EditText) findViewById(R.id.))
			})
		}
	});
    }

    public void recieverAppointment(View v) {
        setContentView(R.layout.receivers_appointment);
        setNavigator2Home();

	process_bg(String.format("http://%s:8000/app.php?receiver&email=%s&password=%s", 
				ip, useremail, userpassword), new OnProcessedListener() {

		@Override
		public void onProcessed(String result) {
			mHandler.post(new Runnable() {
				// String Date = ((EditText) findViewById(R.id.))
			})
		}
	});
    }

    public void showMsgDirectMenuXml(MenuItem item) {
        final int itemid = item.getItemId();
        if (itemid ==  R.id.changepassword) {
            ChangePassword();
        }
        else if (itemid == R.id.logout)
        {
            useremail = null;
            userpassword = null;
            userphoneno = null;
            username = null;

            loginTextView(null);
        }
    }




    // call login page
    public void loginTextView (View v) {
        if (useremail == null && userpassword == null)
            setContentView(R.layout.layout_login);

        Button loginbutton = (Button) findViewById(R.id.cirLoginButton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
                String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();

                if (email.equals("") || password.equals(""))
                {
                    Toast.makeText(MainActivity.this, "fill required details", Toast.LENGTH_LONG).show();
                    return;
                }

		process_bg(String.format("http://%s:8000/app.php?login&email=%s&password=%s", ip, email, password), 
				new OnProcessedListener() {
		    @Override
		    public void onProcessed(String result) {
			// Use the handler so we're not trying to update the UI from the bg thread
			mHandler.post(new Runnable() {
			    @Override
			    public void run() {
				if (result.substring(0, 4).equals("true")) {
				    try {
					JSONObject jObject = new JSONObject(result.substring(5));

					userphoneno = jObject.getString("phoneno");
					userpassword = jObject.getString("password");
					useremail = jObject.getString("email");
					username = jObject.getString("username");

				    } catch (JSONException ignored) {}
				    HomePage(v);
				}
			    }
			});
		    }
		});
            }
        });

    }


/*
    // If we're done with the ExecutorService, shut it down.
    // (If you want to re-use the ExecutorService,
    // make sure to shut it down whenever everything's completed
    // and you don't need it any more.)
                        if (finished) {
        mExecutor.shutdown();
    }
 */

        public void signupPage(View v) {
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.cirSignupButton);
        View.OnClickListener call_login_page = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TextName = ((EditText) findViewById(R.id.editTextName)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                String Email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
                String phoneNumber = ((EditText) findViewById(R.id.editTextMobile)).getText().toString();

                if (TextName.equals("") || password.equals("") || Email.equals("") || phoneNumber.equals("")) {
                    Toast.makeText(MainActivity.this, "fill required details", Toast.LENGTH_SHORT).show();
                    return;
                }


		process_bg(String.format("http://%s:8000/app.php?register&phoneno=%s&password=%s&email=%s&username=%s",
                                   ip, phoneNumber, password, Email, TextName),  new OnProcessedListener() {
		    @Override
		    public void onProcessed(String result) {
			// Use the handler so we're not trying to update the UI from the bg thread
			mHandler.post(new Runnable() {
			    @Override
			    public void run() {
				if (result.substring(0, 4).equals("true"))
				{
				    useremail = Email;
				    userpassword = password;
				    userphoneno = phoneNumber;
				    username = TextName;

				    loginTextView(null);
				}
			    }
			});
		    }
		});
            }
        };
        button.setOnClickListener(call_login_page);
    }
}

// interface for creating signal process
// signals are send through string
interface OnProcessedListener {
    public void onProcessed(String result);
}
