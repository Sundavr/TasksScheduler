package taskscheduler;

/**
 * Enumeration des declencheurs possible d'une tache.
 * @author Johan
 */
public enum Trigger {
    HOUR("Hour", "Heure"), STARTUP("Application startup", "Au démarrage de l'application");
    
    private static Language language = Language.ENG;
    private final String englishName;
    private final String frenchName;
    
    private Trigger(String englishName, String frenchName) {
        this.englishName = englishName;
        this.frenchName = frenchName;
    }
    
    public static void setLanguage(Language language_) {
        language = language_;
    }
    
    public static Language getLanguage() {
        return language;
    }
    
    public String getName() {
        switch(language) {
            case ENG:
                return this.englishName;
            default:
                return this.frenchName;
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
