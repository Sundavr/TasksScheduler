package taskscheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration des actions possibles pour une tache.
 * @author Johan
 */
public enum TaskAction {
    START_PROGRAM, ALERT;
    
    private static Language language = Language.EN; //par défaut
    private static final HashMap<Language, String> startProgramMap = new HashMap<>();
    private static final HashMap<Language, String> alertMap = new HashMap<>();
    
    /**
     * Initialise l'enumeration avec i18n.
     * @param startProgramMap map qui associe a chaque langue disponible le texte de START_PROGRAM
     * @param alertMap qui associe a chaque langue disponible le texte de ALERT
     */
    public static void init(Map<Language, String> startProgramMap, Map<Language, String> alertMap) {
        TaskAction.startProgramMap.putAll(startProgramMap);
        TaskAction.alertMap.putAll(alertMap);
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
     * s'il n'est pas disponible, retourne l'enumeration (e.g. ALERT)
     * @return le nom de l'enumeration
     */
    public String getName() {
        switch (this) {
            case ALERT:
                return (alertMap.containsKey(language)) ? alertMap.get(language) : "ALERT";
            default: //START_PROGRAM
                return (startProgramMap.containsKey(language)) ? startProgramMap.get(language) : "START_PROGRAM";
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
