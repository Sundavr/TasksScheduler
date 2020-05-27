package taskscheduler;

import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.io.IOException;
import javafx.stage.Stage;

/**
 * Conteneur des parametres de l'application.
 * @author JOHAN
 */
public class ParametersHandler {
    /**
     * Langue de l'application.
     */
    private Language language;
    /**
     * Stage principal de l'application.
     */
    private final Stage stage;
    /**
     * Stage de creation/modification de tache.
     */
    private final Stage taskStage;
    /**
     * Gestionnaire des properties de configuration de l'application.
     */
    private final EditablePropertiesManager configPropertiesManager;
    /**
     * Gestionnaire des properties d'internationalisation de l'application.
     */
    private final I18nPropertiesManager i18nPropertiesManager;
    /**
     * Icon de l'application
     */
    private final TrayIcon trayIcon;
    
    /**
     * Conteneur des parametres de l'application.
     * @param language langage par defaut
     * @param stage stage principal de l'application
     * @param taskStage stage de creation/modification de tache
     * @param configPropertiesManager
     * @param i18nPropertiesManager gestionnaire des properties d'internationalisation de l'application
     * @param trayIcon icone de l'application
     */
    public ParametersHandler(Language language, Stage stage, Stage taskStage, EditablePropertiesManager configPropertiesManager, I18nPropertiesManager i18nPropertiesManager, TrayIcon trayIcon) {
        this.stage = stage;
        this.taskStage= taskStage;
        this.configPropertiesManager = configPropertiesManager;
        this.i18nPropertiesManager = i18nPropertiesManager;
        this.trayIcon = trayIcon;
        updateLanguage(language);
    }
    
    /**
     * Conteneur des parametres de l'application.
     * @param stage stage principal de l'application
     * @param taskStage stage de creation/modification de tache
     * @param configPropertiesManager gestionnaire des properties de configuration de l'application
     * @param i18nPropertiesManager gestionnaire des properties d'internationalisation de l'application
     * @param trayIcon icone de l'application
     */
    public ParametersHandler(Stage stage, Stage taskStage, EditablePropertiesManager configPropertiesManager, I18nPropertiesManager i18nPropertiesManager, TrayIcon trayIcon) {
        this(Language.FR, stage, taskStage, configPropertiesManager, i18nPropertiesManager, trayIcon); //par défaut
    }
    
    /**
     * Retourne le langage de l'application.
     * @return le langage de l'application
     */
    public Language getLanguage() {
        return this.language;
    }
    
    /**
     * Met le langage utilise a jour sans persistance.
     * @param newLanguage la nouvelle langue a utiliser
     */
    private void updateLanguage(Language newLanguage) {
        this.language = newLanguage;
        this.i18nPropertiesManager.setLanguage(this.language);
        TaskAction.setLanguage(this.language);
        Trigger.setLanguage(this.language);
        When.setLanguage(this.language);
    }
    
    /**
     * Change le langage de l'application.
     * @param newLanguage la nouvelle langue a utiliser
     * @throws IOException exception levee quand il est mpossible d'ecrire 
     * dans le fichier config. A noter que le langage de l'application change tout de meme.
     */
    public void setLanguage(Language newLanguage) throws IOException {
        updateLanguage(newLanguage);
        this.configPropertiesManager.setProperty("Language", this.language);
        this.stage.setTitle(this.i18nPropertiesManager.readProperty("mainTitle"));
        this.taskStage.setTitle(this.i18nPropertiesManager.readProperty("mainTitle"));
        for (int i=0; i<this.trayIcon.getPopupMenu().getItemCount(); i++) {
            MenuItem item = this.trayIcon.getPopupMenu().getItem(i);
            if (!item.getLabel().equals("-")) { //!Separator
                item.setLabel(this.i18nPropertiesManager.readProperty(item.getName()));
            }
        }
        
    }
}
