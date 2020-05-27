package taskscheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Page d'ajout ou modification d'une tache.
 * @author Johan
 */
public class TaskController {
    private Stage stage;
    private HomeController homeController;
    private I18nPropertiesManager i18nPropertiesManager;
    private Task task;
    private FileChooser fileChooser;
    private boolean refreshFailed;
    
    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label pathLabel;
    @FXML
    private Label triggerLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label delayLabel;
    @FXML
    private Label repeatLabel;
    @FXML
    private Label argumentsLabel;
    @FXML
    private Label actionLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField pathTextField;
    @FXML
    private ComboBox<Trigger> triggerComboBox;
    @FXML
    private HBox hourHBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField hoursTextField;
    @FXML
    private TextField minutesTextField;
    @FXML
    private TextField secondsTextField;
    @FXML
    private ComboBox<When> whenComboBox;
    @FXML
    private TextField delayTextField;
    @FXML
    private ComboBox<TimeUnit> delayComboBox;
    @FXML
    private TextField repeatTextField;
    @FXML
    private Button repeatIndefinitelyButton;
    @FXML
    private TextField argumentsTextField;
    @FXML
    private ComboBox<TaskAction> actionComboBox;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button validateButton;
    
    /**
     * Initialise la page de creation/modification de tache.
     * @param stage stage de la scene
     * @param homeController Controleur de la page d'acceuil
     * @param i18nPropertiesManager gestionnaire des properties d'internationalisation de l'application
     */
    public void initialize(Stage stage, HomeController homeController, I18nPropertiesManager i18nPropertiesManager) {
        this.stage = stage;
        this.homeController = homeController;
        this.i18nPropertiesManager = i18nPropertiesManager;
        this.task = null;
        this.fileChooser = new FileChooser();
        this.refreshFailed = false;
        this.datePicker.setValue(LocalDate.now());
        this.stage.setOnCloseRequest(event -> newTask());
        refresh();
    }
    
    /**
     * Met a jour le TextField du nom.
     */
    private void updateName() {
        if (((this.task == null || !this.task.getName().equals(this.nameTextField.getText())) 
              && this.homeController.getUsedNames().contains(this.nameTextField.getText()))
           || this.nameTextField.getText().isEmpty()) {
            if (!this.nameTextField.getStyleClass().contains("error")) {
                this.nameTextField.getStyleClass().add("error");
            }
        } else {
            this.nameTextField.getStyleClass().removeAll("error");
        }
    }
    
    @FXML
    private void nameChanged(KeyEvent event) {
        updateName();
    }
    
    /**
     * Met a jour le TextField du path.
     */
    private void updatePath() {
        switch (this.actionComboBox.getSelectionModel().getSelectedItem()) {
            case ALERT:
                this.pathTextField.getStyleClass().remove("error");
                if (!this.pathTextField.getStyleClass().contains("warning")) {
                    this.pathTextField.getStyleClass().add("warning");
                }
                break;
            default:
                this.pathTextField.getStyleClass().remove("warning");
                if (this.pathTextField.getText().isEmpty()) {
                    if (!this.pathTextField.getStyleClass().contains("error")) {
                        this.pathTextField.getStyleClass().add("error");
                    }
                } else {
                    try {
                        Paths.get(this.pathTextField.getText());
                        this.pathTextField.getStyleClass().removeAll("error"); 
                    } catch (InvalidPathException ipe) {
                        if (!this.pathTextField.getStyleClass().contains("error")) {
                            this.pathTextField.getStyleClass().add("error");
                        }
                    }
                }
        }
    }
    
    @FXML
    private void pathChanged(KeyEvent event) {
        updatePath();
    }
    
    @FXML
    private void pathButtonAction(ActionEvent event) {
        File initFile = new File(this.pathTextField.getText());
        if (initFile.exists()) {
            if (!initFile.isDirectory()) {
                this.fileChooser.setInitialDirectory(initFile.getAbsoluteFile().getParentFile());
            } else {
                this.fileChooser.setInitialDirectory(initFile);
            }
        } else {
            this.fileChooser.setInitialDirectory(null);
        }
        File file = this.fileChooser.showOpenDialog(this.stage);
        System.out.println(file);
        if (file != null) {
            this.pathTextField.setText(file.getPath());
            pathChanged(null);
        }
    }
    
