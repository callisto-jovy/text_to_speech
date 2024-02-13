package de.yugata.tts.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.yugata.tts.configuration.TikTokConfiguration;
import de.yugata.tts.util.ArrayUtil;
import de.yugata.tts.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Optional;

/**
 * SEE: <a href="https://github.com/oscie57/tiktok-voice/blob/main/main.py">Python tts example</a>
 */
public class TikTokTTS extends AbstractTTSProvider {


    private final static String USER_AGENT = "com.zhiliaoapp.musically/2022600030 (Linux; U; Android 7.1.2; es_ES; SM-G988N; Build/NRD90M;tt-ok/3.12.13.1)";
    private static final String TTS_API_ENDPOINT = "https://tiktok-tts.weilnet.workers.dev/api/generation";

    private final String apiKey;
    private final String voice;

    public TikTokTTS(TikTokConfiguration configuration) {
        super(configuration);
        this.apiKey = configuration.tikTokSession();
        this.voice = getVoice(configuration.voice());
    }

    @Override
    public Optional<File> generateTTS(String content) {
        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.warn("Tik Tok Session is null or session is empty. Please set a valid session id.");
            return Optional.empty();
        }

        try {
            final File tempFile = File.createTempFile("tiktoktts", ".mp3", configuration.ttsDirectory());
            final FileOutputStream fos = new FileOutputStream(tempFile, true);

            final String[] blocks = StringUtil.splitSentences(cleanText(content), 200);

            for (final String block : blocks) {
                final byte[] data = sendPost(block);
                fos.write(data);
            }

            fos.close();
            return Optional.of(tempFile);
        } catch (IOException e) {
            LOGGER.error("TikTok TTS generation failed with an exception of ", e);
            return Optional.empty();
        }
    }


    private byte[] sendPost(final String text) {

        final JsonObject requestBody = new JsonObject();
        requestBody.addProperty("text", text);
        requestBody.addProperty("voice", voice);
        try {
            final HttpClient client = HttpClient.newHttpClient();

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TTS_API_ENDPOINT))
                    .setHeader("User-Agent", USER_AGENT)
                    //    .setHeader("Cookie", sessionHeader)
                    .setHeader("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();


            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Exception is caught by the parent method & an empty optional is returned...
            if (response.statusCode() != 200) {
                throw new IOException("Illegal response code: " + response.statusCode());
            }

            final JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();

            // TODO: handle errors
            final String data = responseBody.get("data").getAsString();

            return Base64.getDecoder().decode(data);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replaces common tokens which the TikTok tts engine is not able to read correctly.
     *
     * @param in the string to replace.
     * @return the string with the tokens replaced.
     */
    private String cleanText(final String in) {
        return in
                .replace("+", "plus")
                .replace("&", "and");
    }

    /**
     * @param voice the voice to get the id from
     * @return the corresponding voice id for a given {@link TikTokConfiguration.TikTokVoice} or a randomly selected voice.
     */
    private String getVoice(final TikTokConfiguration.TikTokVoice voice) {
        return voice == TikTokConfiguration.TikTokVoice.RANDOM
                ? ArrayUtil.getRandomEnumValue(TikTokConfiguration.TikTokVoice.values()).getId()
                : voice.getId();
    }
}
