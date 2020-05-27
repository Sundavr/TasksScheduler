package taskscheduler;

/**
 * Operating system detector.
 * @author Johan
 */
public class OSDetector {
    public enum OS {
        WIN, UNIX, MAC, SOLARIS
    }
    
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static OS OPERATING_SYSTEM = null; //cache
    
    /**
     * Retourne true s'il s'agit de windows, false sinon.
     * @return true s'il s'agit de windows, false sinon
     */
    public static boolean isWindows() {
        return (OS_NAME.contains("win"));
    }

    /**
     * Retourne true s'il s'agit de unix, false sinon.
     * @return true s'il s'agit de unix, false sinon
     */
    public static boolean isUnix() {
        return (0 < OS_NAME.indexOf("nix") || OS_NAME.contains("nux") || OS_NAME.indexOf("aix") > 0 );
    }
    
    /**
     * Retourne true s'il s'agit de mac, false sinon.
     * @return true s'il s'agit de mac, false sinon
     */
    public static boolean isMac() {
        return (OS_NAME.contains("mac"));
    }
    
    /**
     * Retourne true s'il s'agit de solaris, false sinon.
     * @return true s'il s'agit de solaris, false sinon
     */
    public static boolean isSolaris() {
        return (OS_NAME.contains("sunos"));
    }
    
    /**
     * Retourne le systeme d'exploitation de la machine, null s'il est inconnu.
     * @return le systeme d'exploitation de la machine, null s'il est inconnu
     */
    public static OS getOS() {
        if (OPERATING_SYSTEM != null) return OPERATING_SYSTEM;
        if (isWindows()) OPERATING_SYSTEM = OS.WIN;
        else if (isUnix()) OPERATING_SYSTEM = OS.UNIX;
        else if (isMac()) OPERATING_SYSTEM = OS.MAC;
        else if (isSolaris()) OPERATING_SYSTEM = OS.SOLARIS;
        return OPERATING_SYSTEM;
    }
}
