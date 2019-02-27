package com.youme.candid.ourchat;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.youme.candid.ourchat.util.FirebaseApplication;
//import com.youme.candid.ourchat.util.FirebaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;
    private TextView loginError;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       android.support.v7.app.ActionBar actionBar = getSupportActionBar();
       actionBar.setTitle("Login");

        registerUser = (TextView) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.LoginButton);
        loginError = (TextView)findViewById(R.id.login_error);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Tokenn", "Refreshed: " + refreshedToken);
        System.out.println("tokenn generated ++++  12432++++------------");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("getInstanceId", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("msg", msg);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if (user.equals("")) {
                    username.setError("can't be blank");
                } else if (pass.equals("")) {
                    password.setError("can't be blank");
                } else {
                    String url = "https://ourchat-bae78.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (s.equals("null")) {
                                Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                                    } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                    rQueue.add(request);
                }

            }
        });
    }


}