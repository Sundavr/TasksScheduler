package taskscheduler;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import taskscheduler.Dialog.CheckBoxAlert;

/**
 * MainApp de l'interface graphique du planificateur de taches.
 * @author JOHAN
 */
public class MainApp extends Application {
    private Stage stage;
    private HomeController homeController;
    private boolean trayLaunched;
    private I18nPropertiesManager i18nPropertiesManager;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private TaskScheduler tasksScheduler;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.trayLaunched = false;
        Platform.setImplicitExit(false); //avoid Platform.runLater not being call when stage is hided
        
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);
        
        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("fxml/Home.fxml"));
        Scene homeScene = new Scene(homeLoader.load());
        
        FXMLLoader taskLoader = new FXMLLoader(getClass().getResource("fxml/Task.fxml"));
        Parent taskScene = taskLoader.load();
        Stage taskStage = new Stage();
        taskStage.getIcons().add(icon);
        taskStage.setScene(new Scene(taskScene));
        taskStage.initOwner(stage);
        taskStage.initModality(Modality.APPLICATION_MODAL);

        Language initLanguage = Language.FR;
        EditablePropertiesManager configPropertiesManager = new EditablePropertiesManager();
        try {
            initLanguage = Language.valueOf(configPropertiesManager.readProperty("Language"));
        } catch (IOException ioe) {
            try {
                configPropertiesManager.createFile();
            } catch (IOException ioe2) {
                Dialog.warning("Impossible de créer le fichier '" + configPropertiesManager.getPath() + "'", ioe2.getMessage());
            }
        } catch (IllegalArgumentException iae) { //no enum constant found for the language
            try {
                configPropertiesManager.deleteProperty("Language");
            } catch (IOException ioe) {/*Impossible to access to config file, should never happend*/}
        }
        this.i18nPropertiesManager = new I18nPropertiesManager(initLanguage, Language.values());
        //initialisation des enumérations
        HashMap<Language, String> startProgramMap = new HashMap<>();
        HashMap<Language, String> alertMap = new HashMap<>();
        HashMap<Language, String> hourMap = new HashMap<>();
        HashMap<Language, String> startUpMap = new HashMap<>();
        HashMap<Language, String> oneTimeMap = new HashMap<>();
        HashMap<Language, String> everyDaysMap = new HashMap<>();
        for (Language language : Language.values()) {
            try {
                startProgramMap.put(language, this.i18nPropertiesManager.readProperty(language, "taskActionStartProgram"));
                alertMap.put(language, this.i18nPropertiesManager.readProperty(language, "taskActionAlert"));
                hourMap.put(language, this.i18nPropertiesManager.readProperty(language, "triggerHour"));
                startUpMap.put(language, this.i18nPropertiesManager.readProperty(language, "triggerStartUp"));
                oneTimeMap.put(language, this.i18nPropertiesManager.readProperty(language, "whenOneTime"));
                everyDaysMap.put(language, this.i18nPropertiesManager.readProperty(language, "whenEveryDays"));
            } catch (IOException ioe) {
                Language.addUnavailableLanguages(language);
            }
        }
        if (!Language.getUnavailableLanguages().isEmpty()) {
            Dialog.warning("Language unavalaible", "The languages " + Language.getUnavailableLanguages() + " haven't been founds", false);
        }
        //make sure the init language is available
        if (Language.getUnavailableLanguages().contains(initLanguage)) { //not available
            try {
                configPropertiesManager.deleteProperty("Language");
            } catch (IOException ioe) {/*Impossible to clean up the config file*/}
            if (!Language.getUnavailableLanguages().contains(Language.EN)) {
                initLanguage = Language.EN;
            } else {
                List<Language> availableLanguages = Arrays.asList(Language.values());
                availableLanguages.removeAll(Language.getUnavailableLanguages());
                if (!availableLanguages.isEmpty()) {
                    initLanguage = availableLanguages.get(0);
                } else {
                    Dialog.error("No language available", "Application will exit");
                    exit();
                }
            }
        }
        this.i18nPropertiesManager.setLanguage(initLanguage); //definitive initLanguage
        TaskAction.init(startProgramMap, alertMap);
        Trigger.init(hourMap, startUpMap);
        When.init(oneTimeMap, everyDaysMap);
        
        addAppToTray();
        ParametersHandler parametersHandler = new ParametersHandler(initLanguage, stage, taskStage, configPropertiesManager, this.i18nPropertiesManager, this.trayIcon);
        
        this.homeController = ((HomeController)homeLoader.getController());
        TaskController taskController = ((TaskController)taskLoader.getController());
        
        String tasksFileName = "tasks.json";
        JsonManager tasksJson = new JsonManager(tasksFileName);
        this.tasksScheduler = new TaskScheduler(tasksJson, this.trayIcon);
        
        this.homeController.initialize(taskStage, taskController, configPropertiesManager, this.i18nPropertiesManager, parametersHandler, tasksScheduler);
        taskController.initialize(taskStage, this.homeController, this.i18nPropertiesManager);
        
        try {
            stage.setTitle(this.i18nPropertiesManager.readProperty("mainTitle"));
            taskStage.setTitle(this.i18nPropertiesManager.readProperty("mainTitle"));
        } catch (IOException ioe) {
            stage.setTitle("TaskScheduler");
            taskStage.setTitle("TaskScheduler");
        }
        
        stage.setOnCloseRequest(event -> {
            if (!this.trayLaunched) {
                exit();
                return;
            }
            try {
                switch (ButtonData.valueOf(configPropertiesManager.readProperty("Close"))) {
                    case FINISH:
                        exit();
                    case OTHER:
                        return;
                }
            } catch (IOException ex) {/*Pas de choix préenregistré ou invalide*/}
            ButtonType exitButton = new ButtonType("Exit", ButtonData.FINISH);
            ButtonType minimizeButton = new ButtonType("Minimize", ButtonData.OTHER);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            CheckBoxAlert alert;
            try {
                exitButton = new ButtonType(this.i18nPropertiesManager.readProperty("exit"), ButtonData.FINISH);
                minimizeButton = new ButtonType(this.i18nPropertiesManager.readProperty("minimize"), ButtonData.OTHER);
                cancelButton = new ButtonType(this.i18nPropertiesManager.readProperty("cancelButton"), ButtonData.CANCEL_CLOSE);
                alert = Dialog.createAlertWithCheckBox(this.i18nPropertiesManager.readProperty("exitAlertTitle"), this.i18nPropertiesManager.readProperty("exitAlertContent"), i18nPropertiesManager.readProperty("exitAlertCheckBox"), exitButton, minimizeButton, cancelButton);
            } catch (IOException ioe) {
                alert = Dialog.createAlertWithCheckBox("Close", "Do you want to exit or minimize the application ?"
                        + "\nExiting the application will cancel every scheduled tasks."
                        + "\nYou can change it later in the options", "Do not ask again", exitButton, minimizeButton, cancelButton);
            }
            ButtonData result = alert.showAndWait().get().getButtonData();
            if (!result.equals(ButtonData.CANCEL_CLOSE)) {
                try {
                    configPropertiesManager.setProperty("Close", (alert.getCheckBoxValue()) ? result : null);
                } catch (IOException ioe) {
                    try {
                        Dialog.error(MessageFormat.format(this.i18nPropertiesManager.readProperty("impossibleWrite"), configPropertiesManager.getPath()), ioe.getMessage());
                    } catch (IOException | NullPointerException ex) {
                        Dialog.warning("Impossible to write in the file '" + configPropertiesManager.getPath() + "'", ioe.getMessage());
                    }
                }
            }
            switch(result) {
                case FINISH:
                    exit();
                    break;
                case OTHER:
                    break;
                default: //canceled
                    event.consume();
            }
        });
        
        this.stage.setScene(homeScene);
        try {
            if (configPropertiesManager.readProperty("StartUp").equals(HomeController.MINIMIZE)) {
                hideStage();
            } else {
                showStage();
            }
        } catch (IOException ioe) {
            showStage();/*Pas de choix préenregistré ou invalide*/
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            if (!java.awt.SystemTray.isSupported()) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("noTraySupportTitle"), this.i18nPropertiesManager.readProperty("noTraySupportContent"));
                } catch (IOException ioe) {
                    Dialog.error("No system tray support", "Your system doesn't support tray, you won't be able to minimize the application as notification");
                }
                this.trayLaunched = false;
                return;
            }
            
            this.tray = java.awt.SystemTray.getSystemTray();
            BufferedImage trayIconImage = ImageIO.read(getClass().getResource("icon.png"));
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            int trayIconHeight = new TrayIcon(trayIconImage).getSize().height;
            this.trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, trayIconHeight, java.awt.Image.SCALE_SMOOTH));
            this.tray.add(this.trayIcon);
            
            this.trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if ((event.getButton() == MouseEvent.BUTTON1) && (event.getClickCount() == 1)) {
                        Platform.runLater(() -> {
                            if (stage.isShowing()) hideStage();
                            else showStage();
                        });
                    }
                }
            });
            
            MenuItem openMenuItem = new MenuItem("Open");
            openMenuItem.setName("openMenuItem");
            try {
                openMenuItem.setLabel(this.i18nPropertiesManager.readProperty("openMenuItem"));
            } catch (IOException ioe) {}
            openMenuItem.setFont(Font.decode(null).deriveFont(Font.BOLD));
            openMenuItem.addActionListener(event -> Platform.runLater(this::showStage));
            
            MenuItem minimizeMenuItem = new MenuItem("Minimize");
            minimizeMenuItem.setName("minimizeMenuItem");
            try {
                minimizeMenuItem.setLabel(this.i18nPropertiesManager.readProperty("minimizeMenuItem"));
            } catch (IOException ioe) {}
            minimizeMenuItem.setFont(Font.decode(null).deriveFont(Font.BOLD));
            minimizeMenuItem.addActionListener(event -> Platform.runLater(() -> hideStage()));
            
            MenuItem exitMenuItem = new MenuItem("Exit");
            exitMenuItem.setName("exitMenuItem");
            try {
                exitMenuItem.setLabel(this.i18nPropertiesManager.readProperty("exitMenuItem"));
            } catch (IOException ioe) {}
            exitMenuItem.addActionListener(event -> exit());
            
            this.trayLaunched = true;
            
            //setup the popup menu for the application.
            final PopupMenu popup = new PopupMenu();
            popup.add(openMenuItem);
            popup.add(minimizeMenuItem);
            popup.addSeparator();
            popup.add(exitMenuItem);
            this.trayIcon.setPopupMenu(popup);
        } catch (java.awt.AWTException | IOException ex) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("unableInitTrayTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("unableInitTrayContent"), ex.getMessage()));
            } catch (IOException | NullPointerException ex2) {
                Dialog.error("Unable to init tray", "Impossible to init tray, you won't be able to minimize the application as notification.\n" + ex.getMessage());
            }
            this.trayLaunched = false;
        }
    }
    
    /**
     * Affiche le message comme notification.
     * @param title titre du message
     * @param message le message a afficher
     * @param type type du message
     */
    public void displayMessage(String title, String message, MessageType type) {
        javax.swing.SwingUtilities.invokeLater(() -> this.trayIcon.displayMessage(title, message, type));
    }
    
    /**
     * Affiche le message comme notification.
     * @param title titre du message
     * @param message le message a afficher
     */
    public void displayMessage(String title, String message) {
        displayMessage(title, message, MessageType.INFO);
    }
    
    /**
     * Affiche le stage principal et s'assure qu'il est bien au premier plan.
     */
    private void showStage() {
        if (this.stage != null) {
            this.stage.show();
            this.stage.setIconified(false);
            this.stage.toFront();
            this.homeController.autoRefresh();
        }
    }
    
    /**
     * Cache le stage principal.
     */
    private void hideStage() {
        if (this.stage != null) {
            this.stage.hide();
            this.stage.toBack();
            this.homeController.stopAutoRefresh();
        }
    }
    
    /**
     * Stop l'application.
     */
    public void exit() {
        Platform.runLater(() -> {
            if (this.tasksScheduler != null) tasksScheduler.stop();
            if (this.tray != null) this.tray.remove(this.trayIcon);
            Platform.exit();
            System.exit(0); //s'assure de correctement stopper l'application
        });
    }
}
