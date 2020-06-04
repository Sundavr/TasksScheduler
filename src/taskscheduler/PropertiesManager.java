package taskscheduler;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Gestionnaire de properties.
 * @author Johan
 */
public abstract class PropertiesManager {
    /**
     * Retourne le chemin du repertoire utilise.
     * @return le chemin du repertoire utilise
     */
    public abstract String getDirectory();
    
    /**
     * Retourne le nom du fichier properties utilise.
     * @return le nom du fichier properties utilise
     */
    public abstract String getFileName();
    
    /**
     * Retourne le chemin de stockage.
     * @return le chemin de stockage
     */
    public abstract String getPath();
    
    /**
     * Retourne le chemin de stockage pour le fichier donne.
     * @param fileName nom du fichier
     * @return le chemin de stockage
     */
    public abstract String getPath(String fileName);

    /**
     * Modifie le repertoire utilise.
     * @param directory le chemin correspondant au nouveau repertoire
     */
    public abstract void setDirectory(String directory);

    /**
     * Modifie le nom du fichier properties utilise.
     * @param fileName le nouveau nom du fichier
     */
    public abstract void setFileName(String fileName);
    
    /**
     * Formatte le message donne avec gestion des arguments.
     * Le message doit avoir des guillements "'" remplaces par des doubles 
     * guillements "''" et les arguments sont indiques a l'aide de "{<code>argNum</code>}" ou 
     * "''{<code>argNum</code>}''".
     * <code>argNum</code> doit etre une valeure entiere positive
     * @param message message a formatter
     * @param args les arguments du message
     * @return le message formatte
     * @throws NullPointerException si le message est null
     */
    public String formatMessage(String message, Object... args) {
        return MessageFormat.format(message, args);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public abstract String readProperty(String key) throws IOException;
    
    /**
     * Recupere la valeur associee a la propriete donnee.
     * S'il n'y en a pas, retourne <code>orElse</code>.
     * @param key nom de la propriete
     * @param orElse valuer retournee si la propriete n'existe pas ou n'a pas pu 
     * etre lue
     * @return la valeur associee a la propriete donnee
     */
    public String readPropertyOrElse(String key, String orElse) {
        try {
            return readProperty(key);
        } catch (IOException ioe) {
            return orElse;
        }
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee.
     * La valeur est ensuite formatte via la methode 
     * {@link #formatMessage(String, Object...)}
     * @param key nom de la propriete
     * @param args les arguments de la valeur associee a la cle
     * @return la valeur formattee associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public String readFormattedProperty(String key, Object... args) throws IOException {
        return formatMessage(readProperty(key), args);
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee.
     * S'il n'y en a pas, retourne <code>orElse</code>.
     * La valeur est ensuite formatte via la methode 
     * {@link #formatMessage(String, Object...)}
     * @param key nom de la propriete
     * @param orElse valuer retournee si la propriete n'existe pas ou n'a pas pu 
     * etre lue
     * @param args les arguments de la valeur associee a la cle
     * @return la valeur formattee associee a la propriete donnee
     */
    public String readFormattedPropertyOrElse(String key, String orElse, Object... args) {
        try {
            return formatMessage(readProperty(key), args);
        } catch (IOException ioe) {
            return orElse;
        }
    }
    
    /**
     * Recupere la valeur associee a la propriete donnee, comportement identique 
     * a {@link #readProperty(String) readProperty(key)}.
     * @param key nom de la propriete
     * @return la valeur associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readProperty(String)
     */
    public abstract String getProperty(String key) throws IOException;
    
    /**
     * Recupere la valeur Byte associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Byte associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de byte analysable.
     */
    public Byte readByte(String key) throws IOException, NumberFormatException {
        return Byte.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Byte associee a la propriete donnee, 
     * comportement identique a {@link #readByte(String) readByte(key)}.
     * @param key nom de la propriete
     * @return la valeur Byte associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de byte analysable.
     * @see #readByte(String)
     */
    public Byte getByte(String key) throws IOException, NumberFormatException {
        return readByte(key);
    }
    
    /**
     * Recupere la valeur Character associee a la propriete donnee, 
     * {@link java.​lang#Character.MIN_VALUE} si la property est vide.
     * @param key nom de la propriete
     * @return la valeur Character associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public Character readCharacter(String key) throws IOException {
        if (readProperty(key).isEmpty()) return Character.MIN_VALUE;
        return readProperty(key).charAt(0);
    }
    
    /**
     * Recupere la valeur Character associee a la propriete donnee, 
     * {@link java.​lang#Character.MIN_VALUE} si la property est vide.
     * Comportement identique a {@link #readCharacter(String) readCharacter(key)}.
     * @param key nom de la propriete
     * @return la valeur Character associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readCharacter(String)
     */
    public Character getCharacter(String key) throws IOException {
        return readCharacter(key);
    }
    
    /**
     * Recupere la valeur Boolean associee a la propriete donnee, 
     * false si la propriete est differente de <code>true</code> en ignorant la casse.
     * @param key nom de la propriete
     * @return la valeur Boolean associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     */
    public Boolean readBoolean(String key) throws IOException {
        return Boolean.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Boolean associee a la propriete donnee, 
     * false si la propriete est differente de <code>true</code> en ignorant la casse.
     * Comportement identique a {@link #readBoolean(String) readBoolean(key)}.
     * @param key nom de la propriete
     * @return la valeur Boolean associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readBoolean(String)
     */
    public Boolean getBoolean(String key) throws IOException {
        return readBoolean(key);
    }
    
    /**
     * Recupere la valeur Short associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Short associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de short analysable
     */
    public Short readShort(String key) throws IOException, NumberFormatException {
        return Short.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Short associee a la propriete donnee, 
     * comportement identique a {@link #readShort(String) readShort(key)}.
     * @param key nom de la propriete
     * @return la valeur Short associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @see #readShort(String)
     * @throws NumberFormatException - Si le String ne contient pas de short analysable
     */
    public Short getShort(String key) throws IOException, NumberFormatException {
        return readShort(key);
    }
    
    /**
     * Recupere la valeur Integer associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Integer associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas d'integer analysable
     */
    public Integer readInteger(String key) throws IOException, NumberFormatException {
        return Integer.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Integer associee a la propriete donnee, 
     * comportement identique a {@link #readInteger(String) readInteger(key)}.
     * @param key nom de la propriete
     * @return la valeur Integer associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas d'integer analysable
     * @see #readInteger(String)
     */
    public Integer getInteger(String key) throws IOException, NumberFormatException {
        return readInteger(key);
    }
    
    /**
     * Recupere la valeur Long associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Long associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de long analysable
     */
    public Long readLong(String key) throws IOException, NumberFormatException {
        return Long.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Long associee a la propriete donnee, 
     * comportement identique a {@link #readLong(String) readLong(key)}.
     * @param key nom de la propriete
     * @return la valeur Long associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de long analysable
     * @see #readLong(String)
     */
    public Long getLong(String key) throws IOException, NumberFormatException {
        return readLong(key);
    }
    
    /**
     * Recupere la valeur Float associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Float associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de float analysable
     */
    public Float readFloat(String key) throws IOException, NumberFormatException {
        return Float.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Float associee a la propriete donnee, 
     * comportement identique a {@link #readFloat(String) readFloat(key)}.
     * @param key nom de la propriete
     * @return la valeur Float associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de float analysable
     * @see #readFloat(String)
     */
    public Float getFloat(String key) throws IOException, NumberFormatException {
        return readFloat(key);
    }
    
    /**
     * Recupere la valeur Double associee a la propriete donnee.
     * @param key nom de la propriete
     * @return la valeur Double associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de double analysable
     */
    public Double readDouble(String key) throws IOException, NumberFormatException {
        return Double.valueOf(readProperty(key));
    }
    
    /**
     * Recupere la valeur Double associee a la propriete donnee, 
     * comportement identique a {@link #readDouble(String) readDouble(key)}.
     * @param key nom de la propriete
     * @return la valeur Double associee a la propriete donnee
     * @throws IOException Impossible de lire le fichier properties ou aucune propriete portant ce nom trouvee
     * @throws NumberFormatException - Si le String ne contient pas de double analysable
     * @see #readDouble(String)
     */
    public Double getDouble(String key) throws IOException, NumberFormatException {
        return readDouble(key);
    }
}