    /**
     * Cache ou rend visible les composants qui doivent/ne doivent pas etre la.
     */
    private synchronized void updateTrigger() {
        if (this.triggerComboBox.getSelectionModel().getSelectedItem().equals(Trigger.HOUR)) {
            Platform.runLater(() -> {
                new Thread(() -> {
                    this.triggerComboBox.setDisable(true);
                    while((this.hourHBox.getHeight() < 100) && (this.hourHBox.getPrefHeight() < 100)) {
                        try { Thread.sleep(10); } catch (InterruptedException ex) {}
                        this.hourHBox.setPrefHeight(this.hourHBox.getPrefHeight()+1);
                    }
                    this.hourHBox.setPrefHeight(100);
                    this.hourHBox.setVisible(true);
                    this.triggerComboBox.setDisable(false);
                }).start();
            });
        } else {
            this.hourHBox.setVisible(false);
            Platform.runLater(() -> {
                new Thread(() -> {
                    this.triggerComboBox.setDisable(true);
                    while((this.hourHBox.getHeight() > 0) && (this.hourHBox.getPrefHeight() > 0)) {
                        try { Thread.sleep(10); } catch (InterruptedException ex) {}
                        this.hourHBox.setPrefHeight(this.hourHBox.getPrefHeight()-1);
                    }
                    this.triggerComboBox.setDisable(false);
                }).start();
            });
        }
    }
    
    @FXML
    private void triggerComboBoxAction(ActionEvent event) {
        updateTrigger();
    }
    
    @FXML
    private void whenComboBoxAction(ActionEvent event) {
        if (When.EVERY_DAYS.equals(this.whenComboBox.getSelectionModel().getSelectedItem())) {
            this.repeatIndefinitelyButton.setVisible(false);
        } else {
            this.repeatIndefinitelyButton.setVisible(true);
        }
        updateRepeat();
    }
    
    /**
     * Remplace toutes les correspondances a la regex par le texte de remplacement.
     * @param textField le TextField a modifier
     * @param regex pattern a chercher
     * @param replacement texte de remplacement pour les correspondances a la regex
     */
    private void replaceAll(TextField textField, String regex, String replacement) {
        int caretPosition = textField.getCaretPosition();
        textField.setText(textField.getText().replaceAll(regex, replacement));
        textField.positionCaret(caretPosition-1);
    }
    
    @FXML
    private void hoursChanged(KeyEvent event) {
        if (!this.hoursTextField.getText().matches("\\d*")) {
            replaceAll(this.hoursTextField, "[\\D]", "");
        }
        if (!this.hoursTextField.getText().isEmpty() && Integer.parseInt(this.hoursTextField.getText()) > 23) {
            this.hoursTextField.setText("23");
        }
    }
    
    @FXML
    private void minutesChanged(KeyEvent event) {
        if (!this.minutesTextField.getText().matches("\\d*")) {
            replaceAll(this.minutesTextField, "[\\D]", "");
        }
        if (!this.minutesTextField.getText().isEmpty() && Integer.parseInt(this.minutesTextField.getText()) > 59) {
            this.minutesTextField.setText("59");
        }
    }
    
    @FXML
    private void secondsChanged(KeyEvent event) {
        if (!this.secondsTextField.getText().matches("\\d*")) {
            replaceAll(this.secondsTextField, "[\\D]", "");
        }
        if (!this.secondsTextField.getText().isEmpty() && Integer.parseInt(this.secondsTextField.getText()) > 59) {
            this.secondsTextField.setText("59");
        }
    }
    
