package taskscheduler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONException;
import taskscheduler.ActionsManager.Action;
import taskscheduler.ActionsManager.ActionsManager;
import taskscheduler.ActionsManager.InvertOperation;
import taskscheduler.ActionsManager.OperationType;

/**
 * Page d'acceuil du TaskScheduler.
 * @author JOHAN
 */
public class HomeController implements PropertyChangeListener {
    /**
     * Ouvrir l'application.
     */
    public static final String OPEN = "Open";
    /**
     * Minimiser l'application.
     */
    public static final String MINIMIZE = "Minimize";
    public String tasksFileName;
    private Stage taskStage;
    private TaskController taskController;
    private EditablePropertiesManager configPropertiesManager;
    private I18nPropertiesManager i18nPropertiesManager;
    private ParametersHandler parametersHandler;
    private TaskScheduler tasksScheduler;
    private ActionsManager actionsManager;
    private boolean refreshFailed;
    private TaskModel copiedTaskModel;
    private Timer autoRefreshTimer;
    private ButtonType okButtonType;
    private ButtonType cancelButtonType;
    
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu optionsMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private Button allTasksButton;
    @FXML
    private Button scheduledTasksButton;
    @FXML
    private MenuItem newTaskMenuItem;
    @FXML
    private MenuItem cancelTasksMenuItem;
    @FXML
    private MenuItem resheduleTasksMenuItem;
    @FXML
    private MenuItem startUpMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem duplicateMenuItem;
    @FXML
    private MenuItem languageMenuItem;
    @FXML
    private MenuItem exitActionMenuItem;
    @FXML
    private MenuItem missedTasksMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private TableView<TaskModel> tasksTable;
    @FXML
    private TableColumn<TableView<TaskModel>, String> nameColumn;
    @FXML
    private TableColumn<TableView<TaskModel>, String> triggerColumn;
    @FXML
    private TableColumn<TableView<TaskModel>, String> actionColumn;
    @FXML
    private TableColumn<TableView<TaskModel>, String> descriptionColumn;
    @FXML
    private TableView<TaskModel> scheduledTasksTable;
    @FXML
    private TableColumn<TableView<TaskModel>, String> nameColumn2;
    @FXML
    private TableColumn<TableView<TaskModel>, String> actionColumn2;
    @FXML
    private TableColumn<TableView<TaskModel>, String> repeatColumn2;
    @FXML
    private TableColumn<TableView<TaskModel>, Time> timeLeftColumn2;
    @FXML
    private HBox tasksTableButtons;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox scheduledTasksTableButtons;
    @FXML
    private Button cancelButton;
    
