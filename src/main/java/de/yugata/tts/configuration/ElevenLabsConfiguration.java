package de.yugata.tts.configuration;

import java.io.File;

public class ElevenLabsConfiguration extends AbstractTTSConfiguration {

    /**
     * (Optional) API key used for authentication with the ElevenLabs API.
     * If no API key is set, a daily limit is imposed rather than a monthly limit
     * as determined by the user's ElevenLabs API plan.
     */
    private String apiKey;

    /**
     * The TTS voice for ElevenLabs to use.
     */
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