    /**
     * Met a jour le TextField du delai.
     */
    private void updateDelay() {
        if ((this.delayComboBox.getSelectionModel().getSelectedItem().equals(TimeUnit.SECONDS)
             && !this.delayTextField.getText().matches("\\d*"))
          || !this.delayTextField.getText().replaceFirst("\\.", "").matches("\\d*")) {
            replaceAll(this.delayTextField, "[\\D]", "");
        }
        try {
            if (!(this.delayTextField.getText().isEmpty())) {
                int delay = 1;
                switch (this.delayComboBox.getSelectionModel().getSelectedItem()) {
                    case HOURS:
                        delay = Integer.parseInt((long)(Double.parseDouble(this.delayTextField.getText())*3600) + "");
                        break;
                    case MINUTES:
                        delay = Integer.parseInt((long)(Double.parseDouble(this.delayTextField.getText())*60) + "");
                        break;
                    case SECONDS:
                        delay = Integer.parseInt(this.delayTextField.getText());
                        break;
                }
                if ((delay < 0) || ((delay == 0) && (Integer.parseInt(this.repeatTextField.getText()) < 0))) { //répétitions infinies
                    this.delayTextField.setText("1");
                }
            }
            this.delayTextField.getStyleClass().removeAll("error");
        } catch (NumberFormatException nfe) {
            if (!this.delayTextField.getStyleClass().contains("error")) {
                this.delayTextField.getStyleClass().add("error");
            }
        }
    }
    
    @FXML
    private void delayChanged(KeyEvent event) {
        switch (event.getCode()) {
            case S:
                this.delayComboBox.getSelectionModel().select(TimeUnit.SECONDS);
                break;
            case M:
                this.delayComboBox.getSelectionModel().select(TimeUnit.MINUTES);
                break;
            case H:
                this.delayComboBox.getSelectionModel().select(TimeUnit.HOURS);
                break;
        }
        updateDelay();
    }
    
    @FXML
    private void delayComboBoxAction(ActionEvent event) {
        updateDelay();
    }
    
    /**
     * Met a jour le TextField du nombre de repetitions.
     */
    private void updateRepeat() {
        if ((!this.repeatTextField.getText().matches("\\d*"))
         && (!this.repeatTextField.getText().equals("-1") || When.EVERY_DAYS.equals(this.whenComboBox.getSelectionModel().getSelectedItem()))) {
            replaceAll(this.repeatTextField, "[\\D]", "");
        }
        try {
            if (!(this.repeatTextField.getText().isEmpty())) {
                Integer.parseInt(this.repeatTextField.getText());
            }
            this.repeatTextField.getStyleClass().removeAll("error");
        } catch (NumberFormatException nfe) {
            if (!this.repeatTextField.getStyleClass().contains("error")) {
                this.repeatTextField.getStyleClass().add("error");
            }
        }
    }
    
    @FXML
    private void repeatChanged(KeyEvent event) {
        updateRepeat();
        updateDelay();
    }
    
    @FXML
    private void infiniteRepeatAction(ActionEvent event) {
        this.repeatTextField.setText("-1");
        updateDelay();
    }
    
    @FXML
    private void actionComboxBoxAction(ActionEvent event) {
        updatePath();
    }
    
