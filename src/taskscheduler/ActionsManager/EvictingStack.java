package taskscheduler.ActionsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * The EvictingStack class represents a last-in-first-out (LIFO) stack of objects.
 * It has a maximal size and evict the first element (at the bottom of the stack)
 * when a new item is added while it's full.
 * The null elements aren't accepted.
 * It extends class Stack. The usual push and pop operations are provided, 
 * as well as a method to peek at the top item on the stack, a method to test 
 * for whether the stack is empty, and a method to search the stack for an 
 * item and discover how far it is from the top.\n
 * When a stack is first created, it contains no items.
 * @param <E> elements type in this EvictingStack
 * @author Johan
 */
public class EvictingStack<E> extends Stack<E> {
    private long maxSize;
    
    /**
     * Creates an empty EvictingStack.
     * @param maxSize maximal size of the stack
     */
    public EvictingStack(long maxSize) {
        this.maxSize = maxSize;
        if (this.maxSize < 0) this.maxSize = 0;
    }
    
    /**
     * Return the maximal size of this Stack.
     * @return the maximal size of this Stack
     */
    public synchronized long getMaxSize() {
        return this.maxSize;
    }
    
    /**
     * Change the maximal size of this stack.
     * @param maxSize the new size of this stack
     */
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Enleve les elements en bas de la pile tant que le taille de la pile 
     * excede la taille maximale.
     * @return le dernier element enleve
     */
    private E evict() {
        E evictedItem = null;
        while (this.size() > this.maxSize) {
            evictedItem = remove(0);
        }
        return evictedItem;
    }
    
    @Override
    public synchronized boolean add(E e) {
        if (e == null) return false;
        boolean result = super.add(e);
        evict();
        return result;
    }
    
    @Override
    public void add(int index, E e) {
        if (e == null) return;
        super.add(index, e);
        evict();
    }
    
    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        if (c == null) return false;
        boolean result = super.addAll(c.stream().filter(e -> e != null).collect(Collectors.toList()));
        evict();
        return result;
    }
    
    @Override
    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        if (c == null) return false;
        boolean result = super.addAll(index, c.stream().filter(e -> e != null).collect(Collectors.toList()));
        evict();
        return result;
    }
    
    @Override
    public synchronized void addElement(E obj) {
        if (obj == null) return;
        super.addElement(obj);
        evict();
    }
    
    @Override
    public synchronized void insertElementAt(E obj, int index) {
        if (obj == null) return;
        super.insertElementAt(obj, index);
        evict();
    }
    
    /**
     * Pushes an item onto the top of this stack.
     * @param item the item to be pushed onto this stack
     * @return the first element removed from the stack if the stack was full, null otherwise
     */
    @Override
    public E push(E item) {
        if (item == null) return null;
        super.add(item);
        return evict();
    }
    
    /**
     * Empty the stack and return all elements from top to bottom order.
     * @return all the elements present in the stack starting with the elements at the top
     */
    public List<E> popAll() {
        List<E> allElements = new ArrayList<>();
        while (!isEmpty()) allElements.add(pop());
        return allElements;
    }
}
