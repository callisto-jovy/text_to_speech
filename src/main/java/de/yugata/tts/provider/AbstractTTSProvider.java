package de.yugata.tts;

import java.io.File;

public abstract class TTSProvider {

    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0";
    private final TTSConfiguration configuration;

    public TTSProvider(TTSConfiguration configuration) {
        this.configuration = configuration;
    }


    /**
     * Generates tts-files for a given content.
     *
     * @param content the content-string to tts.
     * @return the temporary file generated.
     */
    public abstract File generateTTS(final String content);

}
