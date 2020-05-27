package taskscheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestionnaire de properties, la racine des fichiers properties commence dans 
 * le dossiers ressources du projet (ex : src sous netbeans).
 * Les properties ne peuvent pas etre modifiees car elle seront incluses dans 
 * le jar de l'application une fois compilee.
 * @author Johan
 */
public class RessourcesPropertiesManager extends PropertiesManager {
    public static final String DEFAULT_DIRECTORY = "";
    public static final String DEFAULT_FILE_NAME = "config.properties";
    private final Properties properties  = new Properties();
    private String directory;
    private String fileName;
    
    /**
     * Constructeur du gestionnaire de properties.
     * @param directory chemin du repertoire utilise
     * @param fileName nom du fichier properties
     */
    public RessourcesPropertiesManager(String directory, String fileName) {
        setDirectory(directory);
        setFileName(fileName);
    }
    
    /**
     * Constructeur du gestionnaire de properties.
     * @param fileName nom du fichier properties
     */
    public RessourcesPropertiesManager(String fileName) {
        this(DEFAULT_DIRECTORY, fileName);
    }
    
    /**
     * Constructeur du gestionnaire de properties.
     */
    public RessourcesPropertiesManager() {
        this(DEFAULT_DIRECTORY, DEFAULT_FILE_NAME);
    }
    
    @Override
    public String getDirectory() {
        return directory;
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public String getPath() {
        return getPath(this.fileName);
    }
    
    @Override
    public String getPath(String fileName) {
        return this.directory + fileName;
    }
    
    @Override
    public void setDirectory(String directory) {
        this.directory = directory;
        if (!this.directory.isEmpty() && (this.directory.charAt(this.directory.length()-1) != '/')) this.directory += '/';
    }
    
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (!this.fileName.contains(".")) { //extension oubliée
            this.fileName += ".properties";
        }
    }
    
    @Override
    public String readProperty(String key) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(getPath());
        if (inputStream == null) throw new IOException("No file '" + getPath() + "' found");
        this.properties.load(inputStream);
        if (this.properties.getProperty(key) == null) throw new IOException("No property name '" + key + "' founded");
        return this.properties.getProperty(key);
    }
    
    @Override
    public String getProperty(String key) throws IOException {
        return readProperty(key);
    }
}
