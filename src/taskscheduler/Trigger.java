package taskscheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration des declencheurs possible d'une tache.
 * @author Johan
 */
public enum Trigger {
    HOUR, STARTUP;
    
    private static Language language = Language.EN; //par défaut
    private static final HashMap<Language, String> hourMap = new HashMap<>();
    private static final HashMap<Language, String> startUpMap = new HashMap<>();
    
    /**
     * Initialise l'enumeration avec i18n.
     * @param hourMap map qui associe a chaque langue disponible le texte de HOUR
     * @param startUpMap qui associe a chaque langue disponible le texte de STARTUP
     */
    public static void init(Map<Language, String> hourMap, Map<Language, String> startUpMap) {
        Trigger.hourMap.putAll(hourMap);
        Trigger.startUpMap.putAll(startUpMap);
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
     * s'il n'est pas disponible, retourne l'enumeration (e.g. HOUR)
     * @return le nom de l'enumeration
     */
    public String getName() {
        switch (this) {
            case HOUR:
                return (hourMap.containsKey(language)) ? hourMap.get(language) : "HOUR";
            default: //STARTUP
                return (startUpMap.containsKey(language)) ? startUpMap.get(language) : "STARTUP";
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
