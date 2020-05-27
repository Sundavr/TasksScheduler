package taskscheduler;

import java.io.IOException;
import java.security.InvalidParameterException;
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
     * @param propertiesManagersMap map des gestionnaires de properties associes au langage
     * @param language langage a utiliser
     */
    public I18nPropertiesManager(String directory, String fileName, Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        if (propertiesManagersMap.containsValue(null)) throw new NullPointerException("La map de properties ne peut pas contenir de valeur null");
        if (!propertiesManagersMap.containsKey(language)) throw new NullPointerException("La map ne contient pas de clé '" + language.name() + "'");
        this.propertiesManagersMap = new HashMap<>(propertiesManagersMap);
        this.language = language;
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param directory chemin du repertoire utilise
     * @param fileName nom du fichier properties
     * @param language langage du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes au langage donne
     */
    public I18nPropertiesManager(String directory, String fileName, Language language, RessourcesPropertiesManager propertiesManager) {
        this(directory, fileName, new HashMap<Language, RessourcesPropertiesManager>() {{
                put(language, propertiesManager);
        }}, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param fileName nom du fichier properties
     * @param propertiesManagersMap map des gestionnaires de properties associes au langage
     * @param language langage a utiliser
     */
    public I18nPropertiesManager(String fileName, Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, fileName, propertiesManagersMap, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param fileName nom du fichier properties
     * @param language langage du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes au langage donne
     */
    public I18nPropertiesManager(String fileName, Language language, RessourcesPropertiesManager propertiesManager) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, fileName, language, propertiesManager);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param propertiesManagersMap map des gestionnaires de properties associes au langage
     * @param language langage a utiliser
     */
    public I18nPropertiesManager(Map<Language, RessourcesPropertiesManager> propertiesManagersMap, Language language) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, RessourcesPropertiesManager.DEFAULT_FILE_NAME, propertiesManagersMap, language);
    }
    
    /**
     * Constructeur du gestionnaire de properties avec i18n.
     * @param language langage du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes au langage donne
     */
    public I18nPropertiesManager(Language language, RessourcesPropertiesManager propertiesManager) {
        this(RessourcesPropertiesManager.DEFAULT_DIRECTORY, RessourcesPropertiesManager.DEFAULT_FILE_NAME, language, propertiesManager);
    }
    
    /**
     * Ajoute un gestionnaire de properties associe au langage donne, 
     * si un gestionnaire de properties existait deja pour ce langage, le remplace.
     * @param language langage du gestionnaire de properties
     * @param propertiesManager gestionnaires de properties associes au langage donne
     */
    public void addPropertiesManager(Language language, RessourcesPropertiesManager propertiesManager) {
        this.propertiesManagersMap.put(language, propertiesManager);
    }
    
    /**
     * Supprime le hestionnaire de properties associe au langage donnee.
     * @param language le langage a supprimer
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
     * Retourne le langage actuellement utilise.
     * @return le langage actuellement utilise
     */
    public Language getLanguage() {
        return this.language;
    }
    
    /**
     * Retourne le chemin du repertoire utilise pour le langage donne, 
     * <code>null</code> si le langage n'est pas disponible.
     * @param language le langage dont on souhaite connaitre le chemin du repertoire utilise
     * @return le chemin du repertoire utilise pour le langage donne, <code>null</code> si le langage n'est pas disponible
     */
    public String getDirectory(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getDirectory();
    }
    
    /**
     * Retourne le chemin du repertoire utilise pour le langage utilise
     * @return le chemin du repertoire utilise pour le langage utilise
     */
    @Override
    public String getDirectory() {
        return getDirectory(this.language);
    }
    
    /**
     * Retourne le nom du fichier properties utilise pour le langage donne, 
     * <code>null</code> si le langage n'est pas disponible.
     * @param language le langage dont on souhaite connaitre le nom du fichier properties utilise
     * @return le nom du fichier properties utilise pour le langage donne, <code>null</code> si le langage n'est pas disponible
     */
    public String getFileName(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getFileName();
    }
    
    /**
     * Retourne le nom du fichier properties utilise pour le langage utilise 
     * @return le nom du fichier properties utilise pour le langage utilise
     */
    @Override
    public String getFileName() {
        return getFileName(this.language);
    }
    
    /**
     * Retourne le chemin de stockage pour le langage donne, <code>null</code> s'il n'y en a pas.
     * @param language le langage dont on souhaite connaitre le chemin de stockage
     * @return le chemin de stockage pour le langage donne, <code>null</code> s'il n'y en a pas
     */
    public String getPath(Language language) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getPath();
    }
    
    /**
     * Retourne le chemin de stockage pour le langage utilise
     * @return le chemin de stockage pour le langage utilise
     */
    @Override
    public String getPath() {
        return getPath(this.language);
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne pour le langage donne, 
     * <code>null</code> s'il n'y en a pas.
     * @param language le langage dont on souhaite connaitre le chemin de stockage
     * @param fileName nom du fichier
     * @return le chemin de stockage pour le fichier donne pour le langage donne
     */
    public String getPath(Language language, String fileName) {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).getPath(fileName);
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne pour le langage utilise.
     * @param fileName nom du fichier
     * @return le chemin de stockage pour le fichier donne pour le langage utilise
     */
    @Override
    public String getPath(String fileName) {
        return getPath(this.language, fileName);
    }
    
    /**
     * Change le langage utilise.
     * @param language nouveau langage a utiliser
     * @exception InvalidParameterException exception levee aucun gestionnaire de properties n'est associe au langage donne
     */
    public void setLanguage(Language language) throws InvalidParameterException {
        if (!this.propertiesManagersMap.containsKey(language)) throw new InvalidParameterException("Le langage '" + language.name() + "' n'est pas disponible");
        this.language = language;
    }
    
    /**
     * Modifie le repertoire utilise pour le langage donne, 
     * sans effet s'il le langage n'est pas disponible.
     * @param language le langage dont on souhaite changer le repertoire
     * @param directory le chemin correspondant au nouveau repertoire 
     */
    public void setDirectory(Language language, String directory) {
        if (!this.propertiesManagersMap.containsKey(language)) return;
        this.propertiesManagersMap.get(language).setDirectory(directory);
    }
    
    /**
     * Modifie le repertoire utilise pour le langage utilise.
     * @param directory le chemin correspondant au nouveau repertoire 
     */
    @Override
    public void setDirectory(String directory) {
        setDirectory(this.language, directory);
    }

    /**
     * Modifie le nom du fichier properties utilise pour le langage donne, 
     * sans effet s'il le langage n'est pas disponible.
     * @param language le langage dont on souhaite changer le nom du fichier properties
     * @param fileName le nouveau nom du fichier
     */
    public void setFileName(Language language, String fileName) {
        if (!this.propertiesManagersMap.containsKey(language)) return;
        this.propertiesManagersMap.get(language).setFileName(fileName);
    }
    
    /**
     * Modifie le nom du fichier properties utilise pour le langage utilise.
     * @param fileName le nouveau nom du fichier
     */
    @Override
    public void setFileName(String fileName) {
        setFileName(this.language, fileName);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour le langage donnee, 
     * <code>null</code> si le langage n'est pas disponible.
     * @param language le langage dont on souhaite ajouter ou modifier une propriete
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee ou <code>null</code> si le langage n'est pas disponible
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public String readProperty(Language language, String key) throws IOException {
        if (!this.propertiesManagersMap.containsKey(language)) return null;
        return this.propertiesManagersMap.get(language).readProperty(key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour le langage utilise.
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    @Override
    public String readProperty(String key) throws IOException {
        return readProperty(this.language, key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour le langage donne, 
     * <code>null</code> si le langage n'est pas disponible.
     * Comportement identique a {@link #readProperty(Language, String) readProperty(language, key)}.
     * @param language le langage dont on souhaite ajouter ou modifier une propriete
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readProperty(Language, String)
     */
    public String getProperty(Language language, String key) throws IOException {
        return readProperty(language, key);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee pour le langage utilise, 
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
