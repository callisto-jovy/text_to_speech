package de.yugata.tts.configuration;

import java.io.File;

public class StreamElementsConfiguration extends AbstractTTSConfiguration {

    private StreamElementsVoice voice;

    public StreamElementsConfiguration(File ttsDirectory) {
        super(ttsDirectory);
    }

    public StreamElementsVoice voice() {
        return voice;
    }

    public StreamElementsConfiguration setVoice(StreamElementsVoice voice) {
        this.voice = voice;
        return this;
    }

    public enum StreamElementsVoice {
        JOEY("Joey"),
        MATTHEW("Matthew"),
        AMY("Amy"),
        KENDRA("Kendra"),
        RANDOM("");



        private final String voiceName;

        StreamElementsVoice(final String voiceName) {
            this.voiceName = voiceName;
        }

        public String voiceName() {
            return voiceName;
        }
    }
}
