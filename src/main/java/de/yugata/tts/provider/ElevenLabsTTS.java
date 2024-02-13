package de.yugata.tts.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.yugata.tts.configuration.ElevenLabsConfiguration;
import de.yugata.tts.util.StringUtil;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


import static de.yugata.tts.util.StringUtil.GSON;

public class ElevenLabsTTS extends AbstractTTSProvider {


    private static final String VOICES_ENDPOINT = "https://api.elevenlabs.io/v1/voices";
    private static final String TTS_ENDPOINT = "https://api.elevenlabs.io/v1/text-to-speech/";

    private final String apiKey;

    public ElevenLabsTTS(ElevenLabsConfiguration configuration) {
        super(configuration);
        // try to grab the api key from the config.
        this.apiKey = configuration.apiKey();
    }


    @Override
    public Optional<File> generateTTS(final String content) {
        /* generate the tts for the content. */
        try {
            // 2500 chars is the max for one request with an api key. 250 chars with no api key.
            final String[] blocks = StringUtil.splitSentences(content, apiKey.isEmpty() ? 250 : 2500);

            // The temporary file created with the audio data.
            final File tempFile = File.createTempFile("elevenlabstts", ".mp3", configuration.ttsDirectory());
            final FileOutputStream fos = new FileOutputStream(tempFile); // Open a new stream to the temp file in which all the blocks are transferred to.

            for (final String block : blocks) {
                sendPost(block, fos);
            }
            fos.close();
            return Optional.of(tempFile);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("ElevenLabs TTS generation failed with an exception of ", e);
            return Optional.empty();
        }
    }


    private void sendPost(final String text, final FileOutputStream fileOutputStream) throws IOException, InterruptedException {
        // Construct the payload.
        final JsonObject payload = new JsonObject();
        payload.addProperty("text", text);
        payload.addProperty("model_id", "eleven_monolingual_v1");

        final HttpClient client = HttpClient.newHttpClient();

        // Create a new builder for the request, so that the api key may be appended to the headers.
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(TTS_ENDPOINT + getVoiceIdFromName()))
                .setHeader("content-type", "application/json")
                .setHeader("user-agent", USER_AGENT)
                .setHeader("accept", "audio/mpeg")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload)));

        if (!apiKey.isEmpty()) {
            requestBuilder.setHeader("xi-api-key", apiKey);
        }

        final HttpResponse<InputStream> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());

        // Exception is caught by the parent method & an empty optional is returned...
        if (response.statusCode() != 200) {
            throw new IOException("Illegal response code: " + response.statusCode());
        }

        // Write to the provided output stream
        try (final InputStream body = response.body()) {
            body.transferTo(fileOutputStream);
        }
    }

    private String getVoiceIdFromName() throws IOException, InterruptedException {
        final String selectedName = ((ElevenLabsConfiguration) configuration).ttsVoice();

        final HttpClient client = HttpClient.newHttpClient();
        // Create a new builder for the request, so that the api key may be appended to the headers.
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VOICES_ENDPOINT))
                .setHeader("content-type", "application/json")
                .setHeader("user-agent", USER_AGENT)
                .GET()
                .build();

        final String content = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        final JsonArray voices = JsonParser.parseString(content).getAsJsonObject().getAsJsonArray("voices");

        for (final JsonElement voiceElement : voices) {
            final JsonObject voice = voiceElement.getAsJsonObject();
            final String voiceName = voice.get("name").getAsString();

            if (voiceName.equalsIgnoreCase(selectedName)) {
                return voice.get("voice_id").getAsString();
            }
        }

        return "21m00Tcm4TlvDq8ikWAM"; //Default: "Rachel"
    }
}
