package com.example.tripease;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignUpPage extends AppCompatActivity {
    SessionManagement sessionManagement;
    Button backsignuppage_btn, signup_btn;
    EditText firstnameEdittext,lastnameEdittext,emailidEdittext,passwordEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        sessionManagement = new SessionManagement(this);

        backsignuppage_btn = findViewById(R.id.back_button_signuppage);
        signup_btn = findViewById(R.id.signup_button_signuppage);
        firstnameEdittext = findViewById(R.id.firstname_edittext_signuppage);
        lastnameEdittext = findViewById(R.id.lastname_edittext_signuppage);
        emailidEdittext = findViewById(R.id.email_edittext_signuppage);
        passwordEdittext = findViewById(R.id.password_edittext_signuppage);

        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if (source.toString().contains(" "))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };

        passwordEdittext.setFilters(new InputFilter[]
        {
            new InputFilter.LengthFilter(12),
            noSpacesFilter
        });

        firstnameEdittext.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        lastnameEdittext.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        emailidEdittext.setFilters(new InputFilter[]{noSpacesFilter});
        passwordEdittext.setFilters(new InputFilter[]{noSpacesFilter});

        backsignuppage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname_txt = firstnameEdittext.getText().toString().trim();
                String lastname_txt = lastnameEdittext.getText().toString().trim();
                String email_txt = emailidEdittext.getText().toString().trim();
                String password_txt = passwordEdittext.getText().toString().trim();

                if (TextUtils.isEmpty(firstname_txt) || TextUtils.isEmpty(lastname_txt) || TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidFirstname(firstname_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Firstname must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidLastname(lastname_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Lastname must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidEmail(email_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Please enter a valid email address",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidPassword(password_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Password must be 8 chars with A-Z,0-9 and symbols",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else
                {
                    registerUser(firstname_txt,lastname_txt,email_txt,password_txt);
                }
            }
        });
    }

    private boolean isValidFirstname(String firstname) {
        return firstname.matches("[a-zA-Z]+");
    }

    private boolean isValidLastname(String lastname) {
        return lastname.matches("[a-zA-Z]+");
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

    private void showRegistrationSuccessDialog()
    {
        Dialog successful_registration_dialogBox = new Dialog(SignUpPage.this);
        successful_registration_dialogBox.setContentView(R.layout.custom_success_dialogbox);
        Button dialogBox_ok_button = successful_registration_dialogBox.findViewById(R.id.okbutton_successDialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_registration_dialogBox.dismiss();
                Intent intent = new Intent(SignUpPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_registration_dialogBox.show();

        MotionToast.Companion.createColorToast(SignUpPage.this,
                "Success", "Press OK to redirect to the Index Page for login.",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
    }

    public void registerUser(final String firstname,final String lastname,final String emailid, final String password)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.register_url, response -> {

            Log.d("REQUEST", "Firstname: " + firstname);
            Log.d("REQUEST", "Lastname: " + lastname);
            Log.d("REQUEST", "Emailid: " + emailid);
            Log.d("REQUEST", "Password: " + password);

            if (response.contains("registration successfull"))
            {
                showRegistrationSuccessDialog();
            }
            else if(response.contains("User data already exists"))
            {
                MotionToast.Companion.createColorToast(SignUpPage.this,
                        "Error", "User with those credentials already Exists!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
            }
            else
            {
                MotionToast.Companion.createColorToast(SignUpPage.this,
                        "Registration Failed", "Registration Failed!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                String errorMessage = volleyError.getMessage();
                if (errorMessage == null)
                {
                    errorMessage = "Unknown error occurred";
                }

                MotionToast.Companion.createColorToast(SignUpPage.this,
                        "Internet Error", "Please check your internet connection",
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));

                Log.d("VOLLEY", errorMessage);

                Log.d("VOLLEY", volleyError.getMessage());
            }
        }) {
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<>();
                params.put("first_name", firstname);
                params.put("last_name", lastname);
                params.put("email_id", emailid);
                params.put("password", password);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}