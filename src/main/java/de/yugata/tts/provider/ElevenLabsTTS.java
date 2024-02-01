package de.yugata.tts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.yugata.bot.VideoCreator;
import de.yugata.bot.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static de.yugata.bot.VideoCreator.GSON;
import static de.yugata.bot.VideoCreator.TRANSCRIPTS_DIRECTORY;

public class ElevenLabsTTS extends TTSProvider {


    private static final String VOICES_ENDPOINT = "https://api.elevenlabs.io/v1/voices";
    private static final String TTS_ENDPOINT = "https://api.elevenlabs.io/v1/text-to-speech/";

    public ElevenLabsTTS(TTSConfiguration configuration) {
        super(configuration);
    }


    @Override
    public File generateTTS(final String content) {
        try {
            // generate the tts for the content.
            // try to grab the api key from the config.
            final String apiKey = VideoCreator.INSTANCE.getConfigHelper().getConfiguration().getString("elevenlabs.key", "");
            //2500 chars is the max for one request with an api key. 250 chars with no api key.
            final String[] blocks = StringUtil.splitIntoBlocksAtDelimiter(content, apiKey.isEmpty() ? 250 : 2500, " ");

            // The temporary file created with the audio data.
            final File tempFile = File.createTempFile("elevenlabstts", ".mp3", TRANSCRIPTS_DIRECTORY);
            final FileOutputStream fos = new FileOutputStream(tempFile); // Open a new stream to the temp file in which all the blocks are transferred to.

            // Iterate over the blocks, send a post for each, get the inputstream and merge into the main temp file.
            for (final String block : blocks) {
                final HttpResponse<InputStream> response = sendPost(block);
                final InputStream inputStream = response.body();

                if (response.statusCode() == 200) {
                    inputStream.transferTo(fos);
                } else {
                    inputStream.transferTo(System.out);
                    throw new RuntimeException("Illegal response code: " + response.statusCode());
                }

                inputStream.close();
            }
            fos.close();
            return tempFile;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Performs a post request to the API and returns the http response.
     *
     * @param text the text to tts.
     * @return InputStream wrapped in an {@link HttpResponse}.
     * @throws IOException          Client send.
     * @throws InterruptedException Client send.
     */
    private HttpResponse<InputStream> sendPost(final String text) throws IOException, InterruptedException {
        // Construct the payload.
        final JsonObject payload = new JsonObject();
        payload.addProperty("text", text);
        payload.addProperty("model_id", "eleven_monolingual_v1");
        // new http client.
        final HttpClient client = HttpClient.newHttpClient();
        // grab the optional api key.
        final String apiKey = VideoCreator.INSTANCE.getConfigHelper().getConfiguration().getString("elevenlabs.key", "");

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

        return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
    }

    private String getVoiceIdFromName() throws IOException, InterruptedException {
        final String selectedName = VideoCreator.INSTANCE.getConfigHelper().getConfiguration().getString("elevenlabs.voice", "Rachel");

        final HttpClient httpClient = HttpClient.newHttpClient();
        // Create a new builder for the request, so that the api key may be appended to the headers.
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VOICES_ENDPOINT))
                .setHeader("content-type", "application/json")
                .setHeader("user-agent", USER_AGENT)
                .GET()
                .build();

        final String content = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        final JsonArray voices = JsonParser.parseString(content).getAsJsonObject().getAsJsonArray("voices");

        for (final JsonElement voiceElement : voices) {
            final JsonObject voice = voiceElement.getAsJsonObject();
            final String voiceName = voice.get("name").getAsString();

            if (voiceName.equalsIgnoreCase(selectedName)) {
                return voice.get("voice_id").getAsString();
            }
        }

        return "21m00Tcm4TlvDq8ikWAM"; //"Rachel"
    }
}
