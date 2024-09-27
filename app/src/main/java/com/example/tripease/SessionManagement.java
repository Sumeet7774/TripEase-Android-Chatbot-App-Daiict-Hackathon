package com.example.tripease;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private SharedPreferences prefs;

    public SessionManagement(Context context)
    {
        prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public String getUserId()
    {
        return prefs.getString("user_id", "");
    }

    public String getEmailId()
    {
        return prefs.getString("email_id", "");
    }

    public void setUserId(String userId)
    {
        prefs.edit().putString("user_id", userId).apply();
    }

    public void setEmailId(String emailId)
    {
        prefs.edit().putString("email_id", emailId).apply();
    }

    public void logout()
    {
        prefs.edit().remove("user_id").apply();
        prefs.edit().remove("email_id").apply();
    }
}
