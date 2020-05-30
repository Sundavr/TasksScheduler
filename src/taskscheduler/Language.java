package taskscheduler;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Enumeration des langages disponibles.
 * @author JOHAN
 */
public enum Language {
    AF("AFRIKAANS"), SQ("ALBANIAN"), AM("AMHARIC"), AR("ARABIC"), HY("ARMENIAN"), 
    AZ("AZERBAIJANI"), EU("BASQUE"), BE("BELARUSIAN"), BN("BENGALI"), 
    BG("BULGARIAN"), MY("BURMESE"), CA("CATALAN"), ZH("CHINESE"), 
    ZH_CN("CHINESE_SIMPLIFIED"), ZH_TW("CHINESE_TRADITIONAL"), HR("CROATIAN"), 
    CS("CZECH"), DA("DANISH"), NL("DUTCH"), EN("ENGLISH"), EO("ESPERANTO"), 
    ET("ESTONIAN"), FI("FINNISH"), FR("FRENCH"), GL("GALICIAN"), 
    KA("GEORGIAN"), DE("GERMAN"), EL("GREEK"), GU("GUJARATI"), 
    IW("HEBREW"), HI("HINDI"), HU("HUNGARIAN"), IS("ICELANDIC"), ID("INDONESIAN"), 
    GA("IRISH"), IT("ITALIAN"), JA("JAPANESE"), KN("KANNADA"), 
    KK("KAZAKH"), KM("KHMER"), KO("KOREAN"), KU("KURDISH"), KY("KYRGYZ"), LO("LAOTHIAN"), 
    LV("LATVIAN"), LT("LITHUANIAN"), MK("MACEDONIAN"), MS("MALAY"), ML("MALAYALAM"), 
    MT("MALTESE"), MR("MARATHI"), MN("MONGOLIAN"), NE("NEPALI"), NO("NORWEGIAN"), 
    PS("PASHTO"), FA("PERSIAN"), PL("POLISH"), PT("PORTUGUESE"), 
    PA("PUNJABI"), RO("ROMANIAN"), RU("RUSSIAN"), SR("SERBIAN"), 
    SD("SINDHI"), SI("SINHALESE"), SK("SLOVAK"), SL("SLOVENIAN"), ES("SPANISH"), 
    SW("SWAHILI"), SV("SWEDISH"), TG("TAJIK"), TA("TAMIL"), TL("TAGALOG"), 
    TE("TELUGU"), TH("THAI"), TR("TURKISH"), UK("UKRANIAN"), 
    UR("URDU"), UZ("UZBEK"), VI("VIETNAMESE"), CY("WELSH"), YI("YIDDISH");
    
    //nom du langage
    private final String name;
    private static final HashSet<Language> unavailableLanguages = new HashSet<>();
    
    private Language(String name_) {
        this.name = name_;
    }
    
    /**
     * Ajoute les langues donnees a la liste des langues non disponibles.
     * @param languages les langues non disponibles
     */
    public static void addUnavailableLanguages(Language... languages) {
        unavailableLanguages.addAll(Arrays.asList(languages));
    }
    
    /**
     * Retourne la liste des langues non disponibles.
     * @return la liste des langues non disponibles
     */
    public static HashSet<Language> getUnavailableLanguages() {
        return new HashSet(unavailableLanguages);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
