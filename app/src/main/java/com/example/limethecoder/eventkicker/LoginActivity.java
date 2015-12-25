package com.example.limethecoder.eventkicker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;

import com.example.limethecoder.eventkicker.net.ApiResponse;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView email;
    EditText password;
    Button login;
    User user;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHandler = new Handler();

        login = (Button)findViewById(R.id.email_sign_in_button);
        password = (EditText)findViewById(R.id.password);
        email = (AutoCompleteTextView)findViewById(R.id.email);

        login.setOnClickListener(this);
    }

    public boolean checkData() {
        boolean is_correct = true;

        String emailData = email.getText().toString();
        String passwordData = password.getText().toString();

        if (emailData.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailData).matches()) {
            email.setError("Invalid email adress");
            is_correct = false;
        } else {
            email.setError(null);
        }

        if (passwordData.isEmpty() || passwordData.length() < 6) {
            password.setError("Password must have at least 6 characters");
            is_correct = false;
        } else {
            password.setError(null);
        }

        return is_correct;
    }

    @Override
    public void onClick(View v) {
        if (!checkData()) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String emailData = email.getText().toString();
        String passwordData = password.getText().toString();

        // TODO: Implement authentication logic here.

        ServiceManager.MyApiEndpointInterface apiService = ServiceManager.newService(emailData, passwordData);
        Call<ApiResponse<User>> call = apiService.login();

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(retrofit.Response<ApiResponse<User>> response, Retrofit retrofit) {

                if(response.code() == 200) {
                    if (response.body() != null && response.body().success) {
                        user = response.body().single;
                        Toast.makeText(getBaseContext(), "Welcome " + user.name, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                    } else {
                        Toast.makeText(getBaseContext(), "Something wrong", Toast.LENGTH_LONG).show();
                        setResult(RESULT_CANCELED);
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
        });


        mHandler.postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        finish();
                        // onLoginFailed();
                    }
                }, 3000);

    }
}