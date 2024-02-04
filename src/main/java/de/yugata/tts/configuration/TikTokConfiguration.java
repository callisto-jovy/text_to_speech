package de.yugata.tts.configuration;

import java.io.File;

public class TikTokConfiguration extends AbstractTTSConfiguration {

    private String tikTokSession;
    private TikTokVoice voice = TikTokVoice.MALE_DEFAULT_US;


    public TikTokVoice voice() {
        return voice;
    }

    public TikTokConfiguration setVoice(TikTokVoice voice) {
        this.voice = voice;
        return this;
    }

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


    public enum TikTokVoice {
        MALE_DEFAULT_US("en_us_001"),
        GAME_ON("en_male_jomboy"),
        JESSIE("en_us_002"),
        WARM("es_mx_002"),
        WACKY("en_male_funny"),
        SCREAM("en_us_ghostface"),
        EMPATHETIC("en_female_samc"),
        SERIOUS("en_male_cody"),
        BEAUTY_GURU("en_female_makeup"),
        BESTIE("en_female_richgirl"),
        TRICKSTER("en_male_grinch"),
        JOEY("en_us_006"),
        STORY_TELLER("en_male_narration"),
        MR_GOODGUY("en_male_deadpool"),
        NARRATOR("en_uk_001"),
        MALE_ENGLISH_UK("en_uk_003"),
        METRO("en_au_001"),
        ALFRED("en_male_jarvis"),
        ASHMAGIC("en_male_ashmagic"),
        OLANTEKKERS("en_male_olantekkers"),
        LORD_CRINGE("en_male_ukneighbor"),
        MR_METICULOUS("en_male_ukbutler"),
        DEBUTANTE("en_female_shenna"),
        VARSITY("en_female_pansino"),
        MARTY("en_male_trevor"),
        POP_LULLABY("en_female_f08_twinkle"),
        CLASSIC_ELECTRIC("en_male_m03_classical"),
        BAE("en_female_betty"),
        CUPID("en_male_cupid"),
        GRANNY("en_female_grandma"),
        COZY("en_male_m2_xhxs_m03_christmas"),
        AUTHOR("en_male_santa_narration"),
        CAROLER("en_male_sing_deep_jingle"),
        SANTA("en_male_santa_effect"),
        NYE_2023("en_female_ht_f08_newyear"),
        MAGICIAN("en_male_wizard"),
        OPERA("en_female_ht_f08_halloween"),
        EUPHORIC("en_female_ht_f08_glorious"),
        HYPETRAIN("en_male_sing_funny_it_goes_up"),
        MELODRAMA("en_female_ht_f08_wonderful_world"),
        QUIRKY_TIME("en_male_m2_xhxs_m03_silly"),
        PEACEFUL("en_female_emotional"),
        TOON_BEAT("en_male_m03_sunshine_soon"),
        OPEN_MIC("en_female_f08_warmy_breeze"),
        JINGLE("en_male_m03_lobby"),
        THANKSGIVING("en_male_sing_funny_thanksgiving"),
        COTTAGECORE("en_female_f08_salut_damour"),
        PROFESSOR("en_us_007"),
        SCIENTIST("en_us_009"),
        CONFIDENCE("en_us_010"),
        SMOOTH("en_au_002"),
        RANDOM("");


        private final String id;


        TikTokVoice(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
