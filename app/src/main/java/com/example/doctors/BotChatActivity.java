package com.example.doctors;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BotChatActivity extends AppCompatActivity {

    private EditText userInput;
    private Button sendButton;
    private TextView chatDisplay;
    private ScrollView scrollView;

    private FirebaseRemoteConfig remoteConfig;
    private String openAiApiKey = "";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_chat);

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        chatDisplay = findViewById(R.id.chatDisplay);
        scrollView = findViewById(R.id.scrollView);

        sendButton.setEnabled(false);

        sendButton.setOnClickListener(v -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                appendChat("You: " + input);
                sendMessageToGPT(input);
                userInput.setText("");
            }
        });

        initRemoteConfig();
    }

    private void initRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(0)
                        .build();

        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(Collections.singletonMap("openai_api_key", "default-fallback"));

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        openAiApiKey = remoteConfig.getString("openai_api_key");
                        if (openAiApiKey == null || openAiApiKey.equals("default-fallback") || openAiApiKey.isEmpty()) {
                            Toast.makeText(this, "Invalid API Key!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Config fetched", Toast.LENGTH_SHORT).show();
                            sendButton.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch config", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void appendChat(String text) {
        runOnUiThread(() -> {
            chatDisplay.append(text + "\n");
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    private void sendMessageToGPT(String message) {
        new Thread(() -> {
            try {
                String jsonBody = gson.toJson(new GPTRequest(
                        "gpt-3.5-turbo",
                        Collections.singletonList(new GPTMessage("user", message))
                ));

                Request request = new Request.Builder()
                        .url(OPENAI_URL)
                        .header("Authorization", "Bearer " + openAiApiKey)
                        .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    GPTResponse gptResponse = gson.fromJson(responseBody, GPTResponse.class);
                    String reply = gptResponse.choices.get(0).message.content;
                    appendChat("Bot: " + reply);
                } else {
                    appendChat("Bot: Failed to get response (" + response.code() + ")");
                }
            } catch (IOException e) {
                e.printStackTrace();
                appendChat("Bot: Error occurred");
            }
        }).start();
    }

    // Data Models
    static class GPTRequest {
        String model;
        java.util.List<GPTMessage> messages;

        GPTRequest(String model, java.util.List<GPTMessage> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    static class GPTMessage {
        String role;
        String content;

        GPTMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    static class GPTResponse {
        java.util.List<Choice> choices;

        static class Choice {
            GPTMessage message;
        }
    }
}
