package taskscheduler;

import java.awt.TrayIcon;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javafx.application.Platform;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Planificateur de taches.
 * @author Johan
 */
public class TaskScheduler {
    public static final String NEW_TASK_PROPERTY = "newTask";
    public static final String DELETED_TASK_PROPERTY = "deletedTask";
    public static final String TASKS_LIST_CHANGED_PROPERTY = "tasksListChanged";
    protected final PropertyChangeSupport changeSupport;
    private final JsonManager tasksJson;
    private TrayIcon trayIcon;
    private Timer timer;
    private final ArrayList<MyTimerTask> scheduledTimerTasks;
    
    /**
     * Constructeur du planificateur de taches.
     * @param tasksJson gestionnaire du fichier JSON de stockage des taches
     */
    public TaskScheduler(JsonManager tasksJson) {
        this.changeSupport = new PropertyChangeSupport(this);
        this.tasksJson = tasksJson;
        this.scheduledTimerTasks = new ArrayList<>();
    }
    
    /**
     * Constructeur du planificateur de taches.
     * @param tasksJson gestionnaire du fichier JSON de stockage des taches
     * @param trayIcon icone de l'application afin d'afficher les notifications
     */
    public TaskScheduler(JsonManager tasksJson, TrayIcon trayIcon) {
        this(tasksJson);
        this.trayIcon = trayIcon;
    }
    
    /**
     * Lance la planification de la tache.
     * @param task la tache a executer
     */
    public void runTask(Task task) {
        if ((task.getRepeat() >= 0) && (task.getRepeat() < task.getNbRepeatsDone())) return; //déjà terminée
        int delay = task.delayMillis();
        long timeLeft = task.timeLeft();
        if (timeLeft < 0) return; //tâche déjà passée
        
        int executionType = ((task.getRepeat() < 0) || When.EVERY_DAYS.equals(task.getWhen())) ? MyTimerTask.INFINITE_EXECUTIONS : MyTimerTask.UNIQUE_EXECUTIONS;
        MyTimerTask timerTask = new MyTimerTask(task, executionType) {
            @Override
            public void run() {
                super.run();
                switch (this.task.getAction()) {
                    case ALERT:
                        displayMessage(this.task.getName(), this.task.getDescription());
                        break;
                    default: //START_PROGRAM
                        try {
                            String command = "cmd /c start \"\" \"" + this.task.getPath().toAbsolutePath() +  "\""; //windows par défaut
                            switch (OSDetector.getOS()) {
                                case UNIX:
                                    command = "\"" + this.task.getPath().toAbsolutePath() + "\"";
                                    break;
                                case MAC:
                                    command = "open -a \"" + this.task.getPath().toAbsolutePath() + "\"";
                                    break;
                            }
                            Runtime.getRuntime().exec(this.task.getArguments().stream()
                                .map(arg -> " " + arg)
                                .reduce(command, String::concat)
                            );
                        } catch (IOException ioe) {
                            //Impossible de lancer le programme
                            System.out.println(ioe.getMessage());
                        }
                        break;
                }
            }
        };
        int repeatsLeft = task.nbRepeatsLeft();
        //ou avec appel recursif a la fin d'un run (plus propres mais pas d'exécutions simultanées)
        if (executionType == MyTimerTask.INFINITE_EXECUTIONS) {
            if ((task.getRepeat() != Task.REPEAT_INDEFINITELY) //ignore taches infinies
             && ((task.getRepeat()+1) != repeatsLeft) //si l'heure est passée mais qu'il reste des répétitions (EVERY_DAY)
             && (task.timeLeftBeforeFirstExecution() > 0)) { //(sécurité)
                long timeLeftBeforeFirstExecution = task.timeLeftBeforeFirstExecution();
                for (int i=0; i<task.getRepeat()-repeatsLeft; i++) { //répétitions déjà passées
                    this.timer.schedule(timerTask.clone(), timeLeftBeforeFirstExecution, Task.DAY_MS);
                    timeLeftBeforeFirstExecution+=delay;
                }
                this.timer.schedule(timerTask.clone(), timeLeftBeforeFirstExecution, Task.DAY_MS);
            }
            for (int i=0; i<repeatsLeft-1; i++) { //plusieurs repetitions tous les jours
                this.timer.schedule(timerTask.clone(), timeLeft, Task.DAY_MS);
                timeLeft+=delay;
            }
            if (When.EVERY_DAYS.equals(task.getWhen())) this.timer.schedule(timerTask, timeLeft, Task.DAY_MS);
            else this.timer.schedule(timerTask, timeLeft, delay);
        } else { //UNIQUE
            for (int i=0; i<repeatsLeft-1; i++) {
                this.timer.schedule(timerTask.clone(), timeLeft);
                timeLeft+=delay;
            }
            this.timer.schedule(timerTask, timeLeft);
        }
    }
    
