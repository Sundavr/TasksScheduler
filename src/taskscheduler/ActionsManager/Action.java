package taskscheduler.ActionsManager;

import java.util.Objects;


/**
 * Action effectuee par l'utilisateur, afin d'eviter d'avoir plusieurs actions similaires 
 * consecutives, les objets utilises doivent redefinir les methodes equals et hashcode.
 * @author Johan
 * @param <T> type des objets affectes par l'action
 */
public class Action<T> {
    private String name;
    private T beforeAction;
    private T afterAction;
    private InvertOperation invertActions;
    
    /**
     * Constructeur d'une Action.
     * @param name Nom de l'action a effectuer
     * @param beforeAction objet avant l'action
     * @param afterAction objet apres l'action
     * @param invertActions operation d'inversion des etats avant/apres
     */
    public Action(String name, T beforeAction, T afterAction, InvertOperation invertActions) {
        this.name = name;
        this.beforeAction = beforeAction;
        this.afterAction = afterAction;
        this.invertActions = invertActions;
    }
    
    /**
     * Retourne le nom de l'action.
     * @return le nom de l'action
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Modifie le nom de l'action.
     * @param name le nouveau nom de l'action
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Retourne l'objet affecte par l'action avant les changements.
     * @return l'objet affecte par l'action avant les changements
     */
    public T getBeforeActionObject() {
        return this.beforeAction;
    }

    /**
     * Retourne l'objet affecte par l'action apres les changements
     * @return l'objet affecte par l'action apres les changements
     */
    public T getAfterActionObject() {
        return this.afterAction;
    }
    
    /**
     * Retourne l'operation d'inversion des etats avant/apres.
     * @return l'operation d'inversion des etats avant/apres
     */
    public InvertOperation getInvertAction() {
        return this.invertActions;
    }
    
    /**
     * Retourne true si l'operation est toujours valide, false sinon.
     * @return true si l'operation est toujours valide, false sinon
     * @see taskscheduler.ActionsManager.InvertOperation
     */
    public boolean isValid() {
        return this.invertActions.isValid();
    }
    
    /**
     * Change l'objet avant l'action.
     * @param beforeAction le nouvelle objet avant l'action
     */
    public void setBeforeActionObject(T beforeAction) {
        this.beforeAction = beforeAction;
    }
    
    /**
     * Change l'objet apres l'action.
     * @param afterAction le nouvelle objet apres l'action
     */
    public void setAfterActionObject(T afterAction) {
        this.afterAction = afterAction;
    }
    
    /**
     * Modifie l'operation d'inversion des etats avant/apres.
     * @param invertActions la nouvelle operation d'inversion des etats avant/apres
     */
    public void setInvertFunction(InvertOperation invertActions) {
        this.invertActions = invertActions;
    }
    
    /**
     * Annule l'action et inverse les etats avant/apres action.
     * @throws java.lang.Exception Impossible d'inverser les deux actions
     */
    public void invert() throws Exception {
        this.invertActions.invert();
        T tmp = this.beforeAction;
        this.beforeAction = this.afterAction;
        this.afterAction = tmp;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.beforeAction);
        hash = 97 * hash + Objects.hashCode(this.afterAction);
        hash = 97 * hash + Objects.hashCode(this.invertActions);
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || !(o instanceof Action)) return false;
        Action action = (Action)o;
        boolean sameBeforeAction = (this.beforeAction == null) ? (action.getBeforeActionObject() == null) : this.beforeAction.equals(action.getBeforeActionObject());
        boolean sameAfterAction = (this.afterAction == null) ? (action.getAfterActionObject() == null) : this.afterAction.equals(action.getAfterActionObject());
        boolean sameInvertAction = this.invertActions.equals(action.getInvertAction());
        return sameBeforeAction && sameAfterAction && sameInvertAction;
    }
    
    @Override
    public String toString() {
        return "Action(" + this.name + ")";
    }
}
