package de.yugata.tts.configuration;

import java.io.File;

public class StreamElementsConfiguration extends AbstractTTSConfiguration {

    /**
     * The voice for the {@link de.yugata.tts.provider.StreamElementsTTS} provider to use.
     * Defaults to a {@link StreamElementsVoice.MATTHEW}.
     */
    private StreamElementsVoice voice = StreamElementsVoice.MATTHEW;

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

    /**
     * TODO: add more voices.
     * For now these voices stem from a previous project of mine; these are the ones i deemed less annoying.
     */
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
