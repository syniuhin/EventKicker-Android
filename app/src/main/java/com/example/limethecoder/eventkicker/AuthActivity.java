package com.example.limethecoder.eventkicker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import com.example.limethecoder.eventkicker.ServiceManager.MyApiEndpointInterface;
import com.example.limethecoder.eventkicker.net.ApiResponse;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;


public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView email;
    AutoCompleteTextView name;
    EditText password;
    EditText repassword;
    Button signup;
    User user;
    public static final int RESULT_FAILED = 77;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        signup = (Button)findViewById(R.id.signup_btn);
        password = (EditText)findViewById(R.id.password_auth);
        repassword = (EditText)findViewById(R.id.repassword);
        email = (AutoCompleteTextView)findViewById(R.id.email_auth);
        name = (AutoCompleteTextView)findViewById(R.id.user_name);

        signup.setOnClickListener(this);
    }

    public boolean checkData() {
        boolean is_correct = true;

        String emailData = email.getText().toString();
        String passwordData = password.getText().toString();
        String repasswordData = repassword.getText().toString();

        name.setError(null);
        email.setError(null);
        password.setError(null);

        if(name.getText().toString().isEmpty())
            name.setError("Enter your name");

        if (emailData.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailData).matches()) {
            email.setError("Invalid email adress");
            is_correct = false;
        }

        if (passwordData.isEmpty() || passwordData.length() < 6) {
            password.setError("Password must have at least 6 characters");
            is_correct = false;
        }

        if (repasswordData.isEmpty() || repasswordData.length() < 6) {
            repassword.setError("Password must have at least 6 characters");
            is_correct = false;
        }

        if(!passwordData.isEmpty() && !repasswordData.isEmpty() && !passwordData.equals(repasswordData)){
            repassword.setError("Password mismatch");
            is_correct = false;
        }

        return is_correct;
    }

    @Override
    public void onClick(View v) {
        if (!checkData()) {
            Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(AuthActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String emailData = email.getText().toString();
        String passwordData = password.getText().toString();
        String nameData = name.getText().toString();

        // TODO: Implement authentication logic here.

        MyApiEndpointInterface apiService = ServiceManager.newService();
        User new_user = new User(nameData, emailData, passwordData);
        Call<ApiResponse<User>> call = apiService.createUser(new_user);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(retrofit.Response<ApiResponse<User>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if(response.body().success)
                    setResult(RESULT_OK);
                else
                    setResult(RESULT_FAILED);
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Log.e("Yo", "SERVER BAD EXCEPTION");
                setResult(RESULT_CANCELED);
            }
        });

      ServiceManager.MyApiEndpointInterface apiServiceAuth = ServiceManager
          .newService(emailData, passwordData);
      Call<User> callAuth = apiService.login();

      callAuth.enqueue(new Callback<User>() {
        @Override
        public void onResponse(retrofit.Response<User> response, Retrofit retrofit) {

          if(response.code() == 200) {
            if (response.body() != null) {
              user = response.body();
              Toast.makeText(getBaseContext(), "Welcome " + user.name, Toast.LENGTH_LONG).show();
              setResult(RESULT_OK);
            } else {
              setResult(RESULT_CANCELED);
            }
          }
          else {
            setResult(RESULT_CANCELED);
          }
          progressDialog.dismiss();
        }

        @Override
        public void onFailure(Throwable t) {
          setResult(RESULT_CANCELED);
        }
      });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        finish();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

}