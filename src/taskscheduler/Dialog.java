package taskscheduler;

import java.util.Collection;
import java.util.Optional;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

/**
 * Affichage d'un message a l'ecran.
 * @author JOHAN
 */
public class Dialog {
    /**
     * Affiche un message bloquant.
     * @param title titre du message
     * @param message message a afficher
     */
    @Deprecated
    public static void message(String title, String message) {
        showMessage(AlertType.NONE, title, message, true);
    }
    
    /**
     * Affiche un message bloquant ou non.
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    @Deprecated
    public static void message(String title, String message, boolean blocking) {
        showMessage(AlertType.NONE, title, message, blocking);
    }
    
    /**
     * Affiche un message de confirmation bloquant.
     * @param title titre du message
     * @param message message a afficher
     */
    public static void confirmation(String title, String message) {
        showMessage(AlertType.CONFIRMATION, title, message, true);
    }
    
    /**
     * Affiche un message de confirmation bloquant ou non.
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    public static void confirmation(String title, String message, boolean blocking) {
        showMessage(AlertType.CONFIRMATION, title, message, blocking);
    }
    
    /**
     * Affiche un message d'information bloquant.
     * @param title titre du message
     * @param message message a afficher
     */
    public static void information(String title, String message) {
        showMessage(AlertType.INFORMATION, title, message, true);
    }
    
    /**
     * Affiche un message d'information bloquant ou non.
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    public static void information(String title, String message, boolean blocking) {
        showMessage(AlertType.INFORMATION, title, message, blocking);
    }
    
    /**
     * Affiche un message d'alerte bloquant.
     * @param title titre du message
     * @param message message a afficher
     */
    public static void warning(String title, String message) {
        showMessage(AlertType.WARNING, title, message, true);
    }
    
    /**
     * Affiche un message d'alerte bloquant ou non.
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    public static void warning(String title, String message, boolean blocking) {
        showMessage(AlertType.WARNING, title, message, blocking);
    }
    
    /**
     * Affiche un message d'erreur bloquant.
     * @param title titre du message
     * @param message message a afficher
     */
    public static void error(String title, String message) {
        showMessage(AlertType.ERROR, title, message, true);
    }
    
    /**
     * Affiche un message d'erreur bloquant ou non.
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    public static void error(String title, String message, boolean blocking) {
        showMessage(AlertType.ERROR, title, message, blocking);
    }
    
    /**
     * Affiche un requete avec un champ a remplir.
     * @param title titre de la requete
     * @param message message a afficher
     * @return le message saisie par l'utilisateur
     */
    public static String request(String title, String message) {
        return showRequest(title, message);
    }
    
