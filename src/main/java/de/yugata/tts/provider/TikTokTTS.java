package de.yugata.tts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.yugata.bot.VideoCreator;
import de.yugata.bot.util.StringUtil;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * SEE: <a href="https://github.com/oscie57/tiktok-voice/blob/main/main.py">Python tts example</a>
 */
public class TikTokTTS extends TTSProvider {

    private final static String USER_AGENT = "com.zhiliaoapp.musically/2022600030 (Linux; U; Android 7.1.2; es_ES; SM-G988N; Build/NRD90M;tt-ok/3.12.13.1)";

    public TikTokTTS(TTSConfiguration configuration) {
        super(configuration);
    }

    @Override
    public File generateTTS(String content) {
        if (!VideoCreator.INSTANCE.getConfigHelper().getConfiguration().containsKey("tik_tok_session")) {
            throw new IllegalStateException("Tik Tok session is null. Please set the key 'tik_tok_session' in the properties.");
        }
        try {
            final File tempFile = File.createTempFile("tiktoktts", "", VideoCreator.TTS_DIRECTORY);
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
        final String speaker = "en_us_006";

        final String url = "https://tiktok-tts.weilnet.workers.dev/api/generation";

        final JsonObject postJson = new JsonObject();
        postJson.addProperty("text", text);
        postJson.addProperty("voice", speaker);

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
}
