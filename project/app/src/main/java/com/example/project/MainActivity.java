package com.example.project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
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
    final String ip = "192.168.13.187";
    View storeView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupPage(null);
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

    @Override
    public void onBackPressed()
    {
	    if (storeView != null)
	    {
            MaterialToolbar materialToolbar = (MaterialToolbar)findViewById(R.id.topbarmenu);
            materialToolbar.removeView(materialToolbar.getChildAt(1));
            materialToolbar.addView(storeView);
            storeView = null;
	    }
    }


    // run process in background
    public void process_bg(final String targeturl, OnProcessedListener listener)
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
        process_bg(String.format("http://%s:8000/app.php?hospital&email=%s&password=%s", ip, useremail, userpassword),
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

            if (storeView == null) {
                MaterialToolbar materialToolbar = (MaterialToolbar)findViewById(R.id.topbarmenu);

                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout2);
                AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(MainActivity.this);

                // Set layout params
                AppBarLayout.LayoutParams p = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                autoCompleteTextView.setLayoutParams(p);
                autoCompleteTextView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);     // options are now visible

                // show hospital name
                AutoHospitals(autoCompleteTextView);

                storeView = materialToolbar.getChildAt(1);

                // remove view add new view
                materialToolbar.removeView(storeView);
                materialToolbar.addView(autoCompleteTextView, 1);
            }
    }

    private ArrayList<String> getHospitalNames() {
        BufferedReader reader = null;
        ArrayList<String> HospitalNames = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("hospitals")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                HospitalNames.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return HospitalNames;
    }

    private void AutoHospitals(AutoCompleteTextView autoCompleteTextView)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, getHospitalNames());
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
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
				ip, useremail, userpassword), new OnProcessedListener() {
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
							emergency_needed.add(new EmergencyNeeded(key, jObject.getString(key)));
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
								text1.setText(emergency_needed.get(position).hospitalname);
								text2.setText(emergency_needed.get(position).bloodgroup);
								return view;
							}
						};
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
        setNavigator2Home();

        Button submitButton = (Button) findViewById(R.id.changepassword);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = ((EditText) findViewById(R.id.oldpassword)).getText().toString();
                String newPassword = ((EditText) findViewById(R.id.newpassword)).getText().toString();

                process_bg(String.format("http://%s:8000/app.php?reset&email=%s&password=%s&newpassword=%s", ip, useremail, oldPassword, newPassword),
                        new OnProcessedListener() {
                            @Override
                            public void onProcessed(String result) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        userpassword = newPassword;
                                        HomePage(v);
                                    }
                                });
                            }
                        });
            }
        });
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

    private void DateDialogCreate(EditText dateEdit) {
        dateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    return;
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // create date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c1 = Calendar.getInstance();
                                c1.set(year, month+1, dayOfMonth);
                                if (!c1.before(c))
                                    dateEdit.setText(String.format("%04d-%02d-%02d", year, month+1, dayOfMonth));
                                else {
                                    dateEdit.setText(String.format("%04d-%02d-%02d",
                                            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
                                }
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void bookdonorsAppointment(View v) {
        setContentView(R.layout.donors_appointment);
        setNavigator2Home();

        DateDialogCreate((EditText) findViewById(R.id.submitdate));
        // show hospital name
        AutoHospitals((AutoCompleteTextView) findViewById(R.id.hospital));

        Button submitButton = (Button) findViewById(R.id.DonorAppointment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateofsubmit = ((EditText)findViewById(R.id.submitdate)).getText().toString();
                String hospitalname = ((AutoCompleteTextView)findViewById(R.id.hospital)).getText().toString();
                RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.whichbloodgroup);
                int idx = radioButtonGroup.indexOfChild(radioButtonGroup.findViewById(radioButtonGroup.getCheckedRadioButtonId()));
                String selectedtext = ((RadioButton) radioButtonGroup.getChildAt(idx)).getText().toString();

                process_bg(String.format("http://%s:8000/app.php?donor&email=%s&password=%s&dateofsubmit=%s&hospitalname=%s&bloodgroup=%s",
                        ip, useremail, userpassword, dateofsubmit, hospitalname, selectedtext), new OnProcessedListener() {

                    @Override
                    public void onProcessed(String result) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                                HomePage(v);
                            }
                        });
                    }
                });
            }
        });
    }



    public void recieverAppointment(View v) {
        setContentView(R.layout.receivers_appointment);
        setNavigator2Home();

        // create date dialog
        DateDialogCreate((EditText) findViewById(R.id.receivedate));

        // show hospital name
        AutoHospitals((AutoCompleteTextView) findViewById(R.id.hospital));

        Button submitButton = (Button) findViewById(R.id.ReceiverAppointment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateofreceive = ((EditText)findViewById(R.id.receivedate)).getText().toString();
                String hospitalname = ((AutoCompleteTextView)findViewById(R.id.hospital)).getText().toString();
                RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.whichbloodgroup);
                int idx = radioButtonGroup.indexOfChild(radioButtonGroup.findViewById(radioButtonGroup.getCheckedRadioButtonId()));
                String selectedtext = ((RadioButton) radioButtonGroup.getChildAt(idx)).getText().toString();

                process_bg(String.format("http://%s:8000/app.php?receiver&email=%s&password=%s&dateofreceive=%s&hospitalname=%s&bloodgroup=%s",
                        ip, useremail, userpassword, dateofreceive, hospitalname, selectedtext), new OnProcessedListener() {

                    @Override
                    public void onProcessed(String result) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                                HomePage(v);
                            }
                        });
                    }
                });
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
        else if (itemid == R.id.search)
        {
            DrawerLayout dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            dLayout.closeDrawer(GravityCompat.START);
            searchthings(null);
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

                                HomePage(v);
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

