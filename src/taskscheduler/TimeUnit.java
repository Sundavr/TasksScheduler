package taskscheduler;

/**
 * Enumeration des unites de temps disponibles.
 * @author Johan
 */
public enum TimeUnit {
    SECONDS("s"), MINUTES("m"), HOURS("h");
    
    private final String name;
    
    private TimeUnit(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
