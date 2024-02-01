package de.yugata.tts.configuration;

import java.io.File;

public class TikTokConfiguration extends AbstractTTSConfiguration{

    private String tikTokSession;


    public TikTokConfiguration(File ttsDirectory) {
        super(ttsDirectory);
    }

    public TikTokConfiguration setTikTokSession(String tikTokSession) {
        this.tikTokSession = tikTokSession;
        return this;
    }

    public String apiKey() {
        return tikTokSession;
    }
}
