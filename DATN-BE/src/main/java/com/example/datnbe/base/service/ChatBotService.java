package com.example.datnbe.base.service;

import com.example.datnbe.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service // Thêm annotation để Spring quản lý bean
public class ChatBotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ApiResponse<String> getResponseFromGemini(List<Map<String, String>> history) {
        try {
            // Chuyển history sang cấu trúc Gemini hiểu được
            List<Map<String, Object>> contents = new ArrayList<>();
            for (Map<String, String> msg : history) {
                contents.add(Map.of(
                        "role", msg.get("role").equals("user") ? "user" : "model",
                        "parts", List.of(Map.of("text", msg.get("content")))
                ));
            }

            Map<String, Object> requestBody = Map.of("contents", contents);
            String fullUrl = apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                String answer = (String) parts.get(0).get("text");

                return ApiResponse.<String>builder()
                        .code(200)
                        .message("Get response from Gemini successfully")
                        .result(answer)
                        .build();
            } else {
                return ApiResponse.<String>builder()
                        .code(204)
                        .message("No response from Gemini")
                        .result(null)
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error connecting to Gemini: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
}
