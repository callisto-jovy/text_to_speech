package de.yugata.tts.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.yugata.tts.configuration.StreamElementsConfiguration;
import de.yugata.tts.configuration.TikTokConfiguration;
import de.yugata.tts.util.ArrayUtil;
import de.yugata.tts.util.StringUtil;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * SEE: <a href="https://github.com/oscie57/tiktok-voice/blob/main/main.py">Python tts example</a>
 */
public class TikTokTTS extends AbstractTTSProvider {

    private final static String USER_AGENT = "com.zhiliaoapp.musically/2022600030 (Linux; U; Android 7.1.2; es_ES; SM-G988N; Build/NRD90M;tt-ok/3.12.13.1)";

    private final String apiKey;
    private final TikTokConfiguration.TikTokVoice voice;

    public TikTokTTS(TikTokConfiguration configuration) {
        super(configuration);
        this.apiKey = configuration.apiKey();
        this.voice = configuration.voice();
    }

    @Override
    public File generateTTS(String content) {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Tik Tok session is null. ");
        }
        try {
            final File tempFile = File.createTempFile("tiktoktts", ".mp3", configuration.ttsDirectory());
            final FileOutputStream fos = new FileOutputStream(tempFile, true);

            final String[] blocks = StringUtil.splitIntoBlocksAtDelimiter(cleanText(content), 200, " ");

            for (final String block : blocks) {
                final byte[] data = sendPost(block);
                fos.write(data);
            }

            fos.close();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String cleanText(final String in) {
        return in.replace("+", "plus").replace("&", "and");
    }

    private byte[] sendPost(final String text) {

        final String url = "https://tiktok-tts.weilnet.workers.dev/api/generation";

        final JsonObject postJson = new JsonObject();
        postJson.addProperty("text", text);
        postJson.addProperty("voice", getVoice());

        try {
            final HttpClient client = HttpClient.newBuilder().build();

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", USER_AGENT)
                    //    .setHeader("Cookie", sessionHeader)
                    .setHeader("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postJson.toString()))
                    .build();


            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                final JsonObject root = JsonParser.parseReader(new InputStreamReader(response.body())).getAsJsonObject();

                // TOD0: handle error
                final String data = root.get("data").getAsString();

                return Base64.getDecoder().decode(data);
            } else {
                System.out.println(response.body());
                throw new RuntimeException("Illegal response code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVoice() {
        return voice == TikTokConfiguration.TikTokVoice.RANDOM
                ? ArrayUtil.getRandomEnumValue(TikTokConfiguration.TikTokVoice.values()).getId()
                : voice.getId();
    }
}
