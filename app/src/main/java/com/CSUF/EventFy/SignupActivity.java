package com.CSUF.EventFy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

;

public class SignupActivity extends AppCompatActivity implements OnDateSetListener {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_DOB) TextView _dobtext;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    private boolean isEmail;

    ProgressDialog progressDialog=null;
    public static final String DATEPICKER_TAG = "datepicker";
public  final  senddata senddataObj = new senddata(true);


@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(SignupActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        _dobtext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1910, calendar.get(Calendar.YEAR) - 10);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }

        });

    }


    private boolean isVibrate() {
        return false;
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    private boolean isCloseOnSingleTapMinute() {
        return false;
    }

    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        _dobtext.setText(year + "-" + month + "-" + day);
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }


        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String dob =  _dobtext.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        try {
                            String result = senddataObj.execute(_emailText.getText().toString(), _passwordText.getText().toString()).get();

                            if (result != null && result.equals("Success"))
                                onSignupSuccess();
                            else {
                                onSignupFailed();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        // onSignupFailed();

                    }
                    }, 500);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);



        //  String str_lastname = json.getString("last_name");

        Intent intent = new Intent(SignupActivity.this, VerifyAccount.class);
        intent.putExtra("userId",_emailText.getText().toString());
        intent.putExtra("uassword",_passwordText.getText().toString());
        intent.putExtra("userName", _nameText.getText().toString());
        intent.putExtra("DOB",_dobtext.getText().toString());
        intent.putExtra("isEmail",isEmail);

        progressDialog.dismiss();
        startActivity(intent);

     //   setResult(RESULT_OK, null);
   //     finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String dob = _dobtext.getText().toString();

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            isEmail = true;
        }else{
             isEmail = false;
        }

        if (name.isEmpty() || name.length() < 3 ) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (dob.isEmpty()){
            _dobtext.setError("Enter a valid Date of Birth");
        valid = false;}
        else
        { _dobtext.setError(null);
        }


        if (email.isEmpty()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    public  class senddata extends AsyncTask<String, String, String>
    {

        public senddata(String email, String password, String userName)
        {

        }

        public senddata(boolean b) {
            super();

        }
        @Override
        protected String doInBackground(String... strings) {

            HttpResponse resp = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(
                    "http://192.168.0.5:8080/EventFy/webapi/signup/checkusernamevalid");
            post.setHeader("content-type", "application/json");

            progressDialog.setProgress(0);


            JSONObject dato = new JSONObject();
            try {

                dato.put("username", strings[0]);

                dato.put("password", strings[1]);

               // dato.put("userName", strings[2]);

              //  dato.put("DOB", strings[3]);


                Log.e("json to send :: ", ""+dato);
                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);
                resp = httpClient.execute(post);

                String result = EntityUtils.toString(resp.getEntity());

                Log.e("result in sign up :: ", ""+result);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();
        }
    }
}