    @FXML
    private void validateButtonAction(ActionEvent event) {
        if (this.nameTextField.getText().isEmpty()) {
            try {
                Dialog.warning(this.i18nPropertiesManager.readProperty("emptyNameTitle"), this.i18nPropertiesManager.readProperty("emptyNameContent"));
                this.nameTextField.setPromptText(this.i18nPropertiesManager.readProperty("namePromptText"));
            } catch (IOException ioe) {
                Dialog.warning("Empty name", "the name should not be empty.");
                this.nameTextField.setPromptText("Choose a name");
            }
            return;
        }
        if (this.pathTextField.getText().isEmpty() && TaskAction.START_PROGRAM.equals(this.actionComboBox.getSelectionModel().getSelectedItem())) {
            try {
                Dialog.warning(this.i18nPropertiesManager.readProperty("emptyPathTitle"), this.i18nPropertiesManager.readProperty("emptyPathContent"));
                this.pathTextField.setPromptText(this.i18nPropertiesManager.readProperty("pathPromptText"));
            } catch (IOException ioe) {
                Dialog.warning("Empty path", "the path should not be empty.");
                this.pathTextField.setPromptText("Choose a path");
            }
            return;
        }
        int hours = 0;
        if (!this.hoursTextField.getText().isEmpty()) {
            try {
                hours = Integer.parseInt(this.hoursTextField.getText());
            } catch (NumberFormatException nfe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("invalidHoursTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("invalidHoursContent"), this.hoursTextField.getText()));
                } catch (IOException ioe) {
                    Dialog.error("Hour error", "the given hour : '" + this.hoursTextField.getText() + "' isn't a valid number.");
                }
                return;
            }
        }
        int minutes = 0;
        if (!this.minutesTextField.getText().isEmpty()) {
            try {
                minutes = Integer.parseInt(this.minutesTextField.getText());
            } catch (NumberFormatException nfe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("invalidMinutesTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("invalidMinutesContent"), this.minutesTextField.getText()));
                } catch (IOException ioe) {
                    Dialog.error("Minutes error", "the given minutes : '" + this.minutesTextField.getText() + "' isn't a valid number.");
                }
                return;
            }
        }
        int seconds = 0;
        if (!this.secondsTextField.getText().isEmpty()) {
            try {
                seconds = Integer.parseInt(this.secondsTextField.getText());
            } catch (NumberFormatException nfe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("invalidSecondsTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("invalidSecondsContent"), this.secondsTextField.getText()));
                } catch (IOException ioe) {
                    Dialog.error("Seconds error", "the given seconds : '" + this.secondsTextField.getText() + "' isn't a valid number.");
                }
                return;
            }
        }
        int delay = 0;
        if (!this.delayTextField.getText().isEmpty()) {
            try {
                switch(this.delayComboBox.getSelectionModel().getSelectedItem()) {
                    case HOURS:
                        delay = Integer.parseInt((long)(Double.parseDouble(this.delayTextField.getText())*3600) + "");
                        break;
                    case MINUTES:
                        delay = Integer.parseInt((long)(Double.parseDouble(this.delayTextField.getText())*60) + "");
                        break;
                    case SECONDS:
                        delay = Integer.parseInt(this.delayTextField.getText());
                        break;
                }
            } catch (NumberFormatException nfe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("invalidDelayTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("invalidDelayContent"), this.delayTextField.getText()));
                } catch (IOException ioe) {
                    Dialog.error("Delay error", "the given delay : '" + this.delayTextField.getText() + "' isn't a valid number.");
                }
                return;
            }
        }
        int repeat = 0;
        if (!this.repeatTextField.getText().isEmpty()) {
            try {
                repeat = Integer.parseInt(this.repeatTextField.getText());
            } catch (NumberFormatException nfe) {
                try {
                    Dialog.error(this.i18nPropertiesManager.readProperty("invalidRepeatTitle"), MessageFormat.format(this.i18nPropertiesManager.readProperty("invalidRepeatContent"), this.repeatTextField.getText()));
                } catch (IOException ioe) {
                    Dialog.error("Repeat error", "the given repeat amount : '" + this.repeatTextField.getText() + "' isn't a correct number.");
                }
                return;
            }
        }
        try {
            setTask(new Task(this.nameTextField.getText(), 
                             this.pathTextField.getText(), 
                             this.triggerComboBox.getSelectionModel().getSelectedItem(), 
                             this.datePicker.getValue(), 
                             new Time(hours, minutes, seconds), 
                             this.whenComboBox.getSelectionModel().getSelectedItem(), 
                             delay, 
                             repeat, 
                             Arrays.asList(this.argumentsTextField.getText().split("\\s")).stream().filter(arg -> !arg.isEmpty()).collect(Collectors.toList()),
                             this.actionComboBox.getSelectionModel().getSelectedItem(),
                             this.descriptionTextField.getText()
                            )
            );
        } catch (InvalidPathException ipe) {
            try {
                Dialog.error(this.i18nPropertiesManager.readProperty("invalidPathTitle"), ipe.getMessage());
            } catch (IOException ioe) {
                Dialog.error("The given path isn't valid", ipe.getMessage());
            }
            return;
        }
        this.stage.close();
    }
    
