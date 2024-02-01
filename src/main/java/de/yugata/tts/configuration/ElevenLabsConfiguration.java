package de.yugata.tts.configuration;

import java.io.File;

public class ElevenLabsConfiguration extends AbstractTTSConfiguration {

    private String apiKey;
    private String ttsVoice;


    public ElevenLabsConfiguration(File ttsDirectory) {
        super(ttsDirectory);
    }

    public String apiKey() {
        return apiKey;
    }

    public ElevenLabsConfiguration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String ttsVoice() {
        return ttsVoice;
    }

    public ElevenLabsConfiguration setTtsVoice(String ttsVoice) {
        this.ttsVoice = ttsVoice;
        return this;
    }
}
