package taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Gestionnaire de JSON.
 * @author Johan
 */
public class JsonManager {
    public static final String DEFAULT_DIRECTORY = "";
    private String directory;
    private String fileName;
    
    /**
     * Constructeur du gestionnaire de JSON.
     * @param directory chemin du repertoire utilise
     * @param fileName nom du fichier json
     */
    public JsonManager(String directory, String fileName) {
        setDirectory(directory);
        setFileName(fileName);
    }
    
    /**
     * Constructeur du gestionnaire de JSON.
     * @param fileName nom du fichier json
     */
    public JsonManager(String fileName) {
        this(DEFAULT_DIRECTORY, fileName);
    }
    
    /**
     * Retourne le chemin du repertoire utilise.
     * @return le chemin du repertoire utilise
     */
    public String getDirectory() {
        return this.directory;
    }
    
    /**
     * Retourne le nom du fichier properties utilise.
     * @return le nom du fichier properties utilise
     */
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * Modifie le repertoire utilise.
     * @param directory le path correspondant au nouveau repertoire
     */
    public void setDirectory(String directory) {
        this.directory = directory;
        if (!this.directory.isEmpty() && (this.directory.charAt(this.directory.length()-1) != File.separator.toCharArray()[0])) this.directory += File.separator;
    }
    
    /**
     * Modifie le nom du fichier properties utilise.
     * @param fileName le nouveau nom du fichier
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (!this.fileName.contains(".")) { //extension oubliée
            this.fileName += ".json";
        }
    }
    
    /**
     * Retourne le chemin de stockage.
     * @return le chemin de stockage
     */
    public String getPath() {
        return getPath(this.fileName);
    }
    
    /**
     * Retourne le chemin de stockage pour le fichier donne.
     * @param fileName nom du fichier
     * @return le chemin de stockage
     */
    public String getPath(String fileName) {
        return this.directory + fileName;
    }
    
    /**
     * Retourne true si le fichier donne contient l'objet donne, false sinon.
     * @param json objet a chercher
     * @param fileName nom du fichier
     * @return true si le fichier donne contient l'objet donne, false sinon
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     */
    public boolean contains(JSONObject json, String fileName) throws FileNotFoundException {
        for (JSONObject o : read(fileName)) {
            if (o.toString().equals(json.toString())) return true;
        }
        return false;
    }
    
    /**
     * Retourne true si le fichier contient l'objet donne, false sinon.
     * @param json objet a chercher
     * @return true si le fichier donne contient l'objet donne, false sinon
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     */
    public boolean contains(JSONObject json) throws FileNotFoundException {
        return contains(json, this.fileName);
    }
    
    /**
     * Retourne true si le fichier donne contient l'objet donne, false sinon.
     * @param object objet a chercher
     * @param fileName nom du fichier
     * @return true si le fichier donne contient l'objet donne, false sinon
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     */
    public boolean contains(Object object, String fileName) throws FileNotFoundException {
        for (JSONObject json : read(fileName)) {
            if (json.toString().equals(new JSONObject(object).toString())) return true;
        }
        return false;
    }
    
    /**
     * Retourne true si le fichier contient l'objet donne, false sinon.
     * @param object objet a chercher
     * @return true si le fichier donne contient l'objet donne, false sinon
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     */
    public boolean contains(Object object) throws FileNotFoundException {
        return contains(object, this.fileName);
    }
    
