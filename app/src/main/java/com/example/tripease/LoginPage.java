package com.example.tripease;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class LoginPage extends AppCompatActivity {
    SessionManagement sessionManagement;
    Button backloginpage_btn, login_btn;
    EditText emailid_edittext, password_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        sessionManagement = new SessionManagement(LoginPage.this);

        backloginpage_btn = findViewById(R.id.back_button_loginpage);
        login_btn = findViewById(R.id.login_button_loginpage);
        emailid_edittext = findViewById(R.id.email_edittext_loginpage);
        password_edittext = findViewById(R.id.password_edittext_loginpage);

        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if (source.toString().contains(" "))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };

        emailid_edittext.setFilters(new InputFilter[]{noSpacesFilter});
        password_edittext.setFilters(new InputFilter[]{noSpacesFilter});

        backloginpage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid_txt = emailid_edittext.getText().toString().trim();
                String password_txt = password_edittext.getText().toString().trim();

                if (TextUtils.isEmpty(emailid_txt) || TextUtils.isEmpty(password_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidEmail(emailid_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Username must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidPassword(password_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Enter a 10-digit phone number.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else
                {
                    loginUser(emailid_txt,password_txt);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        String emailPattern = "^[a-z][a-z0-9]*@gmail\\.com$";
        return (!TextUtils.isEmpty(target) && target.toString().matches(emailPattern));
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    public void loginUser(final String emailid, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonResponse = extractJsonResponse(response);

                        Log.d("retrievedResponse", "Response: " + jsonResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String status = jsonObject.optString("status");
                            String message = jsonObject.optString("message");

                            if ("found".equals(status))
                            {
                                retrieveUserId(emailid);

                                sessionManagement.setEmailId(emailid);

                                MotionToast.Companion.createColorToast(LoginPage.this,
                                        "Success", "Login Successful",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));

                                Intent intent = new Intent(LoginPage.this, HomeScreen.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                            else if ("not found".equals(status))
                            {
                                MotionToast.Companion.createColorToast(LoginPage.this,
                                        "Error", "No such user found with those credentials",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                            }
                            else
                            {
                                MotionToast.Companion.createColorToast(LoginPage.this,
                                        "Error", "Some Error Occurred",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                            }
                        }
                        catch (JSONException e) {
                            MotionToast.Companion.createColorToast(LoginPage.this,
                                    "Error", "Unexpected response format",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                            Log.d("JSON_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPage.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        Log.d("VOLLEY", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_id", emailid);
                params.put("password", password);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String extractJsonResponse(String response) {
        try {
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}") + 1;
            return response.substring(startIndex, endIndex);
        } catch (Exception e) {
            Log.d("ResponseExtraction", "Failed to extract JSON from response: " + response);
            return "{}"; // Return empty JSON object if extraction fails
        }
    }

    private void retrieveUserId(final String emailid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getUserid_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retrieveUserId", "Response: " + response);

                        String userId = extractUserIdFromResponse(response);

                        if (userId != null) {
                            sessionManagement.setUserId(userId);
                            Log.d("Session Userid", "User Id: " + userId);
                        }
                        else
                        {
                            MotionToast.Companion.createColorToast(LoginPage.this,
                                    "Error", "User not found.",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPage.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_id", emailid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String extractUserIdFromResponse(String response) {
        String pattern = "connection successfull(\\w+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractUserIdFromResponse", "Failed to extract user ID from response: " + response);
        return null;
    }

}