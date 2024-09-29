package com.example.tripease;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
    private ImageButton sendButton, micButton;
    private String currentUserId;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int REQUEST_MIC_PERMISSION = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        sessionManagement = new SessionManagement(getContext());
        currentUserId = sessionManagement.getUserId();

        recyclerView = view.findViewById(R.id.recyclerView_chat);
        inputMessage = view.findViewById(R.id.chat_message_input);
        sendButton = view.findViewById(R.id.message_send_btn);
        micButton = view.findViewById(R.id.speech_button);

        chatAdapter = new ChatMessageAdaptor(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> onSendClick());

        micButton.setOnClickListener(v -> {
            if (checkMicPermission()) {
                startSpeechToText();
            } else {
                requestMicPermission();
            }
        });

        return view;
    }

    private boolean checkMicPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMicPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MIC_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MIC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                Toast.makeText(getContext(), "Microphone permission is required to use speech recognition", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                inputMessage.setText(spokenText); // Set the spoken text to EditText
            }
        }
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
