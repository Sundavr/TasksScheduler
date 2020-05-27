package taskscheduler;

import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Heure au format : Heure - minutes - secondes.
 * @author Johan
 */
public class Time implements Cloneable {
    private int hours;
    private int minutes;
    private int seconds;
    
    /**
     * Constructeur de Time.
     * @param hours nombre d'heures
     * @param minutes nombre de minutes
     * @param seconds nombre de secondes
     */
    public Time(int hours, int minutes, int seconds) {
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
    }
    
    /**
     * Constructeur de Time avec un Calendar.
     * @param calendar Calendar avec l'heure souhaitee
     */
    public Time(Calendar calendar) {
        this(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
    
    /**
     * Constructeur de Time en millisecondes.
     * @param milliseconds le nombre de millisecondes
     */
    public Time(long milliseconds) {
        this((int)(milliseconds/3600000), (int)((milliseconds%3600000)/60000), (int)(((milliseconds%3600000)%60000)/1000));
    }
    
    /**
     * Constructeur de time vide (0s).
     */
    public Time() {
        this(0);
    }
    
    /**
     * Constructeur de Time a partir d'un json.
     * @param json le temps au format json
     */
    public Time(JSONObject json) throws JSONException {
        setHours(json.getInt("hours"));
        setMinutes(json.getInt("minutes"));
        setSeconds(json.getInt("seconds"));
    }
    
    /**
     * Retourne le nombre d'heures.
     * @return le nombre d'heures
     */
    public int getHours() {
        return this.hours;
    }
    
    /**
     * Retourne le nombre de minutes.
     * @return le nombre de minutes
     */
    public int getMinutes() {
        return this.minutes;
    }
    
    /**
     * Retourne le nombre de secondes.
     * @return le nombre de secondes
     */
    public int getSeconds() {
        return this.seconds;
    }
    
    /**
     * Retourne le temps en millisecondes.
     * @return le temps en millisecondes
     */
    public long millis() {
        return this.hours*3600000 + this.minutes*60000 + this.seconds*1000;
    }
    
    /**
     * Change le nombre d'heures.
     * @param hours le nouveau nombre d'heures
     */
    public void setHours(int hours) {
        this.hours = hours;
    }
    
    /**
     * Change le nombre de minutes, augmente le nombre d'heures 
     * s'il est superieur a 60.
     * @param minutes le nouveau nombre de minutes
     */
    public void setMinutes(int minutes) {
        if (minutes > 60) {
            this.hours += minutes/60;
            this.minutes += minutes%60;
        } else {
            this.minutes = minutes;
        }
    }
    
    /**
     * Change le nombre de secondes, augmentes le nombre de minutes/heures 
     * s'il est superieur a 60.
     * @param seconds le nouveau nombre de secondes
     */
    public void setSeconds(int seconds) {
        if (seconds > 60) {
            this.hours += seconds/3600;
            this.minutes += (seconds%3600)/60;
            this.minutes += seconds%60;
            
        } else {
            this.seconds = seconds;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.hours;
        hash = 67 * hash + this.minutes;
        hash = 67 * hash + this.seconds;
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || !(o instanceof Time)) return false;
        Time time = (Time)o;
        return this.hours == time.getHours()
            && this.minutes == time.getMinutes()
            && this.seconds == time.getSeconds();
    }
    
    @Override
    public String toString() {
        return this.hours + ":" + this.minutes + ":" + this.seconds;
    }
    
    @Override
    public Time clone() {
        Time clone = null;
        try {
            clone = (Time)super.clone();
        } catch(CloneNotSupportedException cnse) {/*ne peut pas arriver*/}
        return clone;
    }
}
