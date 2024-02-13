package de.yugata.tts.provider;

import de.yugata.tts.configuration.AbstractTTSConfiguration;
import de.yugata.tts.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Bing Translator
 * <p>
 * Technically, this is using Speech, part of Microsoft's Azure Cognitive Services
 * <a href="https://learn.microsoft.com/en-us/azure/cognitive-services/Speech-Service/">Azure</a>
 * <p>
 * The Azure service is a paid API (with some free limits) but we are going through the same endpoint
 * that Bing Translator uses, hence the name of the class.
 * <p>
 * This first step involves making a normal GET request to the translator page and retrieving several variables,
 * including somewhat ironically, AbusePreventionHelper params.
 * With these acquired, we can then make a request to the TTS endpoint ('/tfettts') and receive mp3 audio.
 * <p>
 * A full list of voices/languages supported can be found here:
 * <a href="https://learn.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support?tabs=tts">Microsoft Azure documentation</a>
 * And the voice gallery is also a great resource:
 * <a href="https://speech.microsoft.com/portal/voicegallery">Voice gallery</a>
 * But in testing, there were well over 100 voices that don't work via Bing Translator, with a 500 error
 * being produced. Possibly this is due to using an older version of the API, or Microsoft deciding they
 * are not suitable for their translation service for whatever reason.
 */
public class BingTranslatorTTS extends AbstractTTSProvider {


    /**
     * Default constructor.
     *
     * @param configuration
     */
    public BingTranslatorTTS(AbstractTTSConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Optional<File> generateTTS(String content) {
        try {
            // First step: Simple GET request to the Bing Translator site.


            // Bing (Microsoft Azure) has a character limit of 3000 chars (see the README of https://github.com/chrisjp/tts)
            final File tempFile = File.createTempFile("streamelements", ".mp3", configuration.ttsDirectory());
            final FileOutputStream fos = new FileOutputStream(tempFile, true);

            final String[] blocks = StringUtil.splitSentences(content, 3000);


            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.bing.com/translator"))
                    .setHeader("User-Agent", USER_AGENT)
                    .setHeader("Origin", "https://bing.com")
                    .setHeader("Referer", "https://www.bing.com/translator")
                    .GET()
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            

            fos.close();

            return Optional.empty();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("StreamElements TTS generation failed with an exception of ", e);
            return Optional.empty();
        }
    }


}
