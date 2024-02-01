package de.yugata.tts.configuration;

import java.io.File;

public abstract class AbstractTTSConfiguration {

    protected final File ttsDirectory;


    public AbstractTTSConfiguration(final File ttsDirectory) {
        this.ttsDirectory = ttsDirectory;
    }


    public File ttsDirectory() {
        return ttsDirectory;
    }
}
