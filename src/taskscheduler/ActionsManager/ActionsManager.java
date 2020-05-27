package taskscheduler.ActionsManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Gestionnaires des actions effectuées par l'utilisateur.
 * @author Johan
 */
public class ActionsManager {
    public static final String NB_UNDO_PROPERTY = "nbUndo";
    public static final String NB_REDO_PROPERTY = "nbRedo";
    public static final String BUFFERS_SIZE_PROPERTY = "buffersSize";
    public static final long DEFAULT_BUFFERS_SIZE = 50;
    protected final PropertyChangeSupport changeSupport;
    private final EvictingStack<Action> undoActions;
    private final EvictingStack<Action> redoActions;
    private long buffersSize;
    
    /**
     * Constructeur d'un gestionnaire d'actions.
     * @param buffersSize taille des buffers undo et redo
     */
    public ActionsManager(long buffersSize) {
        this.changeSupport = new PropertyChangeSupport(this);
        this.buffersSize = buffersSize;
        this.undoActions = new EvictingStack<>(this.buffersSize);
        this.redoActions = new EvictingStack<>(this.buffersSize);
    }
    
    /**
     * Constructeur d'un gestionnaire d'actions.
     */
    public ActionsManager() {
        this(DEFAULT_BUFFERS_SIZE);
    }
    
    /**
     * Retourne la taille des buffers undo et redo.
     * @return la taille des buffers undo et redo
     */
    public long getBuffersSize() {
        return this.buffersSize;
    }
    
    /**
     * Modifie la taille des buffers undo et redo.
     * @param buffersSize nouvelle taille des buffers undo et redo
     */
    public void setBuffersSize(long buffersSize) {
        if (this.buffersSize != buffersSize) {
            this.changeSupport.firePropertyChange(BUFFERS_SIZE_PROPERTY, this.buffersSize, buffersSize);
            this.buffersSize = buffersSize;
            this.undoActions.setMaxSize(buffersSize);
            this.redoActions.setMaxSize(buffersSize);
        }
    }
    
    /**
     * Retourne la derniere action a annuler ajoutee au buffer (sommet de pile), 
     * null s'il n'y en a pas.
     * @return la derniere action a annuler ajoutee au buffer (sommet de pile)
     */
    public Action getLastUndoAction() {
        if (this.undoActions.isEmpty()) return null;
        return this.undoActions.peek();
    }
    
    /**
     * Retourne la derniere action a retablir ajoutee au buffer (sommet de pile).
     * @return la derniere action a retablir ajoutee au buffer (sommet de pile)
     */
    public Action getLastRedoAction() {
        if (this.redoActions.isEmpty()) return null;
        return this.redoActions.peek();
    }
    
    /**
     * Retourne le nombre d'actions a annuler en cache.
     * @return le nombre d'actions a annuler en cache
     */
    public long getNbUndoActions() {
        return this.undoActions.size();
    }
    
    /**
     * le nombre d'actions a retablir en cache
     * @return le nombre d'actions a retablir en cache
     */
    public long getNbRedoActions() {
        return this.redoActions.size();
    }
    
    /**
     * Supprime les actions redo et undo inutiles.
     * Commence par les sommets de pile et s'arrete a la premiere action valide pour chaque.
     */
    public void removeInvalidActions() {
        removeInvalidUndo();
        removeInvalidRedo();
    }
    
    /**
     * Supprime les undo inutiles.
     * Commence par le sommet de la pile et s'arrete au premier undo valide.
     */
    public void removeInvalidUndo() {
        while(!this.undoActions.isEmpty()) {
            if (this.undoActions.peek().isValid()) return;
            this.undoActions.pop();
        }
    }
    
    /**
     * Supprime les redo inutiles.
     * Commence par le sommet de la pile et s'arrete au premier redo valide.
     */
    public void removeInvalidRedo() {
        while(!this.redoActions.isEmpty()) {
            if (this.redoActions.peek().isValid()) return;
            this.redoActions.pop();
        }
    }
    
