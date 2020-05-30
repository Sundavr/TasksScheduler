package taskscheduler;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de properties avec internationalisation integree.
 * @author Johan
 */
public class I18nPropertiesManager extends PropertiesManager {
    private HashMap<Language, PropertiesManager> propertiesManagersMap;
    private Language language;
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param directory chemin du repertoire utilise
     * @param fileName nom du fichier properties
     * @param propertiesManagersMap map des gestionnaires de properties associes a la langue
     * @param language langue a utiliser
     */
    public I18nPropertiesManager(String directory, String fileName, Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        if (propertiesManagersMap.containsValue(null)) throw new NullPointerException("The properties map cannot contains null value");
        if (!propertiesManagersMap.containsKey(language)) throw new NullPointerException("The map doesn't contains any key name '" + language.name() + "'");
        this.propertiesManagersMap = new HashMap<>(propertiesManagersMap);
        this.language = language;
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param directory chemin du repertoire utilise
     * @param fileName nom du fichier properties
     * @param language langue du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes a la langue donnee
     */
    public I18nPropertiesManager(String directory, String fileName, Language language, RessourcesPropertiesManager propertiesManager) {
        this(directory, fileName, new HashMap<Language, RessourcesPropertiesManager>() {{
                put(language, propertiesManager);
        }}, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param fileName nom du fichier properties
     * @param propertiesManagersMap map des gestionnaires de properties associes a la langue
     * @param language langue a utiliser
     */
    public I18nPropertiesManager(String fileName, Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, fileName, propertiesManagersMap, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param fileName nom du fichier properties
     * @param language langue du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes a la langue donnee
     */
    public I18nPropertiesManager(String fileName, Language language, RessourcesPropertiesManager propertiesManager) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, fileName, language, propertiesManager);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param propertiesManagersMap map des gestionnaires de properties associes a la langue
     * @param language langue a utiliser
     */
    public I18nPropertiesManager(Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, RessourcesPropertiesManager.DEFAULT_FILE_NAME, propertiesManagersMap, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param language langue du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes a la langue donnee
     */
    public I18nPropertiesManager(Language language, RessourcesPropertiesManager propertiesManager) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, RessourcesPropertiesManager.DEFAULT_FILE_NAME, language, propertiesManager);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n a partir d'un tableau 
     * de langue. Chaque PropertiesManager associe utilisera le fichier 
     * <tt>directory/LANG_SIGLE.properties</tt>
     * @param directory chemin du repertoire utilise
     * @param initLanguage langue par defaut
     * @param languages langue utilisees
     */
    public I18nPropertiesManager(String directory, Language initLanguage, Language... languages) {
        if (languages.length == 0) throw new NullPointerException("Impossible to init the I18nPropertiesManager without any language");
        if (Arrays.stream(languages).noneMatch(initLanguage::equals)) throw new NullPointerException("The map doesn't contains any key name '" + language.name() + "'");
        this.propertiesManagersMap = new HashMap<>();
        for (Language lang : Language.values()) {
            this.propertiesManagersMap.put(lang, new RessourcesPropertiesManager(directory, lang.name() + ".properties"));
        }
        this.language = initLanguage;
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n a partir d'un tableau 
     * de langue. Chaque PropertiesManager associe utilisera le fichier 
     * <tt>directory/LANG_SIGLE.properties</tt> et la langue par defaut sera 
     * la premiere langue donne
     * @param directory chemin du repertoire utilise
     * @param languages langues utilisees
     */
    public I18nPropertiesManager(String directory, Language... languages) {
        this(directory, languages[0], languages);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n a partir d'un tableau 
     * de langue. Chaque PropertiesManager associe utilisera le fichier 
     * <tt>texts/LANG_SIGLE.properties</tt> et la langue par defaut sera 
     * la premiere langue donne
     * @param initLanguage langue par defaut
     * @param languages langues utilisees
     */
    public I18nPropertiesManager(Language initLanguage, Language... languages) {
        this("texts", initLanguage, languages);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n a partir d'un tableau 
     * de langue. Chaque PropertiesManager associe utilisera le fichier 
     * <tt>texts/LANG_SIGLE.properties</tt> et la langue par defaut sera 
     * la premiere langue donne
     * @param languages langues utilisees
     */
    public I18nPropertiesManager(Language... languages) {
        this("texts", languages);
    }
    
    /**
     * Ajoute un gestionnaire de properties associe a la langue donnee, 
     * si un gestionnaire de properties existait deja pour cette langue, le remplace.
     * @param language langue du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes a la langue donnee
     */
    public void addPropertiesManager(Language language, RessourcesPropertiesManager propertiesManager) {
        this.propertiesManagersMap.put(language, propertiesManager);
    }
    
    /**
     * Supprime le hestionnaire de properties associe a la langue donnee.
     * @param language la langue a supprimer
     * @exception IllegalStateException exception levee si le dernier gestionnaire de properties
     * est supprime, il doit toujours y avoir au moins un gestionnaire de proprietes disponible
     */
    public void removePropertiesManager(Language language) throws IllegalStateException {
        if (this.propertiesManagersMap.size() < 2) throw new IllegalStateException("The propertiesManagerMap cannot be empty, impossible to remove '" + language + "'");
        this.propertiesManagersMap.remove(language);
        if (this.language.equals(language)) {
            this.language = this.propertiesManagersMap.keySet().iterator().next();
        }
    }
    
    /**
     * Retourne la langue actuellement utilisee.
     * @return la langue actuellement utilisee
     */
    public Language getLanguage() {
        return this.language;
    }
    
    /**
     * Retourne le chemin du repertoire utilise pour la langue donnee, 
     * <code>null</code> si la langue n'est pas disponible.
     * @param language la langue dont on souhaite connaitre le chemin du repertoire utilise
     * @return le chemin du repertoire utilise pour la langue donnee, <code>null</code> si la langue n'est pas disponible
     */
    public String getDirectory(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getDirectory();
    }
    
    /**
     * Retourne le chemin du repertoire utilise pour la langue utilisee
     * @return le chemin du repertoire utilise pour la langue utilisee
     */
    @Override
    public String getDirectory() {
        return getDirectory(this.language);
    }
    
    /**
     * Retourne le nom du fichier properties utilise pour la langue donnee, 
     * <code>null</code> si la langue n'est pas disponible.
     * @param language la langue dont on souhaite connaitre le nom du fichier properties utilise
     * @return le nom du fichier properties utilise pour la langue donnee, 
     * <code>null</code> si la langue n'est pas disponible
     */
    public String getFileName(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getFileName();
    }
    
    /**
     * Retourne le nom du fichier properties utilise pour la langue utilisee
     * @return le nom du fichier properties utilise pour la langue utilisee
     */
    @Override
    public String getFileName() {
        return getFileName(this.language);
    }
    
    /**
     * Retourne le chemin de stockage pour la langue donnee, <code>null</code> s'il n'y en a pas.
     * @param language la langue dont on souhaite connaitre le chemin de stockage
     * @return le chemin de stockage pour la langue donnee, <code>null</code> s'il n'y en a pas
     */
    public String getPath(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getPath();
    }
    
    /**
     * Retourne le chemin de stockage pour la langue utilisee
     * @return le chemin de stockage pour la langue utilisee
     */
    @Override
    public String getPath() {
        return getPath(this.language);
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne pour la langue donnee, 
     * <code>null</code> s'il n'y en a pas.
     * @param language la langue dont on souhaite connaitre le chemin de stockage
     * @param fileName nom du fichier
     * @return le chemin de stockage pour le fichier donne pour la langue donnee
     */
    public String getPath(Language language, String fileName) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getPath(fileName);
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne pour la langue utilisee.
     * @param fileName nom du fichier
     * @return le chemin de stockage pour le fichier donne pour la langue utilisee
     */
    @Override
    public String getPath(String fileName) {
        return getPath(this.language, fileName);
    }
    
    /**
     * Retourne <code>true</code> si la langue est disponibe, <code>false</code> sinon.
     * @param language la langue a tester
     * @return <code>true</code> si la langue est disponibe, <code>false</code> sino
     */
    public boolean containsLanguage(Language language) {
        return this.propertiesManagersMap.containsKey(language);
    }
    
    /**
     * Change la langue utilisee.
     * @param language nouvelle langue a utiliser
     * @exception InvalidParameterException exception levee aucun gestionnaire de properties n'est associe a la langue donnee
     */
    public void setLanguage(Language language) throws InvalidParameterException {
        if (!this.propertiesManagersMap.containsKey(language)) throw new InvalidParameterException("The language '" + language.name() + "' isn't available");
        this.language = language;
    }
    
    /**
     * Modifie le repertoire utilise pour la langue donne, 
     * sans effet s'il la langue n'est pas disponible.
     * @param language la langue dont on souhaite changer le repertoire
     * @param directory le chemin correspondant au nouveau repertoire 
     */
    public void setDirectory(Language language, String directory) {
        if (!this.propertiesManagersMap.containsKey(language)) return;
        this.propertiesManagersMap.get(language).setDirectory(directory);
    }
    
    /**
     * Modifie le repertoire utilise pour la langue utilise.
     * @param directory le chemin correspondant au nouveau repertoire 
     */
    @Override
    public void setDirectory(String directory) {
        setDirectory(this.language, directory);
    }

    /**
     * Modifie le nom du fichier properties utilise pour la langue donne, 
     * sans effet s'il la langue n'est pas disponible.
     * @param language la langue dont on souhaite changer le nom du fichier properties
     * @param fileName le nouveau nom du fichier
     */
    public void setFileName(Language language, String fileName) {
        if (!this.propertiesManagersMap.containsKey(language)) return;
        this.propertiesManagersMap.get(language).setFileName(fileName);
    }
    
    /**
     * Modifie le nom du fichier properties utilise pour la langue utilise.
     * @param fileName le nouveau nom du fichier
     */
    @Override
    public void setFileName(String fileName) {
        setFileName(this.language, fileName);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour la langue donnee, 
     * <code>null</code> si la langue n'est pas disponible.
     * @param language la langue dont on souhaite ajouter ou modifier une propriete
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee ou <code>null</code> si la langue n'est pas disponible
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public String readProperty(Language language, String key) throws IOException {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).readProperty(key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour la langue utilise.
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    @Override
    public String readProperty(String key) throws IOException {
        return readProperty(this.language, key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour la langue donne, 
     * <code>null</code> si la langue n'est pas disponible.
     * Comportement identique a {@link #readProperty(Language, String) readProperty(language, key)}.
     * @param language la langue dont on souhaite ajouter ou modifier une propriete
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readProperty(Language, String)
     */
    public String getProperty(Language language, String key) throws IOException {
        return readProperty(language, key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour la langue utilise, 
     * comportement identique a {@link #readProperty(String) readProperty(key)}.
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readProperty(String)
     */
    @Override
    public String getProperty(String key) throws IOException {
        return readProperty(key);
    }
}
