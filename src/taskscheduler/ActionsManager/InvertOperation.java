package taskscheduler.ActionsManager;

import java.util.Objects;

/**
 * Operation a effectuer pour inverser deux actions.
 * @author Johan
 */
public abstract class InvertOperation {
    private final OperationType operationType;
    
    /**
     * Constructeur d'une InvertOperation.
     * @param operationType type d'operation effectuee, necessaire pour le equals
     */
    public InvertOperation(OperationType operationType) {
        this.operationType = operationType;
    }
    
    /**
     * Inverse deux actions.
     * @throws java.lang.Exception Impossible d'inverser les deux actions
     */
    public abstract void invert() throws Exception;
    
    /**
     * Retourne true si l'operation est toujours valide, false sinon.
     * Override cette methode permet d'eviter qu'une operation proposee n'est aucun effet.
     * @return true si l'operation est toujours valide, false sinon
     */
    public boolean isValid() {
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.operationType);
        return hash;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || !(o instanceof InvertOperation)) return false;
        return this.operationType.equals(((InvertOperation)o).operationType);
    }
}
