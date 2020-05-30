package taskscheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration des choix de declenchement possibles.
 * @author Johan
 */
public enum When {
   ONE_TIME, EVERY_DAYS;
    
    private static Language language = Language.EN; //par défaut
    private static final HashMap<Language, String> oneTimeMap = new HashMap<>();
    private static final HashMap<Language, String> everyDaysMap = new HashMap<>();
    
    /**
     * Initialise l'enumeration avec i18n.
     * @param oneTimeMap map qui associe a chaque langue disponible le texte de ONE_TIME
     * @param everyDaysMap qui associe a chaque langue disponible le texte de EVERY_DAYS
     */
    public static void init(Map<Language, String> oneTimeMap, Map<Language, String> everyDaysMap) {
        When.oneTimeMap.putAll(oneTimeMap);
        When.everyDaysMap.putAll(everyDaysMap);
    }
    
    /**
     * Modifie la langue utilisee.
     * @param language_ nouvelle langue utilisee
     */
    public static void setLanguage(Language language_) {
        language = language_;
    }
    
    /**
     * Retourne la langue utilisee.
     * @return la langue utilisee
     */
    public static Language getLanguage() {
        return language;
    }
    
    /**
     * Retourne le nom de l'enumeration en fonction de la langue utilisee, 
     * s'il n'est pas disponible, retourne l'enumeration (e.g. EVERY_DAYS)
     * @return le nom de l'enumeration
     */
    public String getName() {
        switch (this) {
            case EVERY_DAYS:
                return (everyDaysMap.containsKey(language)) ? everyDaysMap.get(language) : "EVERY_DAYS";
            default: //ONE_TIME
                return (oneTimeMap.containsKey(language)) ? oneTimeMap.get(language) : "ONE_TIME";
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
