package de.yugata.tts.configuration;

import java.io.File;

/**
 * Builder-style configuration for {@link de.yugata.tts.provider.AbstractTTSProvider} which always contains the directory
 * for the generated TTS files. Other configurations may also add parameters such as API-keys, voices, etc.
 */
public abstract class AbstractTTSConfiguration {

    protected final File ttsDirectory;

    public AbstractTTSConfiguration(final File ttsDirectory) {
        this.ttsDirectory = ttsDirectory;
    }


    public File ttsDirectory() {
        return ttsDirectory;
    }
}
