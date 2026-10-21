package com.meetminutes.controller;

import com.meetminutes.service.GeminiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class GeminiTestController {

    private final GeminiService geminiService;

    public GeminiTestController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/gemini")
    public String testGemini() {

        String sampleTranscript = """
                Ravi: We need to complete the login page by Friday.
                Priya: I will finish the React login page by Thursday.
                Arjun: I will complete the Spring Boot login API by Friday.
                Ravi: We agreed to review the completed work on Monday.
                """;

        return geminiService.generateMeetingMinutes(sampleTranscript);
    }
}