    /**
     * Lance le planificateur de taches.
     * @throws FileNotFoundException Aucun fichier ou illisible
     */
    public void start() throws FileNotFoundException {
        this.timer = new Timer();
        for (Task task : getTasks()) runTask(task);
    }
    
    /**
     * Arrete le planificateur de taches.
     */
    public void stop() {
        if (this.timer != null) this.timer.cancel();
    }
    
    /**
     * Retourne le nom du fichier utilise pour stocker les taches.
     * @return le nom du fichier utilise pour stocker les taches
     */
    public String getFileName() {
        return this.tasksJson.getFileName();
    }
    
    /**
     * Retourne le chemin du repertoire utilise pour stocker les taches.
     * @return le chemin du repertoire utilise pour stocker les taches
     */
    public String getDirectory() {
        return this.tasksJson.getDirectory();
    }
    
    /**
     * Retourne le chemin de stockage.
     * @return le chemin de stockage
     */
    public String getPath() {
        return this.tasksJson.getPath();
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne.
     * @param fileName nom du fichier
     * @return le chemin de stockage
     */
    public String getPath(String fileName) {
        return this.tasksJson.getPath(fileName);
    }
    
    /**
     * Retourne le JsonManager des taches.
     * @return le JsonManager des taches
     */
    public JsonManager getJsonManager() {
        return this.tasksJson;
    }
    
    /**
     * Modifie le nom du fichier utilise pour stocker les taches.
     * @param fileName le nouveau nom du fichier
     */
    public void setFileName(String fileName) {
        this.tasksJson.setFileName(fileName);
    }
    
    /**
     * Modifie le chemin du repertoire utilise pour stocker les taches.
     * @param directory le nouveau chemin du repertoire
     */
    public void setDirectory(String directory) {
        this.tasksJson.setDirectory(directory);
    }
    
    /**
     * Retourne la liste des taches.
     * @return la liste des taches
     * @throws FileNotFoundException Aucun fichier ou illisible
     */
    public List<Task> getTasks() throws FileNotFoundException {
        ArrayList<Task> tasks = new ArrayList<>();
        for (JSONObject json : this.tasksJson.read()) {
            try {
                tasks.add(new Task(json));
            } catch (JSONException je) { //tâche corrompue
                try {
                    this.tasksJson.delete(json);
                } catch (IOException ioe) {}
            }
        }
        return tasks;
    }
    
    /**
     * Charge toutes les taches valides dans la liste donnee, meme en cas d'exception.
     * @param tasksList liste dans laquelle charger les taches
     * @throws FileNotFoundException Aucun fichier ou illisible
     * @throws JSONException Fichier JSON corrompu, une ou des tache(s) sont illisible(s)
     */
    public void loadTasks(List<Task> tasksList) throws FileNotFoundException, JSONException {
        boolean jsonException = false;
        String jsonExceptionMessage = "Corruped file '" + this.tasksJson.getPath() + "': Impossible to get the following tasks {\n";
        for (JSONObject json : this.tasksJson.read()) {
            try {
                tasksList.add(new Task(json));
            } catch (JSONException je) {
                jsonException = true;
                jsonExceptionMessage += json + "\n";
            }
        }
        if (jsonException) {
            throw new JSONException(jsonExceptionMessage + "}");
        }
    }
    
    /**
     * Retourne la liste des taches planifiees.
     * @return la liste des taches planifiees
     */
    public List<Task> getScheduledTasks() {
        synchronized(this.scheduledTimerTasks) {
            return this.scheduledTimerTasks.stream().map(timerTask -> timerTask.getTask()).distinct().collect(Collectors.toList());
        }
    }
    
    /**
     * Retourne le nombre de taches planifiees.
     * @return le nombre de taches planifiees
     */
    public long getNumberOfScheduleTasks() {
        synchronized(this.scheduledTimerTasks) {
            return this.scheduledTimerTasks.stream().distinct().count();
        }
    }
    
    /**
     * Retourne le nombre de repetitions restantes de la tache.
     * @param task tache dont on souhaite connaitre le nombre de repetitions restantes
     * @return le nombre de repetitions restantes de la tache
     */
    public long getNumberOfRepeatsLeft(Task task) {
        synchronized(this.scheduledTimerTasks) {
            if (this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().equals(task))
                                                 .anyMatch(timerTask -> timerTask.getTask().getRepeat() < 0)) {
                return -1;
            }
            return this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().equals(task)).count()-1;
        }
    }
    
    /**
     * Retourne le nombre de repetitions restantes de la tache portant le nom donne.
     * @param name nom de la tache dont on souhaite connaitre le nombre de repetitions restantes
     * @return le nombre de repetitions restantes de la tache portant le nom donne
     */
    public long getNumberOfRepeatsLeft(String name) {
        synchronized(this.scheduledTimerTasks) {
            if (this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().getName().equals(name))
                                                 .anyMatch(timerTask -> timerTask.getTask().getRepeat() < 0)) {
                return -1;
            }
            long nbRepeat = this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().getName().equals(name)).count()-1;
            return (nbRepeat >= 0) ? nbRepeat : 0;
        }
    }
    
    /**
     * Retourne true si le nom est deja utilise, false sinon.
     * @param name le nom a verifier
     * @return true si le nom est deja utilise, false sinon
     * @throws FileNotFoundException Aucun fichier ou illisible
     * @throws JSONException Impossible de lire une tache
     */
    public boolean isNameUsed(String name) throws FileNotFoundException, JSONException {
        return getTasks().stream().anyMatch(t -> t.getName().equals(name));
    }
    
    /**
     * Ajoute une tache.
     * @param task nouvelle tache
     * @throws FileNotFoundException Aucun fichier ou illisible
     * @throws JSONException Impossible de lire une tache
     * @exception IOException Impossible d'ecrire dans le fichier json
     * @exception IllegalArgumentException Le nom de la tache est deja utilise
     */
    public void addTask(Task task) throws JSONException, IOException, IllegalArgumentException {
        boolean nameUsed = false;
        try {
            nameUsed = isNameUsed(task.getName());
        } catch (FileNotFoundException fnfe) {/*le fichier n'existe pas encore donc le nom n'est pas utilisé*/}
        if (nameUsed) throw new IllegalArgumentException("The name '" + task.getName() + "' is already used.");
        this.tasksJson.add(task);
        runTask(task);
    }
    
    /**
     * Met a jour la tache.
     * @param oldTask ancienne tache
     * @param newTask nouvelle tache
     * @throws IOException Impossible d'ecrire dans le fichier json
     * @exception IllegalArgumentException Le nom de la tache est deja utilise
     */
    public void editTask(Task oldTask, Task newTask) throws IOException, IllegalArgumentException {
        if (oldTask == null) return;
        if (!oldTask.equals(newTask)) {
            if (!oldTask.getName().equals(newTask.getName()) && isNameUsed(newTask.getName())) throw new IllegalArgumentException("The name '" + newTask.getName() + "' is already used.");
            this.tasksJson.delete("name", oldTask.getName());
            cancelTask(oldTask.getName());
            addTask(newTask); //ajoute dans le json et lance la tache
        }
    }
    
    /**
     * Supprime la tache.
     * @param name nom de la tache a supprimer
     * @throws IOException Impossible d'ecrire dans le fichier json
     */
    public void deleteTask(String name) throws IOException {
        this.tasksJson.delete("name", name);
        cancelTask(name);
    }
    
    /**
     * Supprime la tache.
     * @param task tache a supprimer
     * @throws IOException Impossible d'ecrire dans le fichier json
     */
    public void deleteTask(Task task) throws IOException {
        deleteTask(task.getName());
    }
    
    /**
     * Vide la liste des taches planifiees.
     */
    private void clearScheduledTasksList() {
        List<Task> oldList = this.scheduledTimerTasks.stream().map(MyTimerTask::getTask).collect(Collectors.toList());
        this.scheduledTimerTasks.clear();
        this.changeSupport.firePropertyChange(TASKS_LIST_CHANGED_PROPERTY, oldList, this.scheduledTimerTasks.stream().map(MyTimerTask::getTask).collect(Collectors.toList()));
    }
    
    /**
     * Annule toutes les taches planifiees.
     */
    public void cancelScheduledTasks() {
        this.timer.cancel();
        this.timer.purge();
        this.timer = new Timer();
        clearScheduledTasksList();
    }
    
    /**
     * Annule toutes les executions de la tache.
     * @param task la tache a annuler
     */
    public void cancelTask(Task task) {
        synchronized(this.scheduledTimerTasks) {
            this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().equals(task))
                                             .collect(Collectors.toList())
                                             .forEach(timerTask -> timerTask.cancel());
        }
    }
    
    /**
     * Annule toutes les executions de la tache portant le nom donne.
     * @param name nom de la tache a annuler
     */
    public void cancelTask(String name) {
        synchronized(this.scheduledTimerTasks) {
            this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().getName().equals(name))
                                             .collect(Collectors.toList())
                                             .forEach(timerTask -> timerTask.cancel());
        }
    }
    
    /**
     * Annule la premiere execution prevue de la tache.
     * @param task tache dont on souhaite annuler la premiere execution
     */
    public void cancelFirst(Task task) {
        synchronized(this.scheduledTimerTasks) {
            this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().equals(task))
                                             .findFirst()
                                             .ifPresent(timerTask -> timerTask.cancel());
        }
    }
    
    /**
     * Annule la premiere execution prevue de la tache portant le nom donne.
     * @param name nom de la tache dont on souhaite annuler la premiere execution
     */
    public void cancelFirst(String name) {
        synchronized(this.scheduledTimerTasks) {
            this.scheduledTimerTasks.stream().filter(timerTask -> timerTask.getTask().getName().equals(name))
                                             .findFirst()
                                             .ifPresent(timerTask -> timerTask.cancel());
        }
    }
    
    /**
     * Relance la planification des taches.
     * @throws FileNotFoundException Aucun fichier ou illisible
     */
    public void rescheduleTasks() throws FileNotFoundException {
        this.timer.cancel();
        this.timer.purge();
        clearScheduledTasksList();
        Task.reset();
        start();
    }
    
    /**
     * Relance la planification de la tache la ou elle s'était arretee, 
     * sans effet si la tache n'est pas arretee.
     * @param task tache a reprendre
     */
    public void resheduleTask(Task task) {
        if (task == null) return;
        if (this.scheduledTimerTasks.stream().noneMatch(timerTask -> timerTask.getTask().equals(task))) {
            runTask(task);
        }
    }
    
    /**
     * Retourne le nombre d'executions de taches manquees, ne fonctionne pas 
     * pour les taches qui se repetent a l'infini ou tous les jours.
     * @return le nombre d'executions des taches manquees
     * @throws FileNotFoundException Aucun fichier ou illisible
     */
    public int getMissedExecutions() throws FileNotFoundException {
        return getTasks().stream()
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
        return getTasks().stream()
                .filter(task -> task.getName().equals(name))
                .map(Task::missedExecutions)
                .reduce(0, (total, taskMissed) -> total + taskMissed);
    }
    
    /**
     * Affiche le message comme notification.
     * @param title titre du message
     * @param message le message a afficher
     * @param type type du message
     */
    public void displayMessage(String title, String message, TrayIcon.MessageType type) {
        if (this.trayIcon == null) {
            Platform.runLater(() -> Dialog.information(title, message, false));
        } else {
            javax.swing.SwingUtilities.invokeLater(() -> this.trayIcon.displayMessage(title, message, type));
        }
    }
    
    /**
     * Affiche le message comme notification.
     * @param title titre du message
     * @param message le message a afficher
     */
    public void displayMessage(String title, String message) {
        displayMessage(title, message, TrayIcon.MessageType.INFO);
    }
    
    /**
     * TimerTask cloneable avec la capactie a retourner la tache.
     */
    private abstract class MyTimerTask extends TimerTask implements Cloneable {
        /**
         * Nombre de repetitions limite, chaque tache se lance une seule fois.
         */
        public static final int UNIQUE_EXECUTIONS = 0;
        /**
         * La tache se relance a l'infini.
         */
        public static final int INFINITE_EXECUTIONS = 1;
        protected Task task;
        protected int executionsType;
        
        /**
         * Constructeur d'un MyTimerTask.
         * @param task tache a executer par ce TimerTask
         * @param executionsType type d'execution (unique ou infinie)
         */
        public MyTimerTask(Task task, int executionsType) {
            this.task = task;
            this.executionsType = executionsType;
            init();
        }
        
        /**
         * Ajoute ce TimerTask a la liste des TimerTasks a executer et signale 
         * a la tache qu'une repetition de plus est prevue.
         */
        protected void init() {
            synchronized(scheduledTimerTasks) {
                scheduledTimerTasks.add(this);
            }
            this.task.incrementNbRepeatsScheduled();
            changeSupport.firePropertyChange(NEW_TASK_PROPERTY, null, this.task);
        }
        
        /**
         * Retire ce TimerTask de la liste des TimerTasks a executer et signale 
         * a la tache qu'une repetition a ete effectuee.
         */
        protected void end() {
            synchronized(scheduledTimerTasks) {
                scheduledTimerTasks.remove(this);
            }
            changeSupport.firePropertyChange(DELETED_TASK_PROPERTY, this.task, null);
        }
        
        public String getName() {
            return this.task.getName();
        }
        
        /**
         * Retourne la tache a executer par ce TimerTask.
         * @return la tache a executer par ce TimerTask
         */
        public Task getTask() {
            return this.task;
        }
        
        /**
         * Change la tache a executer.
         * @param task la nouvelle tache a executer
         */
        public void setTask(Task task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            this.task.incrementNbRepeatsDone();
            try {
                tasksJson.replace("name", this.task.getName(), this.task); //update
            } catch (IOException ioe) {
                //Impossible de mettre à jour la tache, ne devrait jamais arriver car le fichier json existe si la tache existe
                System.out.println(ioe.getMessage());
            }
            if (this.executionsType == UNIQUE_EXECUTIONS) end();
        }
        
        @Override
        public boolean cancel() {
            this.task.decrementNbRepeatsScheduled();
            end();
            return super.cancel();
        }
        
        @Override
        public MyTimerTask clone() {
            MyTimerTask clone = null;
            try {
                clone = (MyTimerTask)super.clone();
                clone.init();
            } catch(CloneNotSupportedException cnse) {/*ne peut pas arriver*/}
            return clone;
        }
    }
    
    /**
     * Ajout d'un écouteur.
     * @param listener nouvel ecouteur
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Ajout d'un ecouteur sur la propriete designee.
     * @param listener ecouteur concerne
     * @param propertyNames la ou les propriete(s) a ecouter
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener, String... propertyNames) {
        for (String propertyName : propertyNames)
            this.changeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Retire un écouteur.
     * @param listener ecouteur a retire
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.changeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Retire un ecouteur de la propriété designee.
     * @param listener ecouteur concerne
     * @param propertyNames propriete(s) a arreter d'ecouter
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener, String... propertyNames) {
        for (String propertyName : propertyNames)
            this.changeSupport.removePropertyChangeListener(propertyName, listener);
    }
}