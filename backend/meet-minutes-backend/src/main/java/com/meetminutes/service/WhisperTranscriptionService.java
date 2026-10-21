
package com.meetminutes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class WhisperTranscriptionService implements TranscriptionService {

    @Value("${whisper.executable}")
    private String whisperExecutable;

    @Value("${whisper.model}")
    private String whisperModel;

    @Override
    public String transcribe(String filePath) throws Exception {

        Path audioFile = Path.of(filePath);
        Path executable = Path.of(whisperExecutable);
        Path model = Path.of(whisperModel);

        if (!Files.isRegularFile(executable)) {
            throw new IOException(
                    "Whisper executable not found: " + executable
            );
        }

        if (!Files.isRegularFile(model)) {
            throw new IOException(
                    "Whisper model not found: " + model
            );
        }

        if (!Files.isRegularFile(audioFile)) {
            throw new IOException(
                    "Audio file not found: " + audioFile
            );
        }

        Path outputDirectory =
                Files.createTempDirectory("meet-minutes-whisper-");

        String outputPrefix =
                outputDirectory.resolve("transcript").toString();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    executable.toString(),
                    "-m", model.toString(),
                    "-f", audioFile.toAbsolutePath().toString(),
                    "-l", "auto",
                    "-otxt",
                    "-of", outputPrefix
            );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            String consoleOutput;

            try (var input = process.getInputStream()) {
                consoleOutput = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException(
                        "Whisper failed with exit code "
                                + exitCode + ". Output: " + consoleOutput
                );
            }

            Path transcriptFile =
                    Path.of(outputPrefix + ".txt");

            if (!Files.isRegularFile(transcriptFile)) {
                throw new IOException(
                        "Whisper completed but transcript file was not created."
                );
            }

            String transcript = Files.readString(
                    transcriptFile, StandardCharsets.UTF_8
            ).trim();

            if (transcript.isBlank()) {
                throw new IOException(
                        "Whisper returned an empty transcript."
                );
            }

            return transcript;

        } finally {
            try (var files = Files.walk(outputDirectory)) {
                files.sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                                // Temporary-file cleanup is best effort.
                            }
                        });
            }
        }
    }
}
