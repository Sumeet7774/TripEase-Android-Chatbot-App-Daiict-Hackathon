package com.example.tripease;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tripease.ApiEndpoints;
import com.example.tripease.ChatMessageAdaptor;
import com.example.tripease.ChatMessageModel;
import com.example.tripease.R;
import com.example.tripease.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {
    SessionManagement sessionManagement;
    private RecyclerView recyclerView;
    private ChatMessageAdaptor chatAdapter;
    private List<ChatMessageModel> chatMessages = new ArrayList<>();
    private EditText inputMessage;
    private ImageButton sendButton;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        sessionManagement = new SessionManagement(getContext());

        currentUserId = sessionManagement.getUserId();

        recyclerView = view.findViewById(R.id.recyclerView_chat);
        inputMessage = view.findViewById(R.id.chat_message_input);
        sendButton = view.findViewById(R.id.message_send_btn);

        chatAdapter = new ChatMessageAdaptor(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> onSendClick());

        return view;
    }

    private void onSendClick() {
        String message = inputMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            sendMessageToBot(message, currentUserId);
            inputMessage.setText("");  // Clear the input field
        } else {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessageToBot(final String userMessage, final String userId) {
        // Show the user's message in the chat
        chatAdapter.addMessage(new ChatMessageModel("user", userMessage));
        Log.d("User Message", userMessage);

        // Create a JSON object with user data
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", userId);
            params.put("message", userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make network request to bot using JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiEndpoints.chatbot_url, params,
                response -> {
                    // Log the raw response from the Groq API
                    Log.d("ChatFragment", "Raw response from Groq API: " + response.toString());

                    // Parse the JSON response
                    try {
                        String status = response.getString("status");

                        if ("success".equals(status)) {
                            String botResponse = response.getString("bot_response");
                            Log.d("Bot Response", botResponse);

                            // Show bot's response in the chat
                            chatAdapter.addMessage(new ChatMessageModel("bot", botResponse));

                            // Scroll to the bottom
                            recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                        } else {
                            // Handle case when status is not success
                            Toast.makeText(getContext(), "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