    /**
     * Cree le fichier json s'il n'existe pas deja.
     * @throws IOException exception levee quand il est impossible de creer le fichier json
     */
    public void createFile() throws IOException {
        File dir = new File(this.directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(getPath());
        if (!file.exists()) {
            file.createNewFile();
        } else if (!file.isFile()) {
            throw new IOException("The file '" + file.getAbsolutePath() + "' is a directory, impossible to create the json file");
        }
    }
    
    /**
     * Ecrit l'objet donne en memoire dans le fichier donne.
     * @param json objet json a persister
     * @param fileName nom du fichier de stockage
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void write(JSONObject json, String fileName) throws IOException {
        createFile();
        try (FileWriter writer = new FileWriter(getPath(fileName))) {
            writer.write(json.toString(1));
        }
    }
    
    /**
     * Ecrit l'objet json donne en memoire.
     * @param json objet json a persister
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void write(JSONObject json) throws IOException {
        write(json, this.fileName);
    }
    
    /**
     * Stock les objets donnes dans le fichier donne.
     * @param objectsList liste des objets a stocker
     * @param fileName nom du fichier de stockage
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void write(List<Object> objectsList, String fileName) throws IOException {
        JSONObject json = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        json.put("Objects", jSONArray); //JSONException impossible car clé jamais null
        objectsList.forEach(o -> {
            jSONArray.put(new JSONObject(o));
        });
        write(json, fileName);
    }
    
    /**
     * Stock les objets donnees.
     * @param objectsList liste des objets a stocker
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void write(List<Object> objectsList) throws IOException {
        write(objectsList, this.fileName);
    }
    
    /**
     * Stock les JSONObject donnes dans le fichier donne.
     * @param jsonList liste des JSONObject a stocker
     * @param fileName nom du fichier de stockage
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void writeJSON(List<JSONObject> jsonList, String fileName) throws IOException {
        JSONObject json = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        json.put("Objects", jSONArray); //JSONException impossible car clé jamais null
        jsonList.forEach(o -> {
            jSONArray.put(o);
        });
        write(json, fileName);
    }
    
    /**
     * Stock les JSONObject donnes.
     * @param jsonList liste des JSONObject a stocker
     * @throws IOException exception levee quand l'ecriture a echouee
     */
    public void writeJSON(List<JSONObject> jsonList) throws IOException {
        writeJSON(jsonList, this.fileName);
    }
    
    /**
     * Retourne la liste des objets stockes dans le fichier donne.
     * @param fileName fichier dans lequel lire les objets
     * @return la liste des objets stockes
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas ou n'est pas lisible
     */
    public ArrayList<JSONObject> read(String fileName) throws FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileReader(getPath(fileName)));
        ArrayList<JSONObject> result = new ArrayList<>();
        JSONArray objectsStored;
        try {
            objectsStored = new JSONObject(tokener).getJSONArray("Objects");
            for (int i=0; i<objectsStored.length(); i++) {
                result.add(objectsStored.getJSONObject(i));
            }
        } catch (JSONException je) {
            throw new FileNotFoundException(je.getMessage());
        }
        return result;
    }
    
    /**
     * Retourne la liste des objets stockes.
     * @return la liste des objets stockes
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas ou n'est pas lisible
     */
    public ArrayList<JSONObject> read() throws FileNotFoundException {
        return read(this.fileName);
    }
    
    /**
     * Ajoute l'objet dans le fichier de donne s'il n'y est pas deja se base 
     * sur les getters pour la persistance.
     * @param object objet a ajouter au fichier
     * @param fileName nom du fichier de stockage
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void add(Object object, String fileName) throws IOException {
        ArrayList<JSONObject> storedObject;
        try {
            storedObject = read(fileName);
        } catch (FileNotFoundException fnfe) {
            storedObject = new ArrayList<>();
        }
        for (JSONObject o : storedObject) {
            if (o.toString().equals(new JSONObject(object).toString())) return;
        }
        storedObject.add(new JSONObject(object));
        writeJSON(storedObject, fileName);
    }
    
    /**
     * Ajoute l'objet dans le fichier de stockage s'il n'y est pas deja se base 
     * sur les getters pour la persistance.
     * @param object objet a ajouter au fichier
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void add(Object object) throws IOException {
        add(object, this.fileName);
    }
    
    /**
     * Supprime l'objet donne du fichier donne.
     * @param json objet a supprimer du fichier
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(JSONObject json, String fileName) throws FileNotFoundException, IOException {
        List<JSONObject> storedObjects = read(fileName).stream()
                .filter(o -> !o.toString().equals(json.toString()))
                .collect(Collectors.toList());
        writeJSON(storedObjects, fileName);
    }
    
    /**
     * Supprime l'objets donne du fichier.
     * @param json objet a supprimer du fichier
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(JSONObject json) throws FileNotFoundException, IOException {
        delete(json, this.fileName);
    }
    
    /**
     * Supprime l'objet donne du fichier donne.
     * @param object objet a supprimer du fichier
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(Object object, String fileName) throws FileNotFoundException, IOException {
        delete(new JSONObject(object));
    }
    
    /**
     * Supprime l'objets donne du fichier.
     * @param object objet a supprimer du fichier
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(Object object) throws FileNotFoundException, IOException {
        delete(object, this.fileName);
    }
    
    /**
     * Supprime tous les objets qui possedent la valeur indiquee associee a 
     * la cle donnee dans le fichier donnee.
     * @param key cle
     * @param value valeur du/des objet(s) a supprimer
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(String key, String value, String fileName) throws FileNotFoundException, IOException {
        List<JSONObject> storedObjects = read(fileName).stream()
                .filter(o -> !o.has(key) || !o.getString(key).equals(value))
                .collect(Collectors.toList());
        writeJSON(storedObjects, fileName);
    }
    
    /**
     * Supprime tous les objets qui possedent la valeur indiquee associee a 
     * la cle donnee dans le fichier.
     * @param key cle
     * @param value valeur du/des objet(s) a supprimer
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void delete(String key, String value) throws FileNotFoundException, IOException {
        delete(key, value, this.fileName);
    }
    
    /**
     * Remplace l'objet oldObject par newObject dans le fichier donne.
     * @param oldObject ancien objet
     * @param newObject nouvel objet
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(JSONObject oldObject, JSONObject newObject, String fileName) throws FileNotFoundException, IOException {
        delete(oldObject, fileName);
        add(newObject, fileName);
    }
    
    /**
     * Remplace l'objet oldObject par newObject dans le fichier.
     * @param oldObject ancien objet
     * @param newObject nouvel objet
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(JSONObject oldObject, JSONObject newObject) throws FileNotFoundException, IOException {
        replace(oldObject, newObject, this.fileName);
    }
    
    /**
     * Remplace l'objet oldObject par newObject dans le fichier donne.
     * @param oldObject ancien objet
     * @param newObject nouvel objet
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(Object oldObject, Object newObject, String fileName) throws FileNotFoundException, IOException {
        delete(oldObject, fileName);
        add(newObject, fileName);
    }
    
    /**
     * Remplace l'objet oldObject par newObject dans le fichier.
     * @param oldObject ancien objet
     * @param newObject nouvel objet
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(Object oldObject, Object newObject) throws FileNotFoundException, IOException {
        replace(oldObject, newObject, this.fileName);
    }
    
    /**
     * Remplace l'objet avec la valeur donnee associee a la cle donnee 
     * par newObject dans le fichier donne.
     * @param key la cle
     * @param value valeur associee a la cle
     * @param newObject nouvel objet
     * @param fileName nom du fichier de stockage
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(String key, String value, Object newObject, String fileName) throws FileNotFoundException, IOException {
        delete(key, value, fileName);
        add(newObject, fileName);
    }
    
    /**
     * Remplace l'objet avec la valeur donnee associee a la cle donnee 
     * par newObject dans le fichier.
     * @param key la cle
     * @param value valeur associee a la cle
     * @param newObject nouvel objet
     * @throws FileNotFoundException exception levee quand le fichier n'existe pas
     * @throws IOException exception levee quand l'ecriture dans le fichier a echouee
     */
    public void replace(String key, String value, Object newObject) throws FileNotFoundException, IOException {
        replace(key, value, newObject, this.fileName);
    }
}
