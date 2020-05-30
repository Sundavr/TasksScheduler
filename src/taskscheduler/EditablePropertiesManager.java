package taskscheduler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Gestionnaire de properties editables.
 * @author Johan
 */
public class EditablePropertiesManager extends PropertiesManager {
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
    public EditablePropertiesManager(String directory, String fileName) {
        setDirectory(directory);
        setFileName(fileName);
    }
    
    /**
     * Constructeur du gestionnaire de properties.
     * @param fileName nom du fichier properties
     */
    public EditablePropertiesManager(String fileName) {
        this(DEFAULT_DIRECTORY, fileName);
    }
    
    /**
     * Constructeur du gestionnaire de properties.
     */
    public EditablePropertiesManager() {
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
        if (!this.directory.isEmpty() && (this.directory.charAt(this.directory.length()-1) != File.separator.toCharArray()[0])) this.directory += File.separator;
    }
    
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (!this.fileName.contains(".")) { //extension oubliée
            this.fileName += ".properties";
        }
    }
    
    /**
     * Cree le fichier properties s'il n'existe pas deja.
     * @throws java.io.IOException exception levee quand il est impossible de creer le fichier properties
     */
    public void createFile() throws IOException {
        File dir = new File(this.directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(getPath());
        if (!file.exists()) {
            file.createNewFile();
        } else if (!file.isFile()) {
            throw new IOException("The file '" + file.getAbsolutePath() + "' is a directory, impossible to create the properties file");
        }
    }
    
    /**
     * Ajoute ou met a jour la propriete donnee.
     * @param key nom de la propriete
     * @param value valeur associe a cette propriete
     * @throws IOException Impossible de lire ou ecrire dans le fichier properties
     */
    public void setProperty(String key, Object value) throws IOException {
        if (key == null) return;
        createFile();
        try (FileReader reader = new FileReader(getPath());
            FileWriter writer = new FileWriter(getPath())) {
            this.properties.load(reader);
            if (value == null) {
                this.properties.remove(key);
            } else {
                this.properties.put(key, value.toString());
            }
            this.properties.store(writer, null);
        }
    }
    
    @Override
    public String readProperty(String key) throws IOException {
        try (FileReader reader = new FileReader(getPath())) {
            this.properties.load(reader);
            if (this.properties.get(key) == null) throw new IOException("No property name '" + key + "' founded");
            return this.properties.getProperty(key);
        }
    }
    
    @Override
    public String getProperty(String key) throws IOException {
        return readProperty(key);
    }
    
    /**
     * Supprime la propriete donnee.
     * @param key nom de la propriete a supprimer
     * @throws IOException Impossible de lire ou ecrire dans le fichier properties
     */
    public void deleteProperty(String key) throws IOException {
        try (FileReader reader = new FileReader(getPath());
            FileWriter writer = new FileWriter(getPath())) {
            this.properties.load(reader);
            this.properties.remove(key);
            this.properties.store(writer, null);
        }
    }
}
