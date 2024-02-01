package de.yugata.tts;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

public class StreamElementsTTS extends TTSProvider {

    private static final String[] VOICES = {
            "Joey",
            "Matthew",
            "Amy",
            "Kendra",
    };

    private static final String API_ENDPOINT = "https://api.streamelements.com/kappa/v2/speech?";

    public StreamElementsTTS(TTSConfiguration configuration) {
        super(configuration);
    }

    @Override
    public File generateTTS(String content) {
        final String voice = getRandomVoice();

        try {
            return generateTTS(content, voice);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private File generateTTS(final String text, final String voice) throws IOException, InterruptedException {
        final String params = String.format("voice=%s&text=%s", URLEncoder.encode(voice, StandardCharsets.UTF_8), URLEncoder.encode(text, StandardCharsets.UTF_8));


        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT.concat(params)))
                .setHeader("User-Agent", USER_AGENT)
                .GET()
                .build();

        final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        final File tempFile = File.createTempFile("streamlabstts", ".mp3");

        try (final InputStream body = response.body()) {

            if (response.statusCode() == 200) {
                body.transferTo(Files.newOutputStream(tempFile.toPath()));
            } else {
                body.transferTo(System.out);
                throw new RuntimeException("Illegal response code: " + response.statusCode());
            }
        }
        return tempFile;
    }


    private String getRandomVoice() {
        return VOICES[ThreadLocalRandom.current().nextInt(VOICES.length)];
    }
}
