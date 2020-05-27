package taskscheduler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tache a executer.
 * @author Johan
 */
public class Task implements Cloneable {
    /**
     * Moment du lancement de l'application, utilise pour connaitre le temps 
     * restant en cas de lancement au demarrage.
     */
    public static long START_TIME = System.currentTimeMillis();
    /**
     * Reset le moment de lancement de l'application.
     */
    public static void reset() {
        START_TIME = System.currentTimeMillis();
    }
    public static int REPEAT_INDEFINITELY = -1;
    public static final int DAY_MS = 86400000; //24 heures en millisecondes
    private final Calendar calendar = GregorianCalendar.getInstance();
    private String name;
    private Path path;
    private Trigger trigger;
    private LocalDate date;
    private Time time;
    private When when;
    private int delay;
    private int repeat;
    private ArrayList<String> arguments;
    private TaskAction action;
    private String description;
    private int nbRepeatsScheduled;
    private int nbRepeatsDone;
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     * @param nbRepeatsDone nombre d'executions de la tache deja effectuees
     */
    public Task(String name, Path path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, List<String> arguments, TaskAction action, String description, int nbRepeatsDone) {
        this.name = name;
        this.path = path;
        this.trigger = trigger;
        this.date = date;
        setTime(time);
        this.when = when;
        this.delay = delay;
        this.repeat = repeat;
        this.arguments = new ArrayList<>(arguments);
        this.action = action;
        this.description = description;
        this.nbRepeatsScheduled = 0;
        this.nbRepeatsDone = nbRepeatsDone;
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     * @param nbRepeatsDone nombre d'executions de la tache deja effectuees
     */
    public Task(String name, String path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, List<String> arguments, TaskAction action, String description, int nbRepeatsDone) {
        this(name, Paths.get(path), trigger, date, time, when, delay, repeat, arguments, action, description, nbRepeatsDone);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, List<String> arguments, TaskAction action, String description) {
        this(name, path, trigger, date, time, when, delay, repeat, arguments, action, description, 0);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, int delay, int repeat, List<String> arguments, TaskAction action, String description) {
        this(name, path, trigger, LocalDate.now(), new Time(0), When.ONE_TIME, delay, repeat, arguments, action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, List<String> arguments, TaskAction action, String description) {
        this(name, Paths.get(path), trigger, date, time, when, delay, repeat, arguments, action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, int delay, int repeat, List<String> arguments, TaskAction action, String description) {
        this(name, Paths.get(path), trigger, delay, repeat, arguments, action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, String[] arguments, TaskAction action, String description) {
        this(name, path, trigger, date, time, when, delay, repeat, Arrays.asList(arguments), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, int delay, int repeat, String[] arguments, TaskAction action, String description) {
        this(name, path, trigger, delay, repeat, Arrays.asList(arguments), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, String[] arguments, TaskAction action, String description) {
        this(name, path, trigger, date, time, when, delay, repeat, Arrays.asList(arguments), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param arguments arguments a ajouter a l'execution
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, int delay, int repeat, String[] arguments, TaskAction action, String description) {
        this(name, path, trigger, delay, repeat, Arrays.asList(arguments), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, TaskAction action, String description) {
        this(name, path, trigger, date, time, when, delay, repeat, new ArrayList<>(), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, Path path, Trigger trigger, int delay, int repeat, TaskAction action, String description) {
        this(name, path, trigger, delay, repeat, new ArrayList<>(), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param date date a laquelle declencher la tache, necessite un trigger compatible
     * @param time heure a laquelle declencher la tache, necessite un trigger compatible
     * @param when quand la tache sera-t-elle declenchee (ex : tous les jours)
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, LocalDate date, Time time, When when, int delay, int repeat, TaskAction action, String description) {
        this(name, path, trigger, date, time, when, delay, repeat, new ArrayList<>(), action, description);
    }
    
    /**
     * Contructeur d'une tache.
     * @param name nom de la tache
     * @param path chemin du fichier a executer
     * @param trigger declencheur de la tache
     * @param delay delai entre 2 executions de la tache
     * @param repeat nombre de repetitions de la tache
     * @param action action a executer
     * @param description description de la tache
     */
    public Task(String name, String path, Trigger trigger, int delay, int repeat, TaskAction action, String description) {
        this(name, path, trigger, delay, repeat, new ArrayList<>(), action, description);
    }
    
    /**
     * Constructeur d'une tache a partir d'un json.
     * @param json la tache au format json
     */
    public Task(JSONObject json) throws JSONException {
        this(json.getString("name"), 
             json.getString("path"), 
             Trigger.valueOf(json.getString("trigger")), 
             LocalDate.parse(json.getString("date")), 
             new Time((JSONObject)json.get("time")), 
             When.valueOf(json.getString("when")), 
             json.getInt("delay"), 
             json.getInt("repeat"), 
             ((JSONArray)json.get("arguments")).toList().stream().map(arg -> arg.toString()).collect(Collectors.toList()), 
             TaskAction.valueOf(json.getString("action")), 
             json.getString("description"),
             json.getInt("nbRepeatsDone")
        );
    }
    
    /**
     * Retourne le nom de la tache.
     * @return le nom de la tache
     */
    public String getName() {
        return name;
    }
    
    /**
     * Retourne le chemin du fichier a executer.
     * @return le chemin du fichier a executer
     */
    public Path getPath() {
        return path;
    }
    
    /**
     * Retourne le declencheur de la tache.
     * @return le declencheur de la tache
     */
    public Trigger getTrigger() {
        return this.trigger;
    }
    
    /**
     * Retourne la date a laquelle declencher la tache.
     * @return la date a laquelle declencher la tache
     */
    public LocalDate getDate() {
        return this.date;
    }
    
    /**
     * Retourne la date a partir du debut de la journee.
     * @return la date a partir du debut de la journee
     */
    public Date startOfDay() {
        return Date.from(this.date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Retourne la date a partir de la fin de la journee.
     * @return la date a partir de la fin de la journee
     */
    public Date endOfDay() {
        return Date.from(this.date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Retourne l'heure a laquelle declencher la tache.
     * @return l'heure a laquelle declencher la tache
     */
    public Time getTime() {
        return this.time;
    }
    
    /**
     * Retourne quand la tache sera declenchee (ex : tous les jours), une tache
     * qui se lance tous les jours n'est pas compatible avec un delai infini.
     * @return quand la tache sera declenchee (ex : tous les jours)
     */
    public When getWhen() {
        return this.when;
    }
    
    /**
     * Retourne le delai entre 2 executions de la tache (en secondes).
     * @return le delai entre 2 executions de la tache (en secondes)
     */
    public int getDelay() {
        return this.delay;
    }
    
    /**
     * Retourne le delai entre 2 executions de la tache (en millisecondes).
     * @return le delai entre 2 executions de la tache (en millisecondes)
     */
    public int delayMillis() {
        return this.delay*1000;
    }
    
    /**
     * Retourne le nombre de repetitions de la tache, -1 represente .
     * @return le nombre de repetitions de la tache
     */
    public int getRepeat() {
        return repeat;
    }
    
    /**
     * Retourne les arguments a ajouter a l'execution
     * @return les arguments a ajouter a l'execution
     */
    public ArrayList<String> getArguments() {
        return arguments;
    }
    
    /**
     * Retourne l'action a executer.
     * @return l'action a executer
     */
    public TaskAction getAction() {
        return this.action;
    }
    
    /**
     * Retourne la description de la tache.
     * @return la description de la tache
     */
    public String getDescription() {
        return description;
    }
    
    
    
    /**
     * Modifie le nom de la tache.
     * @param name le nouveau nom de la tache
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Modifie le declencheur de la tache.
     * @param trigger le nouveau declencheur de la tache
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }
    
    /**
     * Modifie la date a laquelle declencher la tache.
     * @param date la nouvelle date a laquelle declencher la tache
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Modifie l'heure a laquelle declencher la tache
     * @param time la nouvelle heure a laquelle declencher la tache
     */
    public void setTime(Time time) {
        this.time = time;
        if (this.time.getHours() > 23) this.time.setHours(23);
        if (this.time.getMinutes() > 59) this.time.setMinutes(59);
        if (this.time.getSeconds() > 59) this.time.setSeconds(59);
    }
    
    /**
     * Modifie quand la tache sera declenchee (ex : tous les jours).
     * @param when le nouveau taux de declenchement de la tache
     */
    public void setWhen(When when) {
        this.when = when;
    }
    
    /**
     * Modifie le chemin du fichier a executer.
     * @param path le nouveau chemin du fichier a executer
     */
    public void setPath(Path path) {
        this.path = path;
    }
    
    /**
     * Modifie le delai entre 2 executions de la tache, en cas de repetitions 
     * infinies le delai minimum est de 1s.
     * @param delay le nouveau delai entre 2 executions
     */
    public void setDelay(int delay) {
        this.delay = delay;
        if ((this.repeat == REPEAT_INDEFINITELY) && this.delay < 1) {
            this.delay = 1;
        }
    }
    
    /**
     * Modifie le nombre de repetitions de la tache.
     * @param repeat le nouveau nombre de repetitions de la tache
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
    
    /**
     * Modifie les arguments a ajouter a l'execution.
     * @param arguments les nouveaux arguments a ajouter a l'execution
     */
    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }
   
    /**
     * Modifie l'action a executer.
     * @param action la nouveau action a executer
     */
    public void setAction(TaskAction action) {
        this.action = action;
    }
    
    /**
     * Modifie la description de la tache.
     * @param description la nouvelle description de la tache
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Retourne le nombre de repetitions effectuees.
     * @return le nombre de repetitions effectuees
     */
    public long getNbRepeatsDone() {
        return this.nbRepeatsDone;
    }
    
    /**
     * Incremente de 1 le nombre de repetitions effectuees, et diminue de 1 
     * le nombre de repetitions planifiees.
     */
    public synchronized void incrementNbRepeatsDone() {
        this.nbRepeatsDone++;
        this.nbRepeatsScheduled--;
    }
        
    /**
     * Retourne le nombre de repetitions planifiees.
     * @return le nombre de repetitions planifiees
     */
    public long nbRepeatsScheduled() {
        return this.nbRepeatsScheduled;
    }
    
    /**
     * Incremente de 1 le nombre de repetitions planifiees.
     */
    public synchronized void incrementNbRepeatsScheduled() {
        this.nbRepeatsScheduled++;
    }
    
    /**
     * Decremente de 1 le nombre de repetitions planifiees.
     */
    public synchronized void decrementNbRepeatsScheduled() {
        this.nbRepeatsScheduled--;
    }
    
    /**
     * Reset le nombre de repetitions effectuees.
     */
    public void resetNbRepeatDone() {
        this.nbRepeatsDone = 0;
    }
    
    /**
     * Reset le nombre de repetitions planifiees.
     */
    public void resetNbRepeatScheduled() {
        this.nbRepeatsScheduled = 0;
    }
    
    /**
     * Reset les nombres de repetitions effectuees et planifiees.
     */
    public void resetNbRepeats() {
        resetNbRepeatDone();
        resetNbRepeatScheduled();
    }
    
    /**
     * Retourne le nombre de repetitions restantes a effectuer.
     * @return le nombre de repetitions restantes a effectuer
     */
    public int nbRepeatsLeft() {
        if (this.repeat == -1) return this.repeat;
        int repeatsLeft = this.repeat+1;
        long timeLeft = delayMillis();
        if (this.when.equals(When.EVERY_DAYS)) {
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft = delayMillis() - (System.currentTimeMillis() - START_TIME);
                while (timeLeft <= 0) { //si le temps de la première exécution est passé
                    repeatsLeft--;
                    long timeLeftIfRepeat = timeLeft;
                    for (int i=0; i<this.repeat; i++) { //on regarde si une repetition restante est a effectuer (pas encore passee)
                        timeLeftIfRepeat += delayMillis(); //sinon ajoute le temps jusqu'à la prochaine répétition
                        if (timeLeftIfRepeat > 0) break; //répétition trouvée, stop
                        repeatsLeft--;
                    }
                    if (timeLeftIfRepeat > 0) { //si répétition trouvée replace le temps avant prochaine execution de la tache
                       timeLeft = timeLeftIfRepeat;
                    }
                    if (timeLeft <= 0) {
                        timeLeft += DAY_MS;
                        repeatsLeft = this.repeat+1;
                    }
                }
            } else { //HOUR
                this.calendar.setTime(new Date());
                long now = new Time(this.calendar).millis(); //heure actuelle
                long taskTime = this.time.millis() + delayMillis();
                if ((now - taskTime) > 0 && ((now - taskTime) < delayMillis()*this.repeat)) { //répétitions en cours
                    repeatsLeft--;
                    repeatsLeft -= (now - taskTime)/delayMillis();
                }
                if (repeatsLeft <= 0) repeatsLeft = this.repeat+1;
            }
        } else { //ONE_TIME
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft -= (System.currentTimeMillis() - START_TIME);
            } else { //HOUR
                timeLeft += startOfDay().getTime() - System.currentTimeMillis() + this.time.millis();
            }
            if (timeLeft <= 0) { //si le temps de la première exécution est passé
                repeatsLeft--;
                for (int i=0; i<this.repeat; i++) { //on regarde si une repetition restante est a effectuer (pas encore passee)
                    timeLeft += delayMillis(); //sinon ajoute le temps jusqu'à la prochaine répétition
                    if (timeLeft > 0) break; //répétition trouvée, stop
                    repeatsLeft--; //sinon retire une répétition
                }
            }
        }
        return repeatsLeft;
    }
    
    /**
     * Retourne le temps restant avant la prochaine premiere execution de la tache,
     * meme comportement que <code>timeLeft()</code> s'il n'y a aucune repetition.
     * @return le temps restant avant la prochaine première exécution de la tâche
     */
    public long timeLeftBeforeFirstExecution() {
        long timeLeft; //temps avant lancement de la tâche en milliseconde avant la prochaine répétition
        if (this.when.equals(When.EVERY_DAYS)) {
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft = delayMillis() - (System.currentTimeMillis() - START_TIME);
                while (timeLeft <= 0) { //si le temps de la première exécution est passé
                    timeLeft += DAY_MS;
                }
            } else { //HOUR
                long daysLeft = (endOfDay().getTime() - System.currentTimeMillis())/DAY_MS;
                this.calendar.setTime(new Date());
                long now = new Time(this.calendar).millis(); //heure actuelle
                long taskTime = this.time.millis() + delayMillis();
                if ((now - taskTime) > 0) { //heure passée
                    timeLeft = DAY_MS - (now - taskTime); //24h - différence
                    //si la date n'est pas encore atteinte (enlève 1 jour car inclu dans timeLeft déjà calculé)
                    if (daysLeft > 1) timeLeft += DAY_MS*(daysLeft-1); //+ 24h * (nombre de jour restants - 1)
                } else {
                    timeLeft = taskTime - now;
                    //si la date n'est pas encore atteinte
                    if (daysLeft > 0) timeLeft += DAY_MS*daysLeft; //+ 24h * nombre de jour restants
                }
            }
        } else { //ONE_TIME
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft = delayMillis() - (System.currentTimeMillis() - START_TIME);
            } else { //HOUR
                timeLeft = delayMillis() + startOfDay().getTime() - System.currentTimeMillis() + this.time.millis();
            }
        }
        return timeLeft;
    }
    
    /**
     * Retourne le temps restant avant la prochaine execution de la tache.
     * @return le temps restant avant la prochaine execution de la tache
     */
    public long timeLeft() {
        long timeLeft; //temps avant lancement de la tâche en milliseconde avant la prochaine repetition
        if (this.when.equals(When.EVERY_DAYS)) {
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft = delayMillis() - (System.currentTimeMillis() - START_TIME);
                while (timeLeft <= 0) { //s'il le temps de la première exécution est passé
                    long timeLeftIfRepeat = timeLeft;
                    for (int i=0; i<this.repeat; i++) { //on regarde si une repetition restante est a effectuer (pas encore passee)
                        timeLeftIfRepeat += delayMillis(); //sinon ajoute le temps jusqu'à la prochaine répétition
                        if (timeLeftIfRepeat > 0) break; //répétition trouvée, stop
                    }
                    if (timeLeftIfRepeat > 0) { //si répétition trouvée replace le temps avant prochaine execution de la tache
                       timeLeft = timeLeftIfRepeat;
                    }
                    if (timeLeft <= 0) timeLeft += DAY_MS;
                }
            } else { //HOUR
                long daysLeft = (endOfDay().getTime() - System.currentTimeMillis())/DAY_MS;
                this.calendar.setTime(new Date());
                long now = new Time(this.calendar).millis(); //heure actuelle
                long taskTime = this.time.millis() + delayMillis();
                if ((now - taskTime) > 0) { //heure passée
                    if ((now - taskTime) < delayMillis()*this.repeat) { //répétitions en cours
                        timeLeft = delayMillis() - (now - taskTime)%delayMillis(); //délai - diff%délai
                    } else { //sinon
                        timeLeft = DAY_MS - (now - taskTime); //24 - différence
                        //si la date n'est pas encore atteinte (enlève 1 jour car inclu dans timeLeft déjà calculé)
                        if (daysLeft > 1) timeLeft += DAY_MS*(daysLeft-1); //+ 24h * (nombre de jour restants - 1)
                     }
                } else {
                    timeLeft = taskTime - now;
                    //si la date n'est pas encore atteinte
                    if (daysLeft > 0) timeLeft += DAY_MS*daysLeft; //+ 24h * nombre de jour restants
                }
            }
        } else { //ONE_TIME
            if (this.trigger.equals(Trigger.STARTUP)) {
                timeLeft = delayMillis() - (System.currentTimeMillis() - START_TIME);
            } else { //HOUR
                timeLeft = delayMillis() + startOfDay().getTime() - System.currentTimeMillis() + this.time.millis();
            }
            if (timeLeft <= 0) { //s'il le temps de la première exécution est passé
                for (int i=0; i<this.repeat; i++) { //on regarde si une repetition restante est a effectuer (pas encore passee)
                    timeLeft += delayMillis(); //sinon ajoute le temps jusqu'à la prochaine répétition
                    if (timeLeft > 0) break; //répétition trouvée, stop
                }
                if (this.repeat == -1) {
                    timeLeft = delayMillis() + timeLeft%delayMillis();
                }
            }
        }
        return timeLeft;
    }
    
    /**
     * Retourne le nombre d'executions de la tache manquees, ne fonctionne pas 
     * pour les taches qui se repetent a l'infini ou tous les jours.
     * @return le nombre d'executions de la tache manquees
     */
    public int missedExecutions() {
        if ((this.repeat == Task.REPEAT_INDEFINITELY) || When.EVERY_DAYS.equals(this.when)) {
            return 0;
        }
        return (this.repeat+1 - nbRepeatsLeft())-this.nbRepeatsDone;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.path);
        hash = 29 * hash + Objects.hashCode(this.trigger);
        hash = 29 * hash + Objects.hashCode(this.date);
        hash = 29 * hash + Objects.hashCode(this.time);
        hash = 29 * hash + Objects.hashCode(this.when);
        hash = 29 * hash + this.delay;
        hash = 29 * hash + this.repeat;
        hash = 29 * hash + Objects.hashCode(this.arguments);
        hash = 29 * hash + Objects.hashCode(this.action);
        hash = 29 * hash + Objects.hashCode(this.description);
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || !(o instanceof Task)) return false;
        Task task = (Task)o;
        return this.name.equals(task.name)
            && this.path.equals(task.getPath())
            && this.trigger.equals(task.getTrigger())
            && this.date.equals(task.date) 
            && this.time.equals(task.getTime())
            && this.when.equals(task.getWhen())
            && (this.delay == task.getDelay())
            && (this.repeat == task.getRepeat())
            && this.arguments.equals(task.getArguments())
            && this.action.equals(task.getAction())
            && this.description.equals(task.getDescription());
    }
    
    @Override
    public String toString() {
        Object.class.toString();
        return "{\n" + 
               " \"date\": \"" + this.date + "\",\n" + 
               " \"path\": \"" + this.path + "\",\n" + 
               " \"delay\": " + this.delay + ",\n" + 
               " \"repeat\": " + this.repeat + ",\n" + 
               " \"name\": \"" + this.name + "\",\n" + 
               " \"description\": \"" + this.description + "\",\n" + 
               " \"action\": \"" + this.action.name() + "\",\n" + 
               " \"arguments\": " + this.arguments + ",\n" + 
               " \"time\": {\n" + 
               "  \"hours\": " + this.time.getHours() + ",\n" + 
               "  \"seconds\": " + this.time.getSeconds()+ ",\n" + 
               "  \"minutes\": " + this.time.getMinutes()+ "\n" + 
               " },\n" +
               " \"trigger\": \"" + this.trigger.name() + "\",\n" + 
               " \"when\": \"" + this.when.name() + "\"\n" + 
               "}\n";
    }
    
    @Override
    public Task clone() {
        Task clone = null;
        try {
            clone = (Task)super.clone();
            clone.setTime(this.time.clone());
            clone.setArguments((ArrayList<String>)this.arguments.clone());
        } catch(CloneNotSupportedException cnse) {/*ne peut pas arriver*/}
        return clone;
    }
}