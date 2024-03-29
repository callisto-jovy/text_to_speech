package de.yugata.tts.provider;


import de.yugata.tts.configuration.StreamElementsConfiguration;
import de.yugata.tts.util.ArrayUtil;
import de.yugata.tts.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * StreamElements
 * <p>
 * Supports most (but not all) Polly voices that use the 'standard' engine.
 * Voices that only exist in their 'neural' engine will not work.
 * In addition to Polly, StreamElements also makes use of many Google Cloud Text-to-Speech
 * voices, include their neural "WaveNet" ones.
 * Finally, there is a third section of voices whose IDs are merely a name. These are perhaps
 * custom voices created using Google's premium tier Text-to-Speech platform.
 * <p>
 * The full list of Polly voices can be found: <a href="https://docs.aws.amazon.com/polly/latest/dg/voicelist.html">here</a>
 * <p>
 * The full list of Google voices can be found: <a href="https://cloud.google.com/text-to-speech/docs/voices">here</a>
 */
public class StreamElementsTTS extends AbstractTTSProvider {

    /**
     * API endpoint to the StreamElements text-to-speech engine.
     */
    private static final String API_ENDPOINT = "https://api.streamelements.com/kappa/v2/speech?";

    /**
     * The voice id that will be used for generation.
     */
    private final String voice;

    public StreamElementsTTS(StreamElementsConfiguration configuration) {
        super(configuration);
        this.voice = getVoice(configuration.voice());
    }

    @Override
    public Optional<File> generateTTS(String content) {

        try {
            // Stream elements has a character limit of 3000 chars (see the README of https://github.com/chrisjp/tts)

            final File tempFile = File.createTempFile("streamelements", ".mp3", configuration.ttsDirectory());
            final FileOutputStream fos = new FileOutputStream(tempFile, true);

            final String[] blocks = StringUtil.splitSentences(content, 3000);

            for (final String block : blocks) {
                generateTTS(block, fos);
            }

            fos.close();

            return Optional.of(tempFile);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("StreamElements TTS generation failed with an exception of ", e);
            return Optional.empty();
        }
    }

    private void generateTTS(final String text, final FileOutputStream fileOutputStream) throws IOException, InterruptedException {
        final String params = String.format("voice=%s&text=%s",
                URLEncoder.encode(voice, StandardCharsets.UTF_8),
                URLEncoder.encode(text, StandardCharsets.UTF_8)
        );


        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT + params))
                .setHeader("User-Agent", USER_AGENT)
                .GET()
                .build();

        final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        // Exception is caught by the parent method & an empty optional is returned...
        if (response.statusCode() != 200) {
            throw new IOException("Illegal response code: " + response.statusCode());
        }

        // Write to the provided output stream
        try (final InputStream body = response.body()) {
            body.transferTo(fileOutputStream);
        }
    }

    /**
     * @param voice the voice to get the id from
     * @return the corresponding voice id for a given {@link StreamElementsConfiguration.StreamElementsVoice} or a randomly selected voice.
     */
    private String getVoice(final StreamElementsConfiguration.StreamElementsVoice voice) {
        return voice == StreamElementsConfiguration.StreamElementsVoice.RANDOM ?
                ArrayUtil.getRandomEnumValue(StreamElementsConfiguration.StreamElementsVoice.values()).voiceName()
                : voice.voiceName();
    }
}
