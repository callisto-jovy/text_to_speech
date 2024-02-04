import de.yugata.tts.configuration.TikTokConfiguration;
import de.yugata.tts.provider.TikTokTTS;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TikTokTest {

    // generate a mock string for every voice.
    private static final TikTokConfiguration CONFIGURATION = new TikTokConfiguration(new File("out"))
            .setTikTokSession("824beec10ec9fb3c0806ae60f6781eb9");

    @Test
    public void generateMockVoices() {

        for (TikTokConfiguration.TikTokVoice value : TikTokConfiguration.TikTokVoice.values()) {
            CONFIGURATION.setVoice(value);
            final File output = generate();
            System.out.printf("Voice: %s File: %s%n", value.name(), output.getName());
        }
    }

    @Test
    public void testRandomVoice() {
        CONFIGURATION.setVoice(TikTokConfiguration.TikTokVoice.RANDOM);
        generate();
    }

    private File generate() {
        final TikTokTTS tikTokTTS = new TikTokTTS(CONFIGURATION);
        return tikTokTTS.generateTTS("the red brown fox jumps over the lazy dog");
    }
}
