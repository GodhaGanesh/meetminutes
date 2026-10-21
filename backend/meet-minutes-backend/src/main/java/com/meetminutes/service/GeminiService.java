
package com.meetminutes.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    public GeminiService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.url}") String apiUrl,
            ObjectMapper objectMapper
    ) {
        this.restClient = RestClient.builder()
                .defaultHeader("x-goog-api-key", apiKey)
                .build();

        this.apiUrl = apiUrl;
        this.objectMapper = objectMapper;
    }

    public String generateMeetingMinutes(String transcriptText) {

        String prompt = """
                Analyze the meeting transcript below.

                Return only a valid JSON object with exactly these keys:
                {
                  "summary": "Concise meeting summary",
                  "discussionPoints": ["Discussion point"],
                  "decisions": ["Decision"],
                  "actionItems": [
                    {
                      "task": "Task description",
                      "responsiblePerson": "Person's name or Not specified",
                      "deadline": "Deadline or Not specified"
                    }
                  ]
                }

                Rules:
                - Return JSON only, without Markdown code fences.
                - Do not invent facts that are absent from the transcript.
                - Use empty arrays when no items exist.
                - Use "Not specified" for unknown people or deadlines.
                - Keep the exact key names shown above.

                Meeting transcript:
                """ + transcriptText;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"
                )
        );

        JsonNode response = restClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        if (response == null
                || !response.path("candidates").isArray()
                || response.path("candidates").isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned no generated response"
            );
        }

        JsonNode textNode = response
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");

        if (textNode.isMissingNode() || textNode.isNull()) {
            throw new IllegalStateException(
                    "Gemini response did not contain generated text"
            );
        }

        return textNode.asText();
    }

    public JsonNode generateMeetingMinutesJson(String transcriptText) {
        String generatedText = generateMeetingMinutes(transcriptText);

        try {
            JsonNode minutesJson = objectMapper.readTree(generatedText);

            if (!minutesJson.isObject()
                    || !minutesJson.path("summary").isTextual()
                    || !minutesJson.path("discussionPoints").isArray()
                    || !minutesJson.path("decisions").isArray()
                    || !minutesJson.path("actionItems").isArray()) {
                throw new IllegalStateException(
                        "Gemini returned JSON with an unexpected structure"
                );
            }

            return minutesJson;

        } catch (tools.jackson.core.JacksonException exception) {
            throw new IllegalStateException(
                    "Gemini returned invalid JSON",
                    exception
            );
        }
    }
}