    @FXML
    private void keyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            validateButtonAction(null);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            this.stage.fireEvent(new WindowEvent(this.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
        event.consume();
    }
    
    /**
     * Modifie la tache.
     * @param task la nouvelle tache
     */
    public void setTask(Task task) {
        this.task = task;
        refresh();
    }
    
    /**
     * Retourne la tache.
     * @return la tache
     */
    public Task getTask() {
        return this.task;
    }
    
    /**
     * Supprime l'anciennce tache.
     */
    public void newTask() {
        setTask(null);
    }
    
    /**
     * Refraichit/met a jour la scene.
     */
    public void refresh() {
        Platform.runLater(() -> {
            try {
                //Labels
                this.titleLabel.setText(this.i18nPropertiesManager.readProperty("titleLabel"));
                this.nameLabel.setText(this.i18nPropertiesManager.readProperty("nameLabel"));
                this.pathLabel.setText(this.i18nPropertiesManager.readProperty("pathLabel"));
                this.triggerLabel.setText(this.i18nPropertiesManager.readProperty("triggerLabel"));
                this.dateLabel.setText(this.i18nPropertiesManager.readProperty("dateLabel"));
                this.delayLabel.setText(this.i18nPropertiesManager.readProperty("delayLabel"));
                this.repeatLabel.setText(this.i18nPropertiesManager.readProperty("repeatLabel"));
                this.argumentsLabel.setText(this.i18nPropertiesManager.readProperty("argumentsLabel"));
                this.actionLabel.setText(this.i18nPropertiesManager.readProperty("actionLabel"));
                this.descriptionLabel.setText(this.i18nPropertiesManager.readProperty("descriptionLabel"));

                //Buttons
                this.repeatIndefinitelyButton.setText(this.i18nPropertiesManager.readProperty("repeatIndefinitelyButton"));
                this.validateButton.setText(this.i18nPropertiesManager.readProperty("validateButton"));
            } catch (IOException ioe) {
                if (!refreshFailed) Dialog.warning("Error while reading the file '" + this.i18nPropertiesManager.getPath() + "'", ioe.getMessage());
            }
            this.triggerComboBox.getItems().setAll(Trigger.values());
            this.whenComboBox.getItems().setAll(When.values());
            this.delayComboBox.getItems().setAll(TimeUnit.values());
            this.actionComboBox.getItems().setAll(TaskAction.values());
            this.nameTextField.setPromptText("");
            this.pathTextField.setPromptText("");
            if (this.task == null) {
                this.nameTextField.clear();
                this.pathTextField.clear();
                this.datePicker.setValue(LocalDate.now());
                this.hoursTextField.setText("0");
                this.minutesTextField.setText("0");
                this.secondsTextField.setText("0");
                this.delayTextField.setText("0");
                this.repeatTextField.setText("0");
                this.argumentsTextField.setText("");
                this.descriptionTextField.setText("");
                this.triggerComboBox.getSelectionModel().select(Trigger.STARTUP);
                this.whenComboBox.getSelectionModel().selectFirst();
                this.delayComboBox.getSelectionModel().select(TimeUnit.SECONDS);
                this.actionComboBox.getSelectionModel().selectFirst();
            } else {
                this.nameTextField.setText(this.task.getName());
                this.pathTextField.setText(this.task.getPath().toString());
                this.datePicker.setValue(this.task.getDate());
                this.hoursTextField.setText(Integer.toString(this.task.getTime().getHours()));
                this.minutesTextField.setText(Integer.toString(this.task.getTime().getMinutes()));
                this.secondsTextField.setText(Integer.toString(this.task.getTime().getSeconds()));
                this.repeatTextField.setText(Integer.toString(this.task.getRepeat()));
                this.argumentsTextField.setText(this.task.getArguments().stream().reduce("", (arg1, arg2) -> arg1 + arg2 + " "));
                this.descriptionTextField.setText(this.task.getDescription());
                this.triggerComboBox.getSelectionModel().select(this.task.getTrigger());
                this.whenComboBox.getSelectionModel().select(this.task.getWhen());
                int delay = this.task.getDelay();
                if (delay == 0) {
                    this.delayComboBox.getSelectionModel().select(TimeUnit.SECONDS);
                    this.delayTextField.setText("0");
                } else if (delay%3600 == 0) {
                    this.delayTextField.setText(Integer.toString(this.task.getDelay()/3600));
                    this.delayComboBox.getSelectionModel().select(TimeUnit.HOURS);
                } else if (delay%60 == 0) {
                    this.delayComboBox.getSelectionModel().select(TimeUnit.MINUTES);
                    this.delayTextField.setText(Integer.toString(this.task.getDelay()/60));
                } else {
                    this.delayComboBox.getSelectionModel().select(TimeUnit.SECONDS);
                    this.delayTextField.setText(Integer.toString(this.task.getDelay()));
                }
                this.actionComboBox.getSelectionModel().select(this.task.getAction());
            }
            whenComboBoxAction(null);
            updateName();
            updatePath();
            updateTrigger();
            updateDelay();
            updateRepeat();
        });
    }
}