    /**
     * Affiche un message a l'ecran.
     * @param type type du message
     * @param title titre du message
     * @param message message a afficher
     * @param blocking message bloquant ou non
     */
    private static void showMessage(AlertType type, String title, String message, boolean blocking) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        if (blocking) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }
    
    /**
     * Affiche une requete a l'ecran.
     * @param title titre de la requete
     * @param message reponse par defaut
     */
    private static String showRequest(String title, String message) {
        TextInputDialog request = new TextInputDialog(message);
        request.setTitle(title);
        request.setHeaderText(null);
        Optional<String> response = request.showAndWait();
        if (response.isPresent() && !response.get().isEmpty()) {
            return response.get();
        } else {
            return null;
        }
    }
    
    /**
     * Alert avec CheckBox.
     * @author Johan
     */
    public static class CheckBoxAlert extends Alert {
        private CheckBox checkBox;
        
        /**
         * Constructeur d'une Alert avec CheckBox.
         * @param alertType type de l'Alert
         * @param checkBoxMessage message de la CheckBox
         * @see Alert#Alert(AlertType)
         */
        public CheckBoxAlert(AlertType alertType, String checkBoxMessage) {
            super(alertType);
            init(checkBoxMessage);
        }
        
        /**
         * Constructeur d'une Alert avec CheckBox.
         * @param alertType type de l'Alert
         * @see Alert#Alert(AlertType)
         */
        public CheckBoxAlert(AlertType alertType) {
            this(alertType, "");
        }
        
        /**
         * Constructeur d'une Alert avec CheckBox.
         * @param alertType type de l'Alert
         * @param contentText texte de l'Alert
         * @param checkBoxMessage message de la CheckBox
         * @param buttons boutons de l'Alert
         * @see Alert#Alert(AlertType, String, ButtonType...)
         */
        public CheckBoxAlert(AlertType alertType, String contentText, String checkBoxMessage, ButtonType... buttons) {
            super(alertType, contentText, buttons);
            init(checkBoxMessage);
        }
        
        /**
         * Constructeur d'une Alert avec CheckBox.
         * @param alertType type de l'Alert
         * @param contentText texte de l'Alert
         * @param buttons boutons de l'Alert
         * @see Alert#Alert(AlertType, String, ButtonType...)
         */
        public CheckBoxAlert(AlertType alertType, String contentText, ButtonType... buttons) {
            this(alertType, contentText, "", buttons);
        }
        
        /**
         * Initialise l'alerte avec CheckBox.
         */
        private void init(String checkBoxMessage) {
            getDialogPane().applyCss();
            this.checkBox = new CheckBox(checkBoxMessage);
            setDialogPane(new DialogPane() {
                @Override
                protected Node createDetailsButton() {
                    return checkBox;
                }
            });
            Node graphic = getDialogPane().getGraphic();
            getDialogPane().setExpandableContent(new Group());
            getDialogPane().setExpanded(true);
            getDialogPane().setGraphic(graphic);
        }
        
        /**
         * Retourne la valeur de la CheckBox.
         * @return la valeur de la CheckBox
         */
        public boolean getCheckBoxValue() {
            return this.checkBox.isSelected();
        }
        
        /**
         * Modifie le message de l'Alert.
         * @param message nouveau message
         */
        public void setMessage(String message) {
            getDialogPane().setContentText(message);
        }
        
        /**
         * Change le message de la CheckBox.
         * @param checkBoxMessage le nouveau message de la CheckBox
         */
        public void setCheckBoxMessage(String checkBoxMessage) {
            this.checkBox.setText(checkBoxMessage);
        }
        
        /**
         * Modifie la valeur de la CheckBox.
         * @param newValue nouvelle valeur de la CheckBox
         */
        public void setCheckBoxValue(boolean newValue) {
            this.checkBox.setSelected(newValue);
        }
        
        /**
         * Modifie les boutons de l'Alert.
         * @param buttonsTypes les nouveaux boutons
         */
        public void setButtonsType(ButtonType buttonsTypes) {
            getButtonTypes().setAll(buttonsTypes);
        }
        
        /**
         * Modifie les boutons de l'Alert.
         * @param buttonsTypes les nouveaux boutons
         */
        public void setButtonsType(Collection<? extends ButtonType> buttonsTypes) {
            getButtonTypes().setAll(buttonsTypes);
        }
    }
    
    /**
     * Retourne une alerte avec CheckBox integree.
     * @param type type d'alert
     * @param title titre de l'alerte
     * @param headerText en tete
     * @param message message de l'alerte
     * @param checkBoxMessage message de la CheckBox
     * @param buttonTypes type des boutons
     * @return une alerte avec CheckBox integree
     */
    public static CheckBoxAlert createAlertWithCheckBox(AlertType type, String title, String headerText, 
                String message, String checkBoxMessage, ButtonType... buttonTypes) {
        CheckBoxAlert alert = new CheckBoxAlert(type, checkBoxMessage);
        if (buttonTypes.length == 0) {
            alert.getDialogPane().getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        } else {
            alert.getDialogPane().getButtonTypes().setAll(buttonTypes);
        }
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.getDialogPane().setContentText(message);
        return alert;
    }
    
    /**
     * Retourne une alerte avec CheckBox integree.
     * @param title titre de l'alerte
     * @param message message de l'alerte
     * @param checkBoxMessage message de la CheckBox
     * @param buttonTypes type des boutons
     * @return une alerte avec CheckBox integree
     */
    public static CheckBoxAlert createAlertWithCheckBox(String title, String message, String checkBoxMessage, ButtonType... buttonTypes) {
        return createAlertWithCheckBox(AlertType.CONFIRMATION, title, null, message, checkBoxMessage, buttonTypes);
    }
}
