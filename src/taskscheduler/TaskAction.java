package taskscheduler;

/**
 * Enumeration des actions possibles pour une tache.
 * @author Johan
 */
public enum TaskAction {
    START_PROGRAM("Start a program","Démarrer un programme"), ALERT("Display an alert","Afficher une alerte");
    
    private static Language language = Language.ENG;
    private final String englishName;
    private final String frenchName;
    
    private TaskAction(String englishName, String frenchName) {
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