    /**
     * Ajoute une action a annuler dans le buffer si elle est differente de la 
     * derniere action du buffer (sommet de pile) et qu'elle est valide.
     * @param action action a annuler
     */
    public void addUndoAction(Action action) {
        if ((!this.undoActions.isEmpty() && this.undoActions.peek().equals(action)) //évite les doublons d'affilé
          || !action.isValid()) return; 
        this.undoActions.push(action);
        removeInvalidActions();
        this.changeSupport.firePropertyChange(NB_UNDO_PROPERTY, this.undoActions.size()-1, this.undoActions.size());
    }
    
    /**
     * Ajoute une action a retablir dans le buffer.
     * @param action action a retablir
     */
    public void addRedoAction(Action action) {
        if ((!this.redoActions.isEmpty() && this.redoActions.peek().equals(action)) //évite les doublons d'affilé
          || !action.isValid()) return; //n'ajoute pas une operation inutile
        this.redoActions.push(action);
        removeInvalidActions();
        this.changeSupport.firePropertyChange(NB_REDO_PROPERTY, this.redoActions.size()-1, this.redoActions.size());
    }
    
    /**
     * Supprime la derniere action effectuee.
     * @return la derniere action effectuee s'il y en a une, null sinon
     */
    public Action removeUndoAction() {
        if (this.undoActions.isEmpty()) return null;
        Action action = this.undoActions.pop();
        this.changeSupport.firePropertyChange(NB_UNDO_PROPERTY, this.undoActions.size()+1, this.undoActions.size());
        return action;
    }
    
    /**
     * Supprime la derniere action annulee.
     * @return la derniere action annulee s'il y en a une, null sinon
     */
    public Action removeRedoAction() {
        if (this.redoActions.isEmpty()) return null;
        Action action = this.redoActions.pop();
        this.changeSupport.firePropertyChange(NB_REDO_PROPERTY, this.redoActions.size()+1, this.redoActions.size());
        return action;
    }
    
    /**
     * Annule la derniere action effectuee.
     * @throws java.lang.Exception Impossible d'annuler l'action
     */
    public void undo() throws Exception {
        if (!this.undoActions.isEmpty()) {
            Action lastAction = removeUndoAction();
            lastAction.invert();
            addRedoAction(lastAction);
        }
    }
    
    /**
     * Retabli la deriere action annulee.
     * @throws java.lang.Exception Impossible de retablir l'action
     */
    public void redo() throws Exception {
        if (!this.redoActions.isEmpty()) {
            Action lastCanceledAction = removeRedoAction();
            lastCanceledAction.invert();
            addUndoAction(lastCanceledAction);
        }
    }
    
    /**
     * Annule toutes les actions effectuees encore en cache.
     * @throws java.lang.Exception Impossible de retablir l'action
     */
    public void undoAll() throws Exception{
        while (!this.undoActions.isEmpty()) undo();
    }
    
    /**
     * Retabli toutes les actions annulees encore en cache.
     * @throws java.lang.Exception Impossible de retablir l'action
     */
    public void redoAll() throws Exception {
        while (!this.redoActions.isEmpty()) redo();
    }
    
    /**
     * Supprime toutes les actions undo et redo en cache.
     */
    public void clear() {
        clearUndo();
        clearRedo();
    }
    
    /**
     * Supprime toutes les actions undo en cache.
     */
    public void clearUndo() {
        this.changeSupport.firePropertyChange(NB_UNDO_PROPERTY, this.undoActions.size(), 0);
        this.undoActions.clear();
    }
    
    /**
     * Supprime toutes les actions redo en cache.
     */
    public void clearRedo() {
        this.changeSupport.firePropertyChange(NB_REDO_PROPERTY, this.redoActions.size(), 0);
        this.redoActions.clear();
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
