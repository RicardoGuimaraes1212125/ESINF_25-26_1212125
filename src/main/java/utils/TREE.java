package utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/*
 * @author DEI-ESINF
 * @param <E>
 */

public class TREE<E extends Comparable<E>> extends BST<E>{
 
    
     /*
     * @param element A valid element within the tree
     * @return the path to a given element in the tree
     */
    public List<E> path(E elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

     /*
     * @return the set of the leaf node elements of the tree
     */
    public Set<E> leafs(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /*
    * @return an array with the minimum and the maximum values of the tree
    */
    public E[] range() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /*
    *  @return the set of elements belonging to the diameter of the BST
    */
    public Set<E> diameter(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /*
    *  @return the previous element of the tree for a given element
    */
    public E findPredecessor (E element){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
    /*
    * – verify if the current and tree BST are identical.
    */
    public boolean identical(BST<E> tree){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
        
    /*
    * – remove all elements in the current BST that are outside the range [low, high]
    */
    public void truncate(E low, E high){
        throw new UnsupportedOperationException("Not supported yet.");
    }  
            
    /*
     *– return true if BST<E> tree is a sub tree of the BST<E>. 
     */        
    public boolean isSubTree(BST<E> tree){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
    
    public boolean isSymmetric(){
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
    
    public TREE<E> minimumSubtree (Set<E> elems ) {
        throw new UnsupportedOperationException("Not supported yet.");
    } 
    
}
