package com.example.tripease;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {
    private EditText chatmessageInput;
    private ImageButton sendBtn;
    private int currentUserId = 1;  // Assuming this is an integer for sending messages

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatmessageInput = view.findViewById(R.id.chat_message_input);
        sendBtn = view.findViewById(R.id.message_send_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMessage = chatmessageInput.getText().toString();

                if (!userMessage.isEmpty()) {
                    sendMessageToBot(userMessage, currentUserId);
                } else {
                    Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void sendMessageToBot(final String userMessage, final int userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.chatbot_url,
                response -> {
                    Log.d("REQUEST", "User Message: " + userMessage);
                    Log.d("REQUEST", "User ID: " + userId);
                    Log.d("RESPONSE", "Raw response: " + response);

                    // Parse the JSON response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        // Check the status
                        String status = jsonResponse.getString("status");
                        if ("success".equals(status)) {
                            String responseUserId = jsonResponse.getString("user_id"); // User ID as String
                            String responseMessage = jsonResponse.getString("user_message");
                            String botResponse = jsonResponse.getString("bot_response");

                            Log.d("BOTRESPONSE", "Bot Response: " + botResponse);
                            displayMessageInChat(responseMessage, botResponse); // Display user and bot message
                        } else {
                            Toast.makeText(getContext(), "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to get a valid response from the bot.", Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    String errorMessage = volleyError.getMessage();
                    if (errorMessage == null) {
                        errorMessage = "Unknown error occurred";
                    }
                    Toast.makeText(getContext(), "Please check your internet connection: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("VOLLEY", errorMessage);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));  // user ID as integer sent in request
                params.put("message", userMessage);  // user input message
                return params;
            }
        };

        // Ensure VolleySingleton is properly initialized and added to the request queue
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    // Method to display message in the chat (update the UI)
    private void displayMessageInChat(String userMessage, String botMessage) {
        // Implement your logic to display the message in the chat
        // For example, update a RecyclerView or TextView
        // Example implementation with Toasts for simplicity:
        Toast.makeText(getContext(), "You: " + userMessage, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Bot: " + botMessage, Toast.LENGTH_SHORT).show();

        // Replace the above Toasts with your RecyclerView logic to append messages.
        // E.g., if you're using a RecyclerView, add the messages to a list and notify the adapter.
    }
}