    /**
     * Initialise la page d'accueil.
     * @param taskStage stage de creation/modification de tache
     * @param taskController controleur de la page de creation/modification de tache
     * @param configPropertiesManager gestionnaire des properties de configuration de l'application
     * @param i18nPropertiesManager gestionnaire des properties d'internationalisation de l'application
     * @param parametersHandler handler des parametres de l'application
     * @param tasksScheduler planificateur de taches
     */
    void initialize(Stage taskStage, TaskController taskController, EditablePropertiesManager configPropertiesManager, I18nPropertiesManager i18nPropertiesManager, ParametersHandler parametersHandler, TaskScheduler tasksScheduler) {
        this.taskStage = taskStage;
        this.taskController = taskController;
        this.configPropertiesManager = configPropertiesManager;
        this.i18nPropertiesManager = i18nPropertiesManager;
        this.parametersHandler = parametersHandler;
        this.tasksScheduler = tasksScheduler;
        this.tasksFileName = this.tasksScheduler.getFileName();
        this.actionsManager = new ActionsManager();
        this.refreshFailed = false;
        this.okButtonType = ButtonType.OK;
        this.cancelButtonType = ButtonType.CANCEL;
        //tasksTableView
        this.nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        this.triggerColumn.setCellValueFactory(new PropertyValueFactory("trigger"));
        this.actionColumn.setCellValueFactory(new PropertyValueFactory("action"));
        this.descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
        try {
            this.tasksTable.setPlaceholder(new Text(this.i18nPropertiesManager.readProperty("tasksTablePlaceholder")));
        } catch (IOException ioe) {
            this.tasksTable.setPlaceholder(new Text("No task available"));
        }
        if (this.tasksTable.getSelectionModel().getSelectedItem() == null) {
            this.duplicateMenuItem.setDisable(true);
            this.editButton.setDisable(true);
            this.deleteButton.setDisable(true);
        } else {
            this.duplicateMenuItem.setDisable(false);
            this.editButton.setDisable(false);
            this.deleteButton.setDisable(false);
        }
        this.tasksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.duplicateMenuItem.setDisable(true);
                this.editButton.setDisable(true);
                this.deleteButton.setDisable(true);
            } else {
                this.duplicateMenuItem.setDisable(false);
                this.editButton.setDisable(false);
                this.deleteButton.setDisable(false);
            }
        });
        refresh();
        //sheduledTasksTableView
        this.nameColumn2.setCellValueFactory(new PropertyValueFactory("name"));
        this.actionColumn2.setCellValueFactory(new PropertyValueFactory("action"));
        this.repeatColumn2.setCellValueFactory(new PropertyValueFactory("repeatsLeft"));
        this.timeLeftColumn2.setCellValueFactory(new PropertyValueFactory("timeLeft"));
        try {
            this.scheduledTasksTable.setPlaceholder(new Text(this.i18nPropertiesManager.readProperty("tasksTablePlaceholder")));
        } catch (IOException ioe) {
            this.scheduledTasksTable.setPlaceholder(new Text("No task scheduled"));
        }
        if (this.scheduledTasksTable.getSelectionModel().getSelectedItem() == null) {
            this.cancelButton.setDisable(true);
        } else {
            this.cancelButton.setDisable(false);
        }
        this.scheduledTasksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (this.scheduledTasksTable.getSelectionModel().getSelectedItem() == null) {
                this.cancelButton.setDisable(true);
            } else {
                this.cancelButton.setDisable(false);
            }
        });
        this.actionsManager.addPropertyChangeListener(this, ActionsManager.NB_UNDO_PROPERTY, ActionsManager.NB_REDO_PROPERTY);
        this.tasksScheduler.addPropertyChangeListener(this);
        //Sélection de TableView à afficher
        selectTasksView();
        //Liste des tâches
        ArrayList<Task> tasksList = new ArrayList<>();
        try {
            this.tasksScheduler.loadTasks(tasksList);
        } catch (FileNotFoundException fnfe) {/*Aucun fichier ou illisible*/
        } catch (JSONException je) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleReadTaskTitle"), je.getMessage());
            } catch (IOException ioe) {
                Dialog.error("Impossible to read a task", je.getMessage());
            }
        } catch (InvalidPathException ipe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleGetTaskPath"), ipe.getMessage());
            } catch (IOException ioe) {
                Dialog.error("Impossible to get a task's path", ipe.getMessage());
            }
        }
        tasksList.forEach(task -> {
            this.tasksTable.getItems().add(new TaskModel(task));
        });
        //Lance le planificateur de taches
        try {
            this.tasksScheduler.start();
        } catch (FileNotFoundException fnfe) {/*Aucun fichier ou illisible*/}
        autoRefresh();
        getMissedTasks().forEach(task -> {
            boolean displayMessages = true;
            try  {
                displayMessages = Boolean.valueOf(this.configPropertiesManager.readProperty("displayMissedTasks"));
            } catch (IOException ioe) {/*Pas de choix préenregistré ou invalide*/}
            if (displayMessages) {
                try {
                    this.tasksScheduler.displayMessage(MessageFormat.format(this.i18nPropertiesManager.readProperty("missedTask"), task.getName()), task.getDescription());
                } catch (IOException | NullPointerException ex) {
                    this.tasksScheduler.displayMessage("Task missed: " + task.getName(), task.getDescription());
                }
            }
        });
    }
    
    /**
     * Mise a jour automatique de la scene toutes les <code>period</code>ms donnee.
     * @param period temps entre 2 refresh de la scene en millisecondes
     */
    public void autoRefresh(long period) {
        if (this.autoRefreshTimer != null) {
            this.autoRefreshTimer.cancel();
            this.autoRefreshTimer.purge();
        }
        this.autoRefreshTimer = new Timer();
        this.autoRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduledTasksTable.refresh();
                updateUndoRedoItems();
            }
        }, 0, period);
    }
    
    /**
     * Mise a jour automatique de la scene toutes les secondes.
     */
    public void autoRefresh() {
        autoRefresh(1000);
    }
    
    /**
     * Arrete la mise a jour automatique de la scene.
     */
    public void stopAutoRefresh() {
        if (this.autoRefreshTimer != null) {
            this.autoRefreshTimer.cancel();
            this.autoRefreshTimer.purge();
            this.autoRefreshTimer = null;
        }
    }
    
    public List<String> getUsedNames() {
        return this.tasksTable.getItems().stream().map(t -> t.getName()).collect(Collectors.toList());
    }
    
    /**
     * Retourne le nombre d'executions de taches manquees, ne fonctionne pas 
     * pour les taches qui se repetent a l'infini ou tous les jours.
     * @return le nombre d'executions des taches manquees
     */
    public int getMissedExecutions() {
        return this.tasksTable.getItems().stream()
                .map(TaskModel::getTask)
                .map(Task::missedExecutions)
                .reduce(0, (total, taskMissed) -> total + taskMissed);
    }
    
    /**
     * Retourne le nombre d'executions manquees de la taches, ne fonctionne pas 
     * pour les taches qui se repetent a l'infini ou tous les jours.
     * @param name nom de la tache dont on souhaite connaitre le nombre d'executions manquees
     * @return le nombre d'executions manquees de la taches
     * @throws FileNotFoundException Aucun fichier ou illisible
     */
    public int getMissedExecutions(String name) throws FileNotFoundException {
        return this.tasksTable.getItems().stream()
                .filter(taskModel -> taskModel.getName().equals(name))
                .map(TaskModel::getTask)
                .map(Task::missedExecutions)
                .reduce(0, (total, taskMissed) -> total + taskMissed);
    }
    
    /**
     * Retourne les taches dont certaines executions ont etees manquees, ne 
     * fonctionne pas pour les taches qui se repetent a l'infini ou tous les jours.
     * @return les taches dont certaines executions ont etees manquees
     */
    public List<Task> getMissedTasks() {
        return this.tasksTable.getItems().stream()
                .map(TaskModel::getTask)
                .filter(task -> task.missedExecutions() > 0)
                .collect(Collectors.toList());
    }
    
    /**
     * Selectionne et affiche la table des taches.
     */
    public void selectTasksView() {
        this.tasksTable.setVisible(true);
        this.scheduledTasksTable.setVisible(false);
        this.tasksTableButtons.setVisible(true);
        this.scheduledTasksTableButtons.setVisible(false);
        if (!this.allTasksButton.getStyleClass().contains("tableViewSelectedChooserButton")) {
            this.allTasksButton.getStyleClass().add("tableViewSelectedChooserButton");
        }
        this.scheduledTasksButton.getStyleClass().remove("tableViewSelectedChooserButton");
    }
    
    /**
     * Selectionne et affiche la table des taches planifiees.
     */
    public void selectScheduledTasksView() {
        this.tasksTable.setVisible(false);
        this.scheduledTasksTable.setVisible(true);
        this.tasksTableButtons.setVisible(false);
        this.scheduledTasksTableButtons.setVisible(true);
        if (!this.scheduledTasksButton.getStyleClass().contains("tableViewSelectedChooserButton")) {
            this.scheduledTasksButton.getStyleClass().add("tableViewSelectedChooserButton");
        }
        this.allTasksButton.getStyleClass().remove("tableViewSelectedChooserButton");
    }
    
    /**
     * Change la table affichee entre celles des taches 
     * et celle des taches planifiees.
     */
    public void changeTableView() {
        if (!this.tasksTable.isVisible()) selectTasksView();
        else selectScheduledTasksView();
    }
    
    @FXML
    private void allTasksAction(ActionEvent event) {
        selectTasksView();
    }
    
    @FXML
    private void scheduledTasksAction(ActionEvent event) {
        selectScheduledTasksView();
    }
    
    /**
     * Met a jour les items du menu.
     */
    private void updateUndoRedoItems() {
        Platform.runLater(() -> {
            this.actionsManager.removeInvalidActions();
            try {
                this.undoMenuItem.setText(MessageFormat.format(this.i18nPropertiesManager.readProperty("undoMenuItem"), (this.actionsManager.getNbUndoActions() == 0) ? "" : this.actionsManager.getLastUndoAction().getName()));
                this.redoMenuItem.setText(MessageFormat.format(this.i18nPropertiesManager.readProperty("redoMenuItem"), (this.actionsManager.getNbRedoActions() == 0) ? "" : this.actionsManager.getLastRedoAction().getName()));
            } catch (IOException | NullPointerException ex) {
                this.undoMenuItem.setText(MessageFormat.format("Undo {0}\tCtrl+Z", (this.actionsManager.getNbUndoActions() == 0) ? "" : this.actionsManager.getLastUndoAction().getName()));
                this.redoMenuItem.setText(MessageFormat.format("Redo {0}\tCtrl+Shift+Z", (this.actionsManager.getNbRedoActions() == 0) ? "" : this.actionsManager.getLastRedoAction().getName()));
            }
            this.undoMenuItem.setDisable(this.actionsManager.getNbUndoActions() == 0);
            this.redoMenuItem.setDisable(this.actionsManager.getNbRedoActions() == 0);
        });
    }
    
    /**
     * Retourne true si le nom est deja utilise, false sinon.
     * @param name le nom a verifier
     * @return true si le nom est deja utilise, false sinon
     */
    public boolean isNameUsed(String name) {
        return this.tasksTable.getItems().stream().anyMatch(t -> t.getName().equals(name));
    }
    
    /**
     * Ajoute une tache et son modele, n'inclue pas l'action undo.
     * @param taskModel modele de la tache a ajouter
     * @exception IOException Impossible d'ecrire dans le fichier json
     * @exception IllegalArgumentException Le nom de la tache est deja utilise
     */
    private void addTask(TaskModel taskModel) throws IOException, IllegalArgumentException {
        this.tasksScheduler.addTask(taskModel.getTask());
        this.tasksTable.getItems().add(taskModel);
    }
    
    @FXML
    private void newTaskAction(ActionEvent event) {
        newTask(null);
    }
    
    /**
     * Cree une nouvelle tache.
     * @param previousTaskModel ancienne tache que l'utilisateur a tente de creer (nom deja pris)
     */
    private void newTask(TaskModel previousTaskModel) {
        if (previousTaskModel == null) this.taskController.newTask();
        else this.taskController.setTask(previousTaskModel.getTask());
        Platform.runLater(() -> {
            this.taskStage.showAndWait();
            Task task = this.taskController.getTask();
            if (task == null) return;
            TaskModel taskModel = new TaskModel(task);
            try {
                addTask(taskModel);
                String taskName;
                try {
                    taskName = this.i18nPropertiesManager.readProperty("addTask");
                } catch (IOException ioe) {
                    taskName = "Add Task";
                }
                this.actionsManager.addUndoAction(new Action(taskName, null, taskModel, new InvertOperation(OperationType.ADD) {
                    private boolean added = true;
                    
                    @Override
                    public void invert() throws Exception {
                        if (this.added) {
                            deleteTask(taskModel);
                        } else {
                            addTask(taskModel);
                        }
                        this.added = !this.added;
                    }
                    
                    @Override
                    public boolean isValid() {
                        return this.added == tasksTable.getItems().contains(taskModel);
                    }
                }));
            } catch (IOException ioe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("impossibleCreateTask"), ioe.getMessage());
                } catch (IOException ioe2) {
                    Dialog.error("Impossible to create the task", ioe.getMessage());
                }
            } catch (IllegalArgumentException iae) {
                try {
                    Dialog.warning(this.i18nPropertiesManager.readProperty("nameAlreadyTaken"), iae.getMessage());
                } catch (IOException ioe2) {
                    Dialog.warning("Name already taken", iae.getMessage());
                }
                newTask(taskModel);
            }
        });
    }
    
    @FXML
    private void undoAction(ActionEvent event) {
        try {
            this.actionsManager.undo();
        } catch (Exception ioe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleUndo"), ioe.getMessage());
            } catch (IOException ioe2) {
                Dialog.error("Impossible to undo", ioe.getMessage());
            }
        }
    }
    
    @FXML
    private void redoAction(ActionEvent event) {
        try {
            this.actionsManager.redo();
        } catch (Exception ioe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleRedo"), ioe.getMessage());
            } catch (IOException ioe2) {
                Dialog.error("Impossible to redo", ioe.getMessage());
            }
        }
    }
    
    @FXML
    private void duplicateAction(ActionEvent event) {
        copyTaskModel(this.tasksTable.getSelectionModel().getSelectedItem());
        pasteTaskModel(OperationType.DUPLICATE);
    }
    
    @FXML
    private void languageAction(ActionEvent event) {
        ArrayList<Language> availableLanguages = new ArrayList<>(Arrays.asList(Language.values()));
        availableLanguages.removeAll(Language.getUnavailableLanguages());
        ChoiceDialog<Language> dialog = new ChoiceDialog<>(this.parametersHandler.getLanguage(), availableLanguages);
        dialog.getDialogPane().getButtonTypes().setAll(this.okButtonType, this.cancelButtonType);
        try {
            dialog.setTitle(this.i18nPropertiesManager.readProperty("LanguageDialogTitle"));
            dialog.setContentText(this.i18nPropertiesManager.readProperty("LanguageDialogContent"));
        } catch (IOException ioe) {
            dialog.setTitle("Language choice");
            dialog.setContentText("Language: ");
        }
        dialog.setHeaderText(null);
        Language language = dialog.showAndWait().orElse(this.parametersHandler.getLanguage());
        if (!language.equals(this.parametersHandler.getLanguage())) {
            try {
                this.parametersHandler.setLanguage(language);
            } catch (IllegalArgumentException iae) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("unavailableLanguageTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("unavailableLanguageContent"), language));
                } catch (IOException | NullPointerException ex) {
                    Dialog.error("Unavailable Language", "Sorry but the given language '" + language + "' isn't available");
                }
            } catch (IOException ioe) {
                try {
                    Dialog.error(MessageFormat.format(this.i18nPropertiesManager.readProperty("impossibleWrite"), this.configPropertiesManager.getPath()), ioe.getMessage());
                } catch (IOException | NullPointerException ex) {
                    Dialog.error("Impossible to write in the file '" + this.configPropertiesManager.getPath() + "'", ioe.getMessage());
                }
            }
            refresh();
        }
    }
    
    @FXML
    private void exitActionAction(ActionEvent event) {
        String askMe = "Ask me";
        String exit = "Exit";
        String minimize = "Minimize";
        try {
            askMe = this.i18nPropertiesManager.readProperty("askMe");
            exit = this.i18nPropertiesManager.readProperty("exit");
            minimize = this.i18nPropertiesManager.readProperty("minimize");
        } catch (IOException ioe) {/*Impossible de lire le fichier i18n properties ou aucune property trouvé*/}
        String currentAction = askMe;
        try {
            switch(ButtonData.valueOf(this.configPropertiesManager.readProperty("Close"))) {
                case FINISH:
                    currentAction = exit;
                    break;
                case OTHER:
                    currentAction = minimize;
                    break;
            }
        } catch (IOException ex) {/*Pas de choix préenregistré ou invalide*/}
        ChoiceDialog<String> dialog = new ChoiceDialog<>(currentAction, askMe, exit, minimize);
        dialog.getDialogPane().getButtonTypes().setAll(this.okButtonType, this.cancelButtonType);
        try {
            dialog.setTitle(this.i18nPropertiesManager.readProperty("exitActionDialogTitle"));
            dialog.setContentText(this.i18nPropertiesManager.readProperty("exitActionDialogContent"));
        } catch (IOException ioe) {
            dialog.setTitle("Exit action");
            dialog.setContentText("Action: ");
        }
        dialog.setHeaderText(null);
        String newAction = dialog.showAndWait().orElse("");
        if (!newAction.equals(currentAction) && !newAction.equals("")) {
            try {
                if (newAction.equals(exit)) {
                    this.configPropertiesManager.setProperty("Close", ButtonData.FINISH);
                } else if (newAction.equals(minimize)) {
                    this.configPropertiesManager.setProperty("Close", ButtonData.OTHER);
                } else { //askMe
                    this.configPropertiesManager.setProperty("Close", null);
                }
            } catch (IOException ioe) {
                try {
                    Dialog.error(MessageFormat.format(this.i18nPropertiesManager.readProperty("impossibleWrite"), this.configPropertiesManager.getPath()), ioe.getMessage());
                } catch (IOException | NullPointerException ex) {
                    Dialog.error("Impossible to write in the file '" + this.configPropertiesManager.getPath() + "'", ioe.getMessage());
                }
            }
        }
    }
    
    @FXML
    private void startUpAction(ActionEvent event) {
        String open = "Open";
        String minimize = "Minimize";
        try {
            open = this.i18nPropertiesManager.readProperty("open");
            minimize = this.i18nPropertiesManager.readProperty("minimize");
        } catch (IOException ioe) {/*Impossible de lire le fichier i18n properties ou aucune property trouvé*/}
        String currentAction = open;
        try {
            if (MINIMIZE.equals(this.configPropertiesManager.readProperty("StartUp"))) {
                currentAction = minimize;
            } else {
                currentAction = open;
            }
        } catch (IOException ex) {/*Pas de choix préenregistré ou invalide*/}
        ChoiceDialog<String> dialog = new ChoiceDialog<>(currentAction, open, minimize);
        dialog.getDialogPane().getButtonTypes().setAll(this.okButtonType, this.cancelButtonType);
        try {
            dialog.setTitle(this.i18nPropertiesManager.readProperty("startUpAlertTitle"));
            dialog.setContentText(this.i18nPropertiesManager.readProperty("startUpAlertContent"));
        } catch (IOException ioe) {
            dialog.setTitle("Start-up action");
            dialog.setContentText("What do you want to do when the application starts ?");
        }
        dialog.setHeaderText(null);
        String newAction = dialog.showAndWait().orElse("");
        if (!newAction.equals(currentAction) && !newAction.equals("")) {
            try {
                if (newAction.equals(minimize)) {
                    this.configPropertiesManager.setProperty("StartUp", MINIMIZE);
                } else { //Open
                    this.configPropertiesManager.setProperty("StartUp", OPEN);
                }
            } catch (IOException ioe) {
                try {
                    Dialog.error(MessageFormat.format(this.i18nPropertiesManager.readProperty("impossibleWrite"), this.configPropertiesManager.getPath()), ioe.getMessage());
                } catch (IOException | NullPointerException ex) {
                    Dialog.error("Impossible to write in the file '" + this.configPropertiesManager.getPath() + "'", ioe.getMessage());
                }
            }
        }
    }
    
    @FXML
    private void missedTasksAction(ActionEvent event) {
        Dialog.CheckBoxAlert alert = Dialog.createAlertWithCheckBox("Missed tasks", "Do you want to display missed tasks ?", "Do not display this kind of message anymore", this.okButtonType, this.cancelButtonType);
        try {
            alert.setTitle(this.i18nPropertiesManager.readProperty("missedTaskAlertTitle"));
            alert.setMessage(this.i18nPropertiesManager.readProperty("missedTaskAlertContent"));
            alert.setCheckBoxMessage(this.i18nPropertiesManager.readProperty("missedTaskAlertCheckBoxMessage"));
        } catch (IOException ioe) {/*Impossible de lire le fichier i18n properties ou aucune property trouvé*/}
        try {
            alert.setCheckBoxValue(this.configPropertiesManager.readBoolean("displayMissedTasks"));
        } catch (IOException | IllegalArgumentException ex) {/*Pas de choix préenregistré ou invalide*/}
        try {
            if (alert.showAndWait().get().getButtonData().equals(ButtonData.OK_DONE)) {
                this.configPropertiesManager.setProperty("displayMissedTasks", alert.getCheckBoxValue());
            }
        } catch (IOException ioe) {
            try {
                Dialog.error(MessageFormat.format(this.i18nPropertiesManager.readProperty("impossibleWrite"), this.configPropertiesManager.getPath()), ioe.getMessage());
            } catch (IOException | NullPointerException ex) {
                Dialog.error("Impossible to write in the file '" + this.configPropertiesManager.getPath() + "'", ioe.getMessage());
            }
        }
    }
    
    @FXML
    private void aboutAction(ActionEvent event) {
        try {
            Dialog.information(this.i18nPropertiesManager.readProperty("AboutDialogTitle"), this.i18nPropertiesManager.readProperty("AboutDialogContent"));
        } catch (IOException ioe) {
            Dialog.information("About", "Tasks scheduler application by Johan Gousset.\nFeel free to report any issue at the address:\njg.johangousset@gmail.com");
        }
    }
    
    /**
     * Met a jour le modele de tache.
     * @param taskModel modele de la tache
     * @param newTask nouvelle tache
     * @throws IOException Impossible d'ecrire dans le fichier json
     * @exception IllegalArgumentException Le nom de la tache est deja utilise
     */
    private void editTask(TaskModel taskModel, Task newTask) throws IOException, IllegalArgumentException {
        if (taskModel == null) return;
        if (!taskModel.getTask().equals(newTask)) {
            this.tasksScheduler.editTask(taskModel.getTask(), newTask);
            taskModel.setTask(newTask);
        }
    }
    
    /**
     * Action de modification d'une tache.
     * @param taskModel modele de la tache a modifier
     */
    private void editAction(TaskModel taskModel) {
        editAction(taskModel, true);
    }
    
    /**
     * Action de modification d'une tache.
     * @param taskModel modele de la tache a modifier
     * @param updateTask true pour mettre a jour le TaskController, false sinon. Par 
     * exemple pour ne pas reset les champs affiches apres un refus de changement de tache.
     */
    private void editAction(TaskModel taskModel, boolean updateTask) {
        if (taskModel == null) return;
        if (updateTask) this.taskController.setTask(taskModel.getTask());
        else this.taskController.refresh();
        Platform.runLater(() -> {
            try {
                this.taskStage.showAndWait();
                if (taskController.getTask() == null || taskModel.getTask().equals(this.taskController.getTask())) return; //annulé ou identique
                TaskModel oldTaskModel = taskModel.clone();
                editTask(taskModel, this.taskController.getTask());
                TaskModel newTaskModel = taskModel.clone();
                String taskName;
                try {
                    taskName = this.i18nPropertiesManager.readProperty("editTask");
                } catch (IOException ioe) {
                    taskName = "Edit Task";
                }
                this.actionsManager.addUndoAction(new Action(taskName, oldTaskModel, newTaskModel, new InvertOperation(OperationType.EDIT) {
                    private boolean inverted = false;

                    @Override
                    public void invert() throws Exception {
                        if (this.inverted) {
                            editTask(taskModel, newTaskModel.getTask());
                        } else {
                            editTask(taskModel, oldTaskModel.getTask());
                        }
                        this.inverted = !this.inverted;
                    }

                    @Override
                    public boolean isValid() {
                        if (inverted) return taskModel.equals(oldTaskModel) && tasksTable.getItems().contains(taskModel);
                        return taskModel.equals(newTaskModel) && tasksTable.getItems().contains(taskModel);
                    }
                }));
            } catch (IOException ioe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("impossibleEditTask"), ioe.getMessage());
                } catch (IOException ioe2) {
                    Dialog.error("Impossible to edit the task", ioe.getMessage());
                }
            } catch (IllegalArgumentException iae) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("nameAlreadyTaken"), iae.getMessage());
                } catch (IOException ioe) {
                    Dialog.error("Name already taken", iae.getMessage());
                }
                editAction(taskModel, false);
            }
        });
    }
    
    @FXML
    private void editButtonAction(ActionEvent event) {
        TaskModel taskModel = this.tasksTable.getSelectionModel().getSelectedItem();
        if (taskModel != null) {
            editAction(taskModel);
        } else {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("error"), this.i18nPropertiesManager.readProperty("noTaskSelected"));
            } catch (IOException ioe) {
                Dialog.error("Error", "No task selected");
            }
        }
    }
    
    /**
     * Supprime une tache et son modele.
     * @param taskModel modele de la tache a supprimer
     */
    private void deleteTask(TaskModel taskModel) {
        if (taskModel == null) return;
        try {
            this.tasksScheduler.deleteTask(taskModel.getTask());
            this.tasksTable.getItems().remove(taskModel);
        } catch (IOException ioe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleDeleteTask"), ioe.getMessage());
            } catch (IOException ioe2) {
                Dialog.error("Impossible to delete the task", ioe.getMessage());
            }
        }
    }
    
    /**
     * Action de suppression d'une tache.
     * @param taskModel modele de la tache a supprimer
     */
    private void deleteAction(TaskModel taskModel) {
        if (taskModel == null) return;
        deleteTask(taskModel);
        String taskName;
        try {
            taskName = this.i18nPropertiesManager.readProperty("deleteTask");
        } catch (IOException ioe) {
            taskName = "Delete Task";
        }
        this.actionsManager.addUndoAction(new Action(taskName, taskModel, null, new InvertOperation(OperationType.DELETE) {
            private boolean deleted = true;
            
            @Override
            public void invert() throws Exception {
                if (this.deleted) {
                    addTask(taskModel);
                } else {
                    deleteTask(taskModel);
                }
                this.deleted = !this.deleted;
            }
            @Override
            public boolean isValid() {
                return this.deleted ^ tasksTable.getItems().contains(taskModel);
            }
        }));
    }
    
    @FXML
    private void deleteButtonAction(ActionEvent event) {
        TaskModel taskModel = this.tasksTable.getSelectionModel().getSelectedItem();
        if (taskModel != null) {
            deleteAction(taskModel);
        } else {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("error"), this.i18nPropertiesManager.readProperty("noTaskSelected"));
            } catch (IOException ioe) {
                Dialog.error("Error", "No task selected");
            }
        }
    }
    
    @FXML
    private void cancelButtonAction(ActionEvent event) {
        TaskModel taskModel = this.scheduledTasksTable.getSelectionModel().getSelectedItem();
        if (taskModel != null) {
            this.tasksScheduler.cancelTask(taskModel.getName());
            String taskName;
            try {
                taskName = this.i18nPropertiesManager.readProperty("cancelTask");
            } catch (IOException ioe) {
                taskName = "Cancel Task";
            }
            this.actionsManager.addUndoAction(new Action(taskName, taskModel, null, new InvertOperation(OperationType.CANCEL) {
                private boolean canceled = true;

                @Override
                public void invert() throws Exception {
                    if (this.canceled) {
                        tasksScheduler.resheduleTask(taskModel.getTask());
                    } else {
                        tasksScheduler.cancelTask(taskModel.getName());
                    }
                    this.canceled = !this.canceled;
                }
                @Override
                public boolean isValid() {
                    return (taskModel.getTask().nbRepeatsLeft() != 0);
                }
            }));
        } else {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("error"), this.i18nPropertiesManager.readProperty("noTaskSelected"));
            } catch (IOException ioe) {
                Dialog.error("Error", "No task selected");
            }
        }
    }
    
    @FXML
    private void cancelScheduledTasks(ActionEvent event) {
        this.tasksScheduler.cancelScheduledTasks();
        try {
            Dialog.information(this.i18nPropertiesManager.readProperty("canceledTasksTitle"), this.i18nPropertiesManager.readProperty("canceledTasksContent"));
        } catch (IOException ioe) {
            Dialog.information("Tasks canceled", "All the scheduled tasks have been successfully canceled.");
        }
    }
    
    @FXML
    private void rescheduleTasks(ActionEvent event) {
        try {
            this.tasksScheduler.rescheduleTasks();
            try {
                Dialog.confirmation(this.i18nPropertiesManager.readProperty("resheduleTasksTitle"), this.i18nPropertiesManager.readProperty("resheduleTasksContent"));
            } catch (IOException ioe) {
                Dialog.confirmation("Tasks resheduled", "All the tasks have been rescheduled.");
            }
        } catch (FileNotFoundException fnfe) {
            try {
                Dialog.warning(this.i18nPropertiesManager.readProperty("resheduleTasksError"), fnfe.getMessage());
            } catch (IOException ioe) {
                Dialog.warning("No task to schedule", fnfe.getMessage());
            }
        }
        
    }
    
    @FXML
    private void exitAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Copie le taskModel donne et stock la copie.
     * @param taskModel le model de la tache a copier
     */
    private void copyTaskModel(TaskModel taskModel) {
        if (taskModel == null) return;
        this.copiedTaskModel = taskModel.clone();
    }
    
    /**
     * Colle/rajoute le modele de la tache copie.
     */
    private void pasteTaskModel(OperationType operationType) {
        if (this.copiedTaskModel == null) return;
        try {
            boolean added = false;
            String name = this.copiedTaskModel.getName();
            int number = 1;
            if ((name.length() > 3) && name.substring(name.length()-3).matches("\\(\\d\\)")) {
                number = Integer.parseInt(name.substring(name.length()-2, name.length()-1));
            }
            while(!added) {
                try {
                    addTask(this.copiedTaskModel);
                    String taskName;
                    try {
                        if (operationType.equals(OperationType.DUPLICATE)) {
                            taskName = this.i18nPropertiesManager.readProperty("duplicateTask");
                        } else {
                            taskName = this.i18nPropertiesManager.readProperty("pasteTask");
                        }
                    } catch (IOException ioe) {
                        if (operationType.equals(OperationType.DUPLICATE)) {
                            taskName = "Duplicate task";
                        } else {
                            taskName = "Duplicate task";
                        }
                    }
                    this.actionsManager.addUndoAction(new Action(taskName, null, this.copiedTaskModel, new InvertOperation(operationType) {
                        private boolean added = true;

                        @Override
                        public void invert() throws Exception {
                            if (this.added) {
                                deleteTask(copiedTaskModel);
                            } else {
                                addTask(copiedTaskModel);
                            }
                            this.added = !this.added;
                        }

                        @Override
                        public boolean isValid() {
                            return this.added == tasksTable.getItems().contains(copiedTaskModel);
                        }
                    }));
                    copyTaskModel(this.copiedTaskModel); //clone
                    added = true;
                } catch (IllegalArgumentException iae) { //nom déjà pris
                    String numCopy = "(" + (number++) + ")";
                    if ((name.length() > 3) && name.substring(name.length()-3).matches("\\(\\d\\)")) {
                        name = name.substring(0, name.length()-3) + numCopy;
                    } else {
                        name = name + numCopy;
                    }
                    this.copiedTaskModel.setName(name);
                }
            }
        } catch (IOException ioe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("impossibleCreateTask"), ioe.getMessage());
            } catch (IOException ioe2) {
                Dialog.error("Impossible to create the task", ioe.getMessage());
            }
        }
    }
    
    @FXML
    private void keyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case Z:
                    if (event.isShiftDown()) {
                        redoAction(null);
                    } else {
                        undoAction(null);
                    }   break;
                case TAB:
                    changeTableView();
                    break;
            }
        }
        event.consume();
    }
    
    @FXML
    public void tasksTableKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case C:
                    copyTaskModel(this.tasksTable.getSelectionModel().getSelectedItem());
                    break;
                case V:
                    pasteTaskModel(OperationType.PASTE);
                    break;
                case X:
                    copyTaskModel(this.tasksTable.getSelectionModel().getSelectedItem());
                    deleteAction(this.tasksTable.getSelectionModel().getSelectedItem());
                    break;
                case UP:
                    if ((this.tasksTable.getSelectionModel().getSelectedIndex() > 0)
                     && (this.tasksTable.getItems().size() > 1)) {
                        Collections.swap(this.tasksTable.getItems(), this.tasksTable.getSelectionModel().getSelectedIndex(), this.tasksTable.getSelectionModel().getSelectedIndex()-1);
                    }
                    break;
                case DOWN:
                    if ((this.tasksTable.getSelectionModel().getSelectedIndex() != -1)
                     && (this.tasksTable.getItems().size() > 1)
                     && (this.tasksTable.getSelectionModel().getSelectedIndex() != this.tasksTable.getItems().size()-1)) {
                        Collections.swap(this.tasksTable.getItems(), this.tasksTable.getSelectionModel().getSelectedIndex(), this.tasksTable.getSelectionModel().getSelectedIndex()+1);
                    }
                    break;
                case Z:
                    if (event.isShiftDown()) {
                        redoAction(null);
                    } else {
                        undoAction(null);
                    }   break;
                case TAB:
                    changeTableView();
                    break;
            }
        } else {
            switch (event.getCode()) {
                case UP:
                    if (this.tasksTable.getSelectionModel().getSelectedIndex() == -1) {
                        this.tasksTable.getSelectionModel().select(0);
                    } else if (this.tasksTable.getSelectionModel().getSelectedIndex() != 0) {
                        this.tasksTable.getSelectionModel().select(this.tasksTable.getSelectionModel().getSelectedIndex()-1);
                    }
                    break;
                case DOWN:
                    if (this.tasksTable.getSelectionModel().getSelectedIndex() == -1) {
                        this.tasksTable.getSelectionModel().selectLast();
                    } else if (this.tasksTable.getSelectionModel().getSelectedIndex() != this.tasksTable.getItems().size()-1) {
                        this.tasksTable.getSelectionModel().select(this.tasksTable.getSelectionModel().getSelectedIndex()+1);
                    }
                    break;
                case ADD:
                    newTask(null);
                    break;
                case ENTER:
                    editAction(this.tasksTable.getSelectionModel().getSelectedItem());
                    break;
                case DELETE:
                    deleteAction(this.tasksTable.getSelectionModel().getSelectedItem());
                    break;
            }
        }
        event.consume();
    }
    
    @FXML
    public void scheduledTasksTableKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case UP:
                    if ((this.scheduledTasksTable.getSelectionModel().getSelectedIndex() > 0)
                     && (this.scheduledTasksTable.getItems().size() > 1)) {
                        Collections.swap(this.scheduledTasksTable.getItems(), this.scheduledTasksTable.getSelectionModel().getSelectedIndex(), this.scheduledTasksTable.getSelectionModel().getSelectedIndex()-1);
                    }
                    break;
                case DOWN:
                    if ((this.scheduledTasksTable.getSelectionModel().getSelectedIndex() != -1)
                     && (this.scheduledTasksTable.getItems().size() > 1)
                     && (this.scheduledTasksTable.getSelectionModel().getSelectedIndex() != this.scheduledTasksTable.getItems().size()-1)) {
                        Collections.swap(this.scheduledTasksTable.getItems(), this.scheduledTasksTable.getSelectionModel().getSelectedIndex(), this.scheduledTasksTable.getSelectionModel().getSelectedIndex()+1);
                    }
                    break;
                case Z:
                    if (event.isShiftDown()) {
                        redoAction(null);
                    } else {
                        undoAction(null);
                    }   break;
                case TAB:
                    changeTableView();
                    break;
            }
        } else {
            switch (event.getCode()) {
                case UP:
                    if (this.scheduledTasksTable.getSelectionModel().getSelectedIndex() == -1) {
                        this.scheduledTasksTable.getSelectionModel().select(0);
                    } else if (this.scheduledTasksTable.getSelectionModel().getSelectedIndex() != 0) {
                        this.scheduledTasksTable.getSelectionModel().select(this.scheduledTasksTable.getSelectionModel().getSelectedIndex()-1);
                    }
                    break;
                case DOWN:
                    if (this.scheduledTasksTable.getSelectionModel().getSelectedIndex() == -1) {
                        this.scheduledTasksTable.getSelectionModel().selectLast();
                    } else if (this.scheduledTasksTable.getSelectionModel().getSelectedIndex() != this.scheduledTasksTable.getItems().size()-1) {
                        this.scheduledTasksTable.getSelectionModel().select(this.scheduledTasksTable.getSelectionModel().getSelectedIndex()+1);
                    }
                    break;
                case DELETE:
                    cancelButtonAction(null);
                    break;
            }
        }
        event.consume();
    }
    
    @FXML
    private void tasksTableMouseClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() > 1){
                editAction(this.tasksTable.getSelectionModel().getSelectedItem());
            }
        }
    }
    
    @Override
    public synchronized void propertyChange(PropertyChangeEvent event) {
        switch(event.getPropertyName()) {
            case ActionsManager.NB_UNDO_PROPERTY:
            case ActionsManager.NB_REDO_PROPERTY:
                updateUndoRedoItems();
                break;
            case TaskScheduler.NEW_TASK_PROPERTY:
                Task newTask = (Task)event.getNewValue();
                if (this.scheduledTasksTable.getItems().stream()
                    .noneMatch(taskModel -> taskModel.getName().equals(newTask.getName()))) {
                    this.scheduledTasksTable.getItems().add(new TaskModel(newTask));
                }
                break;
            case TaskScheduler.DELETED_TASK_PROPERTY:
                Task deletedTask = (Task)event.getOldValue();
                this.scheduledTasksTable.getItems().stream()
                    .filter(taskModel -> taskModel.getName().equals(deletedTask.getName()))
                    .findAny().ifPresent(taskModel -> {
                        if (taskModel.getRepeatsLeft() <= 0) {
                            this.scheduledTasksTable.getItems().remove(taskModel);
                        }
                    });
                break;
            case TaskScheduler.TASKS_LIST_CHANGED_PROPERTY:
                this.scheduledTasksTable.getItems().setAll(((List<Task>)event.getNewValue()).stream().map(TaskModel::new).collect(Collectors.toList()));
                break;
        }
    }
    
    /**
     * Refraichit/met a jour la scene.
     */
    public void refresh() {
        try {
            this.tasksTable.setPlaceholder(new Text(this.i18nPropertiesManager.readProperty("tasksTablePlaceholder")));
            //Menu bar
            this.fileMenu.setText(this.i18nPropertiesManager.readProperty("fileMenu"));
            this.editMenu.setText(this.i18nPropertiesManager.readProperty("editMenu"));
            this.optionsMenu.setText(this.i18nPropertiesManager.readProperty("optionsMenu"));
            this.helpMenu.setText(this.i18nPropertiesManager.readProperty("helpMenu"));
            
            //Menu items
            this.newTaskMenuItem.setText(this.i18nPropertiesManager.readProperty("newTaskMenuItem"));
            this.cancelTasksMenuItem.setText(this.i18nPropertiesManager.readProperty("cancelTasksMenuItem"));
            this.resheduleTasksMenuItem.setText(this.i18nPropertiesManager.readProperty("resheduleTasksMenuItem"));
            this.startUpMenuItem.setText(this.i18nPropertiesManager.readProperty("startUpMenuItem"));
            this.exitMenuItem.setText(this.i18nPropertiesManager.readProperty("exitMenuItem"));
            updateUndoRedoItems();
            this.duplicateMenuItem.setText(this.i18nPropertiesManager.readProperty("duplicateMenuItem"));
            this.languageMenuItem.setText(this.i18nPropertiesManager.readProperty("languageMenuItem"));
            this.exitActionMenuItem.setText(this.i18nPropertiesManager.readProperty("exitActionMenuItem"));
            this.missedTasksMenuItem.setText(this.i18nPropertiesManager.readProperty("missedTasksMenuItem"));
            this.aboutMenuItem.setText(this.i18nPropertiesManager.readProperty("aboutMenuItem"));
            
            //tasksTable columns
            this.nameColumn.setText(this.i18nPropertiesManager.readProperty("nameColumn"));
            this.triggerColumn.setText(this.i18nPropertiesManager.readProperty("triggerColumn"));
            this.actionColumn.setText(this.i18nPropertiesManager.readProperty("actionColumn"));
            this.descriptionColumn.setText(this.i18nPropertiesManager.readProperty("descriptionColumn"));
            
            //scheduledTasksTable columns
            this.nameColumn2.setText(this.i18nPropertiesManager.readProperty("nameColumn"));
            this.actionColumn2.setText(this.i18nPropertiesManager.readProperty("actionColumn"));
            this.repeatColumn2.setText(this.i18nPropertiesManager.readProperty("repeatsLeftColumn"));
            this.timeLeftColumn2.setText(this.i18nPropertiesManager.readProperty("timeLeftColumn"));
            
            //buttons
            this.allTasksButton.setText(this.i18nPropertiesManager.readProperty("allTasksButton"));
            this.scheduledTasksButton.setText(this.i18nPropertiesManager.readProperty("scheduledTasksButton"));
            this.editButton.setText(this.i18nPropertiesManager.readProperty("editButton"));
            this.deleteButton.setText(this.i18nPropertiesManager.readProperty("deleteButton"));
            this.cancelButton.setText(this.i18nPropertiesManager.readProperty("cancelButton"));
            this.okButtonType = new ButtonType(this.i18nPropertiesManager.readProperty("okButtonType"), ButtonData.OK_DONE);
            this.cancelButtonType = new ButtonType(this.i18nPropertiesManager.readProperty("cancelButton"), ButtonData.CANCEL_CLOSE);
        } catch (IOException ioe) {
            if (!refreshFailed) Dialog.warning("Error while reading the file '" + this.i18nPropertiesManager.getPath() + "'", ioe.getMessage());
        }
        this.tasksTable.refresh();
    }
    
    /**
     * Modele javaFX d'une tache pour une TableView.
     */
    public class TaskModel implements Cloneable {
        private Task task;
        
        /**
         * Constructeur d'un modele de tache.
         * @param task la tache du modele
         */
        public TaskModel(Task task) {
            this.task = task;
        }
        
        public Task getTask() {
            return this.task;
        }
        public void setTask(Task task) {
            this.task = task;
            tasksTable.refresh();
        }
        
        public String getName() {
            return this.task.getName();
        }
        public void setName(String name) {
            this.task.setName(name);
            tasksTable.refresh();
        }
        
        public Trigger getTrigger() {
            return this.task.getTrigger();
        }
        public void setTrigger(Trigger trigger) {
            this.task.setTrigger(trigger);
            tasksTable.refresh();
        }
        
        public int getRepeat() {
            return this.task.getRepeat();
        }
        public void setRepeat(int repeat) {
            this.task.setRepeat(repeat);
        }
        
        public TaskAction getAction() {
            return this.task.getAction();
        }
        public void setAction(TaskAction action) {
            this.task.setAction(action);
            tasksTable.refresh();
        }
        
        public String getDescription() {
            return this.task.getDescription();
        }
        public void setDescription(String description) {
            this.task.setDescription(description);
            tasksTable.refresh();
        }
        
        /**
         * Retourne le nombre de repetitions restantes.
         * @return le nombre de repetitions restantes
         */
        public long getRepeatsLeft() {
            if (this.task.getRepeat() == -1) return -1;
            return this.task.nbRepeatsScheduled();
        }
        
        /**
         * Retourne le temps restant.
         * @return le temps restant
         */
        public Time getTimeLeft() {
            return new Time(this.task.timeLeft());
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.task);
            return hash;
        }
        
        @Override
        public boolean equals(Object o ) {
            if (this == o) return true;
            if ((o == null) || !(o instanceof TaskModel)) return false;
            return this.task.equals(((TaskModel)o).getTask());
        }
        
        @Override
        public String toString() {
            return "TaskModel (" + this.task.toString() + ")";
        }
        
        @Override
        public TaskModel clone() {
            TaskModel clone = null;
            try {
                clone = (TaskModel)super.clone();
                clone.setTask(this.task.clone());
            } catch(CloneNotSupportedException cnse) {/*ne peut pas arriver*/}
            return clone;
        }
    }
}
