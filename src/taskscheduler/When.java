package taskscheduler;

/**
 * Enumeration des choix de declenchement possibles.
 * @author Johan
 */
public enum When {
   ONE_TIME("One time", "Une fois"), EVERY_DAYS("Every days", "Tous les jours");
    
    private static Language language = Language.ENG;
    private final String englishName;
    private final String frenchName;
    
    private When(String englishName, String frenchName) {
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
