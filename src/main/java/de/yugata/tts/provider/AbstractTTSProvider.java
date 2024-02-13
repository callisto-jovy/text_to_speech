package de.yugata.tts.provider;

import de.yugata.tts.configuration.AbstractTTSConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public abstract class AbstractTTSProvider {

    /**
     * Hard-coded user agent for the subclasses.
     */
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0";

    /**
     * Class- and subclass-bound logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTTSProvider.class);
    /**
     * Dependency injected {@link AbstractTTSConfiguration} to pass in API keys, the desired voice, etc.
     */
    protected final AbstractTTSConfiguration configuration;

    /**
     * Default constructor.
     */
    public AbstractTTSProvider(AbstractTTSConfiguration configuration) {
        this.configuration = configuration;
    }


    /**
     * Generates tts-files for a given content.
     *
     * @param content the content-string to tts.
     * @return the temporary file generated.
     */
    public abstract Optional<File> generateTTS(final String content);

}
