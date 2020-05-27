package taskscheduler;

/**
 * Enumeration des langages disponibles.
 * @author JOHAN
 */
public enum Language {
    FR("FR"), ENG("ENG");
    
    //nom du langage
    private final String name;
    
    private Language(String name_) {
        this.name = name_;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
